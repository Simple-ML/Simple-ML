package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.util.ParseWithStdlib
import de.unibonn.simpleml.utils.Proposals
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainValues
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
    private lateinit var parseWithStdlib: ParseWithStdlib

    private val primitiveWorkflowSteps = """
        |package test
        |
        |step empty() {}
        |step boolean(b: Boolean) {}
        |step float(f: Float) {}
        |step int(i: Int) {}
        |step string(s: String) {}
    """.trimMargin()

    @Test
    fun `should contain workflow steps with primitive parameters when no result is passed`() {
        val context = parseWithStdlib.parse(primitiveWorkflowSteps)
        context.shouldNotBeNull()

        val workflowSteps = context.eAllContents()
            .asSequence()
            .filter { it is SmlWorkflowStep }
            .toList()
            .toTypedArray()
        workflowSteps.shouldHaveSize(5)

        val descriptions = proposals.listCallables(context, null)
        descriptions.shouldContainValues(*workflowSteps)
    }
}
