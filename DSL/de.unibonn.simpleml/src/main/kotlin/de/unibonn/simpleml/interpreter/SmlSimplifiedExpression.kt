package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractResult
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlStep

sealed interface SmlSimplifiedExpression

internal sealed interface SmlIntermediateExpression: SmlSimplifiedExpression

internal sealed interface SmlSimplifiedCallable : SmlIntermediateExpression {
    val parameters: List<SmlParameter>
}

internal data class SmlSimplifiedBlockLambda(
    override val parameters: List<SmlParameter>,
//    val statements: List<Sml>
    val lambda: SmlBlockLambda
) : SmlSimplifiedCallable

internal data class SmlSimplifiedExpressionLambda(
    override val parameters: List<SmlParameter>,
    val expression: SmlAbstractExpression
) : SmlSimplifiedCallable

internal data class SmlSimplifiedStep(
    override val parameters: List<SmlParameter>,
    val step: SmlStep
) : SmlSimplifiedCallable

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
