package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val MISSING_REQUIRED_PARAMETER = "MISSING_REQUIRED_PARAMETER"
const val NO_POSITIONAL_ARGUMENTS_AFTER_FIRST_NAMED_ARGUMENT = "NO_POSITIONAL_ARGUMENTS_AFTER_FIRST_NAMED_ARGUMENT"
const val TOO_MANY_ARGUMENTS = "TOO_MANY_ARGUMENTS"
const val UNIQUE_PARAMETERS = "UNIQUE_PARAMETERS"

class ArgumentListChecker : AbstractSimpleMLChecker() {

    @Check
    fun missingRequiredParameter(smlArgumentList: SmlArgumentList) {
        val parameters = smlArgumentList.parametersOrNull() ?: return
        val requiredParameters = parameters.filter { it.isRequired() }
        val givenParameters = smlArgumentList.arguments.mapNotNull { it.parameterOrNull() }
        val missingRequiredParameters = requiredParameters - givenParameters

        missingRequiredParameters.forEach {
            error(
                    "The parameter '${it.name}' is required and must be set here.",
                    null,
                    MISSING_REQUIRED_PARAMETER
            )
        }
    }

    @Check
    fun noPositionalArgumentsAfterFirstNamedArgument(smlArgumentList: SmlArgumentList) {
        val firstNamedArgumentIndex = smlArgumentList.arguments.indexOfFirst { it.isNamed() }
        if (firstNamedArgumentIndex == -1) {
            return
        }

        smlArgumentList.arguments
                .drop(firstNamedArgumentIndex + 1)
                .filter { it.isPositional() }
                .forEach {
                    error(
                            "After the first named argument all arguments must be named.",
                            it,
                            null,
                            NO_POSITIONAL_ARGUMENTS_AFTER_FIRST_NAMED_ARGUMENT
                    )
                }
    }

    @Check
    fun tooManyArguments(smlArgumentList: SmlArgumentList) {
        val parameters = smlArgumentList.parametersOrNull()
        if (parameters == null || parameters.any { it.isVararg }) {
            return
        }

        val maximumExpectedNumberOfArguments = parameters.size
        val actualNumberOfArguments = smlArgumentList.arguments.size

        if (actualNumberOfArguments > maximumExpectedNumberOfArguments) {
            val minimumExpectedNumberOfArguments = parameters.filter { it.isRequired() }.size
            val message = buildString {
                append("Expected ")

                when {
                    minimumExpectedNumberOfArguments != maximumExpectedNumberOfArguments -> {
                        append("between $minimumExpectedNumberOfArguments and $maximumExpectedNumberOfArguments arguments")
                    }
                    minimumExpectedNumberOfArguments == 1 -> append("exactly 1 argument")
                    else -> append("exactly $minimumExpectedNumberOfArguments arguments")
                }

                append(" but got $actualNumberOfArguments.")
            }

            error(
                    message,
                    null,
                    TOO_MANY_ARGUMENTS
            )
        }
    }

    @Check
    fun uniqueParameters(smlArgumentList: SmlArgumentList) {
        smlArgumentList.arguments
                .filter { it.parameterOrNull() != null }
                .duplicatesBy { it.parameterOrNull()?.name }
                .forEach {
                    error(
                            "The parameter '${it.parameterOrNull()?.name}' is already set.",
                            it,
                            null,
                            UNIQUE_PARAMETERS
                    )
                }
    }
}