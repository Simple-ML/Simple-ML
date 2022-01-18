package de.unibonn.simpleml.staticAnalysis.partialEvaluation

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInfixOperation
import de.unibonn.simpleml.emf.createSmlInt
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlPrefixOperation
import de.unibonn.simpleml.emf.createSmlString
import de.unibonn.simpleml.emf.createSmlTemplateString
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.statementsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
        val compilationUnit = parseHelper.parseResourceWithStdlib("partialEvaluation/callables.smltest")
        compilationUnit.shouldNotBeNull()

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
//
//        @Test
//        fun `toConstantExpression should return null for block lambda`() {
//            val testData = createSmlBlockLambda()
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return null for impure block lambda`() {
//            impureBlockLambda.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return intermediate block lambda for pure block lambda`() {
//            pureBlockLambda.simplify(emptyMap()).shouldBeInstanceOf<SmlIntermediateBlockLambda>()
//        }
//
//        @Test
//        fun `simplify should return null for block lambda with recursive call`() {
//            recursiveBlockLambda.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `toConstantExpression should return null for expression lambda`() {
//            val testData = createSmlExpressionLambda(result = createSmlNull())
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return null for impure expression lambda`() {
//            impureExpressionLambda.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return intermediate expression lambda for pure expression lambda`() {
//            pureExpressionLambda.simplify(emptyMap()).shouldBeInstanceOf<SmlIntermediateExpressionLambda>()
//        }
//
//        @Test
//        fun `simplify should return null for expression lambda with recursive call`() {
//            recursiveExpressionLambda.simplify(emptyMap()).shouldBeNull()
//        }
    }

    @Nested
    inner class Argument {

        @Test
        fun `should return inlined value for arguments`() {
            val value = createSmlNull()
            val testData = createSmlArgument(value = value)
            testData.toInlinedExpressionOrNull() shouldBe value
        }
    }

    @Nested
    inner class ParenthesizedExpression {

//        @Test
//        fun `should return expression as constant expression for parenthesized expressions`() {
//            val testData = createSmlParenthesizedExpression(createSmlNull())
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
//        }
    }

    @Nested
    inner class Call {

        private lateinit var compilationUnit: SmlCompilationUnit

        @BeforeEach
        fun reset() {
            compilationUnit = parseHelper.parseResourceWithStdlib("partialEvaluation/calls.smltest")!!
        }

//        @Test
//        fun `should evaluate calls of block lambdas`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToBlockLambda")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate calls of expression lambdas`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToExpressionLambda")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate calls of steps`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToStep")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate calls of steps with variadic parameter`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("callToStepWithVariadicParameter")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should substitute parameters that were bound at call of a lambda`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
//                "parameterAssignedDuringCall"
//            )
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(10)
//        }
//
//        @Test
//        fun `should substitute parameters that were bound at creation of a lambda`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
//                "parameterAssignedDuringCreationOfLambda"
//            )
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate calls with lambda as parameter`() {
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("lambdaAsParameter")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should return null otherwise`() {
//            val testData = createSmlCall(receiver = createSmlNull())
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
    }

    @Nested
    inner class MemberAccess {

//        @Test
//        fun `should return constant enum variant if referenced enum variant has no parameters`() {
//            val testEnumVariant = createSmlEnumVariant(name = "TestEnumVariant")
//            val testEnum = createSmlEnum(
//                name = "TestEnum",
//                variants = listOf(testEnumVariant)
//            )
//            val testData = createSmlMemberAccess(
//                receiver = createSmlReference(testEnum),
//                member = createSmlReference(testEnumVariant)
//            )
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantEnumVariant(testEnumVariant)
//        }
//
//        @Test
//        fun `should return null if referenced enum variant has parameters`() {
//            val testEnumVariant = createSmlEnumVariant(
//                name = "TestEnumVariant",
//                parameters = listOf(
//                    createSmlParameter(name = "testParameter")
//                )
//            )
//            val testEnum = createSmlEnum(
//                name = "TestEnum",
//                variants = listOf(testEnumVariant)
//            )
//            val testData = createSmlMemberAccess(
//                receiver = createSmlReference(testEnum),
//                member = createSmlReference(testEnumVariant)
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should return constant null if receiver is constant null and member access is null safe`() {
//            val testData = createSmlMemberAccess(
//                receiver = createSmlNull(),
//                member = createSmlReference(createSmlAttribute("testAttribute")),
//                isNullSafe = true
//            )
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
//        }
//
//        @Test
//        fun `should return null if receiver is constant null and member access is not null safe`() {
//            val testData = createSmlMemberAccess(
//                receiver = createSmlNull(),
//                member = createSmlReference(createSmlAttribute("testAttribute"))
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should access the result of a call by name if result exists`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/memberAccesses.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulResultAccess")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should return null if accessed result does not exist`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/memberAccesses.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("failedResultAccess")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should return null for other receivers`() {
//            val testData = createSmlMemberAccess(
//                receiver = createSmlInt(1),
//                member = createSmlReference(
//                    createSmlEnumVariant(
//                        name = "TestEnumVariant",
//                        parameters = listOf(
//                            createSmlParameter(name = "testParameter")
//                        )
//                    )
//                )
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
    }

    @Nested
    inner class Reference {

//        @Test
//        fun `should return constant enum variant if referenced enum variant has no parameters`() {
//            val testEnumVariant = createSmlEnumVariant(name = "TestEnumVariant")
//            val testData = createSmlReference(
//                declaration = testEnumVariant
//            )
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantEnumVariant(testEnumVariant)
//        }
//
//        @Test
//        fun `should return null if referenced enum variant has parameters`() {
//            val testEnumVariant = createSmlEnumVariant(
//                name = "TestEnumVariant",
//                parameters = listOf(
//                    createSmlParameter(name = "testParameter")
//                )
//            )
//            val testData = createSmlReference(
//                declaration = testEnumVariant
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should convert assigned value of referenced placeholder`() {
//            val testPlaceholder = createSmlPlaceholder("testPlaceholder")
//            createSmlAssignment(
//                assignees = listOf(testPlaceholder),
//                createSmlNull()
//            )
//            val testData = createSmlReference(
//                declaration = testPlaceholder
//            )
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantNull
//        }
//
//        @Test
//        fun `should return null if referenced placeholder has no assigned value`() {
//            val testData = createSmlReference(
//                declaration = createSmlPlaceholder("testPlaceholder")
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return substituted value if it exists`() {
//            val testParameter = createSmlParameter("testParameter")
//            val testData = createSmlReference(
//                declaration = testParameter
//            )
//
//            testData.simplify(mapOf(testParameter to SmlConstantNull)) shouldBe SmlConstantNull
//        }
//
//        @Test
//        fun `simplify should return default value if referenced parameter is not substituted but optional`() {
//            val testParameter = createSmlParameter(
//                name = "testParameter",
//                defaultValue = createSmlNull()
//            )
//            val testData = createSmlReference(
//                declaration = testParameter
//            )
//
//            testData.simplify(emptyMap()) shouldBe SmlConstantNull
//        }
//
//        @Test
//        fun `simplify should return null if referenced parameter is required and not substituted`() {
//            val testParameter = createSmlParameter("testParameter")
//            val testData = createSmlReference(
//                declaration = testParameter
//            )
//
//            testData.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `toConstantExpression should return null if step is referenced`() {
//            val testData = createSmlReference(createSmlStep("testStep"))
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return null if referenced step is impure`() {
//            val testData = createSmlReference(impureStep)
//            testData.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `simplify should return intermediate step if referenced step is pure`() {
//            val testData = createSmlReference(pureStep)
//            testData.simplify(emptyMap()).shouldBeInstanceOf<SmlIntermediateStep>()
//        }
//
//        @Test
//        fun `simplify should return null if referenced step has recursive calls`() {
//            val testData = createSmlReference(recursiveStep)
//            testData.simplify(emptyMap()).shouldBeNull()
//        }
//
//        @Test
//        fun `should return value of placeholders inside valid assignment with call as expression`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/references.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("successfulRecordAssignment")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should return null for references to placeholders inside invalid assignment with call as expression`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/references.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("failedRecordAssignment")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
//
//        @Test
//        fun `should evaluate references to placeholders (assigned, called step has different yield order)`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/references.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
//                "recordAssignmentWithDifferentYieldOrder"
//            )
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate references to placeholders (assigned, called step has missing yield)`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/references.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>("recordAssignmentWithMissingYield")
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should evaluate references to placeholders (assigned, called step has additional yield)`() {
//            val compilationUnit = parseHelper.parseResource("partialEvaluation/references.smltest")
//            compilationUnit.shouldNotBeNull()
//
//            val workflow = compilationUnit.findUniqueDeclarationOrFail<SmlWorkflow>(
//                "recordAssignmentWithAdditionalYield"
//            )
//            val testData = workflow.expectedExpression()
//
//            testData.toConstantExpressionOrNull() shouldBe SmlConstantInt(1)
//        }
//
//        @Test
//        fun `should return null for other declarations`() {
//            val testData = createSmlReference(
//                declaration = createSmlAnnotation("TestAnnotation")
//            )
//
//            testData.toConstantExpressionOrNull().shouldBeNull()
//        }
    }
}

/**
 * Helper method for tests loaded from a resource that returns the expression of the first expression statement in the
 * workflow.
 */
private fun SmlWorkflow.expectedExpression() = statementsOrEmpty()
    .filterIsInstance<SmlExpressionStatement>()
    .firstOrNull()
    .shouldNotBeNull()
    .expression
