package de.unibonn.simpleml.conversion

import com.google.inject.Singleton
import org.eclipse.xtext.conversion.impl.STRINGValueConverter

/**
 * Handles the conversion between the textual representation of a string (including delimiters and escape sequences) to
 * and its actual value.
 *
 * Example: The string `"myString \{"` in a DSL program has the value `myString {`.
 */
@Singleton
open class SimpleMLSTRINGValueConverter : STRINGValueConverter() {

    private val converter by lazy {
        Implementation()
    }

    /**
     * Converts the value to its textual representation by adding delimiters and escaping characters.
     */
    override fun toEscapedString(value: String?): String {
        return '"' + converter.convertToJavaString(value, false) + '"'
    }

    /**
     * Creates the implementation of the converter to unescape characters. This method is **not** called  for escaping
     * by the superclass.
     */
    override fun createConverter(): STRINGValueConverter.Implementation {
        return converter
    }

    /**
     * The implementation of the converter to escape/unescape characters.
     */
    private inner class Implementation : STRINGValueConverter.Implementation() {

        /**
         * Escapes the character and adds it to the builder. This is necessary for serialization.
         */
        override fun escapeAndAppendTo(c: Char, useUnicode: Boolean, result: StringBuilder) {
            val appendMe: String
            when (c) {
                '\b' -> appendMe = "\\b"
                '\t' -> appendMe = "\\t"
                '\n' -> appendMe = "\\n"
                '\u000c' -> appendMe = "\\u000c"
                '\r' -> appendMe = "\\r"
                '"' -> appendMe = "\\\""
                // Don't escape "'"
                '\\' -> appendMe = "\\\\"
                '{' -> appendMe = "\\{" // Necessary due to string templates
                else -> {
                    if (useUnicode && mustEncodeAsEscapeSequence(c)) {
                        result.append("\\u")
                        var i = 12
                        while (i >= 0) {
                            result.append(toHex(c.code shr i and 0xF))
                            i -= 4
                        }
                    } else {
                        result.append(c)
                    }
                    return
                }
            }
            result.append(appendMe)
        }

        /**
         * Unescapes the character and adds it to the builder. This method defines which escape sequences are legal.
         */
        override fun doUnescapeCharAndAppendTo(
            string: String,
            useUnicode: Boolean,
            index: Int,
            result: StringBuilder
        ): Int {
            var c = string[index]
            when (c) {
                'b' -> c = '\b'
                't' -> c = '\t'
                'n' -> c = '\n'
                'f' -> c = '\u000c'
                'r' -> c = '\r'
                '"', '\'', '\\', '{' -> {} // '{' necessary due to string templates
                'u' -> return when {
                    useUnicode -> unescapeUnicodeSequence(string, index + 1, result)
                    else -> handleUnknownEscapeSequence(string, c, false, index + 1, result)
                }
                else -> return handleUnknownEscapeSequence(string, c, useUnicode, index + 1, result)
            }
            validateAndAppendChar(c, result)
            return index + 1
        }
    }

    override fun getInvalidEscapeSequenceMessage(): String {
        return "Invalid escape sequence (valid ones are  \\b  \\t  \\n  \\f  \\r  \\\"  \\'  \\\\  \\{ )."
    }
}
