package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.placeholdersOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import de.unibonn.simpleml.utils.usesIn
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val PARAMETER_IS_UNUSED = "PARAMETER_IS_UNUSED"

class WorkflowStepChecker : AbstractSimpleMLChecker() {

    @Check
    fun parameterIsUnused(smlWorkflowStep: SmlWorkflowStep) {
        smlWorkflowStep.parametersOrEmpty()
                .filter { it.usesIn(smlWorkflowStep).none() }
                .forEach {
                    warning(
                            "This parameter is unused.",
                            it,
                            Literals.SML_DECLARATION__NAME,
                            PARAMETER_IS_UNUSED
                    )
                }
    }

    @Check
    fun uniqueNames(smlWorkflowStep: SmlWorkflowStep) {
        val declarations = smlWorkflowStep.parametersOrEmpty() + smlWorkflowStep.resultsOrEmpty() + smlWorkflowStep.placeholdersOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter, result or placeholder with name '${it.name}' exists already in this workflow step."
        }
    }
}