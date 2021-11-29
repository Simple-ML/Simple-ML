package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlType
import org.eclipse.xtext.naming.IQualifiedNameProvider

class QualifiedNameProvider @Inject constructor(
    private val qualifiedNameProvider: IQualifiedNameProvider
) {

    fun qualifiedNameOrNull(smlType: SmlType?): String? {
        return when (val smlClassOrInterfaceOrNull = smlType.resolveToClassOrNull()) {
            is SmlClass -> qualifiedNameOrNull(smlClassOrInterfaceOrNull)
            else -> null
        }
    }

    fun qualifiedNameOrNull(smlClass: SmlClass): String {
        return qualifiedNameProvider.getFullyQualifiedName(smlClass).toString()
    }
}
