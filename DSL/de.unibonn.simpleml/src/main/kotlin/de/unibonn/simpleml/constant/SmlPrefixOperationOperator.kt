package de.unibonn.simpleml.constant

import de.unibonn.simpleml.simpleML.SmlPrefixOperation

/**
 * The possible values for an [SmlPrefixOperation].
 */
enum class SmlPrefixOperationOperator(val operator: String) {

    /**
     * Logical negation.
     */
    Not("not"),

    /**
     * Arithmetic negation.
     */
    Minus("-");

    override fun toString(): String {
        return name
    }
}
