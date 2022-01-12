package de.unibonn.simpleml.partialEvaluation

import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractResult
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlYield

sealed interface SmlSimplifiedExpression

internal sealed interface SmlIntermediateExpression: SmlSimplifiedExpression

internal sealed interface SmlIntermediateCallable : SmlIntermediateExpression {
    val parameters: List<SmlParameter>
}

internal data class SmlIntermediateBlockLambda(
    override val parameters: List<SmlParameter>,
    val results: List<SmlBlockLambdaResult>
) : SmlIntermediateCallable

internal data class SmlIntermediateExpressionLambda(
    override val parameters: List<SmlParameter>,
    val result: SmlAbstractExpression
) : SmlIntermediateCallable

internal data class SmlIntermediateStep(
    override val parameters: List<SmlParameter>,
    val yields: List<SmlYield>
) : SmlIntermediateCallable

class SmlSimplifiedRecord(
    resultToValueEntries: List<Pair<SmlAbstractResult, SmlConstantExpression?>>
) : SmlIntermediateExpression {
    val resultToValue = resultToValueEntries.toMap()

    override fun toString(): String {
        return resultToValue.entries.joinToString(prefix = "{", postfix = "}") { (result, value) ->
            "${result.name}=$value"
        }
    }
}

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
