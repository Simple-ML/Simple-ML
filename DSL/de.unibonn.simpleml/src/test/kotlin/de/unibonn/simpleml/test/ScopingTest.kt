package de.unibonn.simpleml.test

import com.google.inject.Inject
import de.unibonn.simpleml.emf.annotationUsesOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlLambdaYield
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.test.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.test.assertions.shouldBeResolved
import de.unibonn.simpleml.test.assertions.shouldNotBeResolved
import de.unibonn.simpleml.test.util.ParseHelper
import de.unibonn.simpleml.test.util.ResourceName
import de.unibonn.simpleml.utils.descendants
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val ANNOTATION_USE = "annotationUse"
private const val ARGUMENT = "argument"
private const val IMPORT_WITH_ALIAS = "importWithAlias"
private const val NAMED_TYPE = "namedType"
private const val REFERENCE = "reference"
private const val TYPE_ARGUMENT = "typeArgument"
private const val TYPE_PARAMETER_CONSTRAINT = "typeParameterConstraint"
private const val YIELD = "yield"

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class ScopingTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Nested
    inner class AnnotationUse {

        @Test
        fun `should resolve annotations in same file`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)

            val annotationInSameFile = findUniqueDeclarationOrFail<SmlAnnotation>("AnnotationInSameFile")

            val referencedAnnotation = annotationUses[0].annotation
            referencedAnnotation.shouldBeResolved()
            referencedAnnotation.shouldBe(annotationInSameFile)
        }

        @Test
        fun `should resolve annotations in same package`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)

            val annotation = annotationUses[1].annotation
            annotation.shouldBeResolved()
            annotation.name.shouldBe("AnnotationInSamePackage")
        }

        @Test
        fun `should resolve annotations in another package if imported`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)

            val annotation = annotationUses[2].annotation
            annotation.shouldBeResolved()
            annotation.name.shouldBe("AnnotationInOtherPackage1")
        }

        @Test
        fun `should not resolve annotations in another package if not imported`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[3].annotation.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve unknown declaration`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[4].annotation.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not an annotation`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[5].annotation.shouldNotBeResolved()
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should resolve parameter in use annotation in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInAnnotationInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInAnnotationInSameFile")

                val referencedParameter = arguments[0].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInAnnotationInSameFile)
            }

        @Test
        fun `should resolve parameter in called callable in same workflow step`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInCallableInSameStep =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInCallableInSameStep")

                val referencedParameter = arguments[1].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInCallableInSameStep)
            }

        @Test
        fun `should resolve parameter in called class in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInClassInSameFile = findUniqueDeclarationOrFail<SmlParameter>("parameterInClassInSameFile")

                val referencedParameter = arguments[2].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInClassInSameFile)
            }

        @Test
        fun `should resolve parameter in called enum variant in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInEnumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInEnumVariantInSameFile")

                val referencedParameter = arguments[3].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInEnumVariantInSameFile)
            }

        @Test
        fun `should resolve parameter in called function in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInFunctionSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInFunctionSameFile")

                val referencedParameter = arguments[4].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInFunctionSameFile)
            }

        @Test
        fun `should resolve parameter in called lambda in same workflow step`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInLambdaInSameStep =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInLambdaInSameStep")

                val referencedParameter = arguments[5].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInLambdaInSameStep)
            }

        @Test
        fun `should resolve parameter in called workflow step in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val parameterInWorkflowStepInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("parameterInWorkflowStepInSameFile")

                val referencedParameter = arguments[6].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInWorkflowStepInSameFile)
            }

        @Test
        fun `should resolve parameter in called function in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val referencedParameter = arguments[7].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.name.shouldBe("parameterInSamePackage")
            }

        @Test
        fun `should resolve parameter in called function that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)

                val referencedParameter = arguments[8].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.name.shouldBe("parameterInOtherPackage1")
            }

        @Test
        fun `should not resolve parameter in called function that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)
                arguments[9].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)
                arguments[10].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)
                arguments[11].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(15)
                arguments[12].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(15)
            arguments[13].parameter.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not a parameter`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(15)
            arguments[14].parameter.shouldNotBeResolved()
        }
    }

    @Nested
    inner class ImportWithAlias {

        @Test
        fun `should resolve alias name of declaration in same file`() = withResource(IMPORT_WITH_ALIAS) {
            val aliasNameInSameFile = findUniqueDeclarationOrFail<SmlParameter>("aliasNameInSameFile")
            val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSameFile")

            val type = aliasNameInSameFile.type
            type.shouldBeInstanceOf<SmlNamedType>()

            val declaration = type.declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(classInSameFile)
        }

        @Test
        fun `should resolve original name of declaration in same file`() = withResource(IMPORT_WITH_ALIAS) {
            val originalNameInSameFile = findUniqueDeclarationOrFail<SmlParameter>("originalNameInSameFile")

            val type = originalNameInSameFile.type
            type.shouldBeInstanceOf<SmlNamedType>()

            val declaration = type.declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInSameFile")
        }

        @Test
        fun `should resolve alias name of declaration in same package`() = withResource(IMPORT_WITH_ALIAS) {
            val aliasNameInSamePackage = findUniqueDeclarationOrFail<SmlParameter>("aliasNameInSamePackage")

            val type = aliasNameInSamePackage.type
            type.shouldBeInstanceOf<SmlNamedType>()

            val declaration = type.declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInSamePackage")
        }

        @Test
        fun `should resolve original name of declaration in same package`() = withResource(IMPORT_WITH_ALIAS) {
            val originalNameInSamePackage = findUniqueDeclarationOrFail<SmlParameter>("originalNameInSamePackage")

            val type = originalNameInSamePackage.type
            type.shouldBeInstanceOf<SmlNamedType>()

            val declaration = type.declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInSamePackage")
        }

        @Test
        fun `should resolve alias name of declaration in other package`() = withResource(IMPORT_WITH_ALIAS) {
            val aliasNameInOtherPackage = findUniqueDeclarationOrFail<SmlParameter>("aliasNameInOtherPackage")

            val type = aliasNameInOtherPackage.type
            type.shouldBeInstanceOf<SmlNamedType>()

            val declaration = type.declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInOtherPackage")
        }

        @Test
        fun `should not resolve original name of declaration in other package`() = withResource(IMPORT_WITH_ALIAS) {
            val originalNameInOtherPackage = findUniqueDeclarationOrFail<SmlParameter>("originalNameInOtherPackage")

            val type = originalNameInOtherPackage.type
            type.shouldBeInstanceOf<SmlNamedType>()
            type.declaration.shouldNotBeResolved()
        }
    }

    @Nested
    inner class NamedType {

        @Test
        fun `should resolve class in same file`() = withResource(NAMED_TYPE) {
            val paramClassInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramClassInSameFile")
            val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSameFile")

            val parameterType = paramClassInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.shouldBeResolved()
            referencedClass.shouldBe(classInSameFile)
        }

        @Test
        fun `should resolve enum in same file`() = withResource(NAMED_TYPE) {
            val paramEnumInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInSameFile")
            val enumInSameFile = findUniqueDeclarationOrFail<SmlEnum>("EnumInSameFile")

            val parameterType = paramEnumInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.shouldBeResolved()
            referencedEnum.shouldBe(enumInSameFile)
        }

        @Test
        fun `should resolve class in same package`() = withResource(NAMED_TYPE) {
            val paramClassInSamePackage = findUniqueDeclarationOrFail<SmlParameter>("paramClassInSamePackage")

            val parameterType = paramClassInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.shouldBeResolved()
            referencedClass.name.shouldBe("ClassInSamePackage")
        }

        @Test
        fun `should resolve enum in same package`() = withResource(NAMED_TYPE) {
            val paramEnumInSamePackage = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInSamePackage")

            val parameterType = paramEnumInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.shouldBeResolved()
            referencedEnum.name.shouldBe("EnumInSamePackage")
        }

        @Test
        fun `should resolve class in another package if imported`() = withResource(NAMED_TYPE) {
            val paramClassInOtherPackage1 = findUniqueDeclarationOrFail<SmlParameter>("paramClassInOtherPackage1")

            val parameterType = paramClassInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.shouldBeResolved()
            referencedClass.name.shouldBe("ClassInOtherPackage1")
        }

        @Test
        fun `should resolve enum in another package if imported`() = withResource(NAMED_TYPE) {
            val paramEnumInOtherPackage1 = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInOtherPackage1")

            val parameterType = paramEnumInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.shouldBeResolved()
            referencedEnum.name.shouldBe("EnumInOtherPackage1")
        }

        @Test
        fun `should not resolve class in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramClassInOtherPackage2 = findUniqueDeclarationOrFail<SmlParameter>("paramClassInOtherPackage2")

            val parameterType = paramClassInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve enum in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramEnumInOtherPackage2 = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInOtherPackage2")

            val parameterType = paramEnumInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.shouldNotBeResolved()
        }

        @Test
        fun `should resolve type parameters in same function`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSameFunction =
                findUniqueDeclarationOrFail<SmlParameter>("paramTypeParameterInSameFunction")
            val typeParameter = findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_SAME_FUNCTION")

            val parameterType = paramTypeParameterInSameFunction.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedTypeParameter = parameterType.declaration
            referencedTypeParameter.shouldBeResolved()
            referencedTypeParameter.shouldBe(typeParameter)
        }

        @Test
        fun `should not resolve type parameters in another declaration in same file`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramTypeParameterInSameFile")

            val parameterType = paramTypeParameterInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve type parameters in another declaration in same package`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSamePackage =
                findUniqueDeclarationOrFail<SmlParameter>("paramTypeParameterInSamePackage")

            val parameterType = paramTypeParameterInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve type parameters in another declaration in another package`() =
            withResource(NAMED_TYPE) {
                val paramTypeParameterInOtherPackage =
                    findUniqueDeclarationOrFail<SmlParameter>("paramTypeParameterInOtherPackage")

                val parameterType = paramTypeParameterInOtherPackage.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                val referencedInterface = parameterType.declaration
                referencedInterface.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(NAMED_TYPE) {
            val paramUnresolvedNamedTypeDeclaration =
                findUniqueDeclarationOrFail<SmlParameter>("paramUnresolvedNamedTypeDeclaration")

            val parameterType = paramUnresolvedNamedTypeDeclaration.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not a named type declaration`() = withResource(NAMED_TYPE) {
            val paramNotANamedTypeDeclaration =
                findUniqueDeclarationOrFail<SmlParameter>("paramNotANamedTypeDeclaration")

            val parameterType = paramNotANamedTypeDeclaration.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldNotBeResolved()
        }

        @Nested
        inner class MemberType {
            @Test
            fun `should resolve class within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramClassInClassInSameFile")
                val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInClassInSameFile")

                val parameterType = paramClassInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedClass = parameterType.member.declaration
                referencedClass.shouldBeResolved()
                referencedClass.shouldBe(classInSameFile)
            }

            @Test
            fun `should resolve enum within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInClassInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInClassInSameFile")
                val enumInSameFile = findUniqueDeclarationOrFail<SmlEnum>("EnumInClassInSameFile")

                val parameterType = paramEnumInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedEnum = parameterType.member.declaration
                referencedEnum.shouldBeResolved()
                referencedEnum.shouldBe(enumInSameFile)
            }

            @Test
            fun `should resolve enum variant with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumVariantInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramEnumVariantInSameFile")
                val enumVariantInSameFile = findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameFile")

                val parameterType = paramEnumVariantInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedEnumVariant = parameterType.member.declaration
                referencedEnumVariant.shouldBeResolved()
                referencedEnumVariant.shouldBe(enumVariantInSameFile)
            }

            @Test
            fun `should not resolve class within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedClassInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedClassInClassInSameFile")

                val parameterType = paramUnqualifiedClassInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()
                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve enum within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedEnumInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedEnumInClassInSameFile")

                val parameterType = paramUnqualifiedEnumInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()
                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve enum variant with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedEnumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedEnumVariantInSameFile")

                val parameterType = paramUnqualifiedEnumVariantInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()
                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should resolve inherited class within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInSuperClass = findUniqueDeclarationOrFail<SmlParameter>("paramClassInSuperClass")
                val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSuperClass")

                val parameterType = paramClassInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedClass = parameterType.member.declaration
                referencedClass.shouldBeResolved()
                referencedClass.shouldBe(classInSameFile)
            }

            @Test
            fun `should resolve inherited enum within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInSuperClass = findUniqueDeclarationOrFail<SmlParameter>("paramEnumInSuperClass")
                val enumInSameFile = findUniqueDeclarationOrFail<SmlEnum>("EnumInSuperClass")

                val parameterType = paramEnumInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedEnum = parameterType.member.declaration
                referencedEnum.shouldBeResolved()
                referencedEnum.shouldBe(enumInSameFile)
            }
        }
    }

    @Nested
    inner class Reference {

        @Test
        fun `should not resolve annotation in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToAnnotations")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[0].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve annotation in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToAnnotations")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[1].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve annotation in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToAnnotations")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[2].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve annotation in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToAnnotations")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should resolve class in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToClasses")
            val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSameFile")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(classInSameFile)
        }

        @Test
        fun `should resolve class in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToClasses")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInSamePackage")
        }

        @Test
        fun `should resolve class in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToClasses")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[2].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("ClassInOtherPackage1")
        }

        @Test
        fun `should not resolve class in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToClasses")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should resolve enum in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToEnums")
            val enumInSameFile = findUniqueDeclarationOrFail<SmlEnum>("EnumInSameFile")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(enumInSameFile)
        }

        @Test
        fun `should resolve enum in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToEnums")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("EnumInSamePackage")
        }

        @Test
        fun `should resolve enum in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToEnums")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[2].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("EnumInOtherPackage1")
        }

        @Test
        fun `should not resolve enum in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToEnums")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should resolve global function in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToGlobalFunctions")
            val globalFunctionInSameFile = findUniqueDeclarationOrFail<SmlFunction>("globalFunctionInSameFile")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(globalFunctionInSameFile)
        }

        @Test
        fun `should resolve global function in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToGlobalFunctions")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("globalFunctionInSamePackage")
        }

        @Test
        fun `should resolve global function in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToGlobalFunctions")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[2].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("globalFunctionInOtherPackage1")
        }

        @Test
        fun `should not resolve global function in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToGlobalFunctions")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve lambda yields`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToLambdaYields")

            val reference = step.descendants<SmlReference>().firstOrNull()
            reference.shouldNotBeNull()
            reference.declaration.shouldNotBeResolved()
        }

        @Test
        fun `should resolve parameter of workflow step in same workflow step`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToParameters")
            val parameterInStep = step.findUniqueDeclarationOrFail<SmlParameter>("parameterInStep")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(5)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(parameterInStep)
        }

        @Test
        fun `should resolve parameter of workflow step in lambda in same workflow step`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToParameters")
            val parameterInStep = step.findUniqueDeclarationOrFail<SmlParameter>("parameterInStep")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(5)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(parameterInStep)
        }

        @Test
        fun `should resolve parameter of lambda in same lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToParameters")
                val parameterInLambda = step.findUniqueDeclarationOrFail<SmlParameter>("parameterInLambda")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[2].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(parameterInLambda)
            }

        @Test
        fun `should resolve parameter of workflow step in lambda within lambda in same workflow step`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToParameters")
                val parameterInStep = step.findUniqueDeclarationOrFail<SmlParameter>("parameterInStep")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[3].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(parameterInStep)
            }

        @Test
        fun `should resolve parameter of lambda in nested lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToParameters")
                val parameterInLambda = step.findUniqueDeclarationOrFail<SmlParameter>("parameterInLambda")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[4].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(parameterInLambda)
            }

        @Test
        fun `should resolve placeholder of workflow step in same workflow step`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToPlaceholders")
            val placeholderInStep = step.findUniqueDeclarationOrFail<SmlPlaceholder>("placeholderInStep")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(5)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(placeholderInStep)
        }

        @Test
        fun `should resolve placeholder of workflow step in lambda in same workflow step`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToPlaceholders")
            val placeholderInStep = step.findUniqueDeclarationOrFail<SmlPlaceholder>("placeholderInStep")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(5)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(placeholderInStep)
        }

        @Test
        fun `should resolve placeholder of lambda in same lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToPlaceholders")
                val placeholderInLambda = step.findUniqueDeclarationOrFail<SmlPlaceholder>("placeholderInLambda")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[2].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(placeholderInLambda)
            }

        @Test
        fun `should resolve placeholder of workflow step in lambda within lambda in same workflow step`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToPlaceholders")
                val placeholderInStep = step.findUniqueDeclarationOrFail<SmlPlaceholder>("placeholderInStep")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[3].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(placeholderInStep)
            }

        @Test
        fun `should resolve placeholder of lambda in nested lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToPlaceholders")
                val placeholderInLambda = step.findUniqueDeclarationOrFail<SmlPlaceholder>("placeholderInLambda")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(5)

                val declaration = references[4].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(placeholderInLambda)
            }

        @Test
        fun `should not resolve type parameters`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToTypeParameters")

            val reference = step.descendants<SmlReference>().firstOrNull()
            reference.shouldNotBeNull()
            reference.declaration.shouldNotBeResolved()
        }

        @Test
        fun `should resolve workflow step in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflowSteps")
            val stepInSameFile = findUniqueDeclarationOrFail<SmlWorkflowStep>("stepInSameFile")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(stepInSameFile)
        }

        @Test
        fun `should resolve workflow step in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflowSteps")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("stepInSamePackage")
        }

        @Test
        fun `should resolve workflow step in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflowSteps")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[2].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("stepInOtherPackage1")
        }

        @Test
        fun `should not resolve workflow step in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflowSteps")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve workflow in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflows")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[0].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve workflow in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflows")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[1].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve workflow in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflows")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[2].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve workflow in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToWorkflows")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)
            references[3].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve placeholder declared later in same workflow step`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("forwardReferences")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(3)
            references[0].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve placeholder declared later from nested lambda`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("forwardReferences")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(3)
            references[1].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve placeholder that lambda is assigned to from body of lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("forwardReferences")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(3)
                references[2].declaration.shouldNotBeResolved()
            }

        @Test
        fun `should resolve declaration shadowed by parameter of workflow step`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("shadowedReferences")

                val parameters = step.parametersOrEmpty()
                parameters.shouldHaveSize(1)

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(4)

                val declaration = references[0].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(parameters[0])
            }

        @Test
        fun `should resolve declaration shadowed by placeholder of workflow step`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("shadowedReferences")

                val placeholders = step.descendants<SmlPlaceholder>().toList()
                placeholders.shouldHaveSize(3)

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(4)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(placeholders[0])
            }

        @Test
        fun `should resolve declaration shadowed by parameter of lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("shadowedReferences")

                val parameters = step.body.descendants<SmlParameter>().toList()
                parameters.shouldHaveSize(1)

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(4)

                val declaration = references[2].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(parameters[0])
            }

        @Test
        fun `should resolve declaration shadowed by placeholder of lambda`() =
            withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("shadowedReferences")

                val placeholders = step.descendants<SmlPlaceholder>().toList()
                placeholders.shouldHaveSize(3)

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(4)

                val declaration = references[3].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(placeholders[2])
            }

        @Test
        fun `should not resolve function locals`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToFunctionLocals")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(2)
            references.forEachAsClue {
                it.declaration.shouldNotBeResolved()
            }
        }

        @Test
        fun `should not resolve lambda locals`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToLambdaLocals")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(3)
            references[0].declaration.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve workflow step locals`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToWorkflowStepLocals")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(3)
            references.forEachAsClue {
                it.declaration.shouldNotBeResolved()
            }
        }

        @Test
        fun `should not resolve unknown declaration`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unresolvedReferences")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(1)
            references[0].declaration.shouldNotBeResolved()
        }

        @Nested
        inner class MemberAccess {

            @Test
            fun `should resolve static class attribute accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val classStaticAttributeInSameFile =
                    findUniqueDeclarationOrFail<SmlAttribute>("classStaticAttributeInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classStaticAttributeInSameFile)
            }

            @Test
            fun `should resolve instance class attribute accessed from class instance`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val classInstanceAttributeInSameFile =
                    findUniqueDeclarationOrFail<SmlAttribute>("classInstanceAttributeInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[3].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classInstanceAttributeInSameFile)
            }

            @Test
            fun `should resolve nested class accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val classInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlClass>("ClassInClassInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classInClassInSameFile)
            }

            @Test
            fun `should resolve nested enum accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val enumInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlEnum>("EnumInClassInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[7].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumInClassInSameFile)
            }

            @Test
            fun `should resolve static class method accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val classStaticMethodInSameFile =
                    findUniqueDeclarationOrFail<SmlFunction>("classStaticMethodInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[9].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classStaticMethodInSameFile)
            }

            @Test
            fun `should resolve instance class method accessed from class instance`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToClassMembers")
                val classInstanceMethodInSameFile =
                    findUniqueDeclarationOrFail<SmlFunction>("classInstanceMethodInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[11].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classInstanceMethodInSameFile)
            }

            @Test
            fun `should resolve enum variants`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToEnumVariants")
                val enumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameFile")

                val references = step.body.descendants<SmlReference>().toList()
                references.shouldHaveSize(2)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantInSameFile)
            }

            @Test
            fun `should resolve enum variants from workflow step annotation`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToEnumVariants")
                val enumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameFile")

                val annotations = step.annotationUsesOrEmpty()
                annotations.shouldHaveSize(1)

                val references = annotations[0].descendants<SmlReference>().toList()
                references.shouldHaveSize(2)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantInSameFile)
            }

            @Test
            fun `should resolve enum variants from parameter annotation`() = withResource(REFERENCE) {
                val parameter =
                    findUniqueDeclarationOrFail<SmlParameter>("referenceToEnumVariantFromParameterAnnotation")
                val enumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameFile")

                val annotations = parameter.annotationUsesOrEmpty()
                annotations.shouldHaveSize(1)

                val references = annotations[0].descendants<SmlReference>().toList()
                references.shouldHaveSize(2)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantInSameFile)
            }

            @Test
            fun `should resolve enum variants of nested enum from class annotation`() = withResource(REFERENCE) {
                val parameter = findUniqueDeclarationOrFail<SmlClass>("ReferencesToEnumVariantsInnerClass")
                val enumVariantInSameFile =
                    findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameClass")

                val annotations = parameter.annotationUsesOrEmpty()
                annotations.shouldHaveSize(2)

                val references = annotations[0].descendants<SmlReference>().toList()
                references.shouldHaveSize(2)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantInSameFile)
            }

            @Test
            fun `should resolve enum variants of global enum from class annotation`() = withResource(REFERENCE) {
                val parameter = findUniqueDeclarationOrFail<SmlClass>("ReferencesToEnumVariantsInnerClass")
                val enumVariantInSameClass =
                    findUniqueDeclarationOrFail<SmlEnumVariant>("EnumVariantInSameFile")

                val annotations = parameter.annotationUsesOrEmpty()
                annotations.shouldHaveSize(2)

                val references = annotations[1].descendants<SmlReference>().toList()
                references.shouldHaveSize(2)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantInSameClass)
            }

            @Test
            fun `should resolve parameters of enum variants`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToEnumVariantParameters")
                val enumVariantParameterInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("enumVariantParameterInSameFile")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(3)

                val declaration = references[2].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumVariantParameterInSameFile)
            }

            @Test
            fun `should resolve inherited static class attribute accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                val superClassStaticAttribute =
                    findUniqueDeclarationOrFail<SmlAttribute>("superClassStaticAttribute")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(superClassStaticAttribute)
            }

            @Test
            fun `should resolve inherited instance class attribute accessed from class instance`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                    val superClassInstanceAttribute =
                        findUniqueDeclarationOrFail<SmlAttribute>("superClassInstanceAttribute")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(12)

                    val declaration = references[3].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(superClassInstanceAttribute)
                }

            @Test
            fun `should resolve inherited nested class accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                val classInSuperClass =
                    findUniqueDeclarationOrFail<SmlClass>("ClassInSuperClass")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(classInSuperClass)
            }

            @Test
            fun `should resolve inherited nested enum accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                val enumInSuperClass =
                    findUniqueDeclarationOrFail<SmlEnum>("EnumInSuperClass")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[7].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(enumInSuperClass)
            }

            @Test
            fun `should resolve inherited static class method accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                val superClassStaticMethod =
                    findUniqueDeclarationOrFail<SmlFunction>("superClassStaticMethod")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(12)

                val declaration = references[9].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(superClassStaticMethod)
            }

            @Test
            fun `should resolve inherited instance class method accessed from class instance`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInheritedClassMembers")
                    val superClassInstanceMethod =
                        findUniqueDeclarationOrFail<SmlFunction>("superClassInstanceMethod")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(12)

                    val declaration = references[11].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(superClassInstanceMethod)
                }

            @Test
            fun `should resolve overridden instance attribute`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToOverriddenMembers")
                val subClassForOverriding = findUniqueDeclarationOrFail<SmlClass>("SubClassForOverriding")
                val instanceAttributeForOverriding =
                    subClassForOverriding.findUniqueDeclarationOrFail<SmlAttribute>("instanceAttributeForOverriding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(instanceAttributeForOverriding)
            }

            @Test
            fun `should resolve overridden instance method`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToOverriddenMembers")
                val subClassForOverriding = findUniqueDeclarationOrFail<SmlClass>("SubClassForOverriding")
                val instanceMethodForOverriding =
                    subClassForOverriding.findUniqueDeclarationOrFail<SmlFunction>("instanceMethodForOverriding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[7].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(instanceMethodForOverriding)
            }

            @Test
            fun `should resolve hidden static attribute`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToHiddenMembers")
                val subClassForHiding = findUniqueDeclarationOrFail<SmlClass>("SubClassForHiding")
                val staticAttributeForHiding =
                    subClassForHiding.findUniqueDeclarationOrFail<SmlAttribute>("staticAttributeForHiding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[1].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(staticAttributeForHiding)
            }

            @Test
            fun `should resolve hidden nested class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToHiddenMembers")
                val subClassForHiding = findUniqueDeclarationOrFail<SmlClass>("SubClassForHiding")
                val nestedClassForHiding =
                    subClassForHiding.findUniqueDeclarationOrFail<SmlClass>("NestedClassForHiding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[3].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(nestedClassForHiding)
            }

            @Test
            fun `should resolve hidden nested enum`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToHiddenMembers")
                val subClassForHiding = findUniqueDeclarationOrFail<SmlClass>("SubClassForHiding")
                val nestedEnumForHiding =
                    subClassForHiding.findUniqueDeclarationOrFail<SmlEnum>("NestedEnumForHiding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(nestedEnumForHiding)
            }

            @Test
            fun `should resolve hidden static method`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToHiddenMembers")
                val subClassForHiding = findUniqueDeclarationOrFail<SmlClass>("SubClassForHiding")
                val staticMethodForHiding =
                    subClassForHiding.findUniqueDeclarationOrFail<SmlFunction>("staticMethodForHiding")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[7].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(staticMethodForHiding)
            }

            @Test
            fun `should not resolve static class members accessed from instance`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToStaticClassMembersFromInstance")
                val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSameFile")

                val references = step.descendants<SmlReference>()
                    .filter { it.declaration != classInSameFile }
                    .toList()
                references.shouldHaveSize(8)
                references.forEachAsClue {
                    it.declaration.shouldNotBeResolved()
                }
            }

            @Test
            fun `should not resolve instance class members accessed from class`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToInstanceClassMembersFromClass")
                val classInSameFile = findUniqueDeclarationOrFail<SmlClass>("ClassInSameFile")

                val references = step.descendants<SmlReference>()
                    .filter { it.declaration != classInSameFile }
                    .toList()
                references.shouldHaveSize(4)
                references.forEachAsClue {
                    it.declaration.shouldNotBeResolved()
                }
            }

            @Test
            fun `should not resolve class members with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToClassMembers")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(6)
                references.forEachAsClue {
                    it.declaration.shouldNotBeResolved()
                }
            }

            @Test
            fun `should not resolve enum variants with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToEnumVariants")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(1)
                references[0].declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve parameters of enum variants with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToEnumVariantParameters")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(1)
                references[0].declaration.shouldNotBeResolved()
            }

            @Test
            fun `should resolve result of callable type with one result without matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToCallableTypeResults")
                    val singleResult = step.findUniqueDeclarationOrFail<SmlResult>("singleResult")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(8)

                    val declaration = references[1].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(singleResult)
                }

            @Test
            fun `should resolve attribute for callable type with one result with matching class attribute`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToCallableTypeResults")
                    val classForResultMemberAccess = findUniqueDeclarationOrFail<SmlClass>("ClassForResultMemberAccess")
                    val result = classForResultMemberAccess.findUniqueDeclarationOrFail<SmlAttribute>("result")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(8)

                    val declaration = references[3].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(result)
                }

            @Test
            fun `should resolve result for callable type with one result with matching enum variant`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToCallableTypeResults")
                    val callableWithOneResultWithIdenticalEnumVariant =
                        step.findUniqueDeclarationOrFail<SmlParameter>("callableWithOneResultWithIdenticalEnumVariant")
                    val result =
                        callableWithOneResultWithIdenticalEnumVariant.findUniqueDeclarationOrFail<SmlResult>("result")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(8)

                    val declaration = references[5].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(result)
                }

            @Test
            fun `should resolve result of callable type with multiple results`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToCallableTypeResults")
                val result1 = step.findUniqueDeclarationOrFail<SmlResult>("result1")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(8)

                val declaration = references[7].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(result1)
            }

            @Test
            fun `should resolve result of function with one result without matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToFunctionResults")
                    val globalFunctionResultInSameFile =
                        findUniqueDeclarationOrFail<SmlResult>("globalFunctionResultInSameFile")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(6)

                    val declaration = references[1].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(globalFunctionResultInSameFile)
                }

            @Test
            fun `should resolve member for function with one result with matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToFunctionResults")
                    val classForResultMemberAccess = findUniqueDeclarationOrFail<SmlClass>("ClassForResultMemberAccess")
                    val result = classForResultMemberAccess.findUniqueDeclarationOrFail<SmlAttribute>("result")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(6)

                    val declaration = references[3].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(result)
                }

            @Test
            fun `should resolve result of function with multiple results`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToFunctionResults")
                val globalFunctionWithTwoResults =
                    findUniqueDeclarationOrFail<SmlFunction>("globalFunctionWithTwoResults")
                val result1 = globalFunctionWithTwoResults.findUniqueDeclarationOrFail<SmlResult>("result1")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(6)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(result1)
            }

            @Test
            fun `should resolve result of lambda with one result without matching member`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToLambdaResults")
                val singleResult = step.findUniqueDeclarationOrFail<SmlLambdaYield>("singleResult")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(7)

                val declaration = references[2].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(singleResult)
            }

            @Test
            fun `should resolve member for lambda with one result with matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToLambdaResults")
                    val classForResultMemberAccess = findUniqueDeclarationOrFail<SmlClass>("ClassForResultMemberAccess")
                    val result = classForResultMemberAccess.findUniqueDeclarationOrFail<SmlAttribute>("result")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(7)

                    val declaration = references[4].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(result)
                }

            @Test
            fun `should resolve result of lambda with multiple results`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToLambdaResults")
                val result1 = step.findUniqueDeclarationOrFail<SmlLambdaYield>("result1")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(7)

                val declaration = references[6].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(result1)
            }

            @Test
            fun `should resolve result of workflow step with one result without matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToWorkflowStepResults")
                    val stepResultInSameFile = findUniqueDeclarationOrFail<SmlResult>("stepResultInSameFile")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(6)

                    val declaration = references[1].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(stepResultInSameFile)
                }

            @Test
            fun `should resolve member for workflow step with one result with matching member`() =
                withResource(REFERENCE) {
                    val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToWorkflowStepResults")
                    val classForResultMemberAccess = findUniqueDeclarationOrFail<SmlClass>("ClassForResultMemberAccess")
                    val result = classForResultMemberAccess.findUniqueDeclarationOrFail<SmlAttribute>("result")

                    val references = step.descendants<SmlReference>().toList()
                    references.shouldHaveSize(6)

                    val declaration = references[3].declaration
                    declaration.shouldBeResolved()
                    declaration.shouldBe(result)
                }

            @Test
            fun `should resolve result of workflow step with multiple results`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("referencesToWorkflowStepResults")
                val stepInSameFileWithTwoResults =
                    findUniqueDeclarationOrFail<SmlWorkflowStep>("stepWithTwoResults")
                val result1 = stepInSameFileWithTwoResults.findUniqueDeclarationOrFail<SmlResult>("result1")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(6)

                val declaration = references[5].declaration
                declaration.shouldBeResolved()
                declaration.shouldBe(result1)
            }
        }
    }

    @Nested
    inner class TypeArgument {

        @Test
        fun `should resolve type parameter in used class in same file`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)

                val typeParameterInSameFile =
                    findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_CLASS_IN_SAME_FILE")

                val referencedTypeParameter = typeArguments[0].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.shouldBe(typeParameterInSameFile)
            }

        @Test
        fun `should resolve type parameter in used enum variant in same file`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)

                val typeParameterInSameFile =
                    findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_ENUM_VARIANT_IN_SAME_FILE")

                val referencedTypeParameter = typeArguments[1].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.shouldBe(typeParameterInSameFile)
            }

        @Test
        fun `should resolve type parameter in used function in same file`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)

                val typeParameterInSameFile =
                    findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_FUNCTION_IN_SAME_FILE")

                val referencedTypeParameter = typeArguments[2].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.shouldBe(typeParameterInSameFile)
            }

        @Test
        fun `should resolve type parameter in used declaration in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)

                val referencedTypeParameter = typeArguments[3].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_SAME_PACKAGE")
            }

        @Test
        fun `should resolve type parameter in used declaration that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)

                val referencedTypeParameter = typeArguments[4].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_OTHER_PACKAGE1")
            }

        @Test
        fun `should not resolve type parameter in used declaration that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)
                typeArguments[5].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)
                typeArguments[6].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)
                typeArguments[7].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(11)
                typeArguments[8].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(11)
            typeArguments[9].typeParameter.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not a type parameter`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(11)
            typeArguments[10].typeParameter.shouldNotBeResolved()
        }
    }

    @Nested
    inner class TypeParameterConstraint {

        @Test
        fun `should resolve type parameter in same class`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val testClass = findUniqueDeclarationOrFail<SmlClass>("TestClass")
            val typeParameterConstraints = testClass.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(1)

            val typeParameterInSameDeclaration =
                findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_SAME_CLASS")

            val referencedTypeParameter = typeParameterConstraints[0].leftOperand
            referencedTypeParameter.shouldBeResolved()
            referencedTypeParameter.shouldBe(typeParameterInSameDeclaration)
        }

        @Test
        fun `should resolve type parameter in same enum variant`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val testEnumVariant = findUniqueDeclarationOrFail<SmlEnumVariant>("TestEnumVariant")
            val typeParameterConstraints = testEnumVariant.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(1)

            val typeParameterInSameDeclaration =
                findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_SAME_ENUM_VARIANT")

            val referencedTypeParameter = typeParameterConstraints[0].leftOperand
            referencedTypeParameter.shouldBeResolved()
            referencedTypeParameter.shouldBe(typeParameterInSameDeclaration)
        }

        @Test
        fun `should resolve type parameter in same function`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
            val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)

            val typeParameterInSameDeclaration =
                findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_SAME_FUNCTION")

            val referencedTypeParameter = typeParameterConstraints[0].leftOperand
            referencedTypeParameter.shouldBeResolved()
            referencedTypeParameter.shouldBe(typeParameterInSameDeclaration)
        }

        @Test
        fun `should not resolve type parameter in another declaration in same file`() = withResource(
            TYPE_PARAMETER_CONSTRAINT
        ) {
            val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
            val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[1].leftOperand.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve type parameter in another declaration in same package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
                val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[2].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
                val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[3].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is not imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
                val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[4].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
            val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[5].leftOperand.shouldNotBeResolved()
        }

        @Test
        fun `should not something that is not a type parameter`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val testFunction = findUniqueDeclarationOrFail<SmlFunction>("testFunction")
            val typeParameterConstraints = testFunction.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[6].leftOperand.shouldNotBeResolved()
        }
    }

    @Nested
    inner class Yield {

        @Test
        fun `should resolve result in same workflow step`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)

            val resultsInSameFunction = findUniqueDeclarationOrFail<SmlResult>("resultInSameStep")

            val referencedResult = yields[0].result
            referencedResult.shouldBeResolved()
            referencedResult.shouldBe(resultsInSameFunction)
        }

        @Test
        fun `should not resolve result in another workflow step in same file`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[1].result.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve result in another workflow step in same package`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[2].result.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve result in another workflow step that is imported and in another package`() =
            withResource(YIELD) {
                val yields = this.descendants<SmlYield>().toList()
                yields.shouldHaveSize(7)
                yields[3].result.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve result in another workflow step that is not imported and in another package`() =
            withResource(YIELD) {
                val yields = this.descendants<SmlYield>().toList()
                yields.shouldHaveSize(7)
                yields[4].result.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[5].result.shouldNotBeResolved()
        }

        @Test
        fun `should not something that is not a result`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[6].result.shouldNotBeResolved()
        }
    }

    private fun withResource(
        resourceName: ResourceName,
        lambda: SmlCompilationUnit.() -> Unit
    ) {

        val compilationUnit =
            parseHelper.parseResourceWithContext(
                "scoping/$resourceName/main.test.simpleml",
                listOf(
                    "scoping/$resourceName/externalsInOtherPackage.test.simpleml",
                    "scoping/$resourceName/externalsInSamePackage.test.simpleml",
                )
            ) ?: throw IllegalArgumentException("File is not a compilation unit.")

        compilationUnit.apply(lambda)
    }
}
