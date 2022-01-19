package de.unibonn.simpleml.staticAnalysis.dataflow

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.emf.createSmlAnnotation
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.createSmlAssignment
import de.unibonn.simpleml.emf.createSmlAttribute
import de.unibonn.simpleml.emf.createSmlBlockLambda
import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlCall
import de.unibonn.simpleml.emf.createSmlEnumVariant
import de.unibonn.simpleml.emf.createSmlExpressionLambda
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInfixOperation
import de.unibonn.simpleml.emf.createSmlInt
import de.unibonn.simpleml.emf.createSmlMemberAccess
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlParameter
import de.unibonn.simpleml.emf.createSmlParenthesizedExpression
import de.unibonn.simpleml.emf.createSmlPlaceholder
import de.unibonn.simpleml.emf.createSmlPrefixOperation
import de.unibonn.simpleml.emf.createSmlReference
import de.unibonn.simpleml.emf.createSmlStep
import de.unibonn.simpleml.emf.createSmlString
import de.unibonn.simpleml.emf.createSmlTemplateString
import de.unibonn.simpleml.emf.statementsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantEnumVariant
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import io.kotest.matchers.maps.shouldHaveKeys
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class ToSourceExpressionTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    private val factory = SimpleMLFactory.eINSTANCE

    @Nested
    inner class BaseCases {

        @Test
        fun `should wrap this expression for boolean literal`() {
            val testData = createSmlBoolean(true)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for float literal`() {
            val testData = createSmlFloat(1.0)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for infix operations`() {
            val testData = createSmlInfixOperation(
                leftOperand = createSmlNull(),
                operator = SmlInfixOperationOperator.Elvis,
                rightOperand = createSmlNull()
            )

            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for int literal`() {
            val testData = createSmlInt(1)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for null literal`() {
            val testData = createSmlNull()
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for prefix operation`() {
            val testData = createSmlPrefixOperation(
                operator = SmlPrefixOperationOperator.Minus,
                operand = createSmlInt(1)
            )

            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for string literal`() {
            val testData = createSmlString("test")
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for template strings`() {
            val testData = createSmlTemplateString(
                stringParts = listOf("start", "end"),
                templateExpressions = listOf(createSmlNull())
            )

            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for template string start`() {
            val testData = factory.createSmlTemplateStringStart().apply { value = "test" }
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for template string inner`() {
            val testData = factory.createSmlTemplateStringInner().apply { value = "test" }
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap this expression for template string end`() {
            val testData = factory.createSmlTemplateStringEnd().apply { value = "test" }
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should wrap block lambda in bound block lambda`() {
            val testData = createSmlBlockLambda { }
            testData.toSourceExpressionOrNull() shouldBe SmlBoundBlockLambda(
                lambda = testData,
                substitutionsOnCreation = emptyMap()
            )
        }

        @Test
        fun `should wrap expression lambda in bound expression lambda`() {
            val testData = createSmlExpressionLambda(result = createSmlNull())
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpressionLambda(
                lambda = testData,
                substitutionsOnCreation = emptyMap()
            )
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should return inlined value`() {
            val value = createSmlNull()
            val testData = createSmlArgument(value = value)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(value, emptyMap())
        }
    }

    @Nested
    inner class ParenthesizedExpression {

        @Test
        fun `should return inlined expression`() {
            val expression = createSmlNull()
            val testData = createSmlParenthesizedExpression(expression = expression)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(expression, emptyMap())
        }
    }

    @Nested
    inner class Call {

        private lateinit var compilationUnit: SmlCompilationUnit

        @BeforeEach
        fun reset() {
            compilationUnit = parseHelper
                .parseResourceWithStdlib("staticAnalysis/partialEvaluation/callsInline.smltest")
                .shouldNotBeNull()
        }

        @Test
        fun `should evaluate calls of pure block lambdas if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureBlockLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of pure block lambdas if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureBlockLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of impure block lambdas if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureBlockLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should not evaluate calls of impure block lambdas if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureBlockLambda")

            val testData = workflow.testExpression()
            testData
                .toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBe(SmlBoundExpression(testData, emptyMap()))
        }

        @Test
        fun `should evaluate calls of pure expression lambdas if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureExpressionLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of pure expression lambdas if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureExpressionLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of impure expression lambdas if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureExpressionLambda")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should not evaluate calls of impure expression lambdas if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureExpressionLambda")

            val testData = workflow.testExpression()
            testData
                .toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBe(SmlBoundExpression(testData, emptyMap()))
        }

        @Test
        fun `should evaluate calls of pure steps if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureStep")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of pure steps if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToPureStep")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls of impure steps if traverseImpureCallables is true`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureStep")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull(stopAtImpureCall = false)
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should not evaluate calls of impure steps if traverseImpureCallables is false`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToImpureStep")

            val testData = workflow.testExpression()
            testData
                .toSourceExpressionOrNull(stopAtImpureCall = true)
                .shouldBe(SmlBoundExpression(testData, emptyMap()))
        }

        @Test
        fun `should evaluate calls of steps with variadic parameter`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToStepWithVariadicParameter")

            val testData = workflow.testExpression()
            testData.toSourceExpressionOrNull()
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should remember parameters that were bound at call of a block lambda`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "parameterAssignedDuringCallOfBlockLambda"
            )
            val parameter = workflow.findUniqueDeclarationOrFail<SmlParameter>("a")

            val testData = workflow.testExpression()
            val result = testData.toSourceExpressionOrNull().shouldBeInstanceOf<SmlBoundExpression>()

            val expression = result.expression.shouldBeInstanceOf<SmlPrefixOperation>()
            expression.operator shouldBe SmlPrefixOperationOperator.Minus.operator
            expression.operand
                .shouldBeInstanceOf<SmlReference>()
                .declaration
                .shouldBe(parameter)

            result.substitutionsOnCreation.shouldHaveKeys(parameter)
        }

        @Test
        fun `should remember parameters that were bound at call of a expression lambda`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "parameterAssignedDuringCallOfExpressionLambda"
            )
            val parameter = workflow.findUniqueDeclarationOrFail<SmlParameter>("a")

            val testData = workflow.testExpression()
            val result = testData.toSourceExpressionOrNull().shouldBeInstanceOf<SmlBoundExpression>()

            val expression = result.expression.shouldBeInstanceOf<SmlPrefixOperation>()
            expression.operator shouldBe SmlPrefixOperationOperator.Minus.operator
            expression.operand
                .shouldBeInstanceOf<SmlReference>()
                .declaration
                .shouldBe(parameter)

            result.substitutionsOnCreation.shouldHaveKeys(parameter)
        }

        @Test
        fun `should remember parameters that were bound at creation of a lambda`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "parameterAssignedDuringCreationOfLambda"
            )
            val testData = workflow.testExpression()

            testData.toSourceExpressionOrNull()
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should evaluate calls with lambda as parameter`() {
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("lambdaAsParameter")
            val testData = workflow.testExpression()

            testData.toSourceExpressionOrNull()
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should return call if receiver is not callable`() {
            val testData = createSmlCall(receiver = createSmlNull())
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should return null if receiver cannot be resolved`() {
            val testData = createSmlCall(receiver = factory.createSmlReference())
            testData.toSourceExpressionOrNull().shouldBeNull()
        }
    }

    @Nested
    inner class MemberAccess {

        @Test
        fun `should access the result of a call by name if result exists`() {
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/memberAccesses.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulResultAccess")
            val testData = workflow.testExpression()

            testData.toSourceExpressionOrNull()
                .shouldBeInstanceOf<SmlBoundExpression>()
                .expression
                .shouldBeInstanceOf<SmlInt>()
                .value
                .shouldBe(1)
        }

        @Test
        fun `should return null if accessed result does not exist`() {
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/memberAccesses.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("failedResultAccess")
            val testData = workflow.testExpression()

            testData.toSourceExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return member access if receiver is not call result`() {
            val testData = createSmlMemberAccess(receiver = createSmlNull(), member = factory.createSmlReference())
            testData.toSourceExpressionOrNull() shouldBe SmlBoundExpression(testData, emptyMap())
        }

        @Test
        fun `should return null if receiver cannot be resolved`() {
            val testData = createSmlMemberAccess(
                receiver = factory.createSmlReference(),
                member = createSmlReference(createSmlAttribute("attribute"))
            )
            testData.toSourceExpressionOrNull().shouldBeNull()
        }
    }

    @Nested
    inner class Reference {

        @Test
        fun `should return constant enum variant if referenced enum variant has no parameters`() { // TODO
            val testEnumVariant = createSmlEnumVariant(name = "TestEnumVariant")
            val testData = createSmlReference(
                declaration = testEnumVariant
            )

            testData.toSourceExpressionOrNull() shouldBe SmlConstantEnumVariant(testEnumVariant)
        }

        @Test
        fun `should return null if referenced enum variant has parameters`() { // TODO
            val testEnumVariant = createSmlEnumVariant(
                name = "TestEnumVariant",
                parameters = listOf(
                    createSmlParameter(name = "testParameter")
                )
            )
            val testData = createSmlReference(
                declaration = testEnumVariant
            )

            testData.toSourceExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should convert assigned value of referenced placeholder`() { // TODO
            val testPlaceholder = createSmlPlaceholder("testPlaceholder")
            createSmlAssignment(
                assignees = listOf(testPlaceholder),
                createSmlNull()
            )
            val testData = createSmlReference(
                declaration = testPlaceholder
            )

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return null if referenced placeholder has no assigned value`() { // TODO
            val testData = createSmlReference(
                declaration = createSmlPlaceholder("testPlaceholder")
            )

            testData.toSourceExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return substituted value if it exists`() { // TODO
            val testParameter = createSmlParameter("testParameter")
            val testData = createSmlReference(
                declaration = testParameter
            )

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return default value if referenced parameter is not substituted but optional`() { // TODO
            val testParameter = createSmlParameter(
                name = "testParameter",
                defaultValue = createSmlNull()
            )
            val testData = createSmlReference(
                declaration = testParameter
            )

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return null if referenced parameter is required and not substituted`() { // TODO
            val testParameter = createSmlParameter("testParameter")
            val testData = createSmlReference(
                declaration = testParameter
            )

            testData.toSourceExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should wrap step in bound step`() {
            val step = createSmlStep("testStep")
            val testData = createSmlReference(step)
            testData.toSourceExpressionOrNull() shouldBe SmlBoundStepReference(step)
        }

        @Test
        fun `should return value of placeholders inside valid assignment with call as expression`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulRecordAssignment")
            val testData = workflow.testExpression()

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should return null for references to placeholders inside invalid assignment with call as expression`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("failedRecordAssignment")
            val testData = workflow.testExpression()

            testData.toSourceExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should evaluate references to placeholders (assigned, called step has different yield order)`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "recordAssignmentWithDifferentYieldOrder"
            )
            val testData = workflow.testExpression()

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate references to placeholders (assigned, called step has missing yield)`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("recordAssignmentWithMissingYield")
            val testData = workflow.testExpression()

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate references to placeholders (assigned, called step has additional yield)`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "recordAssignmentWithAdditionalYield"
            )
            val testData = workflow.testExpression()

            val result = testData.toSourceExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should return null for other declarations`() { // TODO
            val testData = createSmlReference(
                declaration = createSmlAnnotation("TestAnnotation")
            )

            testData.toSourceExpressionOrNull().shouldBeNull()
        }
    }
}

/**
 * Helper method for tests loaded from a resource that returns the expression of the first expression statement in the
 * workflow.
 */
private fun SmlWorkflow.testExpression() = statementsOrEmpty()
    .filterIsInstance<SmlExpressionStatement>()
    .firstOrNull()
    .shouldNotBeNull()
    .expression
