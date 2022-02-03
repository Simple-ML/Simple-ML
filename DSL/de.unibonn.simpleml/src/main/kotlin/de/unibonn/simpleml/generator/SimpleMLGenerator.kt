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
import de.unibonn.simpleml.emf.argumentsOrEmpty
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
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlIndexedAccess
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
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantBoolean
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantEnumVariant
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantFloat
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantInt
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantNull
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantString
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.stdlibAccess.pythonNameOrNull
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

                val fileName = "${resource.baseGeneratedFilePathOrNull()}_${it.correspondingPythonName()}.py"
                val content = """
                        |from gen_${resource.baseFileNameOrNull()} import ${it.correspondingPythonName()}
                        |
                        |if __name__ == '__main__':
                        |$indent${it.correspondingPythonName()}()
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
            append(stepString)
        }

        // Workflows
        val workflowString = compilationUnit
            .descendants<SmlWorkflow>()
            .sortedBy { it.name }
            .joinToString("\n") {
                compileWorkflow(it)
            }
        if (workflowString.isNotBlank()) {
            if (stepString.isNotBlank()) {
                appendLine()
            }
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

    private fun compileWorkflowSteps(step: SmlStep) = buildString {
        append("def ${step.name}(")
        append(step.parametersOrEmpty().joinToString {
            if (it.isVariadic) {
                "*${it.name}"
            } else {
                it.name
            }
        })
        appendLine("):")

        if (step.statementsOrEmpty().isEmpty()) {
            appendLine("${indent}pass")
        } else {
            step.statementsOrEmpty().forEach {
                appendLine("$indent${compileStatement(it)}")
            }

            if (step.resultsOrEmpty().isNotEmpty()) {
                appendLine("${indent}return ${step.resultsOrEmpty().joinToString { it.name }}")
            }
        }
    }

    private fun compileWorkflow(workflow: SmlWorkflow) = buildString {
        appendLine("def ${workflow.correspondingPythonName()}():")
        if (workflow.statementsOrEmpty().isEmpty()) {
            appendLine("${indent}pass")
        } else {
            workflow.statementsOrEmpty().forEach {
                appendLine(compileStatement(it, shouldSavePlaceholders = true).prependIndent(indent))
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
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

    @OptIn(ExperimentalStdlibApi::class)
    private val compileExpression: DeepRecursiveFunction<SmlAbstractExpression, String> =
        DeepRecursiveFunction { expr ->

            // Template string parts
            when (expr) {
                is SmlTemplateStringStart -> return@DeepRecursiveFunction "${expr.value}{ "
                is SmlTemplateStringInner -> return@DeepRecursiveFunction " }${expr.value}{ "
                is SmlTemplateStringEnd -> return@DeepRecursiveFunction " }${expr.value}"
            }

            // Constant expressions
            val constantExpr = expr.toConstantExpressionOrNull()
            if (constantExpr != null) {
                when (constantExpr) {
                    is SmlConstantBoolean -> return@DeepRecursiveFunction if (constantExpr.value) "True" else "False"
                    is SmlConstantEnumVariant -> { /* let remaining code handle this */ }
                    is SmlConstantFloat -> return@DeepRecursiveFunction constantExpr.value.toString()
                    is SmlConstantInt -> return@DeepRecursiveFunction constantExpr.value.toString()
                    is SmlConstantNull -> return@DeepRecursiveFunction "None"
                    is SmlConstantString -> return@DeepRecursiveFunction "'${constantExpr.value}'"
                }
            }

            // Other
            return@DeepRecursiveFunction when (expr) {
                is SmlBoolean -> {
                    if (expr.isTrue) {
                        "True"
                    } else {
                        "False"
                    }
                }
                is SmlCall -> {
                    val receiver = callRecursive(expr.receiver)
                    val arguments = mutableListOf<String>()
                    for (argument in expr.argumentsOrEmpty()) {
                        arguments += if (argument.isNamed()) {
                            "${argument.parameter?.name}=${callRecursive(argument.value)}"
                        } else {
                            callRecursive(argument.value)
                        }
                    }
                    "$receiver(${arguments.joinToString()})"
                }
                is SmlFloat -> {
                    expr.value.toString()
                }
                is SmlInfixOperation -> when (expr.operator()) {
                    Or -> "eager_or(${callRecursive(expr.leftOperand)}, ${callRecursive(expr.rightOperand)})"
                    And -> "eager_and(${callRecursive(expr.leftOperand)}, ${callRecursive(expr.rightOperand)})"
                    IdenticalTo -> "(${callRecursive(expr.leftOperand)}) is (${callRecursive(expr.rightOperand)})"
                    NotIdenticalTo -> "(${callRecursive(expr.leftOperand)}) is not (${callRecursive(expr.rightOperand)})"
                    Elvis -> "eager_elvis(${callRecursive(expr.leftOperand)}, ${callRecursive(expr.rightOperand)})"
                    else -> "(${callRecursive(expr.leftOperand)}) ${expr.operator} (${callRecursive(expr.rightOperand)})"
                }
                is SmlIndexedAccess -> {
                    val receiver = callRecursive(expr.receiver)
                    val index = callRecursive(expr.index)
                    "$receiver[$index]"
                }
                is SmlInt -> {
                    expr.value.toString()
                }
                is SmlNull -> {
                    "None"
                }
                is SmlMemberAccess -> {
                    val receiver = callRecursive(expr.receiver)
                    "$receiver.${expr.member.declaration.name}"
                }
                is SmlParenthesizedExpression -> {
                    callRecursive(expr.expression)
                }
                is SmlPrefixOperation -> when (expr.operator()) {
                    SmlPrefixOperationOperator.Not -> "not (${callRecursive(expr.operand)})"
                    SmlPrefixOperationOperator.Minus -> "-(${callRecursive(expr.operand)})"
                }
                is SmlString -> {
                    "'${expr.value}'"
                }
                is SmlReference -> {
                    expr.declaration.name
                }
                is SmlTemplateString -> {
                    val substrings = mutableListOf<String>()
                    for (expression in expr.expressions) {
                        substrings += callRecursive(expression)
                    }
                    "f'${substrings.joinToString("")}'"
                }
                else -> {
                    println("Unknown expression $expr.")
                    ""
                }
            }
        }
}

/**
 * Returns the name of the Python declaration that corresponds to this [SmlAbstractDeclaration].
 */
private fun SmlAbstractDeclaration.correspondingPythonName(): String {
    return pythonNameOrNull() ?: name
}
