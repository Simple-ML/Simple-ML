package de.unibonn.simpleml.constant

import de.unibonn.simpleml.simpleML.SmlPrefixOperation

/**
 * The possible values for an [SmlPrefixOperation].
 */
enum class SmlTypeParameterConstraintOperator(val operator: String) {

    /**
     * Left operand is a subclass of the right operand. Each class is a subclass of itself for the purpose of this
     * operator.
     */
    SubclassOf("sub"),

    /**
     * Left operand is a superclass of the right operand. Each class is a superclass of itself for the purpose of this
     * operator.
     */
    SuperclassOf("super");

    override fun toString(): String {
        return name
    }
}
