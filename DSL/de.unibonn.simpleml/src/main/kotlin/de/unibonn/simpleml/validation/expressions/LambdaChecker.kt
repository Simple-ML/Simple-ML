package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.utils.lambdaYieldsOrEmpty
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.placeholdersOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

class LambdaChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlLambda: SmlLambda) {
        val declarations = smlLambda.parametersOrEmpty() + smlLambda.placeholdersOrEmpty() + smlLambda.lambdaYieldsOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter, result or placeholder with name '${it.name}' exists already in this lambda."
        }
    }
}