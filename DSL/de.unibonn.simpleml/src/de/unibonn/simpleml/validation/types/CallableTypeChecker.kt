package de.unibonn.simpleml.validation.types

import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

class CallableTypeChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlCallableType: SmlCallableType) {
        val declarations = smlCallableType.parametersOrEmpty() + smlCallableType.resultsOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter or result with name '${it.name}' exists already in this callable type."
        }
    }
}
