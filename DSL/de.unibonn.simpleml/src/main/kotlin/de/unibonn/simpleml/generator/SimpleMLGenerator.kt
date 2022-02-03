package de.unibonn.simpleml.generator

import de.unibonn.simpleml.constant.SmlInfixOperationOperator.And
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Elvis
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.IdenticalTo
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.NotIdenticalTo
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Or
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.constant.isFlowFile
import de.unibonn.simpleml.constant.isTestFile
import de.unibonn.simpleml.constant.operator
import de.unibonn.simpleml.emf.assigneesOrEmpty
import de.unibonn.simpleml.emf.compilationUnitOrNull
import de.unibonn.simpleml.emf.containingCompilationUnitOrNull
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.isGlobal
import de.unibonn.simpleml.emf.isNamed
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.emf.statementsOrEmpty
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantBoolean
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantEnumVariant
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantFloat
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantInt
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantNull
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantString
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

/**
 * Generates code from your model files on save.
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class SimpleMLGenerator : AbstractGenerator() {

    private val indent = "    "

    /**
     * Creates Python workflow and declaration files if the [resource] is either a Simple-ML flow or test file.
     */
    override fun doGenerate(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        if (resource.isFlowFile() || resource.isTestFile()) {
            generateWorkflowFiles(resource, fsa, context)
            generateDeclarationFile(resource, fsa, context)
        }
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
     * is called "test_workflow1.py" and the file for "workflow2" is called "test_workflow2.py". The names are created
     * by taking the Simple-ML file name, removing the file extension, appending an underscore, and then the workflow
     * name.
     */
    private fun generateWorkflowFiles(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        resource.allContents.asSequence()
            .filterIsInstance<SmlWorkflow>()
            .forEach {
                if (context.cancelIndicator.isCanceled) {
                    return
                }

                val fileName = "${resource.baseGeneratedFilePathOrNull()}_${it.name}.py"
                val content = """
                        |from gen_${resource.baseFileNameOrNull()} import ${it.name}
                        |
                        |if __name__ == '__main__':
                        |$indent${it.name}()
                        |
                    """.trimMargin()

                fsa.generateFile(fileName, content)
            }
    }

    private fun generateDeclarationFile(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        if (context.cancelIndicator.isCanceled) {
            return
        }

        val fileName = "${resource.baseGeneratedFilePathOrNull()}.py"
        val compilationUnit = resource.compilationUnitOrNull() ?: return
        val content = compile(compilationUnit)

        fsa.generateFile(fileName, content)
    }

    private fun compile(compilationUnit: SmlCompilationUnit) = buildString {

        // Imports
        val importsString = compileImports(compilationUnit)
        if (importsString.isNotBlank()) {
            appendLine("# Imports ----------------------------------------------------------------------\n")
            appendLine(importsString)
        }

        // Steps
        val stepString = compilationUnit
            .descendants<SmlStep>()
            .sortedBy { it.name }
            .joinToString("\n") {
                compileWorkflowSteps(it)
            }
        if (stepString.isNotBlank()) {
            appendLine("# Steps ------------------------------------------------------------------------\n")
            appendLine(stepString)
        }

        // Workflows
        val workflowString = compilationUnit
            .descendants<SmlWorkflow>()
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

        if (compilationUnit.descendants<SmlPlaceholder>().firstOrNull() != null) {
            appendLine("from runtimeBridge import save_placeHolder")
        }

        val codegenImports = mutableListOf<String>()
        if (compilationUnit.descendants<SmlInfixOperation>().any { it.operator() == Or }) {
            codegenImports += "eager_or"
        }
        if (compilationUnit.descendants<SmlInfixOperation>().any { it.operator() == And }) {
            codegenImports += "eager_and"
        }
        if (compilationUnit.descendants<SmlInfixOperation>().any { it.operator() == Elvis }) {
            codegenImports += "eager_elvis"
        }

        if (codegenImports.isNotEmpty()) {
            appendLine("from simpleml.codegen import ${codegenImports.joinToString()}")
        }

        compilationUnit
            .descendants<SmlReference>()
            .map { it.declaration }
            .filter { it.isGlobal() && it.containingCompilationUnitOrNull() != compilationUnit }
            .mapNotNull {
                val importPath = it.qualifiedNameOrNull().toString()
                    .split(".")
                    .dropLast(1)
                    .toMutableList()

                if (importPath.isEmpty()) {
                    println("Declaration not in a package $it.")
                    null
                } else {
                    if (importPath.first() != "simpleml") {
                        val fileName = it.eResource().baseFileNameOrNull() ?: return@mapNotNull null
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

    private fun compileWorkflowSteps(workflowStep: SmlStep) = buildString {
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

    private fun compileStatement(stmt: SmlAbstractStatement, shouldSavePlaceholders: Boolean = false) = buildString {
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

    // TODO: make deep recursive function
    private fun compileExpression(expr: SmlAbstractExpression): String {
        val constantExpr = expr.toConstantExpressionOrNull()
        if (constantExpr != null) {
            return when (constantExpr) {
                is SmlConstantBoolean -> if (constantExpr.value) "True" else "False"
                is SmlConstantEnumVariant -> TODO()
                SmlConstantNull -> "None"
                is SmlConstantFloat -> constantExpr.value.toString()
                is SmlConstantInt -> constantExpr.value.toString()
                is SmlConstantString -> "'${constantExpr.value}'"
            }
        }

        return when (expr) {
            is SmlBoolean -> {
                if (expr.isTrue) {
                    "True"
                } else {
                    "False"
                }
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
            is SmlFloat -> {
                expr.value.toString()
            }
            is SmlInfixOperation -> when (expr.operator()) {
                Or -> "eager_or(${compileExpression(expr.leftOperand)}, ${compileExpression(expr.rightOperand)})"
                And -> "eager_and(${compileExpression(expr.leftOperand)}, ${compileExpression(expr.rightOperand)})"
                IdenticalTo -> "(${compileExpression(expr.leftOperand)}) is (${compileExpression(expr.rightOperand)})"
                NotIdenticalTo -> "(${compileExpression(expr.leftOperand)}) is not (${compileExpression(expr.rightOperand)})"
                Elvis -> "eager_elvis(${compileExpression(expr.leftOperand)}, ${compileExpression(expr.rightOperand)})"
                else -> "(${compileExpression(expr.leftOperand)}) ${expr.operator} (${compileExpression(expr.rightOperand)})"
            }
            is SmlInt -> {
                expr.value.toString()
            }
            is SmlNull -> {
                "None"
            }
            is SmlMemberAccess -> {
                val receiver = compileExpression(expr.receiver)
                "$receiver.${expr.member.declaration.name}"
            }
            is SmlParenthesizedExpression -> {
                compileExpression(expr.expression)
            }
            is SmlPrefixOperation -> when (expr.operator()) {
                SmlPrefixOperationOperator.Not -> "not (${compileExpression(expr.operand)})"
                SmlPrefixOperationOperator.Minus -> "-(${compileExpression(expr.operand)})"
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
