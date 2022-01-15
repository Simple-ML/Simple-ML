package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isNamed
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlParameter

fun SmlArgument.parameterOrNull(): SmlParameter? {
    when {
        this.isNamed() -> return this.parameter
        else -> {
            val argumentList = this.closestAncestorOrNull<SmlArgumentList>() ?: return null

            val firstNamedArgumentIndex = argumentList.arguments.indexOfFirst { it.isNamed() }
            val thisIndex = argumentList.arguments.indexOf(this)
            if (firstNamedArgumentIndex != -1 && thisIndex > firstNamedArgumentIndex) {
                return null
            }

            val parameter = argumentList.parametersOrNull()?.getOrNull(thisIndex)
            return when {
                parameter == null || parameter.isVariadic -> null
                else -> parameter
            }
        }
    }
}

fun SmlArgumentList.parametersOrNull(): List<SmlParameter>? {
    when (val parent = this.eContainer()) {
        is SmlAnnotationCall -> {
            if (parent.annotation.eIsProxy()) {
                return null
            }

            return parent.annotation.parametersOrEmpty()
        }
        is SmlCall -> return parent.parametersOrNull()
    }

    return emptyList()
}
