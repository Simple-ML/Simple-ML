package de.unibonn.simpleml

import com.google.inject.Inject
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.utils.Proposals
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.maps.shouldContainValues
import io.kotest.matchers.maps.shouldNotContainValue
import io.kotest.matchers.nulls.shouldNotBeNull
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class ProposalsTest {

    @Inject
    private lateinit var proposals: Proposals

    @Inject
    private lateinit var parseHelper: ParseHelper

    private val testProgram = """
        |package test
        |
        |step primitive_empty() {}
        |step primitive_boolean(b: Boolean) {}
        |step primitive_float(f: Float) {}
        |step primitive_int(i: Int) {}
        |step primitive_string(s: String) {}
        |
        |class A()
        |class B()
        |class C()
        |class D() sub C
        |
        |step matching_a(a: A) {}
        |step matching_b(b: B) {}
        |step matching_multiple_c(c1: C, c2: C) {}
        |step not_matching_multiple_c(c: C) {}
        |step matching_multiple_c_d(c: C, d: D) {}
        |step matching_multiple_d_c(d: D, c: C) {}
        |
        |step test_callee() -> (test_result_a: A, test_result_c: C) {
        |    val test_placeholder_a = A();
        |    val test_placeholder_d = D();
        |}
    """.trimMargin()

    @Test
    fun `should contain workflow steps with primitive parameters when no result is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val workflowSteps = context.members
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name.startsWith("primitive") }
            .toList()
        workflowSteps.shouldHaveSize(5)

        val descriptions = proposals.listCallables(context, emptyList())
        descriptions.shouldContainValues(*workflowSteps.toTypedArray())
    }

    @Test
    fun `should contain only workflow steps with matching parameters when a placeholder is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val placeholder = context.findUniqueDeclarationOrFail<SmlPlaceholder>("test_placeholder_a")
        val workflowStepA = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_a")
        val workflowStepB = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_b")

        val descriptions = proposals.listCallables(context, listOf(placeholder))
        descriptions.shouldContainValue(workflowStepA)
        descriptions.shouldNotContainValue(workflowStepB)
    }

    @Test
    fun `should contain only workflow steps with matching parameters when a result is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val result = context.findUniqueDeclarationOrFail<SmlResult>("test_result_a")
        val workflowStepA = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_a")
        val workflowStepB = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_b")

        val descriptions = proposals.listCallables(context, listOf(result))
        descriptions.shouldContainValue(workflowStepA)
        descriptions.shouldNotContainValue(workflowStepB)
    }

    @Test
    fun `should contain only workflow steps with matching parameters when multiple declarations are passed (1)`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val result = context.findUniqueDeclarationOrFail<SmlResult>("test_result_c")
        val matchingWorkflow = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_multiple_c")
        val nonMatchingWorkflow = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("not_matching_multiple_c")

        val descriptions = proposals.listCallables(context, listOf(result, result))
        descriptions.shouldContainValue(matchingWorkflow)
        descriptions.shouldNotContainValue(nonMatchingWorkflow)
    }

    @Test
    fun `should contain only workflow steps with matching parameters when multiple declarations are passed (2)`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val result = context.findUniqueDeclarationOrFail<SmlResult>("test_result_c")
        val placeholder = context.findUniqueDeclarationOrFail<SmlPlaceholder>("test_placeholder_d")
        val matchingWorkflow1 = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_multiple_c_d")
        val matchingWorkflow2 = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_multiple_d_c")

        // Inverse order of placeholder and result than (3)
        val descriptions = proposals.listCallables(context, listOf(result, placeholder))
        descriptions.shouldContainValue(matchingWorkflow1)
        descriptions.shouldContainValue(matchingWorkflow2)
    }

    @Test
    fun `should contain only workflow steps with matching parameters when multiple declarations are passed (3)`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val result = context.findUniqueDeclarationOrFail<SmlResult>("test_result_c")
        val placeholder = context.findUniqueDeclarationOrFail<SmlPlaceholder>("test_placeholder_d")
        val matchingWorkflow1 = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_multiple_c_d")
        val matchingWorkflow2 = context.findUniqueDeclarationOrFail<SmlWorkflowStep>("matching_multiple_d_c")

        // Inverse order of placeholder and result than (2)
        val descriptions = proposals.listCallables(context, listOf(placeholder, result))
        descriptions.shouldContainValue(matchingWorkflow1)
        descriptions.shouldContainValue(matchingWorkflow2)
    }
}
