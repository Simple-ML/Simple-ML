@file:Suppress("ClassName")

package de.unibonn.simpleml.test.naming

import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.naming.toQualifiedName
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.test.SimpleMLInjectorProvider
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.eclipse.xtext.naming.IQualifiedNameConverter
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class QualifiedNameProviderTest {

    private val factory = SimpleMLPackage.eINSTANCE.simpleMLFactory

    @Nested
    inner class fullyQualifiedName {

        @Test
        fun `should handle declarations with simple names`() {
            val myClass = factory.createSmlClass().apply {
                name = "MyClass"
            }

            factory.createSmlCompilationUnit().apply {
                name = "tests"
                members += myClass
            }

            myClass.fullyQualifiedName() shouldBe "tests.MyClass".toQualifiedName()
        }

        @Test
        fun `should handle declarations with escaped names`() {
            val myClass = factory.createSmlClass().apply {
                name = "`MyClass`"
            }

            factory.createSmlCompilationUnit().apply {
                name = "`tests`"
                members += myClass
            }

            myClass.fullyQualifiedName() shouldBe "`tests`.`MyClass`".toQualifiedName()
        }
    }

    @Nested
    inner class toQualifiedName {

        @Test
        fun `should convert string to qualified name`() {
            val qualifiedName = "tests.MyClass".toQualifiedName()

            qualifiedName.segmentCount shouldBe 2
            qualifiedName.getSegment(0) shouldBe "tests"
            qualifiedName.getSegment(1) shouldBe "MyClass"
        }
    }
}
