@file:Suppress("MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.stdlib

import de.unibonn.simpleml.emf.annotationUsesOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlFunction
import org.eclipse.xtext.naming.QualifiedName

/**
 * Important annotations in the standard library.
 */
object StdlibAnnotations {

    /**
     * The declaration should no longer be used.
     *
     * @see isDeprecated
     */
    val Deprecated: QualifiedName = StdlibPackages.lang.append("Deprecated")

    /**
     * The annotation can be used multiple times for the same declaration.
     *
     * @see isMultiUse
     * @see SingleUse
     * @see isSingleUse
     */
    val MultiUse: QualifiedName = StdlibPackages.lang.append("MultiUse")

    /**
     * The function returns the same results for the same arguments and has no side effects.
     *
     * @see isPure
     */
    val Pure: QualifiedName = StdlibPackages.lang.append("Pure")
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Deprecated` annotation.
 */
fun SmlDeclaration.isDeprecated() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Deprecated
}

/**
 * Checks if the annotation is annotated with the `simpleml.lang.MultiUse` annotation.
 */
fun SmlAnnotation.isMultiUse() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.MultiUse
}

/**
 * Checks if the function is annotated with the `simpleml.lang.Pure` annotation.
 */
fun SmlFunction.isPure() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Pure
}

/**
 * Checks if the annotation is not annotated with the `simpleml.lang.MultiUse` annotation.
 */
fun SmlAnnotation.isSingleUse() = !this.isMultiUse()
