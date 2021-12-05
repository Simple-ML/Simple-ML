package de.unibonn.simpleml.naming

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlDeclaration
import org.eclipse.xtext.naming.IQualifiedNameConverter
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.naming.QualifiedName

internal object InjectionTarget {

    @Inject
    lateinit var qualifiedNameConverter: IQualifiedNameConverter

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
 * Converts a string to a qualified name.
 */
fun String.toQualifiedName(): QualifiedName {
    return InjectionTarget.qualifiedNameConverter.toQualifiedName(this)
}
