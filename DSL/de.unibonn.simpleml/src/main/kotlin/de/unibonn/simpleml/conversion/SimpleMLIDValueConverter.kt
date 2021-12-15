package de.unibonn.simpleml.conversion

import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.nodemodel.INode

/**
 * Handles the conversion between the textual representation of an ID (including the escaping of keywords)  and its
 * actual value.
 *
 * Example: The ID ``` `fun` ``` in a DSL program has the value `fun`.
 */
class SimpleMLIDValueConverter : IDValueConverter() {

    /**
     * Adds surrounding backticks as necessary.
     */
    override fun toEscapedString(value: String): String {
        println("toescapedID " + value)
        println(mustEscape(value))
        return if (mustEscape(value)) "`$value`" else value
    }

    /**
     * Removes surrounding backticks.
     */
    override fun toValue(string: String?, node: INode?): String? {
        println("tovalueID " + string)
        return string?.removeSurrounding("`")
    }
}
