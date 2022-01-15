@file:Suppress("unused")

package de.unibonn.simpleml.utils

import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType

sealed interface ClassResult {
    object Unresolvable : ClassResult
    object NotAClass : ClassResult
    class Class(val `class`: SmlClass) : ClassResult
}

fun SmlAbstractType?.maybeClass(): ClassResult {
    return when (this) {
        is SmlNamedType -> {
            val declaration = this.declaration
            if (declaration.isResolved()) {
                if (declaration is SmlClass) {
                    ClassResult.Class(declaration)
                } else {
                    ClassResult.NotAClass
                }
            } else {
                ClassResult.Unresolvable
            }
        }
        is SmlMemberType -> this.member.maybeClass()
        else -> ClassResult.Unresolvable
    }
}

fun SmlAbstractType?.classOrNull(): SmlClass? {
    return when (val result = this.maybeClass()) {
        is ClassResult.Class -> result.`class`
        else -> null
    }
}
