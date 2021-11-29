package de.unibonn.simpleml.typing

import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum

// TODO this breaks in Eclipse; we get reference errors; always use injection via annotations!!!!!!!!!!
// private val qualifiedNameProvider: IQualifiedNameProvider =
//        SimpleMLStandaloneSetup()
//                .createInjectorAndDoEMFRegistration()
//                .getInstance(IQualifiedNameProvider::class.java)

sealed class Type {
    open fun toSimpleString() = toString()
}

class TupleType(val types: List<Type>) : Type() {
    override fun toString(): String {
        val types = types.joinToString()
        return "($types)"
    }

    override fun toSimpleString(): String {
        val types = types.joinToString { it.toSimpleString() }
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

sealed class NamedType(smlDeclaration: SmlDeclaration) : Type() {
    val simpleName: String = smlDeclaration.name
    val qualifiedName: String =
        simpleName // TODO qualifiedNameProvider.getFullyQualifiedName(smlDeclaration).toString()

    abstract val isNullable: Boolean
    abstract val isStatic: Boolean

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
    override val isNullable: Boolean,
    override val isStatic: Boolean
) : NamedType(smlClass) {

    override fun toString() = super.toString()
}

data class EnumType(
    val smlEnum: SmlEnum,
    override val isNullable: Boolean,
    override val isStatic: Boolean
) : NamedType(smlEnum) {

    override fun toString() = super.toString()
}

object NothingType : Type() {
    override fun toString() = "Nothing"
}
