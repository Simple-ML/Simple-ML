@file:Suppress("MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.stdlib

import org.eclipse.xtext.naming.QualifiedName

/**
 * Important classes in the standard library.
 */
object StdlibClasses {
    val Any: QualifiedName = StdlibPackages.lang.append("Any")
    val Boolean: QualifiedName = StdlibPackages.lang.append("Boolean")
    val Float: QualifiedName = StdlibPackages.lang.append("Float")
    val Int: QualifiedName = StdlibPackages.lang.append("Int")
    val String: QualifiedName = StdlibPackages.lang.append("String")
}
