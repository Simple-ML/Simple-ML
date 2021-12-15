package de.unibonn.simpleml.conversion

import com.google.inject.Inject
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToFormattedString
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLSTRINGValueConverterTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Inject
    private lateinit var stringValueConverter: SimpleMLSTRINGValueConverter

    @Test
    fun `should unescape opening curly brace (simple)`() {
        stringValueConverter.toValue("\"\\{\"", null) shouldBe "{"
    }

    @Test
    fun `should unescape opening curly brace`() {
        val compilationUnit = parseHelper.parseResource("conversion/stringValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("escapedOpeningBrace")

        val strings = workflow.descendants<SmlString>().toList()
        strings.shouldHaveSize(1)
        strings[0].value shouldBe "{"
    }

    @Test
    fun `should escape opening curly brace (simple)`() {
        stringValueConverter.toString("{") shouldBe "\"\\{\""
    }

    @Test
    fun `should escape opening curly brace`() {
        val compilationUnit = parseHelper.parseResource("conversion/stringValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("unescapedOpeningBrace")

        val strings = workflow.descendants<SmlString>().toList()
        strings.shouldHaveSize(1)

        val result = strings[0].serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "\"\\{\""
    }

    @Test
    fun `should unescape single quote (simple)`() {
        stringValueConverter.toValue("\"\\'\"", null) shouldBe "'"
    }

    @Test
    fun `should unescape single quote`() {
        val compilationUnit = parseHelper.parseResource("conversion/stringValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("escapedSingleQuote")

        val strings = workflow.descendants<SmlString>().toList()
        strings.shouldHaveSize(1)
        strings[0].value shouldBe "'"
    }

    @Test
    fun `should not escape single quote (simple)`() {
        stringValueConverter.toString("'") shouldBe "\"'\""
    }

    @Test
    fun `should not escape single quote`() {
        val compilationUnit = parseHelper.parseResource("conversion/stringValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("unescapedSingleQuote")

        val strings = workflow.descendants<SmlString>().toList()
        strings.shouldHaveSize(1)

        val result = strings[0].serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "\"'\""
    }
}
