package de.unibonn.simpleml.validation.expressions

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Elvis
import de.unibonn.simpleml.partialEvaluation.SmlConstantNull
import de.unibonn.simpleml.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.typing.NamedType
import de.unibonn.simpleml.typing.TypeComputer
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

class InfixOperationChecker @Inject constructor(
    private val typeComputer: TypeComputer
) : AbstractSimpleMLChecker() {

    @Check
    fun dispatchCheckInfixOperation(smlInfixOperation: SmlInfixOperation) {
        when (smlInfixOperation.operator) {
            Elvis.operator -> checkElvisOperator(smlInfixOperation)
        }
    }

    private fun checkElvisOperator(smlInfixOperation: SmlInfixOperation) {
        val type = typeComputer.typeOf(smlInfixOperation.leftOperand)
        if (!(type is NamedType && type.isNullable)) {
            info(
                "The left operand is never null so the elvis operator is unnecessary (keep left operand).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
            return
        }

        val value = smlInfixOperation.leftOperand.toConstantExpressionOrNull()
        if (value is SmlConstantNull) {
            info(
                "The left operand is always null so the elvis operator is unnecessary (keep right operand).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
        }
    }
}
