package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClassOrInterface
import de.unibonn.simpleml.utils.ClassHierarchy

class TypeConformance @Inject constructor(
    private val classHierarchy: ClassHierarchy
) {
    fun isSubstitutableFor(type1: Type, type2: Type): Boolean {
        return when (type2) {
            is ClassType -> isSubstitutableFor(type1, type2.smlClass)
            is InterfaceType -> isSubstitutableFor(type1, type2.smlInterface)
            else -> false
        }
    }

    fun isSubstitutableFor(type1: Type, classOrInterface: SmlClassOrInterface): Boolean {
        return when (type1) {
            is ClassType -> classHierarchy.isSubtypeOf(type1.smlClass, classOrInterface)
            is InterfaceType -> classHierarchy.isSubtypeOf(type1.smlInterface, classOrInterface)
            else -> false
        }
    }
}
