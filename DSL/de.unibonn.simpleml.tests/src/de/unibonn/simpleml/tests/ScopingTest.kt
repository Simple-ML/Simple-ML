package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlInterface
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
import de.unibonn.simpleml.tests.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.tests.assertions.shouldBeResolved
import de.unibonn.simpleml.tests.assertions.shouldNotBeResolved
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.tests.util.ResourceName
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
        fun `should resolve parameter in called function in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val parameterInSameFile = findUniqueDeclarationOrFail<SmlParameter>("parameterInSameFile")

                val referencedParameter = arguments[0].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.shouldBe(parameterInSameFile)
            }

        @Test
        fun `should resolve parameter in called function in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val referencedParameter = arguments[1].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.name.shouldBe("parameterInSamePackage")
            }

        @Test
        fun `should resolve parameter in called function that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val referencedParameter = arguments[2].parameter
                referencedParameter.shouldBeResolved()
                referencedParameter.name.shouldBe("parameterInOtherPackage1")
            }

        @Test
        fun `should not resolve parameter in called function that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[3].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[4].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[5].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[6].parameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(9)
            arguments[7].parameter.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not a parameter`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(9)
            arguments[8].parameter.shouldNotBeResolved()
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
        fun `should resolve interface in same file`() = withResource(NAMED_TYPE) {
            val paramInterfaceInSameFile = findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInSameFile")
            val interfaceInSameFile = findUniqueDeclarationOrFail<SmlInterface>("InterfaceInSameFile")

            val parameterType = paramInterfaceInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldBeResolved()
            referencedInterface.shouldBe(interfaceInSameFile)
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
        fun `should resolve interface in same package`() = withResource(NAMED_TYPE) {
            val paramInterfaceInSamePackage = findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInSamePackage")

            val parameterType = paramInterfaceInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldBeResolved()
            referencedInterface.name.shouldBe("InterfaceInSamePackage")
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
        fun `should resolve interface in another package if imported`() = withResource(NAMED_TYPE) {
            val paramInterfaceInOtherPackage1 =
                findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInOtherPackage1")

            val parameterType = paramInterfaceInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldBeResolved()
            referencedInterface.name.shouldBe("InterfaceInOtherPackage1")
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
        fun `should not resolve interface in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramInterfaceInOtherPackage2 =
                findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInOtherPackage2")

            val parameterType = paramInterfaceInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.shouldNotBeResolved()
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
            fun `should resolve interface within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInClassInSameFile")
                val interfaceInSameFile = findUniqueDeclarationOrFail<SmlInterface>("InterfaceInClassInSameFile")

                val parameterType = paramInterfaceInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedInterface = parameterType.member.declaration
                referencedInterface.shouldBeResolved()
                referencedInterface.shouldBe(interfaceInSameFile)
            }

            @Test
            fun `should not resolve class within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramClassInInterfaceInSameFile")

                val parameterType = paramClassInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve enum within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramEnumInInterfaceInSameFile")

                val parameterType = paramEnumInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve interface within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInInterfaceInSameFile")

                val parameterType = paramInterfaceInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.shouldNotBeResolved()
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
            fun `should not resolve interface within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedInterfaceInClassInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedInterfaceInClassInSameFile")

                val parameterType = paramUnqualifiedInterfaceInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve class within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedClassInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedClassInInterfaceInSameFile")

                val parameterType = paramUnqualifiedClassInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve enum within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedEnumInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedEnumInInterfaceInSameFile")

                val parameterType = paramUnqualifiedEnumInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve interface within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedInterfaceInInterfaceInSameFile =
                    findUniqueDeclarationOrFail<SmlParameter>("paramUnqualifiedInterfaceInInterfaceInSameFile")

                val parameterType = paramUnqualifiedInterfaceInInterfaceInSameFile.type
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

            @Test
            fun `should resolve inherited interface within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInSuperClass = findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInSuperClass")
                val interfaceInSameFile = findUniqueDeclarationOrFail<SmlInterface>("InterfaceInSuperClass")

                val parameterType = paramInterfaceInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedInterface = parameterType.member.declaration
                referencedInterface.shouldBeResolved()
                referencedInterface.shouldBe(interfaceInSameFile)
            }

            @Test
            fun `should not resolve inherited class within interface with qualified access`() =
                withResource(NAMED_TYPE) {
                    val paramClassInSuperInterface =
                        findUniqueDeclarationOrFail<SmlParameter>("paramClassInSuperInterface")

                    val parameterType = paramClassInSuperInterface.type
                    parameterType.shouldBeInstanceOf<SmlMemberType>()

                    parameterType.member.declaration.shouldNotBeResolved()
                }

            @Test
            fun `should not resolve inherited enum within interface with qualified access`() =
                withResource(NAMED_TYPE) {
                    val paramEnumInSuperInterface =
                        findUniqueDeclarationOrFail<SmlParameter>("paramEnumInSuperInterface")

                    val parameterType = paramEnumInSuperInterface.type
                    parameterType.shouldBeInstanceOf<SmlMemberType>()

                    parameterType.member.declaration.shouldNotBeResolved()
                }

            @Test
            fun `should not resolve inherited interface within interface with qualified access`() =
                withResource(NAMED_TYPE) {
                    val paramInterfaceInSuperInterface =
                        findUniqueDeclarationOrFail<SmlParameter>("paramInterfaceInSuperInterface")

                    val parameterType = paramInterfaceInSuperInterface.type
                    parameterType.shouldBeInstanceOf<SmlMemberType>()

                    parameterType.member.declaration.shouldNotBeResolved()
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
        fun `should resolve interface in same file`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToInterfaces")
            val interfaceInSameFile = findUniqueDeclarationOrFail<SmlInterface>("InterfaceInSameFile")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[0].declaration
            declaration.shouldBeResolved()
            declaration.shouldBe(interfaceInSameFile)
        }

        @Test
        fun `should resolve interface in same package`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToInterfaces")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[1].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("InterfaceInSamePackage")
        }

        @Test
        fun `should resolve interface in another package if imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToInterfaces")

            val references = step.descendants<SmlReference>().toList()
            references.shouldHaveSize(4)

            val declaration = references[2].declaration
            declaration.shouldBeResolved()
            declaration.name.shouldBe("InterfaceInOtherPackage1")
        }

        @Test
        fun `should not resolve interface in another package if not imported`() = withResource(REFERENCE) {
            val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("directReferencesToInterfaces")

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
            fun `should not resolve class members with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToClassMembers")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(7)
                references.forEachAsClue {
                    it.declaration.shouldNotBeResolved()
                }
            }

            @Test
            fun `should not resolve enum instances with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToEnumInstances")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(1)
                references[0].declaration.shouldNotBeResolved()
            }

            @Test
            fun `should not resolve interface members with unqualified access`() = withResource(REFERENCE) {
                val step = findUniqueDeclarationOrFail<SmlWorkflowStep>("unqualifiedReferencesToInterfaceMembers")

                val references = step.descendants<SmlReference>().toList()
                references.shouldHaveSize(7)
                references.forEachAsClue {
                    it.declaration.shouldNotBeResolved()
                }
            }
        }
    }

    @Nested
    inner class TypeArgument {

        @Test
        fun `should resolve type parameter in used declaration in same file`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val typeParameterInSameFile =
                    findUniqueDeclarationOrFail<SmlTypeParameter>("TYPE_PARAMETER_IN_SAME_FILE")

                val referencedTypeParameter = typeArguments[0].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.shouldBe(typeParameterInSameFile)
            }

        @Test
        fun `should resolve type parameter in used declaration in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val referencedTypeParameter = typeArguments[1].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_SAME_PACKAGE")
            }

        @Test
        fun `should resolve type parameter in used declaration that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val referencedTypeParameter = typeArguments[2].typeParameter
                referencedTypeParameter.shouldBeResolved()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_OTHER_PACKAGE1")
            }

        @Test
        fun `should not resolve type parameter in used declaration that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[3].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[4].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[5].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[6].typeParameter.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(9)
            typeArguments[7].typeParameter.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve something that is not a type parameter`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(9)
            typeArguments[8].typeParameter.shouldNotBeResolved()
        }
    }

    @Nested
    inner class TypeParameterConstraint {

        @Test
        fun `should resolve type parameter in same declaration`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
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
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[1].leftOperand.shouldNotBeResolved()
        }

        @Test
        fun `should not resolve type parameter in another declaration in same package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[2].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[3].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is not imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[4].leftOperand.shouldNotBeResolved()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[5].leftOperand.shouldNotBeResolved()
        }

        @Test
        fun `should not something that is not a type parameter`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
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
                "languageTests/scoping/$resourceName/main.test.simpleml",
                listOf(
                    "languageTests/scoping/$resourceName/externalsInOtherPackage.test.simpleml",
                    "languageTests/scoping/$resourceName/externalsInSamePackage.test.simpleml",
                )
            ) ?: throw IllegalArgumentException("File is not a compilation unit.")

        compilationUnit.apply(lambda)
    }
}
