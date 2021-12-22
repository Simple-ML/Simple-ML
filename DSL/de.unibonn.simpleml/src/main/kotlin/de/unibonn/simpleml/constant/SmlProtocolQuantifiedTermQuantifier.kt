package de.unibonn.simpleml.constant

import de.unibonn.simpleml.simpleML.SmlProtocolQuantifiedTerm

/**
 * The possible quantifiers for an [SmlProtocolQuantifiedTerm].
 */
enum class SmlProtocolQuantifiedTermQuantifier(val quantifier: String) {
    ZeroOrOne("?"),
    ZeroOrMore("*"),
    OneOrMore("+");

    override fun toString(): String {
        return name
    }
}
