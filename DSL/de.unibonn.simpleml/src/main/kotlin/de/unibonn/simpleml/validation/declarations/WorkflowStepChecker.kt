package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.usesIn
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.InfoCode
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class WorkflowStepChecker : AbstractSimpleMLChecker() {

    @Check
    fun parameterIsUnused(smlWorkflowStep: SmlWorkflowStep) {
        smlWorkflowStep.parametersOrEmpty()
            .filter { it.usesIn(smlWorkflowStep).none() }
            .forEach {
                warning(
                    "This parameter is unused.",
                    it,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    WarningCode.UnusedParameter
                )
            }
    }

    @Check
    fun uniqueNames(smlWorkflowStep: SmlWorkflowStep) {
        val declarations =
            smlWorkflowStep.parametersOrEmpty() + smlWorkflowStep.resultsOrEmpty() + smlWorkflowStep.placeholdersOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter, result or placeholder with name '${it.name}' exists already in this workflow step."
        }
    }

    @Check
    fun unnecessaryResultList(smlWorkflowStep: SmlWorkflowStep) {
        if (smlWorkflowStep.resultList != null && smlWorkflowStep.resultsOrEmpty().isEmpty()) {
            info(
                "Unnecessary result list.",
                Literals.SML_WORKFLOW_STEP__RESULT_LIST,
                InfoCode.UnnecessaryResultList
            )
        }
    }
}
