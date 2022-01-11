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
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
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
        inner class LessThan {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    0.5 | 1.5 | true
                    0.5 | 1   | true
                    0   | 1.5 | true
                    0   | 1   | true
                    1.5 | 0.5 | false
                    1.5 | 0   | false
                    1   | 0.5 | false
                    1   | 0   | false"""
            )
            fun `should return whether left operand is less than right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.LessThan,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.LessThan,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.LessThan,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class LessThanOrEquals {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    0.5 | 0.5 | true
                    0.5 | 1   | true
                    0   | 1.5 | true
                    0   | 1   | true
                    1.5 | 0.5 | false
                    1.5 | 0   | false
                    1   | 0.5 | false
                    1   | 0   | false"""
            )
            fun `should return whether left operand is less than or equal to right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.LessThanOrEquals,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.LessThanOrEquals,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.LessThanOrEquals,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class GreaterThanOrEquals {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    0.5 | 0.5 | true
                    1.5 | 0   | true
                    1   | 0.5 | true
                    1   | 0   | true
                    0.5 | 1.5 | false
                    0.5 | 1   | false
                    0   | 1.5 | false
                    0   | 1   | false"""
            )
            fun `should return whether left operand is greater than or equal to right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.GreaterThanOrEquals,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.GreaterThanOrEquals,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.GreaterThanOrEquals,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class GreaterThan {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    1.5 | 0.5 | true
                    1.5 | 0   | true
                    1   | 0.5 | true
                    1   | 0   | true
                    0.5 | 1.5 | false
                    0.5 | 1   | false
                    0   | 1.5 | false
                    0   | 1   | false"""
            )
            fun `should return whether left operand is greater than right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Boolean
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.GreaterThan,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(expected)
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.GreaterThan,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.GreaterThan,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class Plus {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    1.5 | 0.25 | 1.75
                    1.5 | 1    | 2.5
                    1   | 0.25 | 1.25
                    1   | 1    | 2"""
            )
            fun `should return sum of left and right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Double
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.Plus,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe expected.toSmlNumber().toConstantExpressionOrNull()
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.Plus,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.Plus,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class Minus {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    1.5 | 0.25 | 1.25
                    1.5 | 1    | 0.5
                    1   | 0.25 | 0.75
                    1   | 1    | 0"""
            )
            fun `should return difference between left and right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Double
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.Minus,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe expected.toSmlNumber().toConstantExpressionOrNull()
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.Minus,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.Minus,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class Times {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    1.5 | 0.5  | 0.75
                    1.5 | 1    | 1.5
                    1   | 0.25 | 0.25
                    1   | 1    | 1"""
            )
            fun `should return product of left and right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Double
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.Times,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe expected.toSmlNumber().toConstantExpressionOrNull()
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.Times,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.Times,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class By {

            @ParameterizedTest
            @CsvSource(
                delimiter = '|',
                textBlock = """
                    0.25 | 0.5   | 0.5
                    1.5  | 1     | 1.5
                    1    | 0.625 | 1.6
                    1    | 1     | 1"""
            )
            fun `should return quotient of left and right operand`(
                leftOperand: Double,
                rightOperand: Double,
                expected: Double
            ) {
                val testData = createSmlInfixOperation(
                    leftOperand = leftOperand.toSmlNumber(),
                    operator = SmlInfixOperationOperator.By,
                    rightOperand = rightOperand.toSmlNumber()
                )

                testData.toConstantExpressionOrNull() shouldBe expected.toSmlNumber().toConstantExpressionOrNull()
            }

            @Test
            fun `should return null if the left operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.By,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant number`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.By,
                    rightOperand = createSmlNull()
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
        }

        @Nested
        inner class Elvis {

            @Test
            fun `should return left operand if it does not evaluate to a constant null`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlBoolean(true),
                    operator = SmlInfixOperationOperator.Elvis,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return right operand if the left operand evaluates to a constant null`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlNull(),
                    operator = SmlInfixOperationOperator.Elvis,
                    rightOperand = createSmlBoolean(true)
                )

                testData.toConstantExpressionOrNull() shouldBe SmlConstantBoolean(true)
            }

            @Test
            fun `should return null if the left operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlCall(
                        receiver = createSmlNull()
                    ),
                    operator = SmlInfixOperationOperator.Elvis,
                    rightOperand = createSmlInt(1)
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }

            @Test
            fun `should return null if the right operand is not a constant expression`() {
                val testData = createSmlInfixOperation(
                    leftOperand = createSmlInt(1),
                    operator = SmlInfixOperationOperator.Elvis,
                    rightOperand = createSmlCall(
                        receiver = createSmlNull()
                    )
                )

                testData.toConstantExpressionOrNull().shouldBeNull()
            }
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

private fun Double.toSmlNumber(): SmlAbstractExpression {
    return when {
        this == this.toInt().toDouble() -> createSmlInt(this.toInt())
        else -> createSmlFloat(this)
    }
}
