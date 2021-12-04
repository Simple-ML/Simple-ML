package de.unibonn.simpleml.names

import com.google.inject.Inject
import de.unibonn.simpleml.emf.annotationsOrEmpty
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlFunction
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.naming.QualifiedName

internal object InjectionTarget {

    @Inject
    lateinit var qualifiedNameProvider: IQualifiedNameProvider
}

/**
 * Returns the fully qualified name of the declaration.
 */
fun SmlDeclaration.fullyQualifiedName(): QualifiedName {
    return InjectionTarget.qualifiedNameProvider.getFullyQualifiedName(this)
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Deprecated` annotation.
 */
fun SmlDeclaration.isDeprecated() = this.annotationsOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Deprecated
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Pure` annotation.
 */
fun SmlFunction.isPure() = this.annotationsOrEmpty().any {
    it.annotation.fullyQualifiedName() == StdlibAnnotations.Pure
}
