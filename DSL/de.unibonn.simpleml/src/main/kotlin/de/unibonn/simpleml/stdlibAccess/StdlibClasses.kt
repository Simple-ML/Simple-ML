package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.simpleML.SmlClass
import org.eclipse.emf.ecore.EObject

/**
 * Important classes in the standard library.
 */
object StdlibClass {
    lateinit var Any: SmlClass
    lateinit var Boolean: SmlClass
    lateinit var Float: SmlClass
    lateinit var Int: SmlClass
    lateinit var Nothing: SmlClass
    lateinit var String: SmlClass
}

/**
 * Loads the important classes in the standard library.
 */
internal fun EObject.loadStdlibClasses() {
    StdlibClass.Any = getStdlibDeclaration(StdlibPackages.lang.append("Any"))
    StdlibClass.Boolean = getStdlibDeclaration(StdlibPackages.lang.append("Boolean"))
    StdlibClass.Float = getStdlibDeclaration(StdlibPackages.lang.append("Float"))
    StdlibClass.Int = getStdlibDeclaration(StdlibPackages.lang.append("Int"))
    StdlibClass.Nothing = getStdlibDeclaration(StdlibPackages.lang.append("Nothing"))
    StdlibClass.String = getStdlibDeclaration(StdlibPackages.lang.append("String"))
}
