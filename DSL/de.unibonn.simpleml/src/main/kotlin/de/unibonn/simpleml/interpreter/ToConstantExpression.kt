package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.constant.SmlInfixOperationOperator.And
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.By
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Elvis
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Equals
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.GreaterThan
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.GreaterThanOrEquals
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.IdenticalTo
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.LessThan
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.LessThanOrEquals
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.NotEquals
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.NotIdenticalTo
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Or
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Plus
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Times
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator.Not
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Minus as InfixMinus
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator.Minus as PrefixMinus

/**
 * Tries to evaluate this expression. On success a [SmlConstantExpression] is returned, otherwise `null`.
 */
fun SmlAbstractExpression.toConstantExpressionOrNull(): SmlConstantExpression? {
    return when (this) {

        // Base cases
        is SmlBoolean -> SmlConstantBoolean(isTrue)
        is SmlFloat -> SmlConstantFloat(value)
        is SmlInt -> SmlConstantInt(value)
        is SmlNull -> SmlConstantNull
        is SmlString -> SmlConstantString(value)
        is SmlTemplateStringStart -> SmlConstantString(value)
        is SmlTemplateStringInner -> SmlConstantString(value)
        is SmlTemplateStringEnd -> SmlConstantString(value)
        is SmlBlockLambda -> null
        is SmlExpressionLambda -> null

        // Simple recursive cases
        is SmlArgument -> value.toConstantExpressionOrNull()
        is SmlInfixOperation -> infixOperationToConstantExpression()
        is SmlParenthesizedExpression -> expression.toConstantExpressionOrNull()
        is SmlPrefixOperation -> prefixOperationToConstantExpression()
        is SmlTemplateString -> templateStringToConstantExpression()

        // Complex recursive cases
        is SmlCall -> callToConstantExpression()
        is SmlMemberAccess -> memberAccessToConstantExpression()
        is SmlReference -> referenceToConstantExpression()

        // Warn if case is missing
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlInfixOperation.infixOperationToConstantExpression(): SmlConstantExpression? {
    val constantLeftOperand = leftOperand.toConstantExpressionOrNull() ?: return null
    val constantRightOperand = rightOperand.toConstantExpressionOrNull() ?: return null

    return when (operator) { // TODO
        Or.operator -> when {
            constantLeftOperand is SmlConstantBoolean && constantRightOperand is SmlConstantBoolean -> {
                SmlConstantBoolean(constantLeftOperand.value || constantRightOperand.value)
            }
            else -> null
        }
        And.operator -> when {
            constantLeftOperand is SmlConstantBoolean && constantRightOperand is SmlConstantBoolean -> {
                SmlConstantBoolean(constantLeftOperand.value && constantRightOperand.value)
            }
            else -> null
        }
        Equals.operator -> SmlConstantBoolean(constantLeftOperand == constantRightOperand)
        NotEquals.operator -> SmlConstantBoolean(constantLeftOperand != constantRightOperand)
        IdenticalTo.operator -> SmlConstantBoolean(constantLeftOperand == constantRightOperand)
        NotIdenticalTo.operator -> SmlConstantBoolean(constantLeftOperand != constantRightOperand)
        LessThan.operator -> null
        LessThanOrEquals.operator -> null
        GreaterThanOrEquals.operator -> null
        GreaterThan.operator -> null
        Plus.operator -> null
        InfixMinus.operator -> null
        Times.operator -> null
        By.operator -> null
        Elvis.operator -> null
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlPrefixOperation.prefixOperationToConstantExpression(): SmlConstantExpression? {
    val constantOperand = operand.toConstantExpressionOrNull() ?: return null

    return when (operator) {
        Not.operator -> when (constantOperand) {
            is SmlConstantBoolean -> SmlConstantBoolean(!constantOperand.value)
            else -> null
        }
        PrefixMinus.operator -> when (constantOperand) {
            is SmlConstantFloat -> SmlConstantFloat(-constantOperand.value)
            is SmlConstantInt -> SmlConstantInt(-constantOperand.value)
            else -> null
        }
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlTemplateString.templateStringToConstantExpression(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlCall.callToConstantExpression(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlMemberAccess.memberAccessToConstantExpression(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlReference.referenceToConstantExpression(): SmlConstantExpression? {
    return null // TODO
}
