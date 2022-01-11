package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.simpleML.SmlEnumVariant

sealed class SmlConstantExpression

data class SmlConstantBoolean(val value: Boolean) : SmlConstantExpression() {
    override fun toString(): String = value.toString()
}

data class SmlConstantEnumVariant(val value: SmlEnumVariant): SmlConstantExpression() {
    override fun toString(): String = value.name
}

sealed class SmlConstantNumber : SmlConstantExpression() {
    abstract val value: Number
}

data class SmlConstantFloat(override val value: Double) : SmlConstantNumber() {
    override fun toString(): String = value.toString()
}

data class SmlConstantInt(override val value: Int) : SmlConstantNumber() {
    override fun toString(): String = value.toString()
}

object SmlConstantNull : SmlConstantExpression() {
    override fun toString(): String = "null"
}

data class SmlConstantString(val value: String) : SmlConstantExpression() {
    override fun toString(): String = value
}
