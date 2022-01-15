package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.isClassMember
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlFunction

fun SmlAbstractDeclaration.isInferredStatic(): Boolean {
    return when {
        !this.isClassMember() -> false
        this is SmlClass || this is SmlEnum -> true
        this is SmlAttribute && this.isStatic -> true
        this is SmlFunction && this.isStatic -> true
        else -> false
    }
}
