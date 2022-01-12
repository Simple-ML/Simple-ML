package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.memberDeclarationsOrEmpty
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.stdlib.StdlibClasses

class ClassHierarchy @Inject constructor(
    private val stdlib: SimpleMLStdlib
) {

    // ClassOrInterface ------------------------------------------------------------------------------------------------

    fun isSubtypeOf(smlClass: SmlClass, other: SmlClass) =
        smlClass == stdlib.getClass(smlClass, StdlibClasses.Nothing.toString()) ||
            smlClass == other || other in superClasses(smlClass)

    fun superClasses(smlClass: SmlClass) = sequence<SmlClass> {
        val visited = mutableSetOf<SmlClass>()

        var current = smlClass.parentClassOrNull()
        while (current != null && current !in visited) {
            yield(current)
            visited += current
            current = current.parentClassOrNull()
        }

        val anyClass = stdlib.getClass(smlClass, StdlibClasses.Any.toString())
        if (anyClass != null && smlClass != anyClass && visited.lastOrNull() != anyClass) {
            yield(anyClass)
        }
    }

    fun superClassMembers(smlClass: SmlClass) =
        superClasses(smlClass).flatMap { it.memberDeclarationsOrEmpty().asSequence() }

    // Function --------------------------------------------------------------------------------------------------------

    fun hiddenFunction(smlFunction: SmlFunction): SmlFunction? {
        val containingClassOrInterface = smlFunction.closestAncestorOrNull<SmlClass>() ?: return null
        return superClassMembers(containingClassOrInterface)
            .filterIsInstance<SmlFunction>()
            .firstOrNull { it.name == smlFunction.name }
    }
}
