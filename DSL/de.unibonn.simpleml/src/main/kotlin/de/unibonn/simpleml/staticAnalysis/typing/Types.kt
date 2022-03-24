package de.unibonn.simpleml.staticAnalysis.typing

import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import org.eclipse.xtext.naming.QualifiedName

sealed class Type {
    abstract fun toSimpleString(): String
}

class RecordType(resultToType: List<Pair<String, Type>>) : Type() {
    private val resultToType = resultToType.toMap()

    override fun toString(): String {
        val types = resultToType.entries.joinToString { (name, type) -> "$name: $type" }
        return "($types)"
    }

    override fun toSimpleString(): String {
        val types = resultToType.entries.joinToString { (name, type) -> "$name: ${type.toSimpleString()}" }
        return "($types)"
    }
}

class CallableType(val parameters: List<Type>, val results: List<Type>) : Type() {
    override fun toString(): String {
        val parameters = parameters.joinToString()
        val results = results.joinToString()

        return "($parameters) -> ($results)"
    }

    override fun toSimpleString(): String {
        val parameters = parameters.joinToString { it.toSimpleString() }
        val results = results.joinToString { it.toSimpleString() }

        return "($parameters) -> ($results)"
    }
}

sealed class NamedType(smlDeclaration: SmlAbstractDeclaration) : Type() {
    val simpleName: String = smlDeclaration.name
    val qualifiedName: QualifiedName = smlDeclaration.qualifiedNameOrNull()!!

    abstract val isNullable: Boolean

    override fun toString() = buildString {
        append(qualifiedName)
        if (isNullable) {
            append("?")
        }
    }

    override fun toSimpleString() = buildString {
        append(simpleName)
        if (isNullable) {
            append("?")
        }
    }
}

data class ClassType(
    val smlClass: SmlClass,
    override val isNullable: Boolean
) : NamedType(smlClass) {

    override fun toString() = super.toString()
}

data class EnumType(
    val smlEnum: SmlEnum,
    override val isNullable: Boolean
) : NamedType(smlEnum) {

    override fun toString() = super.toString()
}

data class EnumVariantType(
    val smlEnumVariant: SmlEnumVariant,
    override val isNullable: Boolean
) : NamedType(smlEnumVariant) {

    override fun toString() = super.toString()
}

data class UnionType(val possibleTypes: Set<Type>): Type() {
    override fun toString(): String {
        return "union<${possibleTypes.joinToString()}>"
    }

    override fun toSimpleString(): String {
        return "union<${possibleTypes.joinToString { it.toSimpleString() }}>"
    }
}

data class VariadicType(val elementType: Type): Type() {
    override fun toString(): String {
        return "variadic<$elementType>"
    }

    override fun toSimpleString(): String {
        return "variadic<${elementType.toSimpleString()}>"
    }
}

object UnresolvedType : Type() {
    override fun toString() = "\$Unresolved"
    override fun toSimpleString() = toString()
}
