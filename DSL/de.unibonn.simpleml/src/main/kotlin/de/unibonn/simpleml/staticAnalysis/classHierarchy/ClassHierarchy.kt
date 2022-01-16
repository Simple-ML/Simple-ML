package de.unibonn.simpleml.staticAnalysis.classHierarchy

import com.google.inject.Inject
import de.unibonn.simpleml.emf.classMembersOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.staticAnalysis.typing.ClassType
import de.unibonn.simpleml.staticAnalysis.typing.TypeComputer
import de.unibonn.simpleml.stdlibAccess.StdlibAccess
import de.unibonn.simpleml.stdlibAccess.StdlibClasses
import de.unibonn.simpleml.utils.uniqueOrNull

internal object ClassHierarchyInjectionTarget {

    @Inject
    lateinit var stdlib: StdlibAccess

    @Inject
    lateinit var typeComputer: TypeComputer
}

fun SmlClass.isSubtypeOf(other: SmlClass) =
    this == ClassHierarchyInjectionTarget.stdlib.getClass(this, StdlibClasses.Nothing.toString()) ||
            this == other || other in superClasses()

private fun SmlClass.superClasses() = sequence<SmlClass> {
    val visited = mutableSetOf<SmlClass>()

    // TODO: multiple parent classes
    var current = parentClassOrNull()
    while (current != null && current !in visited) {
        yield(current)
        visited += current
        current = current.parentClassOrNull()
    }

    val anyClass = ClassHierarchyInjectionTarget.stdlib.getClass(this@superClasses, StdlibClasses.Any.toString())
    if (anyClass != null && this@superClasses != anyClass && visited.lastOrNull() != anyClass) {
        yield(anyClass)
    }
}

fun SmlClass.superClassMembers() =
    this.superClasses().flatMap { it.classMembersOrEmpty().asSequence() }

// TODO only static methods can be hidden
fun SmlFunction.hiddenFunction(): SmlFunction? {
    val containingClassOrInterface = closestAncestorOrNull<SmlClass>() ?: return null
    return containingClassOrInterface.superClassMembers()
        .filterIsInstance<SmlFunction>()
        .firstOrNull { it.name == name }
}

fun SmlClass?.inheritedNonStaticMembersOrEmpty(): Set<SmlAbstractDeclaration> {
    return this?.parentClassesOrEmpty()
        ?.flatMap { it.classMembersOrEmpty() }
        ?.filter { it is SmlAttribute && !it.isStatic || it is SmlFunction && !it.isStatic }
        ?.toSet()
        .orEmpty()
}

fun SmlClass?.parentClassesOrEmpty(): List<SmlClass> {
    return this.parentTypesOrEmpty().mapNotNull {
        (ClassHierarchyInjectionTarget.typeComputer.typeOf(it) as? ClassType)?.smlClass
    }
}

fun SmlClass?.parentClassOrNull(): SmlClass? {
    return this.parentClassesOrEmpty().uniqueOrNull()
}
