@file:Suppress("ClassName")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.serializer.SerializeToStringResult
import de.unibonn.simpleml.serializer.serializeToString
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.withSystemLineBreaks
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SerializerExtensionsTest {

    private val factory = SimpleMLPackage.eINSTANCE.simpleMLFactory

    @Nested
    inner class prettyPrint {

        @Test
        fun `should serialize and format a complete EMF model`() {
            val compilationUnit = factory.createSmlCompilationUnit().apply {
                members += factory.createSmlPackage().apply {
                    annotationUseHolder = factory.createSmlAnnotationUseHolder()
                    name = "tests"
                    members += factory.createSmlClass().apply {
                        annotationUseHolder = factory.createSmlAnnotationUseHolder()
                        name = "MyClass"
                    }
                }
            }

            val result = compilationUnit.serializeToString()
            result.shouldBeInstanceOf<SerializeToStringResult.Formatted>()
            result.code shouldBe (
                """
                |package tests
                |
                |class MyClass
                |
            """.trimMargin().withSystemLineBreaks()
                )
        }

        @Test
        fun `should serialize but not format a subtree of the EMF model`() {
            val `class` = factory.createSmlClass().apply {
                annotationUseHolder = factory.createSmlAnnotationUseHolder()
                name = "MyClass"
                body = factory.createSmlClassBody().apply {
                    members += factory.createSmlAttribute().apply {
                        annotationUseHolder = factory.createSmlAnnotationUseHolder()
                        name = "myAttribute"
                    }
                }
            }

            val result = `class`.serializeToString()
            result.shouldBeInstanceOf<SerializeToStringResult.Unformatted>()
            result.code shouldBe "class MyClass { attr myAttribute }"
        }

        @Test
        fun `should throw a RuntimeException for an incorrect EMF model`() {
            val compilationUnit = factory.createSmlCompilationUnit().apply {
                // Missing SmlAnnotationUseHolder
                members += factory.createSmlPackage().apply {
                    name = "tests"
                }
            }

            val result = compilationUnit.serializeToString()
            result.shouldBeInstanceOf<SerializeToStringResult.WrongEmfModelFailure>()
        }
    }
}
