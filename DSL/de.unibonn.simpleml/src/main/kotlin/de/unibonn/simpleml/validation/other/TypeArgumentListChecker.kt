package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.utils.isNamed
import de.unibonn.simpleml.utils.isPositional
import de.unibonn.simpleml.utils.typeParameterOrNull
import de.unibonn.simpleml.utils.typeParametersOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class TypeArgumentListChecker : AbstractSimpleMLChecker() {

    @Check
    fun missingRequiredTypeParameter(smlTypeArgumentList: SmlTypeArgumentList) {
        val requiredTypeParameters = smlTypeArgumentList.typeParametersOrNull() ?: return
        val givenTypeParameters = smlTypeArgumentList.typeArguments.mapNotNull { it.typeParameterOrNull() }
        val missingRequiredTypeParameters = requiredTypeParameters - givenTypeParameters.toSet()

        missingRequiredTypeParameters.forEach {
            error(
                "The type parameter '${it.name}' is required and must be set here.",
                null,
                ErrorCode.MISSING_REQUIRED_TYPE_PARAMETER
            )
        }
    }

    @Check
    fun noPositionalArgumentsAfterFirstNamedArgument(smlTypeArgumentList: SmlTypeArgumentList) {
        val firstNamedTypeArgumentIndex = smlTypeArgumentList.typeArguments.indexOfFirst { it.isNamed() }
        if (firstNamedTypeArgumentIndex == -1) {
            return
        }

        smlTypeArgumentList.typeArguments
            .drop(firstNamedTypeArgumentIndex + 1)
            .filter { it.isPositional() }
            .forEach {
                error(
                    "After the first named type argument all type arguments must be named.",
                    it,
                    null,
                    ErrorCode.NO_POSITIONAL_TYPE_ARGUMENTS_AFTER_FIRST_NAMED_TYPE_ARGUMENT
                )
            }
    }
}
