package de.unibonn.simpleml.staticAnalysis.partialEvaluation

import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractResult
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.staticAnalysis.isPureCallable

typealias ParameterSubstitutions = Map<SmlParameter, SmlSimplifiedExpression?>

sealed interface SmlSimplifiedExpression

sealed interface SmlInlinedExpression : SmlSimplifiedExpression

sealed interface SmlInlinedCallable : SmlInlinedExpression {
    val callable: SmlAbstractCallable
    val substitutionsOnCreation: ParameterSubstitutions

    val parameters: List<SmlParameter>
        get() = callable.parametersOrEmpty()

    val isPure: Boolean
        get() = callable.isPureCallable()
}

data class SmlInlinedBlockLambda(
    override val callable: SmlBlockLambda,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlInlinedCallable {
    val results: List<SmlBlockLambdaResult>
        get() = callable.lambdaResultsOrEmpty()
}

data class SmlInlinedExpressionLambda(
    override val callable: SmlExpressionLambda,
    override val substitutionsOnCreation: ParameterSubstitutions
) : SmlInlinedCallable {
    val result: SmlAbstractExpression
        get() = callable.result
}

data class SmlInlinedStep(
    override val callable: SmlStep
) : SmlInlinedCallable {
    override val substitutionsOnCreation: ParameterSubstitutions
        get() = emptyMap()

    val results: List<SmlResult>
        get() = callable.resultsOrEmpty()
}

class SmlInlinedRecord(
    resultSubstitutions: List<Pair<SmlAbstractResult, SmlSimplifiedExpression?>>
) : SmlInlinedExpression {
    private val resultSubstitutions = resultSubstitutions.toMap()

    fun getSubstitutionByReferenceOrNull(reference: SmlReference): SmlSimplifiedExpression? {
        val result = reference.declaration as? SmlAbstractResult ?: return null
        return resultSubstitutions[result]
    }

    fun getSubstitutionByIndexOrNull(index: Int?): SmlSimplifiedExpression? {
        if (index == null) {
            return null
        }
        return resultSubstitutions.values.toList().getOrNull(index)
    }

    /**
     * If the record contains exactly one substitution its value is returned. Otherwise, it returns `this`.
     */
    fun unwrap(): SmlSimplifiedExpression? {
        return when (resultSubstitutions.size) {
            1 -> resultSubstitutions.values.first()
            else -> this
        }
    }

    override fun toString(): String {
        return resultSubstitutions.entries.joinToString(prefix = "{", postfix = "}") { (result, value) ->
            "${result.name}=$value"
        }
    }
}

data class SmlInlinedOtherExpression(val expression: SmlAbstractExpression) : SmlInlinedExpression

sealed interface SmlConstantExpression : SmlSimplifiedExpression

data class SmlConstantBoolean(val value: Boolean) : SmlConstantExpression {
    override fun toString(): String = value.toString()
}

data class SmlConstantEnumVariant(val value: SmlEnumVariant) : SmlConstantExpression {
    override fun toString(): String = value.name
}

sealed class SmlConstantNumber : SmlConstantExpression {
    abstract val value: Number
}

data class SmlConstantFloat(override val value: Double) : SmlConstantNumber() {
    override fun toString(): String = value.toString()
}

data class SmlConstantInt(override val value: Int) : SmlConstantNumber() {
    override fun toString(): String = value.toString()
}

object SmlConstantNull : SmlConstantExpression {
    override fun toString(): String = "null"
}

data class SmlConstantString(val value: String) : SmlConstantExpression {
    override fun toString(): String = value
}
