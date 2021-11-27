package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumInstance
import de.unibonn.simpleml.simpleML.SmlExpression
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlLambdaYield
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.LIB_ANY
import de.unibonn.simpleml.utils.LIB_BOOLEAN
import de.unibonn.simpleml.utils.LIB_FLOAT
import de.unibonn.simpleml.utils.LIB_INT
import de.unibonn.simpleml.utils.LIB_STRING
import de.unibonn.simpleml.utils.QualifiedNameProvider
import de.unibonn.simpleml.utils.SimpleMLStdlib
import de.unibonn.simpleml.utils.assignedOrNull
import de.unibonn.simpleml.utils.callableOrNull
import de.unibonn.simpleml.utils.containingEnumOrNull
import de.unibonn.simpleml.utils.lambdaYieldsOrEmpty
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import org.eclipse.emf.ecore.EObject

@Suppress("PrivatePropertyName")
class TypeComputer @Inject constructor(
    private val qualifiedNameProvider: QualifiedNameProvider,
    private val stdlib: SimpleMLStdlib
) {

    private lateinit var context: EObject

    fun typeOf(obj: EObject): Type {
        context = obj
        return obj.inferType(isStatic = false)
    }

    fun hasPrimitiveType(obj: EObject): Boolean {
        context = obj

        val type = typeOf(obj)
        if (type !is ClassType) {
            return false
        }

        val qualifiedName = qualifiedNameProvider.qualifiedNameOrNull(type.smlClass)
        return qualifiedName in setOf(LIB_BOOLEAN, LIB_FLOAT, LIB_INT, LIB_STRING)
    }

    private fun EObject.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> ANY
            this is SmlDeclaration -> this.inferType(isStatic)
            this is SmlExpression -> this.inferType(isStatic)
            this is SmlType -> this.inferType(isStatic)
            this is SmlYield -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic)
            }
            else -> ANY
        }
    }

    private fun SmlDeclaration.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> ANY
            this is SmlAttribute -> type.inferType(isStatic = false)
            this is SmlClass -> ClassType(this, isNullable = false, isStatic = isStatic)
            this is SmlEnum -> EnumType(this, isNullable = false, isStatic = isStatic)
            this is SmlEnumInstance -> {
                val enum = containingEnumOrNull() ?: return ANY
                EnumType(enum, isNullable = false, isStatic = false)
            }
            this is SmlFunction -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            this is SmlInterface -> InterfaceType(this, isNullable = false, isStatic = isStatic)
            this is SmlLambdaYield -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic = false)
            }
            this is SmlParameter -> type.inferType(isStatic = false)
            this is SmlPlaceholder -> {
                val assigned = assignedOrNull() ?: return ANY
                assigned.inferType(isStatic = false)
            }
            this is SmlResult -> type.inferType(isStatic = false)
            this is SmlWorkflowStep -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            else -> ANY
        }
    }

    private fun SmlExpression.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> ANY
            this is SmlBoolean -> BOOLEAN
            this is SmlFloat -> FLOAT
            this is SmlInt -> INT
            this is SmlString -> STRING

            this is SmlCall -> when (val callable = callableOrNull()) {
                is SmlClass -> ClassType(callable, isNullable = false, isStatic = false)
                is SmlCallableType -> {
                    val results = callable.resultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                is SmlFunction -> {
                    val results = callable.resultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                is SmlLambda -> {
                    val results = callable.lambdaYieldsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                is SmlWorkflowStep -> {
                    val results = callable.resultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                else -> ANY
            }
            this is SmlInfixOperation -> when (operator) {
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
            this is SmlLambda -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                lambdaYieldsOrEmpty().map { it.inferType(false) }
            )
            this is SmlMemberAccess -> {
//            if (this.isNullable) {
//                // TODO
//            }
                val member = this.member ?: return ANY
                member.inferType(isStatic = false)
            }
            this is SmlNull -> stdlibType(context, LIB_ANY, isNullable = true)
            this is SmlPrefixOperation -> when (operator) {
                "not" -> BOOLEAN
                "-" -> when (this.operand.inferType(false)) {
                    INT -> INT
                    else -> FLOAT
                }
                else -> NothingType
            }
            this is SmlReference -> { // TODO
                val declaration = this.declaration ?: return ANY
                declaration.inferType(isStatic = true)
            }

            else -> ANY
        }
    }

    private fun SmlType.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> ANY
            this is SmlCallableType -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            this is SmlMemberType -> {
                val member = this.member ?: return ANY
                member.inferType(isStatic = false)
            }
            this is SmlNamedType -> {
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
        return when (val smlClass = stdlib.getClass(context, qualifiedName)) {
            null -> NothingType
            else -> ClassType(smlClass, isNullable, isStatic = false)
        }
    }
}
