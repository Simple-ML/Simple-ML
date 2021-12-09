package de.unibonn.simpleml.ide.symbols

import de.unibonn.simpleml.ide.AbstractSimpleMLLanguageServerTest
import de.unibonn.simpleml.locations.LspRange
import org.junit.jupiter.api.Test


class DocumentSymbolTest : AbstractSimpleMLLanguageServerTest() {
    @Test
    fun testDocumentSymbol_01() {
        testDocumentSymbol {
            val model = """workflow w {
                }
"""
            it.model = model
            val expectedSymbols = listOf(
                ExpectedSymbol(
                    "w",
                    LspRange.fromInts(0, 9, 0, 10, 1),
                    null
                )
            )
            it.expectedSymbols = expectedSymbols.joinToString(separator = "\n")
        }
    }
}

data class ExpectedSymbol(val name: String, val range: LspRange, val containerName: String?) {
    private val indent = "    "

    override fun toString() = buildString {
        appendLine("symbol \"$name\" {")
        appendLine("${indent}kind: 7")
        appendLine("${indent}location: MyModel.smltest $range")

        if (containerName != null) {
            appendLine("${indent}container: \"$containerName\"")
        }

        appendLine("}")
    }
}
