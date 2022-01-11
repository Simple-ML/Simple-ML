package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInt
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlString
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlString
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class ExpressionInterpreterTest {

    private val factory = SimpleMLFactory.eINSTANCE

    @Test
    fun `should return new boolean literal for boolean literal`() {
        val testData = createSmlBoolean(true)

        testData.toConstantExpression().asClue {
            it shouldNotBe testData
            it.shouldBeInstanceOf<SmlBoolean>()
            it.isTrue shouldBe true
        }
    }

    @Test
    fun `should return new float literal for float literal`() {
        val testData = createSmlFloat(1.0)

        testData.toConstantExpression().asClue {
            it shouldNotBe testData
            it.shouldBeInstanceOf<SmlFloat>()
            it.value shouldBe 1.0
        }
    }

    @Test
    fun `should return new int literal for int literal`() {
        val testData = createSmlInt(1)

        testData.toConstantExpression().asClue {
            it shouldNotBe testData
            it.shouldBeInstanceOf<SmlInt>()
            it.value shouldBe 1
        }
    }

    @Test
    fun `should return new null literal for null literal`() {
        val testData = createSmlNull()

        testData.toConstantExpression().asClue {
            it shouldNotBe testData
            it.shouldBeInstanceOf<SmlNull>()
        }
    }

    @Test
    fun `should return new string literal for string literal`() {
        val testData = createSmlString("test")

        testData.toConstantExpression().asClue {
            it shouldNotBe testData
            it.shouldBeInstanceOf<SmlString>()
            it.value shouldBe "test"
        }
    }

    @Test
    fun `should return string literal for template string start`() {
        val testData = factory.createSmlTemplateStringStart().apply {
            value = "test"
        }

        testData.toConstantExpression().asClue {
            it.shouldBeInstanceOf<SmlString>()
            it.value shouldBe "test"
        }
    }

    @Test
    fun `should return string literal for template string inner`() {
        val testData = factory.createSmlTemplateStringInner().apply {
            value = "test"
        }

        testData.toConstantExpression().asClue {
            it.shouldBeInstanceOf<SmlString>()
            it.value shouldBe "test"
        }
    }

    @Test
    fun `should return string literal for template string end`() {
        val testData = factory.createSmlTemplateStringEnd().apply {
            value = "test"
        }

        testData.toConstantExpression().asClue {
            it.shouldBeInstanceOf<SmlString>()
            it.value shouldBe "test"
        }
    }
}
