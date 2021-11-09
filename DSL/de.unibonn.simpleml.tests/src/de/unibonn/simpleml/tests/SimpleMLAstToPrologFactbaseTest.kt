package de.unibonn.simpleml.tests

import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.assertions.findUniqueFactOrFail
import de.unibonn.simpleml.assertions.shouldBeNChildrenOf
import de.unibonn.simpleml.assertions.shouldHaveNAnnotationUses
import de.unibonn.simpleml.assertions.shouldHaveNModifiers
import de.unibonn.simpleml.prolog_bridge.Main
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.ClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.FileS
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.InterfaceT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.util.getResourcePath
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * ## Testing procedure for a fact:
 * - Checks its own primitive arguments
 * - Checks size of lists of children
 * - Ensure IDs of children are correct
 */
class SimpleMLAstToPrologFactbaseTest {

    private val main = SimpleMLStandaloneSetup()
        .createInjectorAndDoEMFRegistration()
        .getInstance(Main::class.java)

    private val testRoot = javaClass.classLoader.getResourcePath("prologVisitorTests").toString()


    // *****************************************************************************************************************
    // Definitions
    // ****************************************************************************************************************/

    @Nested
    inner class Declarations {

        @Nested
        inner class CompilationUnit {

            @Test
            fun `should handle empty files`() = withFactbaseFromFile("empty.simpleml") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                compilationUnitT.asClue {
                    it.`package`.shouldBeNull()
                    it.imports.shouldBeEmpty()
                    it.members.shouldBeEmpty()
                }
            }

            @Test
            fun `should store package`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                compilationUnitT shouldBe compilationUnitT.copy(`package` = "myPackage")
            }

            @Test
            fun `should reference imports`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                shouldBeNChildrenOf(compilationUnitT.imports, compilationUnitT, 3)
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                shouldBeNChildrenOf(compilationUnitT.members, compilationUnitT, 6)
            }

            @Test
            fun `should store filepath in separate relation`() = withFactbaseFromFile("empty.simpleml") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                val fileT = findUniqueFactOrFail<FileS>()

                fileT.asClue {
                    fileT.target shouldBe compilationUnitT.id
                    fileT.path shouldEndWith "prologVisitorTests/empty.simpleml"
                }
            }
        }

        @Nested
        inner class Import {

            @Test
            fun `should handle normal imports`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                importT.asClue {
                    importT.alias.shouldBeNull()
                }
            }

            @Test
            fun `should handle imports with alias`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyOtherClass" }
                importT.asClue {
                    importT.alias shouldBe "Class"
                }
            }

            @Test
            fun `should handle imports with wildcard`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.*" }
                importT.asClue {
                    importT.alias.shouldBeNull()
                }
            }
        }

        @Nested
        inner class Annotation {
            @Test
            fun `should handle simple annotations`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MySimpleAnnotation" }
                annotationT.asClue {
                    annotationT.parameters.shouldBeNull()
                }

                shouldHaveNAnnotationUses(annotationT, 0)
                shouldHaveNModifiers(annotationT, 0)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MyComplexAnnotation" }
                shouldBeNChildrenOf(annotationT.parameters, annotationT, 2)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MyComplexAnnotation" }
                shouldHaveNAnnotationUses(annotationT, 1)
            }

            @Test
            fun `should store modifiers`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MyComplexAnnotation" }
                shouldHaveNModifiers(annotationT, 1)
            }
        }

        @Nested
        inner class Class {
            @Test
            fun `should handle simple classes`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MySimpleClass" }
                classT.asClue {
                    classT.typeParameters.shouldBeNull()
                    classT.parameters.shouldBeNull()
                    classT.parentTypes.shouldBeNull()
                    classT.typeParameterConstraints.shouldBeNull()
                    classT.members.shouldBeNull()
                }

                shouldHaveNAnnotationUses(classT, 0)
                shouldHaveNModifiers(classT, 0)
            }

            @Test
            fun `should reference type parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf(classT.typeParameters, classT, 2)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf(classT.parameters, classT, 2)
            }

            @Test
            fun `should reference parent types`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf(classT.parentTypes, classT, 2)
            }

            @Test
            fun `should reference type parameter constraints`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf(classT.typeParameterConstraints, classT, 2)
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf(classT.members, classT, 5)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldHaveNAnnotationUses(classT, 1)
            }

            @Test
            fun `should store modifiers`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldHaveNModifiers(classT, 1)
            }
        }

        @Nested
        inner class Interface {
            @Test
            fun `should handle simple interfaces`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MySimpleInterface" }
                interfaceT.asClue {
                    interfaceT.typeParameters.shouldBeNull()
                    interfaceT.parameters.shouldBeNull()
                    interfaceT.parentTypes.shouldBeNull()
                    interfaceT.typeParameterConstraints.shouldBeNull()
                    interfaceT.members.shouldBeNull()
                }

                shouldHaveNAnnotationUses(interfaceT, 0)
                shouldHaveNModifiers(interfaceT, 0)
            }

            @Test
            fun `should reference type parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldBeNChildrenOf(interfaceT.typeParameters, interfaceT, 2)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldBeNChildrenOf(interfaceT.parameters, interfaceT, 2)
            }

            @Test
            fun `should reference parent types`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldBeNChildrenOf(interfaceT.parentTypes, interfaceT, 2)
            }

            @Test
            fun `should reference type parameter constraints`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldBeNChildrenOf(interfaceT.typeParameterConstraints, interfaceT, 2)
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldBeNChildrenOf(interfaceT.members, interfaceT, 5)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldHaveNAnnotationUses(interfaceT, 1)
            }

            @Test
            fun `should store modifiers`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
                val interfaceT = findUniqueFactOrFail<InterfaceT> { it.name == "MyComplexInterface" }
                shouldHaveNModifiers(interfaceT, 1)
            }
        }

//        @Test
//        fun `should handle workflows`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val workflow = findUniqueFactOrFail<WorkflowT>()
//            workflow shouldBe workflow.copy(name = "test")
//
//            workflow.statements?.forEach { shouldBeChildOf(it, workflow) }
//        }

//        @Test
//        fun `should handle native processes`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val process = findUniqueFactOrFail<FunctionT> { it.name == "nativeProcess" }
//            process.asClue { it.statements?.shouldBeEmpty() }
//        }
//
//        @Test
//        fun `should handle internal processes`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
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
//        fun `should handle modifiers`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val modifier = findUniqueFactOrFail<ModifierT>()
//            modifier.asClue { it.modifier shouldBe "public" }
//            findUniqueFactOrFail<FunctionT> { it.id == modifier.parent }
//        }

//        @Test
//        fun `should handle required parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val parameter = findUniqueFactOrFail<ParameterT> { it.name == "required" }
//            parameter.asClue {
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//                it.defaultValue.shouldBeNull()
//            }
//        }

//        @Test
//        fun `should handle optional parameters`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val parameter = findUniqueFactOrFail<ParameterT> { it.name == "optional" }
//            parameter.asClue {
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//                it.defaultValue.shouldNotBeNull()
//            }
//
//            shouldBeChildExpressionOf(parameter.defaultValue!!, parameter)
//        }

//        @Test
//        fun `should handle results`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
//            val result = findUniqueFactOrFail<ResultT>()
//            result.asClue {
//                it.name shouldBe "result"
////                it.type?.resolve<ClassT>()?.name shouldBe "Int"
//            }
//        }

//        @Test
//        fun `should handle placeholders`() = withFactbaseFromFile("compilationUnitMembers.simpleml") {
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
