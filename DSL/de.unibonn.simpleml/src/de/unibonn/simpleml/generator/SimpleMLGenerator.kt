package de.unibonn.simpleml.generator

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.utils.*
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import org.eclipse.xtext.naming.IQualifiedNameProvider

/**
 * Generates code from your model files on save.
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class SimpleMLGenerator @Inject constructor(
        private val qualifiedNameProvider: IQualifiedNameProvider
) : AbstractGenerator() {

    private val indent = "    "

    override fun doGenerate(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        if (resource.isStubFile()) {
            return // We do not generate anything for stub files yet
        }

        generateDeclarationFile(resource, fsa, context)
        generateWorkflowFiles(resource, fsa, context)
    }

    private fun generateDeclarationFile(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        if (context.cancelIndicator.isCanceled) {
            return
        }

        val fileName = "${resource.baseGeneratedFilePath()}.py"
        val compilationUnit = resource.compilationUnitOrNull() ?: return
        val content = compile(compilationUnit)

        fsa.generateFile(fileName, content)
    }

    /**
     * Creates one Python file for each workflow in the given resource that just contains a main block that calls the
     * workflow. This way we can run the Python interpreter with the created file to run the workflow.
     *
     * **Example:** Given the following situation
     *  * Simple-ML package: "com.example"
     *  * Simple-ML file:    "test.simpleml"
     *  * Workflow names:    "workflow1", "workflow2"
     *
     * we create two files in the folder "com/example" (determined by the Simple-ML package). The file for "workflow1"
     * is called "test$workflow1.py" and the file for "workflow2" is called "test$workflow2.py". The names are created
     * by taking the Simple-ML file name, removing the file extension, appending a dollar sign, and then the workflow
     * name.
     */
    private fun generateWorkflowFiles(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        resource.allContents.asSequence()
                .filterIsInstance<SmlWorkflow>()
                .forEach {
                    if (context.cancelIndicator.isCanceled) {
                        return
                    }

                    val fileName = "${resource.baseGeneratedFilePath()}_${it.name}.py"
                    val content = """
                        |from gen_${resource.baseFileName()} import ${it.name}
                        |
                        |if __name__ == '__main__':
                        |$indent${it.name}()
                        |
                    """.trimMargin()

                    fsa.generateFile(fileName, content)
                }
    }

    private fun compile(compilationUnit: SmlCompilationUnit) = buildString {

        // Imports
        val importsString = compileImports(compilationUnit)
        appendLine("# Imports ----------------------------------------------------------------------\n")
        appendLine("from runtimeBridge import save_placeHolder")
        if (importsString.isNotBlank()) {
            appendLine(importsString)
        } else {
            appendLine()
        }

        // Workflow steps
        val workflowStepString = compilationUnit.members
                .filterIsInstance<SmlWorkflowStep>()
                .sortedBy { it.name }
                .joinToString("\n") {
                    compileWorkflowSteps(it)
                }
        if (workflowStepString.isNotBlank()) {
            appendLine("# Workflow steps ---------------------------------------------------------------\n")
            appendLine(workflowStepString)
        }

        // Workflows
        val workflowString = compilationUnit.members
                .filterIsInstance<SmlWorkflow>()
                .sortedBy { it.name }
                .joinToString("\n") {
                    compileWorkflow(it)
                }
        if (workflowString.isNotBlank()) {
            appendLine("# Workflows --------------------------------------------------------------------\n")
            append(workflowString)
        }
    }

    private fun compileImports(compilationUnit: SmlCompilationUnit) = buildString {
        // TODO split this into a function that gets all external references inside an arbitrary eObject

        compilationUnit.eAllContents()
                .asSequence()
                .filterIsInstance<SmlReference>()
                .map { it.declaration }
                .filter { it.isCompilationUnitMember() && it.containingCompilationUnitOrNull() != compilationUnit }
                .mapNotNull {
                    val importPath = qualifiedNameProvider.getFullyQualifiedName(it).toString()
                            .split(".")
                            .dropLast(1)
                            .toMutableList()

                    if (importPath.isEmpty()) {
                        println("Declaration not in a package $it.")
                        null
                    } else {
                        if (importPath.first() != "simpleml") {
                            val fileName = it.eResource().baseFileName().split("/").last()
                            importPath += fileName
                        }

                        ImportData(importPath.joinToString("."), it.name)
                    }
                }
                .toSet()
                .groupBy { it.importPath }
                .entries
                .sortedBy { it.key }
                .forEach { (key, value) ->
                    val declarationNames = value.sortedBy { it.declarationName }.joinToString { it.declarationName }
                    appendLine("from $key import $declarationNames")
                }
    }

    private data class ImportData(val importPath: String, val declarationName: String)

    private fun compileWorkflowSteps(workflowStep: SmlWorkflowStep) = buildString {
        append("def ${workflowStep.name}(")
        append(workflowStep.parametersOrEmpty().joinToString { it.name })
        appendLine("):")

        if (workflowStep.statementsOrEmpty().isEmpty()) {
            appendLine("${indent}pass")
        } else {
            workflowStep.statementsOrEmpty().forEach {
                appendLine("$indent${compileStatement(it)}")
            }

            if (workflowStep.resultsOrEmpty().isNotEmpty()) {
                appendLine("${indent}return ${workflowStep.resultsOrEmpty().joinToString { it.name }}")
            }
        }
    }

    private fun compileWorkflow(workflow: SmlWorkflow) = buildString {
        appendLine("def ${workflow.name}():")
        workflow.statementsOrEmpty().forEach {
            appendLine(compileStatement(it, shouldSavePlaceholders = true).prependIndent(indent))
        }
    }

    private fun compileStatement(stmt: SmlStatement, shouldSavePlaceholders: Boolean = false) = buildString {
        when (stmt) {
            is SmlAssignment -> {
                if (stmt.assigneesOrEmpty().isNotEmpty()) {
                    val assignees = stmt.assigneesOrEmpty().joinToString {
                        when (it) {
                            is SmlPlaceholder -> it.name
                            is SmlYield -> it.result.name
                            else -> {
                                "_"
                            }
                        }
                    }
                    append("$assignees = ")
                }
                append(compileExpression(stmt.expression))

                if (shouldSavePlaceholders) {
                    stmt.placeholdersOrEmpty().forEach {
                        append("\nsave_placeHolder('${it.name}', ${it.name})")
                    }
                }
            }
            is SmlExpressionStatement -> {
                append(compileExpression(stmt.expression))
            }
            else -> {
                println("Unknown statement $stmt.")
            }
        }
    }

    private fun compileExpression(expr: SmlExpression): String {
        return when (expr) {
            is SmlBoolean -> {
                if (expr.isTrue) {
                    "True"
                } else {
                    "False"
                }
            }
            is SmlFloat -> {
                expr.value.toString()
            }
            is SmlInfixOperation -> {
                "(${compileExpression(expr.leftOperand)}) ${expr.operator} (${compileExpression(expr.rightOperand)})"
            }
            is SmlInt -> {
                expr.value.toString()
            }
            is SmlMemberAccess -> {
                val receiver = compileExpression(expr.receiver)
                "$receiver.${expr.member.declaration.name}"
            }
            is SmlCall -> {
                val receiver = compileExpression(expr.receiver)
                val arguments = expr.argumentList.arguments.joinToString {
                    if (it.isNamed()) {
                        "${it.parameter?.name}=${compileExpression(it.value)}"
                    } else {
                        compileExpression(it.value)
                    }
                }
                "$receiver($arguments)"
            }
            is SmlParenthesizedExpression -> {
                "(${expr.expression})"
            }
            is SmlPrefixOperation -> {
                "${expr.operator} (${compileExpression(expr.operand)})"
            }
            is SmlString -> {
                "'${expr.value}'"
            }
            is SmlReference -> {
                expr.declaration.name
            }
            else -> {
                println("Unknown expression $expr.")
                ""
            }
        }
    }
}
