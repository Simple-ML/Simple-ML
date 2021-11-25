package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.tests.util.ResourceName
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class ScopingTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Nested
    inner class AnnotationUse {

        @Test
        fun `should resolve annotations in same file`() = withResource("annotationUse") {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val localAnnotations = this.descendants<SmlAnnotation>().toList()
            localAnnotations.shouldHaveSize(1)

            val referencedAnnotation = annotationUses[0].annotation
            referencedAnnotation.eIsProxy().shouldBeFalse()
            referencedAnnotation.shouldBe(localAnnotations[0])
        }

        @Test
        fun `should resolve annotations in same package`() = withResource("annotationUse") {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val annotation = annotationUses[1].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInSamePackage")
        }

        @Test
        fun `should resolve annotations in other package when imported`() = withResource("annotationUse") {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val annotation = annotationUses[2].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInOtherPackage")
        }

        @Test
        fun `should not resolve other annotations`() = withResource("annotationUse") {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val annotation = annotationUses[3].annotation
            annotation.eIsProxy().shouldBeTrue()
        }
    }

    private fun withResource(
        resourceName: ResourceName,
        lambda: SmlCompilationUnit.() -> Unit
    ) {
        val compilationUnit =
            parseHelper.parseResourceWithContext(
                "languageTests/scoping/$resourceName.test.simpleml",
                listOf(
                    "languageTests/scoping/externalsInOtherPackage.stub.simpleml",
                    "languageTests/scoping/externalsInSamePackage.stub.simpleml",
                )
            ) ?: throw IllegalArgumentException("File is not a compilation unit.")

        compilationUnit.apply(lambda)
    }
}
