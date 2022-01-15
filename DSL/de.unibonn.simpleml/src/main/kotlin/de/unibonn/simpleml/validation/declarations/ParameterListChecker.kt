package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.isOptional
import de.unibonn.simpleml.emf.isRequired
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class ParameterListChecker : AbstractSimpleMLChecker() {

    @Check
    fun noRequiredParametersAfterFirstOptionalParameter(smlParameterList: SmlParameterList) {
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
                    ErrorCode.NoRequiredParametersAfterFirstOptionalParameter
                )
            }
    }

    @Check
    fun noMoreParametersAfterVariadic(smlParameterList: SmlParameterList) {
        val firstVariadicParameterIndex = smlParameterList.parameters.indexOfFirst { it.isVariadic }
        if (firstVariadicParameterIndex == -1) {
            return
        }

        smlParameterList.parameters
            .drop(firstVariadicParameterIndex + 1)
            .forEach {
                error(
                    "After a variadic parameter no more parameters must be specified.",
                    it,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    ErrorCode.NoMoreParametersAfterVariadicParameter
                )
            }
    }
}
