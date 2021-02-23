package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.utils.*
import org.eclipse.emf.ecore.EObject

@Suppress("PrivatePropertyName")
class TypeComputer @Inject constructor(
        private val stdlib: SimpleMLStdlib
) {
    private lateinit var context: EObject

    fun typeOf(obj: EObject): Type {
        context = obj
        return obj.inferType(isStatic = false)
    }

    private fun EObject.inferType(isStatic: Boolean): Type {
        return when (this) {
            is SmlDeclaration -> this.inferType(isStatic)
            is SmlExpression -> this.inferType(isStatic)
            is SmlType -> this.inferType(isStatic)
            is SmlYield -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic)
            }
            else -> ANY
        }
    }

    private fun SmlDeclaration.inferType(isStatic: Boolean): Type {
        return when (this) {
            is SmlAttribute -> type.inferType(isStatic = false)
            is SmlClass -> ClassType(this, isNullable = false, isStatic = isStatic)
            is SmlEnum -> EnumType(this, isNullable = false, isStatic = isStatic)
            is SmlEnumInstance -> {
                val enum = containingEnumOrNull() ?: return ANY
                EnumType(enum, isNullable = false, isStatic = false)
            }
            is SmlFunction -> CallableType(
                    parametersOrEmpty().map { it.inferType(false) },
                    resultsOrEmpty().map { it.inferType(false) }
            )
            is SmlInterface -> InterfaceType(this, isNullable = false, isStatic = isStatic)
            is SmlLambdaYield -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic = false)
            }
            is SmlParameter -> type.inferType(isStatic = false)
            is SmlPlaceholder -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic = false)
            }
            is SmlResult -> type.inferType(isStatic = false)
            else -> ANY
        }
    }

    private fun SmlExpression.inferType(isStatic: Boolean): Type {
        return when (this) {
            is SmlBoolean -> BOOLEAN
            is SmlFloat -> FLOAT
            is SmlInt -> INT
            is SmlString -> STRING

            is SmlCall -> when (val callable = callableOrNull()) {
                is SmlClass -> ClassType(callable, isNullable = false, isStatic = false)
                is SmlFunction -> {
                    val results = callable.resultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                else -> ANY
            }
            is SmlInfixOperation -> when (operator) {
                "<", "<=", ">=", ">" -> BOOLEAN
                "==", "!=" -> BOOLEAN
                "===", "!==" -> BOOLEAN
                "or", "and" -> BOOLEAN
                "+", "-", "*", "/" -> when {
                    this.leftOperand.inferType(false) == INT && this.rightOperand.inferType(false) == INT -> INT
                    else -> FLOAT
                }
                "?:" -> ANY // TODO
                else -> NothingType
            }
            is SmlLambda -> CallableType(
                    parametersOrEmpty().map { it.inferType(false) },
                    lambdaYieldsOrEmpty().map { it.inferType(false) }
            )
            is SmlMemberAccess -> {
//            if (this.isNullable) {
//                // TODO
//            }
                val member = this.member ?: return ANY
                member.inferType(isStatic = false)
            }
            is SmlNull -> stdlibType(context, LIB_ANY, isNullable = true)
            is SmlPrefixOperation -> when (operator) {
                "not" -> BOOLEAN
                "-" -> when (this.operand.inferType(false)) {
                    INT -> INT
                    else -> FLOAT
                }
                else -> NothingType
            }
            is SmlReference -> { // TODO
                val declaration = this.declaration ?: return ANY
                declaration.inferType(isStatic = true)
            }

            else -> ANY
        }
    }

    private fun SmlType.inferType(isStatic: Boolean): Type {
        return when (this) {
            is SmlCallableType -> CallableType(
                    parametersOrEmpty().map { it.inferType(false) },
                    resultsOrEmpty().map { it.inferType(false) }
            )
            is SmlMemberType -> {
                val member = this.member ?: return ANY
                member.inferType(isStatic = false)
            }
            is SmlNamedType -> {
                val declaration = this.declaration ?: return ANY
                declaration.inferType(isStatic = isStatic)
            }
            else -> ANY
        }
    }

    private val ANY get() = stdlibType(context, LIB_ANY)
    private val BOOLEAN get() = stdlibType(context, LIB_BOOLEAN)
    private val FLOAT get() = stdlibType(context, LIB_FLOAT)
    private val INT get() = stdlibType(context, LIB_INT)
    private val STRING get() = stdlibType(context, LIB_STRING)

    fun stdlibType(context: EObject, qualifiedName: String, isNullable: Boolean = false): Type {
        val smlClass = stdlib.getClass(context, qualifiedName)
        return when (smlClass) {
            null -> NothingType
            else -> ClassType(smlClass, isNullable, isStatic = false)
        }
    }
}