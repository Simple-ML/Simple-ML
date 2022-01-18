package de.unibonn.simpleml.staticAnalysis.partialEvaluation

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.emf.createSmlAnnotation
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.createSmlAssignment
import de.unibonn.simpleml.emf.createSmlAttribute
import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlCall
import de.unibonn.simpleml.emf.createSmlEnum
import de.unibonn.simpleml.emf.createSmlEnumVariant
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
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.statementsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
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
class ToInlinedExpressionTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    private val factory = SimpleMLFactory.eINSTANCE

    private lateinit var impureBlockLambda: SmlBlockLambda
    private lateinit var pureBlockLambda: SmlBlockLambda
    private lateinit var recursiveBlockLambda: SmlBlockLambda
    private lateinit var impureExpressionLambda: SmlExpressionLambda
    private lateinit var pureExpressionLambda: SmlExpressionLambda
    private lateinit var recursiveExpressionLambda: SmlExpressionLambda
    private lateinit var impureStep: SmlStep
    private lateinit var pureStep: SmlStep
    private lateinit var recursiveStep: SmlStep

    @BeforeEach
    fun reset() {
        val compilationUnit = parseHelper
            .parseResourceWithStdlib("staticAnalysis/partialEvaluation/callables.smltest")
            .shouldNotBeNull()

        val blockLambdas = compilationUnit.descendants<SmlBlockLambda>().toList()
        blockLambdas.shouldHaveSize(3)

        impureBlockLambda = blockLambdas[0]
        pureBlockLambda = blockLambdas[1]
        recursiveBlockLambda = blockLambdas[2]

        val expressionLambdas = compilationUnit.descendants<SmlExpressionLambda>().toList()
        expressionLambdas.shouldHaveSize(3)

        impureExpressionLambda = expressionLambdas[0]
        pureExpressionLambda = expressionLambdas[1]
        recursiveExpressionLambda = expressionLambdas[2]

        impureStep = compilationUnit.findUniqueDeclarationOrFail("impureStep")
        pureStep = compilationUnit.findUniqueDeclarationOrFail("pureStep")
        recursiveStep = compilationUnit.findUniqueDeclarationOrFail("recursiveStep")
    }

    @Nested
    inner class BaseCases {

        @Test
        fun `should return this expression for boolean literal`() {
            val testData = createSmlBoolean(true)
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for float literal`() {
            val testData = createSmlFloat(1.0)
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for infix operations`() {
            val testData = createSmlInfixOperation(
                leftOperand = createSmlNull(),
                operator = SmlInfixOperationOperator.Elvis,
                rightOperand = createSmlNull()
            )

            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for int literal`() {
            val testData = createSmlInt(1)
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for null literal`() {
            val testData = createSmlNull()
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for prefix operation`() {
            val testData = createSmlPrefixOperation(
                operator = SmlPrefixOperationOperator.Minus,
                operand = createSmlInt(1)
            )

            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return value of for string literal`() {
            val testData = createSmlString("test")
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return this expression for template strings`() {
            val testData = createSmlTemplateString(
                stringParts = listOf("start", "end"),
                templateExpressions = listOf(createSmlNull())
            )

            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return value of template string start`() {
            val testData = factory.createSmlTemplateStringStart().apply { value = "test" }
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return value of template string inner`() {
            val testData = factory.createSmlTemplateStringInner().apply { value = "test" }
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should return value of template string end`() {
            val testData = factory.createSmlTemplateStringEnd().apply { value = "test" }
            testData.toInlinedExpressionOrNull() shouldBe testData
        }

        @Test
        fun `should store whether block lambda is pure (pure to true)`() {
            val testData = pureBlockLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateBlockLambda>()
            testData.isPure.shouldBeTrue()
        }

        @Test
        fun `should store whether block lambda is pure (impure to false)`() {
            val testData = impureBlockLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateBlockLambda>()
            testData.isPure.shouldBeFalse()
        }

        @Test
        fun `should store whether block lambda is pure (recursive to false)`() {
            val testData = recursiveBlockLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateBlockLambda>()
            testData.isPure.shouldBeFalse()
        }

        @Test
        fun `should store whether expression lambda is pure (pure to true)`() {
            val testData = pureExpressionLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateExpressionLambda>()
            testData.isPure.shouldBeTrue()
        }

        @Test
        fun `should store whether expression lambda is pure (impure to false)`() {
            val testData = impureExpressionLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateExpressionLambda>()
            testData.isPure.shouldBeFalse()
        }

        @Test
        fun `should store whether expression lambda is pure (recursive to false)`() {
            val testData = recursiveExpressionLambda
                .toInlinedExpressionOrNull()
                .shouldBeInstanceOf<SmlIntermediateExpressionLambda>()
            testData.isPure.shouldBeFalse()
        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should return inlined value for argument`() {
            val value = createSmlNull()
            val testData = createSmlArgument(value = value)
            testData.toInlinedExpressionOrNull() shouldBe value
        }
    }

    @Nested
    inner class ParenthesizedExpression {

        @Test
        fun `should return inlined value for expression`() {
            val expression = createSmlNull()
            val testData = createSmlParenthesizedExpression(expression = expression)
            testData.toInlinedExpressionOrNull() shouldBe expression
        }
    }

    @Nested
    inner class Call {

        private lateinit var compilationUnit: SmlCompilationUnit

        @BeforeEach
        fun reset() {
            compilationUnit = parseHelper
                .parseResourceWithStdlib("staticAnalysis/partialEvaluation/calls.smltest")
                .shouldNotBeNull()
        }

        @Test
        fun `should evaluate calls of block lambdas`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToBlockLambda")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate calls of expression lambdas`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToExpressionLambda")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate calls of steps`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToStep")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate calls of steps with variadic parameter`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToStepWithVariadicParameter")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should substitute parameters that were bound at call of a lambda`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "parameterAssignedDuringCall"
            )
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 10
        }

        @Test
        fun `should substitute parameters that were bound at creation of a lambda`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
                "parameterAssignedDuringCreationOfLambda"
            )
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should evaluate calls with lambda as parameter`() { // TODO
            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("lambdaAsParameter")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should return null otherwise`() { // TODO
            val testData = createSmlCall(receiver = createSmlNull())
            testData.toInlinedExpressionOrNull().shouldBeNull()
        }
    }

    @Nested
    inner class MemberAccess {

        @Test
        fun `should return constant enum variant if referenced enum variant has no parameters`() { // TODO
            val testEnumVariant = createSmlEnumVariant(name = "TestEnumVariant")
            val testEnum = createSmlEnum(
                name = "TestEnum",
                variants = listOf(testEnumVariant)
            )
            val testData = createSmlMemberAccess(
                receiver = createSmlReference(testEnum),
                member = createSmlReference(testEnumVariant)
            )

            testData.toInlinedExpressionOrNull() shouldBe SmlConstantEnumVariant(testEnumVariant)
        }

        @Test
        fun `should return null if referenced enum variant has parameters`() { // TODO
            val testEnumVariant = createSmlEnumVariant(
                name = "TestEnumVariant",
                parameters = listOf(
                    createSmlParameter(name = "testParameter")
                )
            )
            val testEnum = createSmlEnum(
                name = "TestEnum",
                variants = listOf(testEnumVariant)
            )
            val testData = createSmlMemberAccess(
                receiver = createSmlReference(testEnum),
                member = createSmlReference(testEnumVariant)
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return constant null if receiver is constant null and member access is null safe`() { // TODO
            val testData = createSmlMemberAccess(
                receiver = createSmlNull(),
                member = createSmlReference(createSmlAttribute("testAttribute")),
                isNullSafe = true
            )

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return null if receiver is constant null and member access is not null safe`() { // TODO
            val testData = createSmlMemberAccess(
                receiver = createSmlNull(),
                member = createSmlReference(createSmlAttribute("testAttribute"))
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should access the result of a call by name if result exists`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/memberAccesses.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulResultAccess")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should return null if accessed result does not exist`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/memberAccesses.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("failedResultAccess")
            val testData = workflow.testExpression()

            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return null for other receivers`() { // TODO
            val testData = createSmlMemberAccess(
                receiver = createSmlInt(1),
                member = createSmlReference(
                    createSmlEnumVariant(
                        name = "TestEnumVariant",
                        parameters = listOf(
                            createSmlParameter(name = "testParameter")
                        )
                    )
                )
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
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

            testData.toInlinedExpressionOrNull() shouldBe SmlConstantEnumVariant(testEnumVariant)
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

            testData.toInlinedExpressionOrNull().shouldBeNull()
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

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return null if referenced placeholder has no assigned value`() { // TODO
            val testData = createSmlReference(
                declaration = createSmlPlaceholder("testPlaceholder")
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return substituted value if it exists`() { // TODO
            val testParameter = createSmlParameter("testParameter")
            val testData = createSmlReference(
                declaration = testParameter
            )

            val result = testData.toInlinedExpressionOrNull()
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

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlNull>()
        }

        @Test
        fun `should return null if referenced parameter is required and not substituted`() { // TODO
            val testParameter = createSmlParameter("testParameter")
            val testData = createSmlReference(
                declaration = testParameter
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return null if step is referenced`() { // TODO
            val testData = createSmlReference(createSmlStep("testStep"))
            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return null if referenced step is impure`() { // TODO
            val testData = createSmlReference(impureStep)
            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return intermediate step if referenced step is pure`() { // TODO
            val testData = createSmlReference(pureStep)
            testData.toInlinedExpressionOrNull().shouldBeInstanceOf<SmlIntermediateStep>()
        }

        @Test
        fun `should return null if referenced step has recursive calls`() { // TODO
            val testData = createSmlReference(recursiveStep)
            testData.toInlinedExpressionOrNull().shouldBeNull()
        }

        @Test
        fun `should return value of placeholders inside valid assignment with call as expression`() { // TODO
            val compilationUnit = parseHelper
                .parseResource("staticAnalysis/partialEvaluation/references.smltest")
                .shouldNotBeNull()

            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulRecordAssignment")
            val testData = workflow.testExpression()

            val result = testData.toInlinedExpressionOrNull()
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

            testData.toInlinedExpressionOrNull().shouldBeNull()
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

            val result = testData.toInlinedExpressionOrNull()
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

            val result = testData.toInlinedExpressionOrNull()
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

            val result = testData.toInlinedExpressionOrNull()
            result.shouldBeInstanceOf<SmlInt>()
            result.value shouldBe 1
        }

        @Test
        fun `should return null for other declarations`() { // TODO
            val testData = createSmlReference(
                declaration = createSmlAnnotation("TestAnnotation")
            )

            testData.toInlinedExpressionOrNull().shouldBeNull()
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
