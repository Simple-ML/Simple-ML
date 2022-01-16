package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.simpleML.SmlClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.QualifiedName

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
 * Loads the stdlib classes.
 */
internal fun loadStdlibClasses(context: EObject) {
    StdlibClass.Any = getStdlibDeclaration(context, StdlibClassQualifiedNames.Any)
    StdlibClass.Boolean = getStdlibDeclaration(context, StdlibClassQualifiedNames.Boolean)
    StdlibClass.Float = getStdlibDeclaration(context, StdlibClassQualifiedNames.Float)
    StdlibClass.Int = getStdlibDeclaration(context, StdlibClassQualifiedNames.Int)
    StdlibClass.Nothing = getStdlibDeclaration(context, StdlibClassQualifiedNames.Nothing)
    StdlibClass.String = getStdlibDeclaration(context, StdlibClassQualifiedNames.String)
}

/**
 * Qualified names of important classes in the standard library.
 */
private object StdlibClassQualifiedNames {
    val Any: QualifiedName = StdlibPackages.lang.append("Any")
    val Boolean: QualifiedName = StdlibPackages.lang.append("Boolean")
    val Float: QualifiedName = StdlibPackages.lang.append("Float")
    val Int: QualifiedName = StdlibPackages.lang.append("Int")
    val Nothing: QualifiedName = StdlibPackages.lang.append("Nothing")
    val String: QualifiedName = StdlibPackages.lang.append("String")
}
