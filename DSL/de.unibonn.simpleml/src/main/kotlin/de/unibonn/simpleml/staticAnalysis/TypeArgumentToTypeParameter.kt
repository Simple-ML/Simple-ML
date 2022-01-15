package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isNamed
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameter

fun SmlTypeArgument.typeParameterOrNull(): SmlTypeParameter? {
    when {
        this.isNamed() -> return this.typeParameter
        else -> {
            val typeArgumentList = this.closestAncestorOrNull<SmlTypeArgumentList>() ?: return null

            val firstNamedTypeArgumentIndex = typeArgumentList.typeArguments.indexOfFirst { it.isNamed() }
            val thisIndex = typeArgumentList.typeArguments.indexOf(this)
            if (firstNamedTypeArgumentIndex != -1 && thisIndex > firstNamedTypeArgumentIndex) {
                return null
            }

            return typeArgumentList.typeParametersOrNull()?.getOrNull(thisIndex)
        }
    }
}

fun SmlTypeArgumentList.typeParametersOrNull(): List<SmlTypeParameter>? {
    when (val parent = this.eContainer()) {
        is SmlCall -> {
            when (val callable = parent.callableOrNull()) {
                is SmlClass -> return callable.typeParametersOrEmpty()
                is SmlEnumVariant -> return callable.typeParametersOrEmpty()
                is SmlFunction -> return callable.typeParametersOrEmpty()
            }
        }
        is SmlNamedType -> {
            val declaration = parent.declaration
            when {
                declaration.eIsProxy() -> return null
                declaration is SmlClass -> return declaration.typeParametersOrEmpty()
                declaration is SmlEnumVariant -> return declaration.typeParametersOrEmpty()
                declaration is SmlFunction -> return declaration.typeParametersOrEmpty()
            }
        }
    }

    return null
}
