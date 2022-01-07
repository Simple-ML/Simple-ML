package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

class LambdaChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlBlockLambda: SmlBlockLambda) {
        val declarations =
            smlBlockLambda.parametersOrEmpty() + smlBlockLambda.placeholdersOrEmpty() + smlBlockLambda.lambdaResultsOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter, result or placeholder with name '${it.name}' exists already in this lambda."
        }
    }

    @Check
    fun unnecessaryParameterList(smlBlockLambda: SmlBlockLambda) {
        if (smlBlockLambda.parameterList != null && smlBlockLambda.parametersOrEmpty().isEmpty()) {
            info(
                "Unnecessary parameter list.",
                Literals.SML_BLOCK_LAMBDA__PARAMETER_LIST,
                InfoCode.UnnecessaryParameterList
            )
        }
    }
}
