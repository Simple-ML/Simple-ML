package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClassOrInterface
import de.unibonn.simpleml.simpleML.SmlType
import org.eclipse.xtext.naming.IQualifiedNameProvider

class QualifiedNameProvider @Inject constructor(
    private val qualifiedNameProvider: IQualifiedNameProvider
) {

    fun qualifiedNameOrNull(smlType: SmlType?): String? {
        return when (val classOrInterfaceOrNull = smlType.resolveToClassOrInterfaceOrNull()) {
            is SmlClassOrInterface -> qualifiedNameProvider.getFullyQualifiedName(classOrInterfaceOrNull).toString()
            else -> null
        }
    }
}