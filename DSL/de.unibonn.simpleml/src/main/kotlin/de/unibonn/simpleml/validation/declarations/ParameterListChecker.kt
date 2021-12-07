package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.utils.isOptional
import de.unibonn.simpleml.utils.isRequired
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check


class ParameterListChecker : AbstractSimpleMLChecker() {

    @Check
    fun noRequiredParametersAfterFirstOptionalParameter(smlParameterList: SmlParameterList) {
        if (smlParameterList.eContainer() is SmlCallableType) {
            return
        }

        val firstOptionalParameterIndex = smlParameterList.parameters.indexOfFirst { it.isOptional() }
        if (firstOptionalParameterIndex == -1) {
            return
        }

        smlParameterList.parameters
            .drop(firstOptionalParameterIndex + 1)
            .filter { it.isRequired() }
            .forEach {
                error(
                    "After the first optional parameter all parameters must be optional.",
                    it,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    ErrorCode.NO_REQUIRED_PARAMETERS_AFTER_FIRST_OPTIONAL_PARAMETER
                )
            }
    }
}
