package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.tests.util.ResourceName
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
            annotationUses.shouldHaveSize(4)

            val annotationInSameFile = this.descendants<SmlAnnotation>().firstOrNull()
            annotationInSameFile.shouldNotBeNull()

            val referencedAnnotation = annotationUses[0].annotation
            referencedAnnotation.eIsProxy().shouldBeFalse()
            referencedAnnotation.shouldBe(annotationInSameFile)
        }

        @Test
        fun `should resolve annotations in same package`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val annotation = annotationUses[1].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInSamePackage")
        }

        @Test
        fun `should resolve annotations in another package when imported`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)

            val annotation = annotationUses[2].annotation
            annotation.eIsProxy().shouldBeFalse()
            annotation.name.shouldBe("AnnotationInOtherPackage")
        }

        @Test
        fun `should not resolve other annotations`() = withResource(ANNOTATION_USE) {
            val annotationUses = this.descendants<SmlAnnotationUse>().toList()
            annotationUses.shouldHaveSize(4)
            annotationUses[3].annotation.eIsProxy().shouldBeTrue()
        }
    }

    @Nested
    inner class Yield {

        @Test
        fun `should resolve result in same function`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(5)

            val resultsInSameFunction = this.descendants<SmlResult>().find { it.name == "resultInSameFunction" }
            resultsInSameFunction.shouldNotBeNull()

            val referencedResult = yields[0].result
            referencedResult.eIsProxy().shouldBeFalse()
            referencedResult.shouldBe(resultsInSameFunction)
        }

        @Test
        fun `should not resolve result in another function in same file`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(5)
            yields[1].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve result in another function in same package`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(5)
            yields[2].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve result in another function in another package`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(5)
            yields[3].result.eIsProxy().shouldBeTrue()
        }

        @Test
        fun `should not resolve other results`() = withResource(YIELD) {
            val yields = this.descendants<SmlYield>().toList()
            yields.shouldHaveSize(5)
            yields[4].result.eIsProxy().shouldBeTrue()
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
                    "languageTests/scoping/externalsInOtherPackage.test.simpleml",
                    "languageTests/scoping/externalsInSamePackage.test.simpleml",
                )
            ) ?: throw IllegalArgumentException("File is not a compilation unit.")

        compilationUnit.apply(lambda)
    }
}
