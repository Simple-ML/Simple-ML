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
        is SmlInfixOperation -> convertInfixOperation()
        is SmlParenthesizedExpression -> expression.toConstantExpressionOrNull()
        is SmlPrefixOperation -> convertPrefixOperation()
        is SmlTemplateString -> convertTemplateString()

        // Complex recursive cases
        is SmlCall -> convertCall()
        is SmlMemberAccess -> convertMemberAccess()
        is SmlReference -> convertReference()

        // Warn if case is missing
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlInfixOperation.convertInfixOperation(): SmlConstantExpression? {
    val constLeft = leftOperand.toConstantExpressionOrNull() ?: return null
    val constRight = rightOperand.toConstantExpressionOrNull() ?: return null

    return when (operator) {
        Or.operator -> convertLogicalOperation(constLeft, Boolean::or, constRight)
        And.operator -> convertLogicalOperation(constLeft, Boolean::and, constRight)
        Equals.operator -> SmlConstantBoolean(constLeft == constRight)
        NotEquals.operator -> SmlConstantBoolean(constLeft != constRight)
        IdenticalTo.operator -> SmlConstantBoolean(constLeft == constRight)
        NotIdenticalTo.operator -> SmlConstantBoolean(constLeft != constRight)
        LessThan.operator -> convertComparisonOperation(
            constLeft,
            { a, b -> a < b },
            { a, b -> a < b },
            constRight
        )
        LessThanOrEquals.operator -> convertComparisonOperation(
            constLeft,
            { a, b -> a <= b },
            { a, b -> a <= b },
            constRight
        )
        GreaterThanOrEquals.operator -> convertComparisonOperation(
            constLeft,
            { a, b -> a >= b },
            { a, b -> a >= b },
            constRight
        )
        GreaterThan.operator -> convertComparisonOperation(
            constLeft,
            { a, b -> a > b },
            { a, b -> a > b },
            constRight
        )
        Plus.operator -> convertArithmeticOperation(
            constLeft,
            { a, b -> a + b },
            { a, b -> a + b },
            constRight
        )
        InfixMinus.operator -> convertArithmeticOperation(
            constLeft,
            { a, b -> a - b },
            { a, b -> a - b },
            constRight
        )
        Times.operator -> convertArithmeticOperation(
            constLeft,
            { a, b -> a * b },
            { a, b -> a * b },
            constRight
        )
        By.operator -> convertArithmeticOperation(
            constLeft,
            { a, b -> a / b },
            { a, b -> a / b },
            constRight
        )
        Elvis.operator -> when (constLeft) {
            SmlConstantNull -> constRight
            else -> constLeft
        }
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun convertLogicalOperation(
    leftOperand: SmlConstantExpression,
    operation: (Boolean, Boolean) -> Boolean,
    rightOperand: SmlConstantExpression,
): SmlConstantExpression? {

    return when {
        leftOperand is SmlConstantBoolean && rightOperand is SmlConstantBoolean -> {
            SmlConstantBoolean(operation(leftOperand.value, rightOperand.value))
        }
        else -> null
    }
}

private fun convertComparisonOperation(
    leftOperand: SmlConstantExpression,
    doubleOperation: (Double, Double) -> Boolean,
    intOperation: (Int, Int) -> Boolean,
    rightOperand: SmlConstantExpression,
): SmlConstantExpression? {

    return when {
        leftOperand is SmlConstantInt && rightOperand is SmlConstantInt -> {
            SmlConstantBoolean(intOperation(leftOperand.value, rightOperand.value))
        }
        leftOperand is SmlConstantNumber && rightOperand is SmlConstantNumber -> {
            SmlConstantBoolean(doubleOperation(leftOperand.value.toDouble(), rightOperand.value.toDouble()))
        }
        else -> null
    }
}

private fun convertArithmeticOperation(
    leftOperand: SmlConstantExpression,
    doubleOperation: (Double, Double) -> Double,
    intOperation: (Int, Int) -> Int,
    rightOperand: SmlConstantExpression,
): SmlConstantExpression? {

    return when {
        leftOperand is SmlConstantInt && rightOperand is SmlConstantInt -> {
            SmlConstantInt(intOperation(leftOperand.value, rightOperand.value))
        }
        leftOperand is SmlConstantNumber && rightOperand is SmlConstantNumber -> {
            SmlConstantFloat(doubleOperation(leftOperand.value.toDouble(), rightOperand.value.toDouble()))
        }
        else -> null
    }
}

private fun SmlPrefixOperation.convertPrefixOperation(): SmlConstantExpression? {
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

private fun SmlTemplateString.convertTemplateString(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlCall.convertCall(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlMemberAccess.convertMemberAccess(): SmlConstantExpression? {
    return null // TODO
}

private fun SmlReference.convertReference(): SmlConstantExpression? {
    return null // TODO
}
