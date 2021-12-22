package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.utils.usesIn
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.InfoCode
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class StepChecker : AbstractSimpleMLChecker() {

    @Check
    fun parameterIsUnused(smlStep: SmlStep) {
        smlStep.parametersOrEmpty()
            .filter { it.usesIn(smlStep).none() }
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
    fun uniqueNames(smlStep: SmlStep) {
        val declarations =
            smlStep.parametersOrEmpty() + smlStep.resultsOrEmpty() + smlStep.placeholdersOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter, result or placeholder with name '${it.name}' exists already in this step."
        }
    }

    @Check
    fun unnecessaryResultList(smlStep: SmlStep) {
        if (smlStep.resultList != null && smlStep.resultsOrEmpty().isEmpty()) {
            info(
                "Unnecessary result list.",
                Literals.SML_STEP__RESULT_LIST,
                InfoCode.UnnecessaryResultList
            )
        }
    }
}
