package de.unibonn.simpleml.serializer

import org.eclipse.emf.common.notify.impl.AdapterImpl

sealed class CommentAdapter(val text: String) : AdapterImpl()

class SingleLineCommentAdapter(text: String) : CommentAdapter(text) {
    override fun toString(): String {
        return "// $text"
    }
}

class MultiLineCommentAdapter(text: String) : CommentAdapter(text) {
    override fun toString(): String {
        return if (text.lines().size == 1) {
            "/* $text */"
        } else {
            buildString {
                appendLine("/*")
                text.lineSequence().forEach {
                    appendLine(" * $it")
                }
                appendLine(" */")
            }
        }
    }
}