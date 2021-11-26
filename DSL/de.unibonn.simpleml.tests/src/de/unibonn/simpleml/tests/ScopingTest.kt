package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.tests.util.ResourceName
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
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

            val annotationInSameFile = this.descendants<SmlAnnotation>().firstOrNull()
            annotationInSameFile.shouldNotBeNull()

            val referencedAnnotation = annotationUses[0].annotation
            referencedAnnotation.eIsProxy().shouldBeFalse()
            referencedAnnotation.shouldBe(annotationInSameFile)
        }

        @Test
        fun `should resolve annotations in same package`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)

            val annotation = annotationUses[1].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInSamePackage")
        }

        @Test
        fun `should resolve annotations in another package if imported`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)

            val annotation = annotationUses[2].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInOtherPackage1")
        }

        @Test
        fun `should not resolve annotations in another package if not imported`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[3].annotation.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve unknown declaration`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[4].annotation.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve something that is not an annotation`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(6)
            annotationUses[5].annotation.eIsProxy().shouldBeTrue()
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should resolve parameter in called function in same file`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val parameterInSameFile = this.descendants<SmlParameter>().find { it.name == "parameterInSameFile" }
                parameterInSameFile.shouldNotBeNull()

                val referencedParameter = arguments[0].parameter
                referencedParameter.eIsProxy().shouldBeFalse()
                referencedParameter.shouldBe(parameterInSameFile)
            }

        @Test
        fun `should resolve parameter in called function in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val referencedParameter = arguments[1].parameter
                referencedParameter.eIsProxy().shouldBeFalse()
                referencedParameter.name.shouldBe("parameterInSamePackage")
            }

        @Test
        fun `should resolve parameter in called function that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)

                val referencedParameter = arguments[2].parameter
                referencedParameter.eIsProxy().shouldBeFalse()
                referencedParameter.name.shouldBe("parameterInOtherPackage1")
            }

        @Test
        fun `should not resolve parameter in called function that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[3].parameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve parameter in function other than called one in same package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[4].parameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[5].parameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve parameter in function other than called one that is not imported and in another package`() =
            withResource(ARGUMENT) {
                val arguments = this.descendants<SmlArgument>().toList()
                arguments.shouldHaveSize(9)
                arguments[6].parameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(9)
            arguments[7].parameter.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve something that is not a parameter`() = withResource(ARGUMENT) {
            val arguments = this.descendants<SmlArgument>().toList()
            arguments.shouldHaveSize(9)
            arguments[8].parameter.eIsProxy().shouldBeTrue()
        }
    }

    @Nested
    inner class NamedType {

        @Test
        fun `should resolve class in same file`() = withResource(NAMED_TYPE) {
            val paramClassInSameFile = this.descendants<SmlParameter>().find { it.name == "paramClassInSameFile" }
            paramClassInSameFile.shouldNotBeNull()

            val classInSameFile = this.descendants<SmlClass>().find { it.name == "ClassInSameFile" }
            classInSameFile.shouldNotBeNull()

            val parameterType = paramClassInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.eIsProxy().shouldBeFalse()
            referencedClass.shouldBe(classInSameFile)
        }

        @Test
        fun `should resolve enum in same file`() = withResource(NAMED_TYPE) {
            val paramEnumInSameFile = this.descendants<SmlParameter>().find { it.name == "paramEnumInSameFile" }
            paramEnumInSameFile.shouldNotBeNull()

            val enumInSameFile = this.descendants<SmlEnum>().find { it.name == "EnumInSameFile" }
            enumInSameFile.shouldNotBeNull()

            val parameterType = paramEnumInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.eIsProxy().shouldBeFalse()
            referencedEnum.shouldBe(enumInSameFile)
        }

        @Test
        fun `should resolve interface in same file`() = withResource(NAMED_TYPE) {
            val paramInterfaceInSameFile =
                this.descendants<SmlParameter>().find { it.name == "paramInterfaceInSameFile" }
            paramInterfaceInSameFile.shouldNotBeNull()

            val interfaceInSameFile = this.descendants<SmlInterface>().find { it.name == "InterfaceInSameFile" }
            interfaceInSameFile.shouldNotBeNull()

            val parameterType = paramInterfaceInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeFalse()
            referencedInterface.shouldBe(interfaceInSameFile)
        }

        @Test
        fun `should resolve class in same package`() = withResource(NAMED_TYPE) {
            val paramClassInSamePackage = this.descendants<SmlParameter>().find { it.name == "paramClassInSamePackage" }
            paramClassInSamePackage.shouldNotBeNull()

            val parameterType = paramClassInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.eIsProxy().shouldBeFalse()
            referencedClass.name.shouldBe("ClassInSamePackage")
        }

        @Test
        fun `should resolve enum in same package`() = withResource(NAMED_TYPE) {
            val paramEnumInSamePackage = this.descendants<SmlParameter>().find { it.name == "paramEnumInSamePackage" }
            paramEnumInSamePackage.shouldNotBeNull()

            val parameterType = paramEnumInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.eIsProxy().shouldBeFalse()
            referencedEnum.name.shouldBe("EnumInSamePackage")
        }

        @Test
        fun `should resolve interface in same package`() = withResource(NAMED_TYPE) {
            val paramInterfaceInSamePackage =
                this.descendants<SmlParameter>().find { it.name == "paramInterfaceInSamePackage" }
            paramInterfaceInSamePackage.shouldNotBeNull()

            val parameterType = paramInterfaceInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeFalse()
            referencedInterface.name.shouldBe("InterfaceInSamePackage")
        }

        @Test
        fun `should resolve class in another package if imported`() = withResource(NAMED_TYPE) {
            val paramClassInOtherPackage1 =
                this.descendants<SmlParameter>().find { it.name == "paramClassInOtherPackage1" }
            paramClassInOtherPackage1.shouldNotBeNull()

            val parameterType = paramClassInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.eIsProxy().shouldBeFalse()
            referencedClass.name.shouldBe("ClassInOtherPackage1")
        }

        @Test
        fun `should resolve enum in another package if imported`() = withResource(NAMED_TYPE) {
            val paramEnumInOtherPackage1 =
                this.descendants<SmlParameter>().find { it.name == "paramEnumInOtherPackage1" }
            paramEnumInOtherPackage1.shouldNotBeNull()

            val parameterType = paramEnumInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.eIsProxy().shouldBeFalse()
            referencedEnum.name.shouldBe("EnumInOtherPackage1")
        }

        @Test
        fun `should resolve interface in another package if imported`() = withResource(NAMED_TYPE) {
            val paramInterfaceInOtherPackage1 =
                this.descendants<SmlParameter>().find { it.name == "paramInterfaceInOtherPackage1" }
            paramInterfaceInOtherPackage1.shouldNotBeNull()

            val parameterType = paramInterfaceInOtherPackage1.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeFalse()
            referencedInterface.name.shouldBe("InterfaceInOtherPackage1")
        }

        @Test
        fun `should not resolve class in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramClassInOtherPackage2 =
                this.descendants<SmlParameter>().find { it.name == "paramClassInOtherPackage2" }
            paramClassInOtherPackage2.shouldNotBeNull()

            val parameterType = paramClassInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedClass = parameterType.declaration
            referencedClass.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve enum in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramEnumInOtherPackage2 =
                this.descendants<SmlParameter>().find { it.name == "paramEnumInOtherPackage2" }
            paramEnumInOtherPackage2.shouldNotBeNull()

            val parameterType = paramEnumInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedEnum = parameterType.declaration
            referencedEnum.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve interface in another package if not imported`() = withResource(NAMED_TYPE) {
            val paramInterfaceInOtherPackage2 =
                this.descendants<SmlParameter>().find { it.name == "paramInterfaceInOtherPackage2" }
            paramInterfaceInOtherPackage2.shouldNotBeNull()

            val parameterType = paramInterfaceInOtherPackage2.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should resolve type parameters in same function`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSameFunction =
                this.descendants<SmlParameter>().find { it.name == "paramTypeParameterInSameFunction" }
            paramTypeParameterInSameFunction.shouldNotBeNull()

            val typeParameter =
                this.descendants<SmlTypeParameter>().find { it.name == "TYPE_PARAMETER_IN_SAME_FUNCTION" }
            typeParameter.shouldNotBeNull()

            val parameterType = paramTypeParameterInSameFunction.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedTypeParameter = parameterType.declaration
            referencedTypeParameter.eIsProxy().shouldBeFalse()
            referencedTypeParameter.shouldBe(typeParameter)
        }

        @Test
        fun `should not resolve type parameters in another declaration in same file`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSameFile =
                this.descendants<SmlParameter>().find { it.name == "paramTypeParameterInSameFile" }
            paramTypeParameterInSameFile.shouldNotBeNull()

            val parameterType = paramTypeParameterInSameFile.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve type parameters in another declaration in same package`() = withResource(NAMED_TYPE) {
            val paramTypeParameterInSamePackage =
                this.descendants<SmlParameter>().find { it.name == "paramTypeParameterInSamePackage" }
            paramTypeParameterInSamePackage.shouldNotBeNull()

            val parameterType = paramTypeParameterInSamePackage.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve type parameters in another declaration in another package`() =
            withResource(NAMED_TYPE) {
                val paramTypeParameterInOtherPackage =
                    this.descendants<SmlParameter>().find { it.name == "paramTypeParameterInOtherPackage" }
                paramTypeParameterInOtherPackage.shouldNotBeNull()

                val parameterType = paramTypeParameterInOtherPackage.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                val referencedInterface = parameterType.declaration
                referencedInterface.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(NAMED_TYPE) {
            val paramUnresolvedNamedTypeDeclaration =
                this.descendants<SmlParameter>().find { it.name == "paramUnresolvedNamedTypeDeclaration" }
            paramUnresolvedNamedTypeDeclaration.shouldNotBeNull()

            val parameterType = paramUnresolvedNamedTypeDeclaration.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve something that is not a named type declaration`() = withResource(NAMED_TYPE) {
            val paramNotANamedTypeDeclaration =
                this.descendants<SmlParameter>().find { it.name == "paramNotANamedTypeDeclaration" }
            paramNotANamedTypeDeclaration.shouldNotBeNull()

            val parameterType = paramNotANamedTypeDeclaration.type
            parameterType.shouldBeInstanceOf<SmlNamedType>()

            val referencedInterface = parameterType.declaration
            referencedInterface.eIsProxy().shouldBeTrue()
        }

        @Nested
        inner class MemberType {
            @Test
            fun `should resolve class within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramClassInClassInSameFile" }
                paramClassInClassInSameFile.shouldNotBeNull()

                val classInSameFile = this.descendants<SmlClass>().find { it.name == "ClassInClassInSameFile" }
                classInSameFile.shouldNotBeNull()

                val parameterType = paramClassInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedClass = parameterType.member.declaration
                referencedClass.eIsProxy().shouldBeFalse()
                referencedClass.shouldBe(classInSameFile)
            }

            @Test
            fun `should resolve enum within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramEnumInClassInSameFile" }
                paramEnumInClassInSameFile.shouldNotBeNull()

                val enumInSameFile = this.descendants<SmlEnum>().find { it.name == "EnumInClassInSameFile" }
                enumInSameFile.shouldNotBeNull()

                val parameterType = paramEnumInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedEnum = parameterType.member.declaration
                referencedEnum.eIsProxy().shouldBeFalse()
                referencedEnum.shouldBe(enumInSameFile)
            }

            @Test
            fun `should resolve interface within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramInterfaceInClassInSameFile" }
                paramInterfaceInClassInSameFile.shouldNotBeNull()

                val interfaceInSameFile = this.descendants<SmlInterface>().find { it.name == "InterfaceInClassInSameFile" }
                interfaceInSameFile.shouldNotBeNull()

                val parameterType = paramInterfaceInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedInterface = parameterType.member.declaration
                referencedInterface.eIsProxy().shouldBeFalse()
                referencedInterface.shouldBe(interfaceInSameFile)
            }

            @Test
            fun `should not resolve class within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramClassInInterfaceInSameFile" }
                paramClassInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramClassInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve enum within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramEnumInInterfaceInSameFile" }
                paramEnumInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramEnumInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve interface within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramInterfaceInInterfaceInSameFile" }
                paramInterfaceInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramInterfaceInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve class within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedClassInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedClassInClassInSameFile" }
                paramUnqualifiedClassInClassInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedClassInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve enum within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedEnumInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedEnumInClassInSameFile" }
                paramUnqualifiedEnumInClassInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedEnumInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve interface within class with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedInterfaceInClassInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedInterfaceInClassInSameFile" }
                paramUnqualifiedInterfaceInClassInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedInterfaceInClassInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve class within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedClassInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedClassInInterfaceInSameFile" }
                paramUnqualifiedClassInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedClassInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve enum within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedEnumInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedEnumInInterfaceInSameFile" }
                paramUnqualifiedEnumInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedEnumInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve interface within interface with unqualified access`() = withResource(NAMED_TYPE) {
                val paramUnqualifiedInterfaceInInterfaceInSameFile =
                    this.descendants<SmlParameter>().find { it.name == "paramUnqualifiedInterfaceInInterfaceInSameFile" }
                paramUnqualifiedInterfaceInInterfaceInSameFile.shouldNotBeNull()

                val parameterType = paramUnqualifiedInterfaceInInterfaceInSameFile.type
                parameterType.shouldBeInstanceOf<SmlNamedType>()

                parameterType.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should resolve inherited class within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInSuperClass =
                    this.descendants<SmlParameter>().find { it.name == "paramClassInSuperClass" }
                paramClassInSuperClass.shouldNotBeNull()

                val classInSameFile = this.descendants<SmlClass>().find { it.name == "ClassInSuperClass" }
                classInSameFile.shouldNotBeNull()

                val parameterType = paramClassInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedClass = parameterType.member.declaration
                referencedClass.eIsProxy().shouldBeFalse()
                referencedClass.shouldBe(classInSameFile)
            }

            @Test
            fun `should resolve inherited enum within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInSuperClass =
                    this.descendants<SmlParameter>().find { it.name == "paramEnumInSuperClass" }
                paramEnumInSuperClass.shouldNotBeNull()

                val enumInSameFile = this.descendants<SmlEnum>().find { it.name == "EnumInSuperClass" }
                enumInSameFile.shouldNotBeNull()

                val parameterType = paramEnumInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedEnum = parameterType.member.declaration
                referencedEnum.eIsProxy().shouldBeFalse()
                referencedEnum.shouldBe(enumInSameFile)
            }

            @Test
            fun `should resolve inherited interface within class with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInSuperClass =
                    this.descendants<SmlParameter>().find { it.name == "paramInterfaceInSuperClass" }
                paramInterfaceInSuperClass.shouldNotBeNull()

                val interfaceInSameFile = this.descendants<SmlInterface>().find { it.name == "InterfaceInSuperClass" }
                interfaceInSameFile.shouldNotBeNull()

                val parameterType = paramInterfaceInSuperClass.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                val referencedInterface = parameterType.member.declaration
                referencedInterface.eIsProxy().shouldBeFalse()
                referencedInterface.shouldBe(interfaceInSameFile)
            }

            @Test
            fun `should not resolve inherited class within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramClassInSuperInterface =
                    this.descendants<SmlParameter>().find { it.name == "paramClassInSuperInterface" }
                paramClassInSuperInterface.shouldNotBeNull()

                val parameterType = paramClassInSuperInterface.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve inherited enum within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramEnumInSuperInterface =
                    this.descendants<SmlParameter>().find { it.name == "paramEnumInSuperInterface" }
                paramEnumInSuperInterface.shouldNotBeNull()

                val parameterType = paramEnumInSuperInterface.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }

            @Test
            fun `should not resolve inherited interface within interface with qualified access`() = withResource(NAMED_TYPE) {
                val paramInterfaceInSuperInterface =
                    this.descendants<SmlParameter>().find { it.name == "paramInterfaceInSuperInterface" }
                paramInterfaceInSuperInterface.shouldNotBeNull()

                val parameterType = paramInterfaceInSuperInterface.type
                parameterType.shouldBeInstanceOf<SmlMemberType>()

                parameterType.member.declaration.eIsProxy().shouldBeTrue()
            }
        }
    }

//    @Nested
//    inner class Reference {
//        @Test
//        fun `should not resolve unknown declaration`() = withResource(REFERENCE) {
//            val yields = this.descendants<SmlYield>().toList()
//            yields.shouldHaveSize(7)
//            yields[5].result.eIsProxy().shouldBeTrue()
//        }
//    }

    @Nested
    inner class TypeArgument {

        @Test
        fun `should resolve type parameter in used declaration in same file`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val typeParameterInSameFile =
                    this.descendants<SmlTypeParameter>().find { it.name == "TYPE_PARAMETER_IN_SAME_FILE" }
                typeParameterInSameFile.shouldNotBeNull()

                val referencedTypeParameter = typeArguments[0].typeParameter
                referencedTypeParameter.eIsProxy().shouldBeFalse()
                referencedTypeParameter.shouldBe(typeParameterInSameFile)
            }

        @Test
        fun `should resolve type parameter in used declaration in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val referencedTypeParameter = typeArguments[1].typeParameter
                referencedTypeParameter.eIsProxy().shouldBeFalse()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_SAME_PACKAGE")
            }

        @Test
        fun `should resolve type parameter in used declaration that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)

                val referencedTypeParameter = typeArguments[2].typeParameter
                referencedTypeParameter.eIsProxy().shouldBeFalse()
                referencedTypeParameter.name.shouldBe("TYPE_PARAMETER_IN_OTHER_PACKAGE1")
            }

        @Test
        fun `should not resolve type parameter in used declaration that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[3].typeParameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one in same package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[4].typeParameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[5].typeParameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve type parameter in declaration other than used one that is not imported and in another package`() =
            withResource(TYPE_ARGUMENT) {
                val typeArguments = this.descendants<SmlTypeArgument>().toList()
                typeArguments.shouldHaveSize(9)
                typeArguments[6].typeParameter.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(9)
            typeArguments[7].typeParameter.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve something that is not a type parameter`() = withResource(TYPE_ARGUMENT) {
            val typeArguments = this.descendants<SmlTypeArgument>().toList()
            typeArguments.shouldHaveSize(9)
            typeArguments[8].typeParameter.eIsProxy().shouldBeTrue()
        }
    }

    @Nested
    inner class TypeParameterConstraint {

        @Test
        fun `should resolve type parameter in same declaration`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)

            val typeParameterInSameDeclaration =
                this.descendants<SmlTypeParameter>().find { it.name == "TYPE_PARAMETER_IN_SAME_FUNCTION" }
            typeParameterInSameDeclaration.shouldNotBeNull()

            val referencedTypeParameter = typeParameterConstraints[0].leftOperand
            referencedTypeParameter.eIsProxy().shouldBeFalse()
            referencedTypeParameter.shouldBe(typeParameterInSameDeclaration)
        }

        @Test
        fun `should not resolve type parameter in another declaration in same file`() = withResource(
            TYPE_PARAMETER_CONSTRAINT
        ) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[1].leftOperand.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve type parameter in another declaration in same package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[2].leftOperand.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[3].leftOperand.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve type parameter in another declaration that is not imported and in another package`() =
            withResource(TYPE_PARAMETER_CONSTRAINT) {
                val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
                typeParameterConstraints.shouldHaveSize(7)
                typeParameterConstraints[4].leftOperand.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[5].leftOperand.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not something that is not a type parameter`() = withResource(TYPE_PARAMETER_CONSTRAINT) {
            val typeParameterConstraints = this.descendants<SmlTypeParameterConstraint>().toList()
            typeParameterConstraints.shouldHaveSize(7)
            typeParameterConstraints[6].leftOperand.eIsProxy().shouldBeTrue()
        }
    }

    @Nested
    inner class Yield {

        @Test
        fun `should resolve result in same workflow step`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)

            val resultsInSameFunction = this.descendants<SmlResult>().find { it.name == "resultInSameStep" }
            resultsInSameFunction.shouldNotBeNull()

            val referencedResult = yields[0].result
            referencedResult.eIsProxy().shouldBeFalse()
            referencedResult.shouldBe(resultsInSameFunction)
        }

        @Test
        fun `should not resolve result in another workflow step in same file`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[1].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve result in another workflow step in same package`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[2].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve result in another workflow step that is imported and in another package`() =
            withResource(YIELD) {
                val yields = this.descendants<SmlYield>().toList()
                yields.shouldHaveSize(7)
                yields[3].result.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve result in another workflow step that is not imported and in another package`() =
            withResource(YIELD) {
                val yields = this.descendants<SmlYield>().toList()
                yields.shouldHaveSize(7)
                yields[4].result.eIsProxy().shouldBeTrue()
            }

        @Test
        fun `should not resolve unknown declaration`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[5].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not something that is not a result`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(7)
            yields[6].result.eIsProxy().shouldBeTrue()
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
