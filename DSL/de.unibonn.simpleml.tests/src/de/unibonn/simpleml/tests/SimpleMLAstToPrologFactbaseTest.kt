package de.unibonn.simpleml.tests

import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.prolog_bridge.Main
import de.unibonn.simpleml.prolog_bridge.model.facts.*
import de.unibonn.simpleml.tests.assertions.findUniqueFactOrFail
import de.unibonn.simpleml.tests.assertions.shouldBeCloseTo
import de.unibonn.simpleml.util.getResourcePath
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SimpleMLAstToPrologFactbaseTest {

    private val main = SimpleMLStandaloneSetup()
            .createInjectorAndDoEMFRegistration()
            .getInstance(Main::class.java)

    private val testRoot = javaClass.classLoader.getResourcePath("prologVisitorTests").toString()


    // *****************************************************************************************************************
    // Definitions
    // ****************************************************************************************************************/

    @Nested
    inner class Definitions {
        @Test
        fun `should handle empty compilation units`() = withFactbaseFromFile("empty.simpleml") {
            val compilationUnit = findUniqueFactOrFail<CompilationUnitT>()
            compilationUnit.asClue {
                it.imports.shouldBeEmpty()
                it.members.shouldBeEmpty()
            }
        }

        @Test
        fun `should create fileS facts for compilation units`() = withFactbaseFromFile("empty.simpleml") {
            val compilationUnit = findUniqueFactOrFail<CompilationUnitT>()
            val file = findUniqueFactOrFail<FileS>()

            file.asClue {
                it.target.shouldBe(compilationUnit.id)
            }
        }

//        @Test
//        fun `should handle compilation units with definitions`() = withFactbaseFromFile("definitions.simpleml") {
//            val compilationUnit = findUniqueFactOrFail<CompilationUnitT>()
//            compilationUnit.asClue {
//                it.`package`.shouldNotBeNull()
//                it.workflow.shouldNotBeNull()
//                it.processes shouldHaveSize 2
//            }
//
//            findUniqueFactOrFail<FileT> { it.id == compilationUnit.file }
//            shouldBeChildOf(compilationUnit.`package`!!, compilationUnit)
//            shouldBeChildOf(compilationUnit.workflow!!, compilationUnit)
//            compilationUnit.processes.forEach { shouldBeChildOf(it, compilationUnit) }
//        }

//        @Test
//        fun `should handle imports`() = withFactbaseFromFile("definitions.simpleml") {
//            val import = findUniqueFactOrFail<ImportT>()
//            import shouldBe import.copy(imported = "de.unibonn.imported_package.*")
//            findUniqueFactOrFail<CompilationUnitT> { it.id == import.parent }
//        }

//        @Test
//        fun `should handle package`() = withFactbaseFromFile("definitions.simpleml") {
//            val `package` = findUniqueFactOrFail<PackageT>()
//            `package` shouldBe `package`.copy(name = "de.unibonn.test_package")
//            findUniqueFactOrFail<CompilationUnitT> { it.id == `package`.parent }
//        }

//        @Test
//        fun `should handle workflows`() = withFactbaseFromFile("definitions.simpleml") {
//            val workflow = findUniqueFactOrFail<WorkflowT>()
//            workflow shouldBe workflow.copy(name = "test")
//
//            workflow.statements?.forEach { shouldBeChildOf(it, workflow) }
//        }

//        @Test
//        fun `should handle native processes`() = withFactbaseFromFile("definitions.simpleml") {
//            val process = findUniqueFactOrFail<FunctionT> { it.name == "nativeProcess" }
//            process.asClue { it.statements?.shouldBeEmpty() }
//        }
//
//        @Test
//        fun `should handle internal processes`() = withFactbaseFromFile("definitions.simpleml") {
//            val process = findUniqueFactOrFail<FunctionT> { it.name == "internalProcess" }
//            process.asClue {
//                it.parameters shouldHaveSize 2
//                it.results.shouldNotBeNull()
//                it.results!! shouldHaveSize 1
//                it.statements.shouldNotBeNull()
//                it.statements!! shouldHaveSize 1
//            }
//
//            process.parameters.forEach { shouldBeChildOf(it, process) }
//            process.results?.forEach { shouldBeChildOf(it, process) }
//            process.statements?.forEach { shouldBeChildOf(it, process) }
//        }

//        @Test
//        fun `should handle modifiers`() = withFactbaseFromFile("definitions.simpleml") {
//            val modifier = findUniqueFactOrFail<ModifierT>()
//            modifier.asClue { it.modifier shouldBe "public" }
//            findUniqueFactOrFail<FunctionT> { it.id == modifier.parent }
//        }

//        @Test
//        fun `should handle required parameters`() = withFactbaseFromFile("definitions.simpleml") {
//            val parameter = findUniqueFactOrFail<ParameterT> { it.name == "required" }
//            parameter.asClue {
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//                it.defaultValue.shouldBeNull()
//            }
//        }

//        @Test
//        fun `should handle optional parameters`() = withFactbaseFromFile("definitions.simpleml") {
//            val parameter = findUniqueFactOrFail<ParameterT> { it.name == "optional" }
//            parameter.asClue {
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//                it.defaultValue.shouldNotBeNull()
//            }
//
//            shouldBeChildExpressionOf(parameter.defaultValue!!, parameter)
//        }

//        @Test
//        fun `should handle results`() = withFactbaseFromFile("definitions.simpleml") {
//            val result = findUniqueFactOrFail<ResultT>()
//            result.asClue {
//                it.name shouldBe "result"
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//            }
//        }

//        @Test
//        fun `should handle placeholders`() = withFactbaseFromFile("definitions.simpleml") {
//            findUniqueFactOrFail<PlaceholderT> { it.name == "placeholder" }
//        }
    }


    // *****************************************************************************************************************
    // Statements
    // ****************************************************************************************************************/

//    @Nested
//    inner class Statements {
//
//        @Test
//        fun `should handle assignments`() = withFactbaseFromFile("statements.simpleml") {
//            val assignment = findUniqueFactOrFail<AssignmentT>()
//
//            assignment.declarations.forEach { shouldBeChildOf(it, assignment) }
//            shouldBeChildExpressionOf(assignment.value, assignment)
//        }
//
//        @Test
//        fun `should handle expression statements`() = withFactbaseFromFile("statements.simpleml") {
//            val expressionStatement = findUniqueFactOrFail<ExpressionStatementT>()
//            shouldBeChildExpressionOf(expressionStatement.expression, expressionStatement)
//        }
//    }


    // *****************************************************************************************************************
    // Expressions
    // ****************************************************************************************************************/

//    @Nested
//    inner class ComplexExpressions {
//
//        @Test
//        fun `should handle arguments`() = withFactbaseFromFile("expressions.simpleml") {
//            val argument = findUniqueFactOrFail<ArgumentT>()
//            argument.asClue {
//                it.parameter.shouldNotBeNull()
//                it.value.shouldNotBeNull()
//            }
//
//            val parameter = findUniqueFactOrFail<ParameterT> {it.id == argument.parameter}
//            parameter.asClue {
//                it.name shouldBe "argument"
//            }
//
//            shouldBeChildExpressionOf(argument.value, argument)
//        }
//
//        @Test
//        fun `should handle attribute accesses`() = withFactbaseFromFile("expressions.simpleml") {
//            val attributeAccess = findUniqueFactOrFail<AttributeAccessT>()
//            attributeAccess.asClue {
//                it.receiver.shouldNotBeNull()
////                it.attribute shouldBe "attributeAccess" // TODO
//            }
//
//            shouldBeChildExpressionOf(attributeAccess.receiver, attributeAccess)
//        }
//
//        @Test
//        fun `should handle indexed accesses`() = withFactbaseFromFile("expressions.simpleml") {
//            val indexedAccess = findUniqueFactOrFail<IndexedAccessT>()
//            indexedAccess.asClue {
//                it.receiver.shouldNotBeNull()
//                it.index.shouldNotBeNull()
//            }
//
//            shouldBeChildExpressionOf(indexedAccess.receiver, indexedAccess)
//            shouldBeChildExpressionOf(indexedAccess.index, indexedAccess)
//        }
//
//        @Test
//        fun `should handle infix operations`() = withFactbaseFromFile("expressions.simpleml") {
//            val infixOperation = findUniqueFactOrFail<InfixOperationT>()
//            infixOperation.asClue { it.operator shouldBe "+" }
//
//            shouldBeChildExpressionOf(infixOperation.leftOperand, infixOperation)
//            shouldBeChildExpressionOf(infixOperation.rightOperand, infixOperation)
//        }
//
//        @Test
//        fun `should handle prefix operations`() = withFactbaseFromFile("expressions.simpleml") {
//            val prefixOperation = findUniqueFactOrFail<PrefixOperationT>()
//            prefixOperation.asClue { it.operator shouldBe "-" }
//
//            shouldBeChildExpressionOf(prefixOperation.operand, prefixOperation)
//        }
//
//        @Test
//        fun `should handle process calls without receivers`() = withFactbaseFromFile("expressions.simpleml") {
//            val call = findUniqueFactOrFail<CallT> { it.receiver == null }
//            call.asClue {
//                it.callable?.resolve<FunctionT>()?.name shouldBe "callWithoutReceiver"
//                it.arguments.shouldNotBeNull()
//                it.arguments!! shouldHaveSize 1
//            }
//
//            call.arguments!!.forEach { shouldBeChildExpressionOf(it, call) }
//        }
//
//        @Test
//        fun `should handle process calls with receivers`() = withFactbaseFromFile("expressions.simpleml") {
//            val call = findUniqueFactOrFail<CallT> { it.referenced == "callWithReceiver" }
//            call.asClue {
//                it.receiver.shouldNotBeNull()
//                it.arguments.shouldBeEmpty()
//            }
//
//            shouldBeChildExpressionOf(call.receiver!!, call)
//            call.arguments.forEach { shouldBeChildExpressionOf(it, call) }
//        }
//
//        @Test
//        fun `should handle references`() = withFactbaseFromFile("expressions.simpleml") {
//            val referencedPlaceholder = findUniqueFactOrFail<PlaceholderT> { it.name == "reference" }
//            findUniqueFactOrFail<LocalReferenceT> { it.symbol == referencedPlaceholder.id }
//        }
//    }


    // Literals --------------------------------------------------------------------------------------------------------

    @Nested
    inner class Literals {
//
//        @Test
//        fun `should handle boolean literals`() = withFactbaseFromFile("literals.simpleml") {
//            findUniqueFactOrFail<BooleanT> { it.value }
//            findUniqueFactOrFail<BooleanT> { !it.value }
//        }
//
//        @Test
//        fun `should handle float literals`() = withFactbaseFromFile("literals.simpleml") {
//            val literal = findUniqueFactOrFail<FloatT>()
//            literal.asClue { it.value shouldBeCloseTo 1.1 }
//        }
//
//        @Test
//        fun `should handle int literals`() = withFactbaseFromFile("literals.simpleml") {
//            findUniqueFactOrFail<IntT> { it.value == 42 }
//        }
//
//        @Test
//        fun `should handle string literals`() = withFactbaseFromFile("literals.simpleml") {
//            findUniqueFactOrFail<StringT> { it.value == "hello" }
//        }
    }

    private fun withFactbaseFromFile(file: String, lambda: PlFactbase.() -> Unit) {
        main.createFactbase("$testRoot/$file").apply(lambda)
    }
}
