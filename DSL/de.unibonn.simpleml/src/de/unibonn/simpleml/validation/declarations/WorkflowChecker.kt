package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.utils.placeholdersOrEmpty
import de.unibonn.simpleml.utils.statementsOrEmpty
import de.unibonn.simpleml.utils.yieldsOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val NO_YIELD_IN_WORKFLOW = "NO_YIELD_IN_WORKFLOW"

class WorkflowChecker : AbstractSimpleMLChecker() {

    @Check
    fun noYield(smlWorkflow: SmlWorkflow) {
        smlWorkflow.statementsOrEmpty()
                .filterIsInstance<SmlAssignment>()
                .flatMap { it.yieldsOrEmpty() }
                .forEach {
                    error(
                            "Yield must not be used in a workflow.",
                            it,
                            null,
                            NO_YIELD_IN_WORKFLOW
                    )
                }
    }

    @Check
    fun uniqueNames(smlWorkflow: SmlWorkflow) {
        smlWorkflow.placeholdersOrEmpty()
                .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this workflow." }
    }
}