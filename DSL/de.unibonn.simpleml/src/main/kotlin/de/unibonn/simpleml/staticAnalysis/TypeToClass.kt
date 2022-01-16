package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParenthesizedType

sealed interface ClassResult {
    object Unresolvable : ClassResult
    object NotAClass : ClassResult
    class Class(val `class`: SmlClass) : ClassResult
}

fun SmlAbstractType.asClass(): ClassResult {
    return when (this) {
        is SmlNamedType -> {
            val declaration = this.declaration
            when {
                !declaration.isResolved() -> ClassResult.Unresolvable
                declaration is SmlClass -> ClassResult.Class(declaration)
                else -> ClassResult.NotAClass
            }
        }
        is SmlMemberType -> this.member.asClass()
        is SmlParenthesizedType -> this.type.asClass()
        else -> ClassResult.NotAClass
    }
}

fun SmlAbstractType.asClassOrNull(): SmlClass? {
    return when (val result = this.asClass()) {
        is ClassResult.Class -> result.`class`
        else -> null
    }
}
