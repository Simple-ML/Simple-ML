package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.*
import java.util.*

class ClassHierarchy @Inject constructor(
        private val stdlib: SimpleMLStdlib
) {

    // ClassOrInterface ------------------------------------------------------------------------------------------------

    fun isSubtypeOf(smlClassOrInterface: SmlClassOrInterface, other: SmlClassOrInterface) =
            smlClassOrInterface == other || other in superClassesOrInterfaces(smlClassOrInterface)

    fun superClassesOrInterfaces(smlClassOrInterface: SmlClassOrInterface) =
            superClasses(smlClassOrInterface) + superInterfaces(smlClassOrInterface)

    fun superClasses(smlClassOrInterface: SmlClassOrInterface) = sequence<SmlClass> {
        val visited = mutableSetOf<SmlClass>()

        var current = smlClassOrInterface.parentClassOrNull()
        while (current != null && current !in visited) {
            yield(current)
            visited += current
            current = current.parentClassOrNull()
        }

        if (smlClassOrInterface != BuiltinClasses.Any && visited.lastOrNull() != BuiltinClasses.Any) {
            yield(BuiltinClasses.Any)
        }
    }

    fun superInterfaces(smlClassOrInterface: SmlClassOrInterface) = sequence<SmlInterface> {
        val visited = mutableSetOf<SmlInterface>()
        val toDo = LinkedList(smlClassOrInterface.parentInterfacesOrEmpty())
        while (toDo.isNotEmpty()) {
            val current = toDo.pollFirst()
            yield(current)
            visited += current
            toDo.addAll(current.parentInterfacesOrEmpty())
        }
    }

    fun superClassOrInterfaceMembers(smlClassOrInterface: SmlClassOrInterface) =
            superClassMembers(smlClassOrInterface) + superInterfaceMembers(smlClassOrInterface)

    fun superClassMembers(smlClassOrInterface: SmlClassOrInterface) =
            superClasses(smlClassOrInterface).flatMap { it.membersOrEmpty().asSequence() }

    fun superInterfaceMembers(smlClassOrInterface: SmlClassOrInterface) =
            superInterfaces(smlClassOrInterface).flatMap { it.membersOrEmpty().asSequence() }

    // Function --------------------------------------------------------------------------------------------------------

    fun hiddenFunction(smlFunction: SmlFunction): SmlFunction? {
        val containingClassOrInterface = smlFunction.closestAncestorOrNull<SmlClassOrInterface>() ?: return null
        return superClassOrInterfaceMembers(containingClassOrInterface)
                .filterIsInstance<SmlFunction>()
                .firstOrNull { it.name == smlFunction.name }
    }
}