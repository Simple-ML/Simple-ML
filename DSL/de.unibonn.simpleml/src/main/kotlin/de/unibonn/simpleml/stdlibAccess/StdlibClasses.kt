@file:Suppress("FunctionName")

package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.simpleML.SmlClass
import org.eclipse.emf.ecore.EObject

/**
 * Important classes in the standard library.
 */
object StdlibClass {
    fun Any(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Any"))
    }

    fun Boolean(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Boolean"))
    }

    fun Float(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Float"))
    }

    fun Int(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Int"))
    }

    fun Nothing(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Nothing"))
    }

    fun String(context: EObject): SmlClass {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("String"))
    }
}
