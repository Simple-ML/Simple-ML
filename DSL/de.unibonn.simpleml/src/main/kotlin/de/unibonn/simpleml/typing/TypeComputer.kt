package de.unibonn.simpleml.typing

import com.google.inject.Inject
import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlParenthesizedType
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.stdlibAccess.StdlibClasses
import de.unibonn.simpleml.utils.SimpleMLStdlib
import de.unibonn.simpleml.utils.assignedOrNull
import de.unibonn.simpleml.utils.callableOrNull
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

    fun hasPrimitiveType(obj: EObject): Boolean {
        context = obj

        val type = typeOf(obj)
        if (type !is ClassType) {
            return false
        }

        val qualifiedName = type.smlClass.fullyQualifiedNameOrNull()
        return qualifiedName in setOf(
            StdlibClasses.Boolean,
            StdlibClasses.Float,
            StdlibClasses.Int,
            StdlibClasses.String
        )
    }

    private fun EObject.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> Any
            this is SmlAbstractDeclaration -> this.inferType(isStatic)
            this is SmlAbstractExpression -> this.inferType(isStatic)
            this is SmlAbstractType -> this.inferType(isStatic)
            this is SmlYield -> {
                val assigned = assignedOrNull() ?: return Any
                assigned.inferType(isStatic)
            }
            else -> Any
        }
    }

    private fun SmlAbstractDeclaration.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> Any
            this is SmlAttribute -> type.inferType(isStatic = false)
            this is SmlClass -> ClassType(this, isNullable = false, isStatic = isStatic)
            this is SmlEnum -> EnumType(this, isNullable = false, isStatic = isStatic)
            this is SmlEnumVariant -> EnumVariantType(this, isNullable = false, isStatic = isStatic)
            this is SmlFunction -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            this is SmlBlockLambdaResult -> {
                val assigned = assignedOrNull() ?: return Any
                assigned.inferType(isStatic = false)
            }
            this is SmlParameter -> type?.inferType(isStatic = false) ?: Any
            this is SmlPlaceholder -> {
                val assigned = assignedOrNull() ?: return Any
                assigned.inferType(isStatic = false)
            }
            this is SmlResult -> type.inferType(isStatic = false)
            this is SmlStep -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            else -> Any
        }
    }

    private fun SmlAbstractExpression.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> Any
            this is SmlBoolean -> Boolean
            this is SmlFloat -> Float
            this is SmlInt -> Int
            this is SmlString -> String

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
                is SmlBlockLambda -> {
                    val results = callable.lambdaResultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                is SmlStep -> {
                    val results = callable.resultsOrEmpty()
                    when (results.size) {
                        1 -> results.first().inferType(false)
                        else -> TupleType(results.map { it.inferType(false) })
                    }
                }
                else -> Any
            }
            this is SmlInfixOperation -> when (operator) {
                "<", "<=", ">=", ">" -> Boolean
                "==", "!=" -> Boolean
                "===", "!==" -> Boolean
                "or", "and" -> Boolean
                "+", "-", "*", "/" -> when {
                    this.leftOperand.inferType(false) == Int && this.rightOperand.inferType(false) == Int -> Int
                    else -> Float
                }
                "?:" -> Any // TODO
                else -> Nothing
            }
            this is SmlBlockLambda -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                lambdaResultsOrEmpty().map { it.inferType(false) }
            )
            this is SmlMemberAccess -> {
//            if (this.isNullable) {
//                // TODO
//            }
                val member = this.member ?: return Any
                member.inferType(isStatic = false)
            }
            this is SmlNull -> stdlibType(context, StdlibClasses.Any.toString(), isNullable = true)
            this is SmlParenthesizedExpression -> {
                this.expression.inferType(isStatic)
            }
            this is SmlPrefixOperation -> when (operator) {
                "not" -> Boolean
                "-" -> when (this.operand.inferType(false)) {
                    Int -> Int
                    else -> Float
                }
                else -> Nothing
            }
            this is SmlReference -> { // TODO
                val declaration = this.declaration ?: return Any
                declaration.inferType(isStatic = true)
            }

            else -> Any
        }
    }

    private fun SmlAbstractType.inferType(isStatic: Boolean): Type {
        return when {
            this.eIsProxy() -> Any
            this is SmlCallableType -> CallableType(
                parametersOrEmpty().map { it.inferType(false) },
                resultsOrEmpty().map { it.inferType(false) }
            )
            this is SmlMemberType -> {
                val member = this.member ?: return Any
                member.inferType(isStatic = false)
            }
            this is SmlNamedType -> {
                val declaration = this.declaration ?: return NullableAny
                val declarationType = declaration.inferType(isStatic = isStatic)
                when {
                    this.isNullable -> when (declarationType) {
                        is ClassType -> declarationType.copy(isNullable = true)
                        is EnumType -> declarationType.copy(isNullable = true)
                        is EnumVariantType -> declarationType.copy(isNullable = true)
                        else -> NullableAny
                    }
                    else -> declarationType
                }
            }
            this is SmlParenthesizedType -> {
                this.type.inferType(isStatic)
            }
            else -> Any
        }
    }

    private val NullableAny get() = stdlibType(context, StdlibClasses.Any.toString(), isNullable = true)
    private val Any get() = stdlibType(context, StdlibClasses.Any.toString())
    private val Boolean get() = stdlibType(context, StdlibClasses.Boolean.toString())
    private val Float get() = stdlibType(context, StdlibClasses.Float.toString())
    private val Int get() = stdlibType(context, StdlibClasses.Int.toString())
    private val Nothing get() = stdlibType(context, StdlibClasses.Nothing.toString())
    private val String get() = stdlibType(context, StdlibClasses.String.toString())

    internal fun stdlibType(context: EObject, qualifiedName: String, isNullable: Boolean = false): Type {
        return when (val smlClass = stdlib.getClass(context, qualifiedName)) {
            null -> UnresolvedType
            else -> ClassType(smlClass, isNullable, isStatic = false)
        }
    }
}
