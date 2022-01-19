package de.unibonn.simpleml.staticAnalysis.partialEvaluation

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
import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isOptional
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
import de.unibonn.simpleml.simpleML.SmlIndexedAccess
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
import de.unibonn.simpleml.staticAnalysis.indexOrNull
import de.unibonn.simpleml.staticAnalysis.isPureCallable
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.linking.uniqueYieldOrNull
import de.unibonn.simpleml.utils.uniqueOrNull
import de.unibonn.simpleml.constant.SmlInfixOperationOperator.Minus as InfixMinus
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator.Minus as PrefixMinus

/**
 * Tries to evaluate this expression. On success a [SmlConstantExpression] is returned, otherwise `null`.
 */
fun SmlAbstractExpression.toConstantExpressionOrNull(): SmlConstantExpression? {
    return try {
        toConstantExpressionOrNull(emptyMap())
    } catch (e: StackOverflowError) {
        null
    }
}

internal fun SmlAbstractExpression.toConstantExpressionOrNull(substitutions: ParameterSubstitutions2): SmlConstantExpression? {
    return when (val simplifiedExpression = simplify(substitutions)) {
        is SmlConstantExpression? -> simplifiedExpression
        is SmlIntermediateRecord -> simplifiedExpression.unwrap() as? SmlConstantExpression
        else -> null
    }
}

internal tailrec fun SmlAbstractExpression.simplify(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
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
        is SmlIndexedAccess -> simplifyIndexedAccess(substitutions)
        is SmlMemberAccess -> simplifyMemberAccess(substitutions)
        is SmlReference -> simplifyReference(substitutions)

        // Warn if case is missing
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun SmlBlockLambda.simplifyBlockLambda(substitutions: ParameterSubstitutions2): SmlIntermediateBlockLambda? {
    return when {
        isPureCallable(resultIfUnknown = true) -> SmlIntermediateBlockLambda(
            callable = this,
            substitutionsOnCreation = substitutions
        )
        else -> null
    }
}

private fun SmlExpressionLambda.simplifyExpressionLambda(
    substitutions: ParameterSubstitutions2
): SmlIntermediateExpressionLambda? {
    return when {
        isPureCallable(resultIfUnknown = true) -> SmlIntermediateExpressionLambda(
            callable = this,
            substitutionsOnCreation = substitutions
        )
        else -> null
    }
}

private fun SmlInfixOperation.simplifyInfixOp(substitutions: ParameterSubstitutions2): SmlConstantExpression? {

    // By design none of the operators are short-circuited
    val constantLeft = leftOperand.toConstantExpressionOrNull(substitutions) ?: return null
    val constantRight = rightOperand.toConstantExpressionOrNull(substitutions) ?: return null

    return when (operator) {
        Or.operator -> simplifyLogicalOp(constantLeft, Boolean::or, constantRight)
        And.operator -> simplifyLogicalOp(constantLeft, Boolean::and, constantRight)
        Equals.operator -> SmlConstantBoolean(constantLeft == constantRight)
        NotEquals.operator -> SmlConstantBoolean(constantLeft != constantRight)
        IdenticalTo.operator -> SmlConstantBoolean(constantLeft == constantRight)
        NotIdenticalTo.operator -> SmlConstantBoolean(constantLeft != constantRight)
        LessThan.operator -> simplifyComparisonOp(
            constantLeft,
            { a, b -> a < b },
            { a, b -> a < b },
            constantRight
        )
        LessThanOrEquals.operator -> simplifyComparisonOp(
            constantLeft,
            { a, b -> a <= b },
            { a, b -> a <= b },
            constantRight
        )
        GreaterThanOrEquals.operator -> simplifyComparisonOp(
            constantLeft,
            { a, b -> a >= b },
            { a, b -> a >= b },
            constantRight
        )
        GreaterThan.operator -> simplifyComparisonOp(
            constantLeft,
            { a, b -> a > b },
            { a, b -> a > b },
            constantRight
        )
        Plus.operator -> simplifyArithmeticOp(
            constantLeft,
            { a, b -> a + b },
            { a, b -> a + b },
            constantRight
        )
        InfixMinus.operator -> simplifyArithmeticOp(
            constantLeft,
            { a, b -> a - b },
            { a, b -> a - b },
            constantRight
        )
        Times.operator -> simplifyArithmeticOp(
            constantLeft,
            { a, b -> a * b },
            { a, b -> a * b },
            constantRight
        )
        By.operator -> {
            if (constantRight == SmlConstantFloat(0.0) || constantRight == SmlConstantInt(0)) {
                return null
            }

            simplifyArithmeticOp(
                constantLeft,
                { a, b -> a / b },
                { a, b -> a / b },
                constantRight
            )
        }
        Elvis.operator -> when (constantLeft) {
            SmlConstantNull -> constantRight
            else -> constantLeft
        }
        else -> throw IllegalArgumentException("Missing case to handle $this.")
    }
}

private fun simplifyLogicalOp(
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

private fun simplifyComparisonOp(
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

private fun simplifyArithmeticOp(
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

private fun SmlPrefixOperation.simplifyPrefixOp(substitutions: ParameterSubstitutions2): SmlConstantExpression? {
    val constantOperand = operand.toConstantExpressionOrNull(substitutions) ?: return null

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

private fun SmlTemplateString.simplifyTemplateString(substitutions: ParameterSubstitutions2): SmlConstantExpression? {
    val constExpressions = expressions.map {
        it.toConstantExpressionOrNull(substitutions) ?: return null
    }

    return SmlConstantString(constExpressions.joinToString(""))
}

private fun SmlCall.simplifyCall(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
    val simpleReceiver = simplifyReceiver(substitutions) ?: return null
    val newSubstitutions = buildNewSubstitutions(simpleReceiver, substitutions)

    return when (simpleReceiver) {
        is SmlIntermediateBlockLambda -> {
            SmlIntermediateRecord(
                simpleReceiver.results.map {
                    it to it.simplifyAssignee(newSubstitutions)
                }
            )
        }
        is SmlIntermediateExpressionLambda -> simpleReceiver.result.simplify(newSubstitutions)
        is SmlIntermediateStep -> {
            SmlIntermediateRecord(
                simpleReceiver.results.map {
                    it to it.uniqueYieldOrNull()?.simplifyAssignee(newSubstitutions)
                }
            )
        }
    }
}

private fun SmlCall.simplifyReceiver(substitutions: ParameterSubstitutions2): SmlIntermediateCallable? {
    return when (val simpleReceiver = receiver.simplify(substitutions)) {
        is SmlIntermediateRecord -> simpleReceiver.unwrap() as? SmlIntermediateCallable
        is SmlIntermediateCallable -> simpleReceiver
        else -> return null
    }
}

private fun SmlCall.buildNewSubstitutions(
    simpleReceiver: SmlIntermediateCallable,
    oldSubstitutions: ParameterSubstitutions2
): ParameterSubstitutions2 {

    val substitutionsOnCreation = when (simpleReceiver) {
        is SmlIntermediateBlockLambda -> simpleReceiver.substitutionsOnCreation
        is SmlIntermediateExpressionLambda -> simpleReceiver.substitutionsOnCreation
        else -> emptyMap()
    }

    val substitutionsOnCall = argumentsOrEmpty()
        .groupBy { it.parameterOrNull() }
        .mapValues { (parameter, arguments) ->
            when {
                parameter == null -> null
                parameter.isVariadic -> SmlIntermediateVariadicArguments(
                    arguments.map { it.simplify(oldSubstitutions) }
                )
                else -> arguments.uniqueOrNull()?.simplify(oldSubstitutions)
            }
        }

    return buildMap {
        putAll(substitutionsOnCreation)
        substitutionsOnCall.entries.forEach { (parameter, argument) ->
            if (parameter != null) {
                put(parameter, argument)
            }
        }
    }
}

private fun SmlIndexedAccess.simplifyIndexedAccess(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
    val simpleReceiver = receiver.simplify(substitutions) as? SmlIntermediateVariadicArguments ?: return null
    val simpleIndex = index.simplify(substitutions) as? SmlConstantInt ?: return null

    return simpleReceiver.getArgumentByIndexOrNull(simpleIndex.value)
}

private fun SmlMemberAccess.simplifyMemberAccess(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
    if (member.declaration is SmlEnumVariant) {
        return member.simplifyReference(substitutions)
    }

    return when (val simpleReceiver = receiver.simplify(substitutions)) {
        SmlConstantNull -> when {
            isNullSafe -> SmlConstantNull
            else -> null
        }
        is SmlIntermediateRecord -> simpleReceiver.getSubstitutionByReferenceOrNull(member)
        else -> null
    }
}

private fun SmlReference.simplifyReference(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
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

private fun SmlAbstractAssignee.simplifyAssignee(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
    val simpleFullAssignedExpression = closestAncestorOrNull<SmlAssignment>()
        ?.expression
        ?.simplify(substitutions)
        ?: return null

    return when (simpleFullAssignedExpression) {
        is SmlIntermediateRecord -> simpleFullAssignedExpression.getSubstitutionByIndexOrNull(indexOrNull())
        else -> when {
            indexOrNull() == 0 -> simpleFullAssignedExpression
            else -> null
        }
    }
}

private fun SmlParameter.simplifyParameter(substitutions: ParameterSubstitutions2): SmlSimplifiedExpression? {
    return when {
        this in substitutions -> substitutions[this]
        isOptional() -> defaultValue?.simplify(substitutions)
        else -> null
    }
}

private fun SmlStep.simplifyStep(): SmlIntermediateStep? {
    return when {
        isPureCallable(resultIfUnknown = true) -> SmlIntermediateStep(callable = this)
        else -> null
    }
}
