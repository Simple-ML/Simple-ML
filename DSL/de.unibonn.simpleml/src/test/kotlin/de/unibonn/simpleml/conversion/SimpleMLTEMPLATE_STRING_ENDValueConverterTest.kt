@file:Suppress("ClassName")

package de.unibonn.simpleml.conversion

import com.google.inject.Inject
import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.emf.createSmlDummyResource
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlTemplateString
import de.unibonn.simpleml.emf.smlExpressionStatement
import de.unibonn.simpleml.emf.smlWorkflow
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToFormattedString
import de.unibonn.simpleml.simpleML.SmlTemplateStringPart
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLTEMPLATE_STRING_ENDValueConverterTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Inject
    private lateinit var templateStringEndValueConverter: SimpleMLTEMPLATE_STRING_ENDValueConverter

    @Nested
    inner class toValue {
        @Test
        fun `should remove delimiters (direct converter call)`() {
            templateStringEndValueConverter.toValue("}}end\"", null) shouldBe "end"
        }

        @Test
        fun `should remove delimiters (file)`() {
            val compilationUnit = parseHelper.parseResource(
                "conversion/templateStringPartValueConverter.smltest"
            )
            compilationUnit.shouldNotBeNull()

            val stringTemplateParts = compilationUnit.descendants<SmlTemplateStringPart>().toList()
            stringTemplateParts.shouldHaveSize(3)

            stringTemplateParts[2].value shouldBe "end"
        }
    }

    @Nested
    inner class toString {
        @Test
        fun `should add delimiters (direct converter call)`() {
            templateStringEndValueConverter.toString("end") shouldBe "}}end\""
        }

        @Test
        fun `should add delimiters (creator)`() {
            val stringTemplate = createSmlTemplateString(
                listOf("start", "end"),
                listOf(createSmlNull())
            )

            createSmlDummyResource("test", FileExtension.TEST) {
                smlWorkflow("test") {
                    smlExpressionStatement(stringTemplate)
                }
            }

            val expressions = stringTemplate.expressions
            expressions.shouldHaveSize(3)

            val result = expressions[2].serializeToFormattedString()
            result.shouldBeInstanceOf<SerializationResult.Success>()
            result.code shouldBe "}}end\""
        }
    }
}
