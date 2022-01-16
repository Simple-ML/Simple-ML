package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.emf.classMembersOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.staticAnalysis.asClassOrNull
import de.unibonn.simpleml.stdlibAccess.StdlibAccess
import de.unibonn.simpleml.stdlibAccess.StdlibClasses

class ClassHierarchy @Inject constructor(
    private val stdlib: StdlibAccess
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
        superClasses(smlClass).flatMap { it.classMembersOrEmpty().asSequence() }

    // Function --------------------------------------------------------------------------------------------------------

    fun hiddenFunction(smlFunction: SmlFunction): SmlFunction? {
        val containingClassOrInterface = smlFunction.closestAncestorOrNull<SmlClass>() ?: return null
        return superClassMembers(containingClassOrInterface)
            .filterIsInstance<SmlFunction>()
            .firstOrNull { it.name == smlFunction.name }
    }
}

fun SmlClass?.inheritedNonStaticMembersOrEmpty(): Set<SmlAbstractDeclaration> {
    return this?.parentTypesOrEmpty()
        ?.mapNotNull { it.asClassOrNull() }
        ?.flatMap { it.classMembersOrEmpty() }
        ?.filter { it is SmlAttribute && !it.isStatic || it is SmlFunction && !it.isStatic }
        ?.toSet()
        .orEmpty()
}

fun SmlClass?.parentClassesOrEmpty() = this.parentTypesOrEmpty().mapNotNull { it.asClassOrNull() }
fun SmlClass?.parentClassOrNull(): SmlClass? {
    val resolvedParentClasses = this.parentClassesOrEmpty()
    return when (resolvedParentClasses.size) {
        1 -> resolvedParentClasses.first()
        else -> null
    }
}
