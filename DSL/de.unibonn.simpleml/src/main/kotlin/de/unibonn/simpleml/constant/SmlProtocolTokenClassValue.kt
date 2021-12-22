package de.unibonn.simpleml.constant

import de.unibonn.simpleml.simpleML.SmlProtocolTokenClass

/**
 * The possible values for an [SmlProtocolTokenClass].
 */
enum class SmlProtocolTokenClassValue(val value: String) {

    /**
     * Matches any attribute or function.
     */
    Anything("."),

    /**
     * Matches any attribute.
     */
    AnyAttribute("\\a"),

    /**
     * Matches any function.
     */
    AnyFunction("\\f");

    override fun toString(): String {
        return name
    }
}
