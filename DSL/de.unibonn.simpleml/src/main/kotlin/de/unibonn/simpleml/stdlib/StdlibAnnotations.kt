@file:Suppress("MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.stdlib

import de.unibonn.simpleml.emf.annotationCallsOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.utils.uniqueOrNull
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
     * @see isRepeatable
     */
    val Repeatable: QualifiedName = StdlibPackages.lang.append("Repeatable")

    /**
     * The function returns the same results for the same arguments and has no side effects.
     *
     * @see isPure
     */
    val Pure: QualifiedName = StdlibPackages.lang.append("Pure")

    /**
     * The annotation can target only a subset of declaration types.
     */
    val Target: QualifiedName = StdlibPackages.lang.append("Target")
}

/**
 * Returns all uses of the annotation with the given fully qualified name.
 */
fun SmlAbstractDeclaration.annotationUsesOrEmpty(fullyQualifiedName: QualifiedName): List<SmlAnnotationCall> {
    return this.annotationCallsOrEmpty().filter {
        it.annotation.fullyQualifiedName() == fullyQualifiedName
    }
}

/**
 * Returns the unique use of the annotation with the given fully qualified name or `null` if none or multiple exist.
 */
fun SmlAbstractDeclaration.uniqueAnnotationCallOrNull(fullyQualifiedName: QualifiedName): SmlAnnotationCall? {
    return this.annotationUsesOrEmpty(fullyQualifiedName).uniqueOrNull()
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Deprecated` annotation.
 */
fun SmlAbstractDeclaration.isDeprecated(): Boolean {
    return this.annotationCallsOrEmpty().any {
        it.annotation.fullyQualifiedName() == StdlibAnnotations.Deprecated
    }
}

/**
 * Checks if the annotation is annotated with the `simpleml.lang.Repeatable` annotation.
 */
fun SmlAnnotation.isRepeatable(): Boolean {
    return this.annotationCallsOrEmpty().any {
        it.annotation.fullyQualifiedName() == StdlibAnnotations.Repeatable
    }
}

/**
 * Checks if the function is annotated with the `simpleml.lang.Pure` annotation.
 */
fun SmlFunction.isPure(): Boolean {
    return this.annotationCallsOrEmpty().any {
        it.annotation.fullyQualifiedName() == StdlibAnnotations.Pure
    }
}
