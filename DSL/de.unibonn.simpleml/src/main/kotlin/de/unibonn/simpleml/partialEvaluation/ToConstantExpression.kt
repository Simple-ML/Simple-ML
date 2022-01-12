package de.unibonn.simpleml.partialEvaluation

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
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.indexOrNull
import de.unibonn.simpleml.utils.isInferredPure
import de.unibonn.simpleml.utils.isOptional
import de.unibonn.simpleml.utils.uniqueBy
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Minus as InfixMinus
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator.Minus as PrefixMinus

/**
 * Tries to evaluate this expression. On success a [SmlConstantExpression] is returned, otherwise `null`.
 */
fun SmlAbstractExpression.toConstantExpressionOrNull(): SmlConstantExpression? {
    return simplify(emptyMap()) as? SmlConstantExpression
}

internal fun SmlAbstractExpression.simplify(substitutions: Substitutions): SmlSimplifiedExpression? {
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
        is SmlBlockLambda -> simplifyBlockLambda(substitutions)
        is SmlExpressionLambda -> simplifyExpressionLambda(substitutions)

        // Simple recursive cases
        is SmlArgument -> value.simplify(substitutions)
        is SmlInfixOperation -> simplifyInfixOp(substitutions)
        is SmlParenthesizedExpression -> expression.simplify(substitutions)
        is SmlPrefixOperation -> simplifyPrefixOp(substitutions)
        is SmlTemplateString -> simplifyTemplateString(substitutions)

        // Complex recursive cases
        is SmlCall -> simplifyCall(substitutions)
        is SmlMemberAccess -> simplifyMemberAccess(substitutions)
        is SmlReference -> simplifyReference(substitutions)

        // Warn if case is missing
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlBlockLambda.simplifyBlockLambda(substitutions: Substitutions): SmlSimplifiedExpression? {
    return when {
        isInferredPure() -> SmlIntermediateBlockLambda(
            parameters = parametersOrEmpty(),
            results = lambdaResultsOrEmpty(),
            substitutionsDuringCreation = substitutions
        )
        else -> null
    }
}

private fun SmlExpressionLambda.simplifyExpressionLambda(substitutions: Substitutions): SmlSimplifiedExpression? {
    return when {
        isInferredPure() -> SmlIntermediateExpressionLambda(
            parameters = parametersOrEmpty(),
            result = result,
            substitutionsDuringCreation = substitutions
        )
        else -> null
    }
}

private fun SmlInfixOperation.simplifyInfixOp(substitutions: Substitutions): SmlSimplifiedExpression? {

    // By design none of the operators are short-circuited
    val simpleLeft = leftOperand.simplify(substitutions) ?: return null
    val simpleRight = rightOperand.simplify(substitutions) ?: return null

    return when (operator) {
        Or.operator -> simplifyLogicalOp(simpleLeft, Boolean::or, simpleRight)
        And.operator -> simplifyLogicalOp(simpleLeft, Boolean::and, simpleRight)
        Equals.operator -> SmlConstantBoolean(simpleLeft == simpleRight)
        NotEquals.operator -> SmlConstantBoolean(simpleLeft != simpleRight)
        IdenticalTo.operator -> SmlConstantBoolean(simpleLeft == simpleRight)
        NotIdenticalTo.operator -> SmlConstantBoolean(simpleLeft != simpleRight)
        LessThan.operator -> simplifyComparisonOp(
            simpleLeft,
            { a, b -> a < b },
            { a, b -> a < b },
            simpleRight
        )
        LessThanOrEquals.operator -> simplifyComparisonOp(
            simpleLeft,
            { a, b -> a <= b },
            { a, b -> a <= b },
            simpleRight
        )
        GreaterThanOrEquals.operator -> simplifyComparisonOp(
            simpleLeft,
            { a, b -> a >= b },
            { a, b -> a >= b },
            simpleRight
        )
        GreaterThan.operator -> simplifyComparisonOp(
            simpleLeft,
            { a, b -> a > b },
            { a, b -> a > b },
            simpleRight
        )
        Plus.operator -> simplifyArithmeticOp(
            simpleLeft,
            { a, b -> a + b },
            { a, b -> a + b },
            simpleRight
        )
        InfixMinus.operator -> simplifyArithmeticOp(
            simpleLeft,
            { a, b -> a - b },
            { a, b -> a - b },
            simpleRight
        )
        Times.operator -> simplifyArithmeticOp(
            simpleLeft,
            { a, b -> a * b },
            { a, b -> a * b },
            simpleRight
        )
        By.operator -> simplifyArithmeticOp(
            simpleLeft,
            { a, b -> a / b },
            { a, b -> a / b },
            simpleRight
        )
        Elvis.operator -> when (simpleLeft) {
            SmlConstantNull -> simpleRight
            else -> simpleLeft
        }
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun simplifyLogicalOp(
    leftOperand: SmlSimplifiedExpression,
    operation: (Boolean, Boolean) -> Boolean,
    rightOperand: SmlSimplifiedExpression,
): SmlSimplifiedExpression? {

    return when {
        leftOperand is SmlConstantBoolean && rightOperand is SmlConstantBoolean -> {
            SmlConstantBoolean(operation(leftOperand.value, rightOperand.value))
        }
        else -> null
    }
}

private fun simplifyComparisonOp(
    leftOperand: SmlSimplifiedExpression,
    doubleOperation: (Double, Double) -> Boolean,
    intOperation: (Int, Int) -> Boolean,
    rightOperand: SmlSimplifiedExpression,
): SmlSimplifiedExpression? {

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

private fun simplifyArithmeticOp(
    leftOperand: SmlSimplifiedExpression,
    doubleOperation: (Double, Double) -> Double,
    intOperation: (Int, Int) -> Int,
    rightOperand: SmlSimplifiedExpression,
): SmlSimplifiedExpression? {

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

private fun SmlPrefixOperation.simplifyPrefixOp(substitutions: Substitutions): SmlSimplifiedExpression? {
    val constantOperand = operand.simplify(substitutions) ?: return null

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

private fun SmlTemplateString.simplifyTemplateString(substitutions: Substitutions): SmlSimplifiedExpression? {
    val constExpressions = expressions.map {
        it.simplify(substitutions) ?: return null
    }

    return SmlConstantString(constExpressions.joinToString(""))
}

private fun SmlCall.simplifyCall(substitutions: Substitutions): SmlSimplifiedExpression? {
    val simpleReceiver = receiver.simplify(substitutions) as? SmlIntermediateCallable ?: return null

    println(simpleReceiver)
//    val

    when (val simpleReceiver = receiver.simplify(substitutions)) {
        is SmlIntermediateBlockLambda -> null
        is SmlIntermediateExpressionLambda -> null
        is SmlIntermediateStep -> null
        else -> null
    }

    return when (val simpleReceiver = receiver.simplify(substitutions)) {
        is SmlIntermediateBlockLambda -> null
        is SmlIntermediateExpressionLambda -> null
        is SmlIntermediateStep -> null
        else -> null
    }
}

private fun SmlMemberAccess.simplifyMemberAccess(substitutions: Substitutions): SmlSimplifiedExpression? {
    if (member.declaration is SmlEnumVariant) {
        return member.simplifyReference(substitutions)
    }

    return when (val simpleReceiver = receiver.simplify(substitutions)) {
        SmlConstantNull -> when {
            isNullSafe -> SmlConstantNull
            else -> null
        }
        is SmlSimplifiedRecord -> null // TODO implement + test
        else -> null
    }
}

private fun SmlReference.simplifyReference(substitutions: Substitutions): SmlSimplifiedExpression? {
    return when (val declaration = this.declaration) {
        is SmlEnumVariant -> when {
            declaration.parametersOrEmpty().isEmpty() -> SmlConstantEnumVariant(declaration)
            else -> null
        }
        is SmlPlaceholder -> declaration.simplifyAssignee(substitutions)
        is SmlParameter -> declaration.simplifyParameter(substitutions)
        is SmlStep -> declaration.simplifyStep()
        else -> null
    }
}

private fun SmlAbstractAssignee.simplifyAssignee(substitutions: Substitutions): SmlSimplifiedExpression? {
    val simpleFullAssignedExpression = closestAncestorOrNull<SmlAssignment>()
        ?.expression
        ?.simplify(substitutions)
        ?: return null

    return when (simpleFullAssignedExpression) {
        is SmlSimplifiedRecord -> null // TODO: test + access record by index - needs calls
        else -> when {
            indexOrNull() == 0 -> simpleFullAssignedExpression
            else -> null
        }
    }
}

private fun SmlParameter.simplifyParameter(substitutions: Substitutions): SmlSimplifiedExpression? {
    return when {
        this in substitutions -> substitutions[this]
        isOptional() -> defaultValue?.simplify(substitutions)
        else -> null
    }
}

private fun SmlStep.simplifyStep(): SmlIntermediateStep? {
    return when {
        isInferredPure() -> SmlIntermediateStep(
            parameters = parametersOrEmpty(),
            yields = descendants<SmlYield>().toList().uniqueBy { it.result }
        )
        else -> null
    }
}
