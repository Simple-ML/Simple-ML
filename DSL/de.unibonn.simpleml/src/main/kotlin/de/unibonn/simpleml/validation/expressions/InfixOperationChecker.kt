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
        val leftType = typeComputer.typeOf(smlInfixOperation.leftOperand)
        if (!(leftType is NamedType && leftType.isNullable)) {
            info(
                "The left operand is never null so the elvis operator is unnecessary (keep left operand).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
            return
        }

        val leftValue = smlInfixOperation.leftOperand.toConstantExpressionOrNull()
        val rightValue = smlInfixOperation.rightOperand.toConstantExpressionOrNull()
        if (leftValue is SmlConstantNull && rightValue is SmlConstantNull) {
            info(
                "Both operands are always null so the elvis operator is unnecessary (replace with null).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
        } else if (leftValue is SmlConstantNull) {
            info(
                "The left operand is always null so the elvis operator is unnecessary (keep right operand).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
        } else if (rightValue is SmlConstantNull) {
            info(
                "The right operand is always null so the elvis operator is unnecessary (keep left operand).",
                null,
                InfoCode.UnnecessaryElvisOperator
            )
        }
    }
}
