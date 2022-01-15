package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClass

class TypeConformance @Inject constructor(
    private val classHierarchy: ClassHierarchy
) {
    fun isSubstitutableFor(type1: Type, type2: Type): Boolean {
        return when (type2) {
            is ClassType -> isSubstitutableFor(type1, type2.smlClass)
            else -> false
        }
    }

    fun isSubstitutableFor(type1: Type, smlClass: SmlClass): Boolean {
        return when (type1) {
            is ClassType -> classHierarchy.isSubtypeOf(type1.smlClass, smlClass)
            else -> false
        }
    }
}
