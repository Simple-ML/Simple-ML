package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.createSmlBlockLambda
import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlCall
import de.unibonn.simpleml.emf.createSmlExpressionLambda
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInfixOperation
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ToConstantExpressionTest {

    private val factory = SimpleMLFactory.eINSTANCE

    @Nested
    inner class BaseCases {

        @Test
        fun `should return new boolean literal for boolean literal`() {
            val testData = createSmlBoolean(true)
            testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
        }

        @Test
        fun `should return new float literal for float literal`() {
            val testData = createSmlFloat(1.0)
            testData.toConstantExpressionOrNull() shouldBe SmlConstantFloat(1.0)
        }

        @Test
        fun `should return new int literal for int literal`() {
            val testData = createSmlInt(1)
            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
        }

        @Test
        fun `should return new null literal for null literal`() {
            val testData = createSmlNull()
            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
        }

        @Test
        fun `should return new string literal for string literal`() {
            val testData = createSmlString("test")
            testData.toConstantExpressionOrNull() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string start`() {
            val testData = factory.createSmlTemplateStringStart().apply { value = "test" }
            testData.toConstantExpressionOrNull() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string inner`() {
            val testData = factory.createSmlTemplateStringInner().apply { value = "test" }
            testData.toConstantExpressionOrNull() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return string literal for template string end`() {
            val testData = factory.createSmlTemplateStringEnd().apply { value = "test" }
            testData.toConstantExpressionOrNull() shouldBe SmlConstantString("test")
        }

        @Test
        fun `should return null for block lambda`() {
            val testData = createSmlBlockLambda()
            testData.toConstantExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return null for expression lambda`() {
            val testData = createSmlExpressionLambda(result = createSmlNull())
            testData.toConstantExpressionOrNull().shouldBeNull()
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should return value as constant expression for arguments`() {
            val testData = createSmlArgument(value = createSmlNull())
            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
        }
    }

    @Nested
    inner class InfixOperation {

        @Nested
        inner class Or {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    false | false | false
                    false | true  | true
                    true  | false | true
                    true  | true  | true"""
            )
            fun `should return if left or right operand is true`(
                leftOperand: Boolean,
                rightOperand: Boolean,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(leftOperand),
                    operator = SmlInfixOperationOperator.Or,
                    rightOperand = createSmlBoolean(rightOperand)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant boolean`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.Or,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant boolean`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.Or,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class And {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    false | false | false
                    false | true  | false
                    true  | false | false
                    true  | true  | true"""
            )
            fun `should return if left and right operand is true`(
                leftOperand: Boolean,
                rightOperand: Boolean,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(leftOperand),
                    operator = SmlInfixOperationOperator.And,
                    rightOperand = createSmlBoolean(rightOperand)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant boolean`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.And,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant boolean`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.And,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class Equals {

            @Test
            fun `should return true boolean literal if left and right operands are equal`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.Equals,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return false boolean literal if left and right operands are not equal`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.Equals,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the left operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlCall(
                        receiver = createSmlNull()
                    ),
                    operator = SmlInfixOperationOperator.Equals,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.Equals,
                    rightOperand = createSmlCall(
                        receiver = createSmlNull()
                    )
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class NotEquals {

            @Test
            fun `should return true boolean literal if left and right operands are not equal`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotEquals,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return false boolean literal if left and right operands are equal`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotEquals,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the left operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlCall(
                        receiver = createSmlNull()
                    ),
                    operator = SmlInfixOperationOperator.NotEquals,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotEquals,
                    rightOperand = createSmlCall(
                        receiver = createSmlNull()
                    )
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class IdenticalTo {

            @Test
            fun `should return true boolean literal if left and right operands are identical`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.IdenticalTo,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return false boolean literal if left and right operands are not identical`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.IdenticalTo,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the left operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlCall(
                        receiver = createSmlNull()
                    ),
                    operator = SmlInfixOperationOperator.IdenticalTo,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.IdenticalTo,
                    rightOperand = createSmlCall(
                        receiver = createSmlNull()
                    )
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class NotIdenticalTo {

            @Test
            fun `should return true boolean literal if left and right operands are not identical`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotIdenticalTo,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return false boolean literal if left and right operands are identical`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotIdenticalTo,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the left operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlCall(
                        receiver = createSmlNull()
                    ),
                    operator = SmlInfixOperationOperator.NotIdenticalTo,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.NotIdenticalTo,
                    rightOperand = createSmlCall(
                        receiver = createSmlNull()
                    )
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class LessThan { // TODO

        }

        @Nested
        inner class LessThanOrEquals { // TODO

        }

        @Nested
        inner class GreaterThanOrEquals { // TODO

        }

        @Nested
        inner class GreaterThan { // TODO

        }

        @Nested
        inner class Plus { // TODO

        }

        @Nested
        inner class Minus { // TODO

        }

        @Nested
        inner class Times { // TODO

        }

        @Nested
        inner class By { // TODO

        }

        @Nested
        inner class Elvis { // TODO

        }
    }

    @Nested
    inner class ParenthesizedExpression {

        @Test
        fun `should return expression as constant expression for parenthesized expressions`() {
            val testData = createSmlParenthesizedExpression(createSmlNull())
            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
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

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(false)
            }

            @Test
            fun `should return null if the operand is not a constant boolean`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Not,
                    operand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
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

                testData.toConstantExpressionOrNull() shouldBe SmlConstantFloat(-1.0)
            }

            @Test
            fun `should return negated operand if it is a constant int`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Minus,
                    operand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(-1)
            }

            @Test
            fun `should return null if the operand is not a constant number`() {
                val testData = createSmlPrefixOperation(
                    operator = SmlPrefixOperationOperator.Minus,
                    operand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }
    }
}
