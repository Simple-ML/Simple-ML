package de.unibonn.simpleml.stdlib

import de.unibonn.simpleml.emf.annotationUsesOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlFunction
import org.eclipse.xtext.naming.QualifiedName

/**
 * Important packages in the standard library.
 */
object StdlibPackages {

    /**
     * Core package that is implicitly imported into all Simple-ML programs.
     */
    val lang: QualifiedName = QualifiedName.create("simpleml", "lang")
}

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
     * The annotation can be used multiple times on the same declaration.
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

    /**
     * The annotation can only be used once per declaration. This is also the default behavior unless the MultiUse
     * annotation is set.
     *
     * @see isSingleUse
     * @see MultiUse
     * @see isMultiUse
     */
    val SingleUse: QualifiedName = StdlibPackages.lang.append("SingleUse")
}

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

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Deprecated` annotation.
 */
fun SmlDeclaration.isDeprecated() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Deprecated
}

fun SmlAnnotation.isMultiUse() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.MultiUse
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Pure` annotation.
 */
fun SmlFunction.isPure() = this.annotationUsesOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Pure
}

fun SmlAnnotation.isSingleUse() = !this.isMultiUse()
