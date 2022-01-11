package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.createSmlBlockLambda
import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlExpressionLambda
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInt
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlParenthesizedExpression
import de.unibonn.simpleml.emf.createSmlPrefixOperation
import de.unibonn.simpleml.emf.createSmlString
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExpressionInterpreterTest {

    private val factory = SimpleMLFactory.eINSTANCE

    @Nested
    inner class BaseCases {

        @Test
        fun `should return new boolean literal for boolean literal`() {
            val testData = createSmlBoolean(true)
            testData.toConstantExpression() shouldBe SmlConstantBoolean(true)
        }

        @Test
        fun `should return new float literal for float literal`() {
            val testData = createSmlFloat(1.0)
            testData.toConstantExpression() shouldBe SmlConstantFloat(1.0)
        }

        @Test
        fun `should return new int literal for int literal`() {
            val testData = createSmlInt(1)
            testData.toConstantExpression() shouldBe SmlConstantInt(1)
        }

        @Test
        fun `should return new null literal for null literal`() {
            val testData = createSmlNull()
            testData.toConstantExpression() shouldBe SmlConstantNull
        }

        @Test
        fun `should return new string literal for string literal`() {
            val testData = createSmlString("test")
            testData.toConstantExpression() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string start`() {
            val testData = factory.createSmlTemplateStringStart().apply { value = "test" }
            testData.toConstantExpression() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string inner`() {
            val testData = factory.createSmlTemplateStringInner().apply { value = "test" }
            testData.toConstantExpression() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string end`() {
            val testData = factory.createSmlTemplateStringEnd().apply { value = "test" }
            testData.toConstantExpression() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return null for block lambda`() {
            val testData = createSmlBlockLambda()
            testData.toConstantExpression().shouldBeNull()
        }

        @Test
        fun `should return null for expression lambda`() {
            val testData = createSmlExpressionLambda(result = createSmlNull())
            testData.toConstantExpression().shouldBeNull()
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should return value as constant expression for arguments`() {
            val testData = createSmlArgument(value = createSmlNull())
            testData.toConstantExpression() shouldBe SmlConstantNull
        }
    }

    @Nested
    inner class ParenthesizedExpression {

        @Test
        fun `should return expression as constant expression for parenthesized expressions`() {
            val testData = createSmlParenthesizedExpression(createSmlNull())
            testData.toConstantExpression() shouldBe SmlConstantNull
        }
    }

    @Nested
    inner class PrefixOperation {

        @Nested
        inner class Not {

            @Test
            fun `should return negated operand if it is a constant boolean`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Not,
                    operand = createSmlBoolean(true)
                )

                testData.toConstantExpression() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the operand is not a constant boolean`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Not,
                    operand = createSmlNull()
                )

                testData.toConstantExpression().shouldBeNull()
            }
        }

        @Nested
        inner class Minus {

            @Test
            fun `should return negated operand if it is a constant float`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Minus,
                    operand = createSmlFloat(1.0)
                )

                testData.toConstantExpression() shouldBe SmlConstantFloat(-1.0)
            }

            @Test
            fun `should return negated operand if it is a constant int`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Minus,
                    operand = createSmlInt(1)
                )

                testData.toConstantExpression() shouldBe SmlConstantInt(-1)
            }

            @Test
            fun `should return null if the operand is not a constant number`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Minus,
                    operand = createSmlNull()
                )

                testData.toConstantExpression().shouldBeNull()
            }
        }
    }
}
