package de.unibonn.simpleml.staticAnalysis.partialEvaluation

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractResult
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep

typealias ParameterSubstitutions = Map<SmlParameter, SmlInlinedExpression?>
typealias ResultSubstitutions = Map<SmlResult, SmlInlinedExpression?>
typealias ResultSubstitution = Pair<SmlResult, SmlInlinedExpression?>

/**
 * Possible result of [toInlinedExpressionOrNull].
 */
sealed interface SmlInlinedExpression

/**
 * Stores an [SmlAbstractCallable] and the [SmlInlinedExpression]s for its [SmlParameter]s at the time of its creation.
 */
sealed interface SmlBoundCallable : SmlInlinedExpression {

    /**
     * The bound callable.
     */
    val callable: SmlAbstractCallable

    /**
     * Map of [SmlParameter] to the corresponding [SmlInlinedExpression] at the time of creation of the callable.
     */
    val substitutionsOnCreation: ParameterSubstitutions

    /**
     * Parameters of the callable.
     */
    val parameters: List<SmlParameter>
        get() = callable.parametersOrEmpty()
}

/**
 * A bound reference to an [SmlBlockLambda].
 */
data class SmlBoundBlockLambda(
    val lambda: SmlBlockLambda,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlBoundCallable {
    override val callable = lambda
}

/**
 * A bound reference to an [SmlExpressionLambda].
 */
data class SmlBoundExpressionLambda(
    val lambda: SmlExpressionLambda,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlBoundCallable {
    override val callable = lambda
}

/**
 * A bound reference to an [SmlStep].
 */
data class SmlBoundStepReference(val step: SmlStep) : SmlBoundCallable {
    override val callable = step
    override val substitutionsOnCreation: ParameterSubstitutions = emptyMap()
}

/**
 * Maps [SmlResult]s to the corresponding [SmlInlinedExpression].
 */
class SmlInlinedRecord(substitutions: List<ResultSubstitution>) : SmlInlinedExpression {

    /**
     * Map of [SmlResult] to the corresponding [SmlInlinedExpression].
     */
    private val substitutions: ResultSubstitutions = substitutions.toMap()

    /**
     * Returns the [SmlInlinedExpression] that is substituted for the declaration referenced by the [reference] or
     * `null` if no substitution exists
     */
    fun getSubstitutionByReferenceOrNull(reference: SmlReference): SmlInlinedExpression? {
        val result = reference.declaration as? SmlAbstractResult ?: return null
        return substitutions[result]
    }

    /**
     * Returns the [SmlInlinedExpression] at the given [index] or `null` if the [index] is `null` or no entry exists at
     * this [index].
     */
    fun getSubstitutionByIndexOrNull(index: Int?): SmlInlinedExpression? {
        if (index == null) {
            return null
        }
        return substitutions.values.toList().getOrNull(index)
    }

    /**
     * If the record contains exactly one substitution its value is returned. Otherwise, it returns `this`.
     */
    fun unwrap(): SmlInlinedExpression? {
        return when (substitutions.size) {
            1 -> substitutions.values.first()
            else -> this
        }
    }

    override fun toString(): String {
        return substitutions.entries.joinToString(prefix = "{", postfix = "}") { (result, value) ->
            "${result.name}=$value"
        }
    }
}

/**
 * Catch-all case so any [SmlAbstractExpression] can be represented as a [SmlInlinedExpression].
 */
data class SmlInlinedOtherExpression(val expression: SmlAbstractExpression) : SmlInlinedExpression
