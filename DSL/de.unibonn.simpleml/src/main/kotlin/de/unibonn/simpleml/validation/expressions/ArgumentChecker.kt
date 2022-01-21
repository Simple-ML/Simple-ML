package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.stdlibAccess.isConstant
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

class ArgumentChecker : AbstractSimpleMLChecker() {

    @Check(CheckType.NORMAL)
    fun argumentMustBeConstant(smlArgument: SmlArgument) {
        val parameterIsConstant = smlArgument.parameterOrNull()?.isConstant() ?: false

        if (parameterIsConstant && smlArgument.value?.toConstantExpressionOrNull() == null) {
            error(
                "Arguments assigned to constant parameters must be constant.",
                SimpleMLPackage.Literals.SML_ARGUMENT__VALUE,
                ErrorCode.MustBeConstant
            )
        }
    }
}
