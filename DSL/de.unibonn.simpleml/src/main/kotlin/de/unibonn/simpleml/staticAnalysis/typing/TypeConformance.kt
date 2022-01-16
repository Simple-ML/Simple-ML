package de.unibonn.simpleml.staticAnalysis.typing

import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.staticAnalysis.classHierarchy.isSubtypeOf

class TypeConformance {
    fun isSubstitutableFor(type1: Type, type2: Type): Boolean {
        return when (type2) {
            is ClassType -> isSubstitutableFor(type1, type2.smlClass)
            else -> false
        }
    }

    fun isSubstitutableFor(type1: Type, smlClass: SmlClass): Boolean {
        return when (type1) {
            is ClassType -> type1.smlClass.isSubtypeOf(smlClass)
            else -> false
        }
    }
}
