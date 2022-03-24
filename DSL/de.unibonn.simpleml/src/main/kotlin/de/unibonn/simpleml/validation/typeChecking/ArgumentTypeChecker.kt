package de.unibonn.simpleml.validation.typeChecking

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.typing.isSubstitutableFor
import de.unibonn.simpleml.staticAnalysis.typing.type
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class ArgumentTypeChecker : AbstractSimpleMLChecker() {

    @Check
    fun value(smlArgument: SmlArgument) {
        val argumentType = smlArgument.type()
        val parameterType = (smlArgument.parameterOrNull() ?: return).type()

        if (!argumentType.isSubstitutableFor(parameterType)) {
            var argumentTypeString = argumentType.toSimpleString()
            var parameterTypeString = parameterType.toSimpleString()

            if (argumentTypeString == parameterTypeString) {
                argumentTypeString = argumentType.toString()
                parameterTypeString = parameterType.toString()
            }

            error(
                "An argument of type '$argumentTypeString' cannot be assigned to a parameter of type '$parameterTypeString'.",
                Literals.SML_ARGUMENT__VALUE,
                ErrorCode.WrongType
            )
        }
    }
}
