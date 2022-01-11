package de.unibonn.simpleml.interpreter

sealed class SmlConstantExpression

data class SmlConstantBoolean(val value: Boolean) : SmlConstantExpression()
data class SmlConstantFloat(val value: Double) : SmlConstantExpression()
data class SmlConstantInt(val value: Int) : SmlConstantExpression()
object SmlConstantNull : SmlConstantExpression()
data class SmlConstantString(val value: String) : SmlConstantExpression()
