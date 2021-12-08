package de.unibonn.simpleml.test

import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.constants.FileExtensions
import de.unibonn.simpleml.prolog_bridge.Main
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationUseT
import de.unibonn.simpleml.prolog_bridge.model.facts.ArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AssignmentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AttributeT
import de.unibonn.simpleml.prolog_bridge.model.facts.BooleanT
import de.unibonn.simpleml.prolog_bridge.model.facts.CallT
import de.unibonn.simpleml.prolog_bridge.model.facts.CallableTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.ClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.DeclarationT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumVariantT
import de.unibonn.simpleml.prolog_bridge.model.facts.ExpressionStatementT
import de.unibonn.simpleml.prolog_bridge.model.facts.ExpressionT
import de.unibonn.simpleml.prolog_bridge.model.facts.FloatT
import de.unibonn.simpleml.prolog_bridge.model.facts.FunctionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.InfixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.IntT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaYieldT
import de.unibonn.simpleml.prolog_bridge.model.facts.MemberAccessT
import de.unibonn.simpleml.prolog_bridge.model.facts.MemberTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.NamedTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.NodeWithParent
import de.unibonn.simpleml.prolog_bridge.model.facts.NullT
import de.unibonn.simpleml.prolog_bridge.model.facts.PackageT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParenthesizedExpressionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParenthesizedTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.model.facts.PlaceholderT
import de.unibonn.simpleml.prolog_bridge.model.facts.PrefixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.ReferenceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ResourceS
import de.unibonn.simpleml.prolog_bridge.model.facts.ResultT
import de.unibonn.simpleml.prolog_bridge.model.facts.SourceLocationS
import de.unibonn.simpleml.prolog_bridge.model.facts.StarProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.StatementT
import de.unibonn.simpleml.prolog_bridge.model.facts.StringT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringPartT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeParameterConstraintT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.UnionTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.UnresolvedT
import de.unibonn.simpleml.prolog_bridge.model.facts.WildcardT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowStepT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowT
import de.unibonn.simpleml.prolog_bridge.model.facts.YieldT
import de.unibonn.simpleml.test.assertions.findUniqueFactOrFail
import de.unibonn.simpleml.test.assertions.shouldBeChildExpressionOf
import de.unibonn.simpleml.test.assertions.shouldBeChildOf
import de.unibonn.simpleml.test.assertions.shouldBeCloseTo
import de.unibonn.simpleml.test.assertions.shouldBeNChildExpressionsOf
import de.unibonn.simpleml.test.assertions.shouldBeNChildrenOf
import de.unibonn.simpleml.test.assertions.shouldHaveNAnnotationUses
import de.unibonn.simpleml.test.util.getResourcePath
import io.kotest.assertions.asClue
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * ## Testing procedure for a fact:
 * - Check its own primitive arguments
 * - Check size of lists of children
 * - Ensure IDs of children are correct
 */
class AstToPrologFactbaseTest {

    private val main = SimpleMLStandaloneSetup()
        .createInjectorAndDoEMFRegistration()
        .getInstance(Main::class.java)

    private val testRoot = javaClass.classLoader.getResourcePath("astToPrologFactbase").toString()

    // *****************************************************************************************************************
    // Definitions
    // ****************************************************************************************************************/

    @Nested
    inner class Declarations {

        @Nested
        inner class CompilationUnit {
            @Test
            fun `should handle empty compilation units`() = withFactbaseFromFile("empty") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                compilationUnitT.asClue {
                    it.members.shouldBeEmpty()
                }
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("declarations") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                shouldBeNChildrenOf<DeclarationT>(compilationUnitT.members, compilationUnitT, 1)
            }

            @Test
            fun `should store resource URI in separate relation`() = withFactbaseFromFile("empty") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                val resourceS = findUniqueFactOrFail<ResourceS>()
                resourceS.asClue {
                    resourceS.target shouldBe compilationUnitT.id
                    resourceS.uri shouldEndWith "astToPrologFactbase/empty${FileExtensions.TEST}"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("empty") {
                val compilationUnitT = findUniqueFactOrFail<CompilationUnitT>()
                findUniqueFactOrFail<SourceLocationS> { it.target == compilationUnitT.id }
            }
        }

        @Nested
        inner class Annotation {
            @Test
            fun `should handle simple annotations`() = withFactbaseFromFile("declarations") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MySimpleAnnotation" }
                annotationT.asClue {
                    annotationT.parameters.shouldBeNull()
                }

                shouldHaveNAnnotationUses(annotationT, 0)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("declarations") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MyComplexAnnotation" }
                shouldBeNChildrenOf<ParameterT>(annotationT.parameters, annotationT, 2)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MyComplexAnnotation" }
                shouldHaveNAnnotationUses(annotationT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.name == "MySimpleAnnotation" }
                findUniqueFactOrFail<SourceLocationS> { it.target == annotationT.id }
            }
        }

        @Nested
        inner class Attribute {
            @Test
            fun `should handle simple attributes`() = withFactbaseFromFile("declarations") {
                val attributeT = findUniqueFactOrFail<AttributeT> { it.name == "mySimpleAttribute" }
                attributeT.asClue {
                    attributeT.type.shouldBeNull()
                }

                shouldHaveNAnnotationUses(attributeT, 0)
            }

            @Test
            fun `should store isStatic`() = withFactbaseFromFile("declarations") {
                val attributeT = findUniqueFactOrFail<AttributeT> { it.name == "myComplexAttribute" }
                attributeT.asClue {
                    attributeT.isStatic shouldBe true
                }
            }

            @Test
            fun `should reference type`() = withFactbaseFromFile("declarations") {
                val attributeT = findUniqueFactOrFail<AttributeT> { it.name == "myComplexAttribute" }
                shouldBeChildOf<TypeT>(attributeT.type, attributeT)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val attributeT = findUniqueFactOrFail<AttributeT> { it.name == "myComplexAttribute" }
                shouldHaveNAnnotationUses(attributeT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val attributeT = findUniqueFactOrFail<AttributeT> { it.name == "mySimpleAttribute" }
                findUniqueFactOrFail<SourceLocationS> { it.target == attributeT.id }
            }
        }

        @Nested
        inner class Class {
            @Test
            fun `should handle simple classes`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MySimpleClass" }
                classT.asClue {
                    classT.typeParameters.shouldBeNull()
                    classT.parameters.shouldBeNull()
                    classT.parentTypes.shouldBeNull()
                    classT.typeParameterConstraints.shouldBeNull()
                    classT.members.shouldBeNull()
                }

                shouldHaveNAnnotationUses(classT, 0)
            }

            @Test
            fun `should reference type parameters`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf<TypeParameterT>(classT.typeParameters, classT, 2)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf<ParameterT>(classT.parameters, classT, 2)
            }

            @Test
            fun `should reference parent types`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf<TypeT>(classT.parentTypes, classT, 2)
            }

            @Test
            fun `should reference type parameter constraints`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf<TypeParameterConstraintT>(classT.typeParameterConstraints, classT, 2)
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldBeNChildrenOf<DeclarationT>(classT.members, classT, 5)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyComplexClass" }
                shouldHaveNAnnotationUses(classT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MySimpleClass" }
                findUniqueFactOrFail<SourceLocationS> { it.target == classT.id }
            }
        }

        @Nested
        inner class Enum {
            @Test
            fun `should handle simple enums`() = withFactbaseFromFile("declarations") {
                val enumT = findUniqueFactOrFail<EnumT> { it.name == "MySimpleEnum" }
                enumT.asClue {
                    enumT.variants.shouldBeNull()
                }

                shouldHaveNAnnotationUses(enumT, 0)
            }

            @Test
            fun `should reference instances`() = withFactbaseFromFile("declarations") {
                val enumT = findUniqueFactOrFail<EnumT> { it.name == "MyComplexEnum" }
                shouldBeNChildrenOf<EnumVariantT>(enumT.variants, enumT, 2)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val enumT = findUniqueFactOrFail<EnumT> { it.name == "MyComplexEnum" }
                shouldHaveNAnnotationUses(enumT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val enumT = findUniqueFactOrFail<EnumT> { it.name == "MySimpleEnum" }
                findUniqueFactOrFail<SourceLocationS> { it.target == enumT.id }
            }
        }

        @Nested
        inner class EnumInstance {
            @Test
            fun `should handle simple enum instances`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MySimpleVariant" }
                shouldHaveNAnnotationUses(enumVariantT, 0)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MyComplexVariant" }
                shouldHaveNAnnotationUses(enumVariantT, 1)
            }

            @Test
            fun `should reference type parameters`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MyComplexVariant" }
                shouldBeNChildrenOf<TypeParameterT>(enumVariantT.typeParameters, enumVariantT, 2)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MyComplexVariant" }
                shouldBeNChildrenOf<ParameterT>(enumVariantT.parameters, enumVariantT, 2)
            }

            @Test
            fun `should reference type parameter constraints`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MyComplexVariant" }
                shouldBeNChildrenOf<TypeParameterConstraintT>(enumVariantT.typeParameterConstraints, enumVariantT, 2)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val enumVariantT = findUniqueFactOrFail<EnumVariantT> { it.name == "MySimpleVariant" }
                findUniqueFactOrFail<SourceLocationS> { it.target == enumVariantT.id }
            }
        }

        @Nested
        inner class Function {
            @Test
            fun `should handle simple functions`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "mySimpleFunction" }
                functionT.asClue {
                    functionT.typeParameters.shouldBeNull()
                    functionT.parameters.shouldBeEmpty()
                    functionT.results.shouldBeNull()
                    functionT.typeParameterConstraints.shouldBeNull()
                }

                shouldHaveNAnnotationUses(functionT, 0)
            }

            @Test
            fun `should store isStatic`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                functionT.asClue {
                    functionT.isStatic shouldBe true
                }
            }

            @Test
            fun `should reference type parameters`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                shouldBeNChildrenOf<TypeParameterT>(functionT.typeParameters, functionT, 2)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                shouldBeNChildrenOf<ParameterT>(functionT.parameters, functionT, 2)
            }

            @Test
            fun `should reference results`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                shouldBeNChildrenOf<ResultT>(functionT.results, functionT, 2)
            }

            @Test
            fun `should reference type parameter constraints`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                shouldBeNChildrenOf<TypeParameterConstraintT>(functionT.typeParameterConstraints, functionT, 2)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "myComplexFunction" }
                shouldHaveNAnnotationUses(functionT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val functionT = findUniqueFactOrFail<FunctionT> { it.name == "mySimpleFunction" }
                findUniqueFactOrFail<SourceLocationS> { it.target == functionT.id }
            }
        }

        @Nested
        inner class Package {

            @Test
            fun `should store package`() = withFactbaseFromFile("declarations") {
                val packageT = findUniqueFactOrFail<PackageT>()
                packageT.asClue {
                    packageT.name shouldBe "myPackage"
                }
            }

            @Test
            fun `should reference imports`() = withFactbaseFromFile("declarations") {
                val packageT = findUniqueFactOrFail<PackageT>()
                shouldBeNChildrenOf<ImportT>(packageT.imports, packageT, 3)
            }

            @Test
            fun `should reference members`() = withFactbaseFromFile("declarations") {
                val packageT = findUniqueFactOrFail<PackageT>()
                shouldBeNChildrenOf<DeclarationT>(packageT.members, packageT, 12)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val packageT = findUniqueFactOrFail<PackageT>()
                findUniqueFactOrFail<SourceLocationS> { it.target == packageT.id }
            }
        }

        @Nested
        inner class Import {
            @Test
            fun `should handle normal imports`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                importT.asClue {
                    importT.alias.shouldBeNull()
                }
            }

            @Test
            fun `should handle imports with alias`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyOtherClass" }
                importT.asClue {
                    importT.alias shouldBe "Class"
                }
            }

            @Test
            fun `should handle imports with wildcard`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.*" }
                importT.asClue {
                    importT.alias.shouldBeNull()
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
            }
        }

        @Nested
        inner class Parameter {
            @Test
            fun `should handle simple parameters`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "mySimpleParameter" }
                parameterT.asClue {
                    parameterT.isVariadic shouldBe false
                    parameterT.type.shouldBeNull()
                    parameterT.defaultValue.shouldBeNull()
                }

                shouldHaveNAnnotationUses(parameterT, 0)
            }

            @Test
            fun `should store isVariadic`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "myComplexParameter" }
                parameterT.asClue {
                    parameterT.isVariadic shouldBe true
                }
            }

            @Test
            fun `should reference type`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "myComplexParameter" }
                shouldBeChildOf<TypeT>(parameterT.type, parameterT)
            }

            @Test
            fun `should reference default value`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "myComplexParameter" }
                shouldBeChildExpressionOf<ExpressionT>(parameterT.defaultValue, parameterT)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "myComplexParameter" }
                shouldHaveNAnnotationUses(parameterT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val parameterT = findUniqueFactOrFail<ParameterT> { it.name == "mySimpleParameter" }
                findUniqueFactOrFail<SourceLocationS> { it.target == parameterT.id }
            }
        }

        @Nested
        inner class Result {
            @Test
            fun `should handle simple parameters`() = withFactbaseFromFile("declarations") {
                val resultT = findUniqueFactOrFail<ResultT> { it.name == "mySimpleResult" }
                resultT.asClue {
                    resultT.type.shouldBeNull()
                }

                shouldHaveNAnnotationUses(resultT, 0)
            }

            @Test
            fun `should reference type`() = withFactbaseFromFile("declarations") {
                val resultT = findUniqueFactOrFail<ResultT> { it.name == "myComplexResult" }
                shouldBeChildOf<TypeT>(resultT.type, resultT)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val resultT = findUniqueFactOrFail<ResultT> { it.name == "myComplexResult" }
                shouldHaveNAnnotationUses(resultT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val resultT = findUniqueFactOrFail<ResultT> { it.name == "mySimpleResult" }
                findUniqueFactOrFail<SourceLocationS> { it.target == resultT.id }
            }
        }

        @Nested
        inner class TypeParameter {
            @Test
            fun `should handle simple parameters`() = withFactbaseFromFile("declarations") {
                val typeParameterT = findUniqueFactOrFail<TypeParameterT> { it.name == "MY_SIMPLE_TYPE_PARAMETER" }
                typeParameterT.asClue {
                    typeParameterT.variance.shouldBeNull()
                }

                shouldHaveNAnnotationUses(typeParameterT, 0)
            }

            @Test
            fun `should store variance`() = withFactbaseFromFile("declarations") {
                val typeParameterT = findUniqueFactOrFail<TypeParameterT> { it.name == "MY_COMPLEX_TYPE_PARAMETER" }
                typeParameterT.asClue {
                    typeParameterT.variance shouldBe "out"
                }
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val typeParameterT = findUniqueFactOrFail<TypeParameterT> { it.name == "MY_COMPLEX_TYPE_PARAMETER" }
                shouldHaveNAnnotationUses(typeParameterT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val typeParameterT = findUniqueFactOrFail<TypeParameterT> { it.name == "MY_SIMPLE_TYPE_PARAMETER" }
                findUniqueFactOrFail<SourceLocationS> { it.target == typeParameterT.id }
            }
        }

        @Nested
        inner class Workflow {
            @Test
            fun `should handle simple workflows`() = withFactbaseFromFile("declarations") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "mySimpleWorkflow" }
                workflowT.asClue {
                    workflowT.statements.shouldBeEmpty()
                }

                shouldHaveNAnnotationUses(workflowT, 0)
            }

            @Test
            fun `should reference statements`() = withFactbaseFromFile("declarations") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myComplexWorkflow" }
                shouldBeNChildrenOf<StatementT>(workflowT.statements, workflowT, 1)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myComplexWorkflow" }
                shouldHaveNAnnotationUses(workflowT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "mySimpleWorkflow" }
                findUniqueFactOrFail<SourceLocationS> { it.target == workflowT.id }
            }
        }

        @Nested
        inner class WorkflowSteps {
            @Test
            fun `should handle simple workflow steps`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "mySimpleStep" }
                workflowStepT.asClue {
                    workflowStepT.parameters.shouldBeEmpty()
                    workflowStepT.results.shouldBeNull()
                    workflowStepT.statements.shouldBeEmpty()
                }

                shouldHaveNAnnotationUses(workflowStepT, 0)
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myComplexStep" }
                shouldBeNChildrenOf<ParameterT>(workflowStepT.parameters, workflowStepT, 2)
            }

            @Test
            fun `should reference results`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myComplexStep" }
                shouldBeNChildrenOf<ResultT>(workflowStepT.results, workflowStepT, 2)
            }

            @Test
            fun `should reference statements`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myComplexStep" }
                shouldBeNChildrenOf<StatementT>(workflowStepT.statements, workflowStepT, 1)
            }

            @Test
            fun `should store annotation uses`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myComplexStep" }
                shouldHaveNAnnotationUses(workflowStepT, 1)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("declarations") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "mySimpleStep" }
                findUniqueFactOrFail<SourceLocationS> { it.target == workflowStepT.id }
            }
        }
    }

    // *****************************************************************************************************************
    // Statements
    // ****************************************************************************************************************/

    @Nested
    inner class Statements {

        @Nested
        inner class Assignment {
            @Test
            fun `should reference assignees`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                shouldBeNChildrenOf<NodeWithParent>(assignmentT.assignees, assignmentT, 3)
            }

            @Test
            fun `should reference expression`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                shouldBeChildExpressionOf<ExpressionT>(assignmentT.expression, assignmentT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                findUniqueFactOrFail<SourceLocationS> { it.target == assignmentT.id }
            }
        }

        @Nested
        inner class LambdaYield {
            @Test
            fun `should handle lambda yields`() = withFactbaseFromFile("statements") {
                findUniqueFactOrFail<LambdaYieldT> { it.name == "myLambdaYield" }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val lambdaYieldT = findUniqueFactOrFail<LambdaYieldT> { it.name == "myLambdaYield" }
                findUniqueFactOrFail<SourceLocationS> { it.target == lambdaYieldT.id }
            }
        }

        @Nested
        inner class Placeholder {
            @Test
            fun `should handle placeholders`() = withFactbaseFromFile("statements") {
                findUniqueFactOrFail<PlaceholderT> { it.name == "myPlaceholder" }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val placeholderT = findUniqueFactOrFail<PlaceholderT> { it.name == "myPlaceholder" }
                findUniqueFactOrFail<SourceLocationS> { it.target == placeholderT.id }
            }
        }

        @Nested
        inner class Wildcard {
            @Test
            fun `should handle wildcards`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                findUniqueFactOrFail<WildcardT> { it.parent == assignmentT.id }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                val wildcardT = findUniqueFactOrFail<WildcardT> { it.parent == assignmentT.id }
                findUniqueFactOrFail<SourceLocationS> { it.target == wildcardT.id }
            }
        }

        @Nested
        inner class Yield {
            @Test
            fun `should reference result if possible`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                val yieldT = findUniqueFactOrFail<YieldT> { it.parent == assignmentT.id }
                val resultT = findUniqueFactOrFail<ResultT> { it.id == yieldT.result }
                resultT.asClue {
                    resultT.parent shouldBe workflowStepT.id
                    resultT.name shouldBe "a"
                }
            }

            @Test
            fun `should store name for unresolvable results`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myStepWithUnresolvedYield" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                val yieldT = findUniqueFactOrFail<YieldT> { it.parent == assignmentT.id }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == yieldT.result }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "myUnresolvedResult"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myFunctionalStep" }
                val assignmentT = findUniqueFactOrFail<AssignmentT> { it.parent == workflowStepT.id }
                val yieldT = findUniqueFactOrFail<YieldT> { it.parent == assignmentT.id }
                findUniqueFactOrFail<SourceLocationS> { it.target == yieldT.id }
            }
        }

        @Nested
        inner class ExpressionStatement {
            @Test
            fun `should reference expression`() = withFactbaseFromFile("statements") {
                val expressionStatementT = findUniqueFactOrFail<ExpressionStatementT>()
                shouldBeChildExpressionOf<ExpressionT>(expressionStatementT.expression, expressionStatementT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("statements") {
                val expressionStatementT = findUniqueFactOrFail<ExpressionStatementT>()
                findUniqueFactOrFail<SourceLocationS> { it.target == expressionStatementT.id }
            }
        }
    }

    // *****************************************************************************************************************
    // Expressions
    // ****************************************************************************************************************/

    @Nested
    inner class Expressions {

        @Nested
        inner class Argument {
            @Test
            fun `should handle positional arguments`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithPositionalArgument" }
                val argumentT = findUniqueFactOrFail<ArgumentT> { isContainedIn(it, workflowT) }
                argumentT.asClue {
                    argumentT.parameter.shouldBeNull()
                }
            }

            @Test
            fun `should reference parameter if possible`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableNamedArgument" }
                val argumentT = findUniqueFactOrFail<ArgumentT> { isContainedIn(it, workflowT) }
                val parameterT = findUniqueFactOrFail<ParameterT> { it.id == argumentT.parameter }
                parameterT.asClue {
                    parameterT.name shouldBe "a"
                }
            }

            @Test
            fun `should reference value`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableNamedArgument" }
                val argumentT = findUniqueFactOrFail<ArgumentT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(argumentT.value, argumentT)
            }

            @Test
            fun `should store name for unresolvable arguments`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithUnresolvedArgument" }
                val argumentT = findUniqueFactOrFail<ArgumentT> { isContainedIn(it, workflowT) }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == argumentT.parameter }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "myUnresolvedParameter"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithPositionalArgument" }
                val argumentT = findUniqueFactOrFail<ArgumentT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == argumentT.id }
            }
        }

        @Nested
        inner class Boolean {
            @Test
            fun `should store value`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val booleanT = findUniqueFactOrFail<BooleanT> { isContainedIn(it, workflowT) }
                booleanT.asClue {
                    booleanT.value shouldBe true
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val booleanT = findUniqueFactOrFail<BooleanT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == booleanT.id }
            }
        }

        @Nested
        inner class Call {
            @Test
            fun `should handle simple calls`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleCall" }
                val callT = findUniqueFactOrFail<CallT> { isContainedIn(it, workflowT) }
                callT.asClue {
                    callT.typeArguments.shouldBeNull()
                    callT.arguments.shouldBeEmpty()
                }
            }

            @Test
            fun `should reference receiver`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexCall" }
                val callT = findUniqueFactOrFail<CallT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(callT.receiver, callT)
            }

            @Test
            fun `should reference type arguments`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexCall" }
                val callT = findUniqueFactOrFail<CallT> { isContainedIn(it, workflowT) }
                shouldBeNChildrenOf<TypeArgumentT>(callT.typeArguments, callT, 2)
            }

            @Test
            fun `should reference arguments`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexCall" }
                val callT = findUniqueFactOrFail<CallT> { isContainedIn(it, workflowT) }
                shouldBeNChildExpressionsOf<ExpressionT>(callT.arguments, callT, 2)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleCall" }
                val callT = findUniqueFactOrFail<CallT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == callT.id }
            }
        }

        @Nested
        inner class Float {
            @Test
            fun `should store value`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val floatT = findUniqueFactOrFail<FloatT> { isContainedIn(it, workflowT) }
                floatT.asClue {
                    floatT.value.shouldBeCloseTo(1.0)
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val floatT = findUniqueFactOrFail<FloatT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == floatT.id }
            }
        }

        @Nested
        inner class InfixOperation {
            @Test
            fun `should reference left operand`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val infixOperationT = findUniqueFactOrFail<InfixOperationT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(infixOperationT.leftOperand, infixOperationT)
            }

            @Test
            fun `should store operator`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val infixOperationT = findUniqueFactOrFail<InfixOperationT> { isContainedIn(it, workflowT) }
                infixOperationT.asClue {
                    infixOperationT.operator shouldBe "+"
                }
            }

            @Test
            fun `should reference right operand`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val infixOperationT = findUniqueFactOrFail<InfixOperationT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(infixOperationT.rightOperand, infixOperationT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val infixOperationT = findUniqueFactOrFail<InfixOperationT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == infixOperationT.id }
            }
        }

        @Nested
        inner class Int {
            @Test
            fun `should store value`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val intT = findUniqueFactOrFail<IntT> { isContainedIn(it, workflowT) }
                intT.asClue {
                    intT.value shouldBe 42
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val intT = findUniqueFactOrFail<IntT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == intT.id }
            }
        }

        @Nested
        inner class Lambda {
            @Test
            fun `should handle simple lambdas`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleLambda" }
                val lambdaT = findUniqueFactOrFail<LambdaT> { isContainedIn(it, workflowT) }
                lambdaT.asClue {
                    lambdaT.parameters.shouldBeNull()
                    lambdaT.statements.shouldBeEmpty()
                }
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexLambda" }
                val lambdaT = findUniqueFactOrFail<LambdaT> { isContainedIn(it, workflowT) }
                shouldBeNChildrenOf<ParameterT>(lambdaT.parameters, lambdaT, 2)
            }

            @Test
            fun `should reference statements`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexLambda" }
                val lambdaT = findUniqueFactOrFail<LambdaT> { isContainedIn(it, workflowT) }
                shouldBeNChildrenOf<StatementT>(lambdaT.statements, lambdaT, 2)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleLambda" }
                val lambdaT = findUniqueFactOrFail<LambdaT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == lambdaT.id }
            }
        }

        @Nested
        inner class MemberAccess {
            @Test
            fun `should reference receiver`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithMemberAccess" }
                val memberAccessT = findUniqueFactOrFail<MemberAccessT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(memberAccessT.receiver, memberAccessT)
            }

            @Test
            fun `should store null safety`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithMemberAccess" }
                val memberAccessT = findUniqueFactOrFail<MemberAccessT> { isContainedIn(it, workflowT) }
                memberAccessT.asClue {
                    memberAccessT.isNullSafe shouldBe true
                }
            }

            @Test
            fun `should reference member`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithMemberAccess" }
                val memberAccessT = findUniqueFactOrFail<MemberAccessT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ReferenceT>(memberAccessT.member, memberAccessT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithMemberAccess" }
                val memberAccessT = findUniqueFactOrFail<MemberAccessT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == memberAccessT.id }
            }
        }

        @Nested
        inner class Null {
            @Test
            fun `should handle nulls`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                findUniqueFactOrFail<NullT> { isContainedIn(it, workflowT) }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val nullT = findUniqueFactOrFail<NullT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == nullT.id }
            }
        }

        @Nested
        inner class ParenthesizedExpression {
            @Test
            fun `should reference expression`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithParenthesizedExpression" }
                val parenthesizedExpressionT =
                    findUniqueFactOrFail<ParenthesizedExpressionT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(parenthesizedExpressionT.expression, parenthesizedExpressionT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithParenthesizedExpression" }
                val parenthesizedExpressionT =
                    findUniqueFactOrFail<ParenthesizedExpressionT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == parenthesizedExpressionT.id }
            }
        }

        @Nested
        inner class PrefixOperation {
            @Test
            fun `should store operator`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val prefixOperationT = findUniqueFactOrFail<PrefixOperationT> { isContainedIn(it, workflowT) }
                prefixOperationT.asClue {
                    prefixOperationT.operator shouldBe "-"
                }
            }

            @Test
            fun `should reference operand`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val prefixOperationT = findUniqueFactOrFail<PrefixOperationT> { isContainedIn(it, workflowT) }
                shouldBeChildExpressionOf<ExpressionT>(prefixOperationT.operand, prefixOperationT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithOperations" }
                val prefixOperationT = findUniqueFactOrFail<PrefixOperationT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == prefixOperationT.id }
            }
        }

        @Nested
        inner class Reference {
            @Test
            fun `should reference declaration if possible`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableReference" }
                val referenceT = findUniqueFactOrFail<ReferenceT> { isContainedIn(it, workflowT) }
                val placeholderT = findUniqueFactOrFail<PlaceholderT> { it.id == referenceT.symbol }
                placeholderT.asClue {
                    placeholderT.name shouldBe "a"
                }
            }

            @Test
            fun `should store name for unresolvable references`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithUnresolvableReference" }
                val referenceT = findUniqueFactOrFail<ReferenceT> { isContainedIn(it, workflowT) }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == referenceT.symbol }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "myUnresolvedDeclaration"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableReference" }
                val referenceT = findUniqueFactOrFail<ReferenceT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == referenceT.id }
            }
        }

        @Nested
        inner class String {
            @Test
            fun `should store value`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val stringT = findUniqueFactOrFail<StringT> { isContainedIn(it, workflowT) }
                stringT.asClue {
                    stringT.value shouldBe "bla"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithLiterals" }
                val stringT = findUniqueFactOrFail<StringT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == stringT.id }
            }
        }

        @Nested
        inner class TemplateString {
            @Test
            fun `should store expressions`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringT = findUniqueFactOrFail<TemplateStringT> { isContainedIn(it, workflowT) }
                shouldBeNChildExpressionsOf<ExpressionT>(templateStringT.expressions, templateStringT, 5)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringT = findUniqueFactOrFail<TemplateStringT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == templateStringT.id }
            }
        }

        @Nested
        inner class TemplateStringPart {
            @Test
            fun `should store value for prefix`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringParts = findFacts<TemplateStringPartT> { isContainedIn(it, workflowT) }
                templateStringParts.shouldHaveSize(3)

                val prefix = templateStringParts[0]
                prefix.asClue {
                    prefix.value shouldBe "prefix "
                }
            }

            @Test
            fun `should store value for infix`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringParts = findFacts<TemplateStringPartT> { isContainedIn(it, workflowT) }
                templateStringParts.shouldHaveSize(3)

                val prefix = templateStringParts[1]
                prefix.asClue {
                    prefix.value shouldBe " infix "
                }
            }

            @Test
            fun `should store value for suffix`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringParts = findFacts<TemplateStringPartT> { isContainedIn(it, workflowT) }
                templateStringParts.shouldHaveSize(3)

                val prefix = templateStringParts[2]
                prefix.asClue {
                    prefix.value shouldBe " suffix"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("expressions") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithTemplateString" }
                val templateStringParts = findFacts<TemplateStringPartT> { isContainedIn(it, workflowT) }
                templateStringParts.shouldHaveSize(3)
                templateStringParts.forEachAsClue { templateStringPartT ->
                    findUniqueFactOrFail<SourceLocationS> { it.target == templateStringPartT.id }
                }
            }
        }
    }

    // *****************************************************************************************************************
    // Types
    // ****************************************************************************************************************/

    @Nested
    inner class Types {

        @Nested
        inner class CallableType {
            @Test
            fun `should handle simple callable types`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleCallableType" }
                val callableTypeT = findUniqueFactOrFail<CallableTypeT> { isContainedIn(it, workflowStepT) }
                callableTypeT.asClue {
                    callableTypeT.parameters.shouldBeEmpty()
                    callableTypeT.results.shouldBeEmpty()
                }
            }

            @Test
            fun `should reference parameters`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexCallableType" }
                val callableTypeT = findUniqueFactOrFail<CallableTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeNChildrenOf<ParameterT>(callableTypeT.parameters, callableTypeT, 2)
            }

            @Test
            fun `should reference results`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexCallableType" }
                val callableTypeT = findUniqueFactOrFail<CallableTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeNChildrenOf<ResultT>(callableTypeT.results, callableTypeT, 2)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleCallableType" }
                val callableTypeT = findUniqueFactOrFail<CallableTypeT> { isContainedIn(it, workflowStepT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == callableTypeT.id }
            }
        }

        @Nested
        inner class MemberType {
            @Test
            fun `should reference receiver`() = withFactbaseFromFile("types") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithMemberType" }
                val memberTypeT = findUniqueFactOrFail<MemberTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeChildOf<TypeT>(memberTypeT.receiver, memberTypeT)
            }

            @Test
            fun `should reference member`() = withFactbaseFromFile("types") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithMemberType" }
                val memberTypeT = findUniqueFactOrFail<MemberTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeChildOf<NamedTypeT>(memberTypeT.member, memberTypeT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowStepT = findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithMemberType" }
                val memberTypeT = findUniqueFactOrFail<MemberTypeT> { isContainedIn(it, workflowStepT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == memberTypeT.id }
            }
        }

        @Nested
        inner class NamedType {
            @Test
            fun `should handle simple named types`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleResolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                namedTypeT.asClue {
                    namedTypeT.typeArguments.shouldBeNull()
                    namedTypeT.isNullable shouldBe false
                }
            }

            @Test
            fun `should reference declaration if possible`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexResolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                val declarationT = findUniqueFactOrFail<DeclarationT> { it.id == namedTypeT.declaration }
                declarationT.asClue {
                    declarationT.name shouldBe "C"
                }
            }

            @Test
            fun `should reference type arguments`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexResolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeNChildrenOf<TypeArgumentT>(namedTypeT.typeArguments, namedTypeT, 1)
            }

            @Test
            fun `should store nullability`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexResolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                namedTypeT.asClue {
                    namedTypeT.isNullable shouldBe true
                }
            }

            @Test
            fun `should store name for unresolvable named types`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowWithUnresolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == namedTypeT.declaration }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "MyUnresolvedDeclaration"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleResolvableNamedType" }
                val namedTypeT = findUniqueFactOrFail<NamedTypeT> { isContainedIn(it, workflowStepT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == namedTypeT.id }
            }
        }

        @Nested
        inner class ParenthesizedType {
            @Test
            fun `should reference type`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithParenthesizedType" }
                val parenthesizedTypeT = findUniqueFactOrFail<ParenthesizedTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeChildOf<TypeT>(parenthesizedTypeT.type, parenthesizedTypeT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithParenthesizedType" }
                val parenthesizedTypeT = findUniqueFactOrFail<ParenthesizedTypeT> { isContainedIn(it, workflowStepT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == parenthesizedTypeT.id }
            }
        }

        @Nested
        inner class StarProjection {
            @Test
            fun `should handle star projections`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithStarProjection" }
                findUniqueFactOrFail<StarProjectionT> { isContainedIn(it, workflowT) }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithStarProjection" }
                val starProjectionT = findUniqueFactOrFail<StarProjectionT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == starProjectionT.id }
            }
        }

        @Nested
        inner class TypeArgument {
            @Test
            fun `should handle positional type arguments`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithPositionalTypeArgument" }
                val typeArgumentT = findUniqueFactOrFail<TypeArgumentT> { isContainedIn(it, workflowT) }
                typeArgumentT.asClue {
                    typeArgumentT.typeParameter.shouldBeNull()
                }
            }

            @Test
            fun `should reference type parameter if possible`() = withFactbaseFromFile("types") {
                val workflowT =
                    findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableNamedTypeArgument" }
                val typeArgumentT = findUniqueFactOrFail<TypeArgumentT> { isContainedIn(it, workflowT) }
                val typeParameterT = findUniqueFactOrFail<TypeParameterT> { it.id == typeArgumentT.typeParameter }
                typeParameterT.asClue {
                    typeParameterT.name shouldBe "T"
                }
            }

            @Test
            fun `should reference value`() = withFactbaseFromFile("types") {
                val workflowT =
                    findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithResolvableNamedTypeArgument" }
                val typeArgumentT = findUniqueFactOrFail<TypeArgumentT> { isContainedIn(it, workflowT) }
                shouldBeChildOf<NodeWithParent>(typeArgumentT.value, typeArgumentT)
            }

            @Test
            fun `should store name for unresolvable type arguments`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithUnresolvedTypeArgument" }
                val typeArgumentT = findUniqueFactOrFail<TypeArgumentT> { isContainedIn(it, workflowT) }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == typeArgumentT.typeParameter }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "MY_UNRESOLVED_TYPE_PARAMETER"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithPositionalTypeArgument" }
                val typeArgumentT = findUniqueFactOrFail<TypeArgumentT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == typeArgumentT.id }
            }
        }

        @Nested
        inner class TypeParameterConstraint {
            @Test
            fun `should reference left operand if possible`() = withFactbaseFromFile("types") {
                val functionT =
                    findUniqueFactOrFail<FunctionT> { it.name == "myFunctionWithResolvableTypeParameterConstraint" }
                val typeParameterConstraintT =
                    findUniqueFactOrFail<TypeParameterConstraintT> { isContainedIn(it, functionT) }
                val typeParameterT =
                    findUniqueFactOrFail<TypeParameterT> { it.id == typeParameterConstraintT.leftOperand }
                typeParameterT.asClue {
                    typeParameterT.name shouldBe "T"
                }
            }

            @Test
            fun `should store operator`() = withFactbaseFromFile("types") {
                val functionT =
                    findUniqueFactOrFail<FunctionT> { it.name == "myFunctionWithResolvableTypeParameterConstraint" }
                val typeParameterConstraintT =
                    findUniqueFactOrFail<TypeParameterConstraintT> { isContainedIn(it, functionT) }
                typeParameterConstraintT.asClue {
                    typeParameterConstraintT.operator shouldBe "sub"
                }
            }

            @Test
            fun `should reference right operand`() = withFactbaseFromFile("types") {
                val functionT =
                    findUniqueFactOrFail<FunctionT> { it.name == "myFunctionWithResolvableTypeParameterConstraint" }
                val typeParameterConstraintT =
                    findUniqueFactOrFail<TypeParameterConstraintT> { isContainedIn(it, functionT) }
                shouldBeChildOf<TypeT>(typeParameterConstraintT.rightOperand, typeParameterConstraintT)
            }

            @Test
            fun `should store name for unresolvable type parameters`() = withFactbaseFromFile("types") {
                val functionT =
                    findUniqueFactOrFail<FunctionT> { it.name == "myFunctionWithUnresolvableTypeParameterConstraint" }
                val typeParameterConstraintT =
                    findUniqueFactOrFail<TypeParameterConstraintT> { isContainedIn(it, functionT) }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == typeParameterConstraintT.leftOperand }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "MY_UNRESOLVED_TYPE_PARAMETER"
                }
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val functionT =
                    findUniqueFactOrFail<FunctionT> { it.name == "myFunctionWithResolvableTypeParameterConstraint" }
                val typeParameterConstraintT =
                    findUniqueFactOrFail<TypeParameterConstraintT> { isContainedIn(it, functionT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == typeParameterConstraintT.id }
            }
        }

        @Nested
        inner class TypeProjection {
            @Test
            fun `should handle simple type projections`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleTypeProjection" }
                val typeProjectionT = findUniqueFactOrFail<TypeProjectionT> { isContainedIn(it, workflowT) }
                typeProjectionT.asClue {
                    typeProjectionT.variance.shouldBeNull()
                }
            }

            @Test
            fun `should store variance`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexTypeProjection" }
                val typeProjectionT = findUniqueFactOrFail<TypeProjectionT> { isContainedIn(it, workflowT) }
                typeProjectionT.asClue {
                    typeProjectionT.variance shouldBe "out"
                }
            }

            @Test
            fun `should reference type`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithComplexTypeProjection" }
                val typeProjectionT = findUniqueFactOrFail<TypeProjectionT> { isContainedIn(it, workflowT) }
                shouldBeChildOf<TypeT>(typeProjectionT.type, typeProjectionT)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowT = findUniqueFactOrFail<WorkflowT> { it.name == "myWorkflowWithSimpleTypeProjection" }
                val typeProjectionT = findUniqueFactOrFail<TypeProjectionT> { isContainedIn(it, workflowT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == typeProjectionT.id }
            }
        }

        @Nested
        inner class UnionType {
            @Test
            fun `should handle simple union types`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleUnionType" }
                val unionTypeT = findUniqueFactOrFail<UnionTypeT> { isContainedIn(it, workflowStepT) }
                unionTypeT.asClue {
                    unionTypeT.typeArguments.shouldBeEmpty()
                }
            }

            @Test
            fun `should store type arguments`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithComplexUnionType" }
                val unionTypeT = findUniqueFactOrFail<UnionTypeT> { isContainedIn(it, workflowStepT) }
                shouldBeNChildrenOf<TypeArgumentT>(unionTypeT.typeArguments, unionTypeT, 2)
            }

            @Test
            fun `should store source location in separate relation`() = withFactbaseFromFile("types") {
                val workflowStepT =
                    findUniqueFactOrFail<WorkflowStepT> { it.name == "myWorkflowStepWithSimpleUnionType" }
                val unionTypeT = findUniqueFactOrFail<UnionTypeT> { isContainedIn(it, workflowStepT) }
                findUniqueFactOrFail<SourceLocationS> { it.target == unionTypeT.id }
            }
        }
    }

    // *****************************************************************************************************************
    // Other
    // ****************************************************************************************************************/

    @Nested
    inner class Other {

        @Nested
        inner class AnnotationUse {
            @Test
            fun `should handle simple annotation uses`() = withFactbaseFromFile("annotationUses") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyClassWithSimpleAnnotationUse" }
                val annotationUseT = findUniqueFactOrFail<AnnotationUseT> { it.parent == classT.id }
                annotationUseT.asClue {
                    annotationUseT.arguments.shouldBeNull()
                }
            }

            @Test
            fun `should reference annotation if possible`() = withFactbaseFromFile("annotationUses") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyClassWithSimpleAnnotationUse" }
                val annotationUseT = findUniqueFactOrFail<AnnotationUseT> { it.parent == classT.id }
                val annotationT = findUniqueFactOrFail<AnnotationT> { it.id == annotationUseT.annotation }
                annotationT.asClue {
                    annotationT.name shouldBe "MyAnnotation"
                }
            }

            @Test
            fun `should reference arguments`() = withFactbaseFromFile("annotationUses") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyClassWithComplexAnnotationUse" }
                val annotationUseT = findUniqueFactOrFail<AnnotationUseT> { it.parent == classT.id }
                shouldBeNChildExpressionsOf<ExpressionT>(annotationUseT.arguments, annotationUseT, 2)
            }

            @Test
            fun `should store name for unresolvable annotations`() = withFactbaseFromFile("annotationUses") {
                val classT = findUniqueFactOrFail<ClassT> { it.name == "MyClassWithUnresolvedAnnotationUse" }
                val annotationUseT = findUniqueFactOrFail<AnnotationUseT> { it.parent == classT.id }
                val unresolvedT = findUniqueFactOrFail<UnresolvedT> { it.id == annotationUseT.annotation }
                unresolvedT.asClue {
                    unresolvedT.name shouldBe "MyUnresolvedAnnotation"
                }
            }

            @Test
            fun `should store source location in separate relation`() =
                withFactbaseFromFile("annotationUses") {
                    val classT = findUniqueFactOrFail<ClassT> { it.name == "MyClassWithSimpleAnnotationUse" }
                    val annotationUseT = findUniqueFactOrFail<AnnotationUseT> { it.parent == classT.id }
                    findUniqueFactOrFail<SourceLocationS> { it.target == annotationUseT.id }
                }
        }

        @Nested
        inner class SourceLocation {

            @Test
            fun `should store uri hash`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyOtherClass" }
                val sourceLocationS = findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
                sourceLocationS.asClue {
                    sourceLocationS.uriHash shouldBe "//@members.0/@imports.1"
                }
            }

            @Test
            fun `should store offset`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                val sourceLocationS = findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
                sourceLocationS.asClue {
                    // Actual offset depends on the new line characters (19 for just \n or \r and 21 for \r\n)
                    sourceLocationS.offset shouldBeOneOf listOf(19, 21)
                }
            }

            @Test
            fun `should store line`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                val sourceLocationS = findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
                sourceLocationS.asClue {
                    sourceLocationS.line shouldBe 3
                }
            }

            @Test
            fun `should store column`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                val sourceLocationS = findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
                sourceLocationS.asClue {
                    sourceLocationS.column shouldBe 1
                }
            }

            @Test
            fun `should store length`() = withFactbaseFromFile("declarations") {
                val importT = findUniqueFactOrFail<ImportT> { it.importedNamespace == "myPackage.MyClass" }
                val sourceLocationS = findUniqueFactOrFail<SourceLocationS> { it.target == importT.id }
                sourceLocationS.asClue {
                    sourceLocationS.length shouldBe 24
                }
            }
        }
    }

    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

    private fun withFactbaseFromFile(file: String, lambda: PlFactbase.() -> Unit) {
        main.createFactbase("$testRoot/$file${FileExtensions.TEST}").apply(lambda)
    }
}
