package de.unibonn.simpleml.staticAnalysis.dataflow

import de.unibonn.simpleml.emf.createSmlReference
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
import de.unibonn.simpleml.utils.uniqueOrNull

typealias ParameterSubstitutions = Map<SmlParameter, SmlSourceExpression?>
typealias ResultSubstitutions = Map<SmlAbstractResult, SmlSourceExpression?>
typealias ResultSubstitution = Pair<SmlAbstractResult, SmlSourceExpression?>

/**
 * Possible result of [toSourceExpressionOrNull].
 */
sealed interface SmlSourceExpression {

    /**
     * Map of [SmlParameter] to the corresponding [SmlSourceExpression] at the time of creation of the callable.
     */
    val substitutionsOnCreation: ParameterSubstitutions

    /**
     * Returns an [SmlAbstractExpression] that corresponds to this [SmlSourceExpression] or `null` if the conversion is
     * not possible.
     */
    fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression?
}

/**
 * Stores an [SmlAbstractCallable] and the [SmlSourceExpression]s for its [SmlParameter]s at the time of its creation.
 */
sealed interface SmlBoundCallable : SmlSourceExpression {

    /**
     * The bound callable.
     */
    val callable: SmlAbstractCallable

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

    override fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression {
        return lambda
    }
}

/**
 * A bound reference to an [SmlExpressionLambda].
 */
data class SmlBoundExpressionLambda(
    val lambda: SmlExpressionLambda,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlBoundCallable {
    override val callable = lambda

    override fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression {
        return lambda
    }
}

/**
 * A bound reference to an [SmlStep].
 */
data class SmlBoundStepReference(val step: SmlStep) : SmlBoundCallable {
    override val callable = step
    override val substitutionsOnCreation: ParameterSubstitutions = emptyMap()

    override fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression {
        return createSmlReference(callable)
    }
}

/**
 * Maps [SmlResult]s to the corresponding [SmlSourceExpression].
 */
class SmlResultRecord(resultSubstitutions: List<ResultSubstitution>) : SmlSourceExpression {
    override val substitutionsOnCreation: ParameterSubstitutions = emptyMap()

    /**
     * Map of [SmlResult] to the corresponding [SmlSourceExpression].
     */
    private val resultSubstitutions: ResultSubstitutions = resultSubstitutions.toMap()

    /**
     * Returns the [SmlSourceExpression] that is substituted for the declaration referenced by the [reference] or
     * `null` if no substitution exists
     */
    fun getSubstitutionByReferenceOrNull(reference: SmlReference): SmlSourceExpression? {
        val result = reference.declaration as? SmlAbstractResult ?: return null
        return resultSubstitutions[result]
    }

    /**
     * Returns the [SmlSourceExpression] at the given [index] or `null` if the [index] is `null` or no entry exists at
     * this [index].
     */
    fun getSubstitutionByIndexOrNull(index: Int?): SmlSourceExpression? {
        if (index == null) {
            return null
        }
        return resultSubstitutions.values.toList().getOrNull(index)
    }

    /**
     * If the record contains exactly one substitution its value is returned. Otherwise, it returns `this`.
     */
    fun unwrap(): SmlSourceExpression? {
        return when (resultSubstitutions.size) {
            1 -> resultSubstitutions.values.first()
            else -> this
        }
    }

    override fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression? {
        return resultSubstitutions.values.uniqueOrNull()?.toSmlAbstractExpressionOrNull()
    }

    override fun toString(): String {
        return resultSubstitutions.entries.joinToString(prefix = "{", postfix = "}") { (result, value) ->
            "${result.name}=$value"
        }
    }
}

/**
 * Catch-all case so any [SmlAbstractExpression] can be represented as a [SmlSourceExpression].
 */
data class SmlBoundExpression(
    val expression: SmlAbstractExpression,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlSourceExpression {

    override fun toSmlAbstractExpressionOrNull(): SmlAbstractExpression {
        return expression
    }
}
