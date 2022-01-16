@file:Suppress("FunctionName")

package de.unibonn.simpleml.staticAnalysis.typing

import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractObject
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
import de.unibonn.simpleml.staticAnalysis.assignedOrNull
import de.unibonn.simpleml.staticAnalysis.callableOrNull
import de.unibonn.simpleml.stdlibAccess.StdlibClass
import org.eclipse.emf.ecore.EObject

fun SmlAbstractObject.type(): Type {
    return inferType(isStatic = false, this)
}

fun SmlAbstractObject.hasPrimitiveType(): Boolean {
    val type = type()
    if (type !is ClassType) {
        return false
    }

    val qualifiedName = type.smlClass
    return qualifiedName in setOf(
        StdlibClass.Boolean,
        StdlibClass.Float,
        StdlibClass.Int,
        StdlibClass.String
    )
}

private fun EObject.inferType(isStatic: Boolean, context: EObject): Type {
    return when {
        this.eIsProxy() -> Any
        this is SmlAbstractDeclaration -> this.inferType(isStatic, context)
        this is SmlAbstractExpression -> this.inferType(isStatic, context)
        this is SmlAbstractType -> this.inferType(isStatic, context)
        this is SmlYield -> {
            val assigned = assignedOrNull() ?: return Any
            assigned.inferType(isStatic, context)
        }
        else -> Any
    }
}

private fun SmlAbstractDeclaration.inferType(isStatic: Boolean, context: EObject): Type {
    return when {
        this.eIsProxy() -> Any
        this is SmlAttribute -> type.inferType(isStatic = false, context)
        this is SmlClass -> ClassType(this, isNullable = false, isStatic = isStatic)
        this is SmlEnum -> EnumType(this, isNullable = false, isStatic = isStatic)
        this is SmlEnumVariant -> EnumVariantType(
            this,
            isNullable = false,
            isStatic = isStatic
        ) // TODO: should be enum type if it has no parameters
        this is SmlFunction -> CallableType(
            parametersOrEmpty().map { it.inferType(false, context) },
            resultsOrEmpty().map { it.inferType(false, context) }
        )
        this is SmlBlockLambdaResult -> {
            val assigned = assignedOrNull() ?: return Any
            assigned.inferType(isStatic = false, context)
        }
        this is SmlParameter -> type?.inferType(isStatic = false, context) ?: Any
        this is SmlPlaceholder -> {
            val assigned = assignedOrNull() ?: return Any
            assigned.inferType(isStatic = false, context)
        }
        this is SmlResult -> type.inferType(isStatic = false, context)
        this is SmlStep -> CallableType(
            parametersOrEmpty().map { it.inferType(false, context) },
            resultsOrEmpty().map { it.inferType(false, context) }
        )
        else -> Any
    }
}

private fun SmlAbstractExpression.inferType(isStatic: Boolean, context: EObject): Type {
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
                    1 -> results.first().inferType(false, context)
                    else -> TupleType(results.map { it.inferType(false, context) })
                }
            }
            is SmlFunction -> {
                val results = callable.resultsOrEmpty()
                when (results.size) {
                    1 -> results.first().inferType(false, context)
                    else -> TupleType(results.map { it.inferType(false, context) })
                }
            }
            is SmlBlockLambda -> {
                val results = callable.lambdaResultsOrEmpty()
                when (results.size) {
                    1 -> results.first().inferType(false, context)
                    else -> TupleType(results.map { it.inferType(false, context) })
                }
            }
            is SmlStep -> {
                val results = callable.resultsOrEmpty()
                when (results.size) {
                    1 -> results.first().inferType(false, context)
                    else -> TupleType(results.map { it.inferType(false, context) })
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
                this.leftOperand.inferType(false, context) == Int && this.rightOperand.inferType(
                    false,
                    context
                ) == Int -> Int
                else -> Float
            }
            "?:" -> Any // TODO
            else -> Nothing
        }
        this is SmlBlockLambda -> CallableType(
            parametersOrEmpty().map { it.inferType(false, context) },
            lambdaResultsOrEmpty().map { it.inferType(false, context) }
        )
        this is SmlMemberAccess -> {
//            if (this.isNullable) {
//                // TODO
//            }
            val member = this.member ?: return Any
            member.inferType(isStatic = false, context)
        }
        this is SmlNull -> ClassType(StdlibClass.Nothing, isNullable = true)
        this is SmlParenthesizedExpression -> {
            this.expression.inferType(isStatic, context)
        }
        this is SmlPrefixOperation -> when (operator) {
            "not" -> Boolean
            "-" -> when (this.operand.inferType(false, context)) {
                Int -> Int
                else -> Float
            }
            else -> Nothing
        }
        this is SmlReference -> { // TODO
            val declaration = this.declaration ?: return Any
            declaration.inferType(isStatic = true, context)
        }

        else -> Any
    }
}

private fun SmlAbstractType.inferType(isStatic: Boolean, context: EObject): Type {
    return when {
        this.eIsProxy() -> Any
        this is SmlCallableType -> CallableType(
            parametersOrEmpty().map { it.inferType(false, context) },
            resultsOrEmpty().map { it.inferType(false, context) }
        )
        this is SmlMemberType -> {
            val member = this.member ?: return Any
            member.inferType(isStatic = false, context)
        }
        this is SmlNamedType -> {
            val declaration = this.declaration ?: return NullableAny
            val declarationType = declaration.inferType(isStatic = isStatic, context)
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
            this.type.inferType(isStatic, context)
        }
        else -> Any
    }
}

private val NullableAny get() = ClassType(StdlibClass.Any, isNullable = true)
private val Any get() = ClassType(StdlibClass.Any)
private val Boolean get() = ClassType(StdlibClass.Boolean)
private val Float get() = ClassType(StdlibClass.Float)
private val Int get() = ClassType(StdlibClass.Int)
private val Nothing get() = ClassType(StdlibClass.Nothing)
private val String get() = ClassType(StdlibClass.String)
