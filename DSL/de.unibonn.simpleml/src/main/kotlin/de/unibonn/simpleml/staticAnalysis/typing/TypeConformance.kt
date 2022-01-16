package de.unibonn.simpleml.staticAnalysis.typing

import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.staticAnalysis.classHierarchy.isSubtypeOf

fun Type.isSubstitutableFor(other: Type): Boolean {
    return when (other) {
        is ClassType -> this.isSubstitutableFor(other.smlClass)
        else -> false
    }
}

fun Type.isSubstitutableFor(`class`: SmlClass): Boolean {
    return when (this) {
        is ClassType -> this.smlClass.isSubtypeOf(`class`)
        else -> false
    }
}
