package de.unibonn.simpleml

import com.google.inject.Inject
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.util.ParseHelper
import de.unibonn.simpleml.utils.Proposals
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
        |class A
        |class B
        |
        |step matching_a(a: A) {}
        |step matching_b(b: B) {}
        |
        |step test_callee() -> (test_result: A) {
        |    val test_placeholder = A();
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

        val descriptions = proposals.listCallables(context, null)
        descriptions.shouldContainValues(*workflowSteps.toTypedArray())
    }

    @Test
    fun `should contain workflow steps with only matching parameters when a placeholder is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val placeholder = context.eAllContents()
            .asSequence()
            .filterIsInstance<SmlPlaceholder>()
            .filter { it.name == "test_placeholder" }
            .firstOrNull()
        placeholder.shouldNotBeNull()

        val workflowStepA = context.members
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name == "matching_a" }
            .firstOrNull()
        workflowStepA.shouldNotBeNull()

        val workflowStepB = context.members
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name == "matching_b" }
            .firstOrNull()
        workflowStepB.shouldNotBeNull()

        val descriptions = proposals.listCallables(context, placeholder)
        descriptions.shouldContainValue(workflowStepA)
        descriptions.shouldNotContainValue(workflowStepB)
    }

    @Test
    fun `should contain workflow steps with only matching parameters when a result is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)?.uniquePackageOrNull()
        context.shouldNotBeNull()

        val result = context.eAllContents()
            .asSequence()
            .filterIsInstance<SmlResult>()
            .filter { it.name == "test_result" }
            .firstOrNull()
        result.shouldNotBeNull()

        val workflowStepA = context.members
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name == "matching_a" }
            .firstOrNull()
        workflowStepA.shouldNotBeNull()

        val workflowStepB = context.members
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name == "matching_b" }
            .firstOrNull()
        workflowStepB.shouldNotBeNull()

        val descriptions = proposals.listCallables(context, result)
        descriptions.shouldContainValue(workflowStepA)
        descriptions.shouldNotContainValue(workflowStepB)
    }
}
