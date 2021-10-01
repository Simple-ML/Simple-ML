package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.util.ParseWithStdlib
import de.unibonn.simpleml.utils.SimpleMLIndexExtensions
import io.kotest.matchers.maps.shouldContainValues
import io.kotest.matchers.nulls.shouldNotBeNull
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class IndexTest {

    @Inject
    private lateinit var index: SimpleMLIndexExtensions

    @Inject
    private lateinit var parseWithStdlib: ParseWithStdlib

    private val program = """
        |package test
        |
        |workflow test {}
        |
        |step testStep() {}
    """.trimMargin()

    @Test
    fun `should contain workflow steps from the current file`() {
        val context = parseWithStdlib.parse(program)
        context.shouldNotBeNull()

        val workflowSteps = context.eAllContents()
            .asSequence()
            .filter { it is SmlWorkflowStep }
            .toList()
            .toTypedArray()

        val descriptions = index.listCallables(context, null)
        descriptions.shouldContainValues(*workflowSteps)
    }
}
