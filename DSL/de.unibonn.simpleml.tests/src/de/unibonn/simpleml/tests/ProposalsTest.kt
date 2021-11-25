package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.utils.Proposals
import de.unibonn.simpleml.utils.membersOrEmpty
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
    """.trimMargin()

    @Test
    fun `should contain workflow steps with primitive parameters when no result is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)
        context.shouldNotBeNull()

        val workflowSteps = context.membersOrEmpty()
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name.startsWith("primitive") }
            .toList()
        workflowSteps.shouldHaveSize(5)

        val descriptions = proposals.listCallables(context, null)
        descriptions.shouldContainValues(*workflowSteps.toTypedArray())
    }

    @Test
    fun `should contain workflow steps with only matching parameters when a result is passed`() {
        val context = parseHelper.parseProgramTextWithStdlib(testProgram)
        context.shouldNotBeNull()

        val classA = context.membersOrEmpty()
            .asSequence()
            .filterIsInstance<SmlClass>()
            .filter { it.name == "A" }
            .firstOrNull()
        classA.shouldNotBeNull()

        val result = SimpleMLFactory.eINSTANCE.createSmlResult().apply {
            name = "r"
            type = SimpleMLFactory.eINSTANCE.createSmlNamedType().apply {
                isNullable = false
                declaration = classA
            }
        }

        val workflowStepA = context.membersOrEmpty()
            .asSequence()
            .filterIsInstance<SmlWorkflowStep>()
            .filter { it.name == "matching_a" }
            .firstOrNull()
        workflowStepA.shouldNotBeNull()

        val workflowStepB = context.membersOrEmpty()
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
