@file:Suppress("unused")

package de.unibonn.simpleml.utils

import de.unibonn.simpleml.constants.Modifiers
import de.unibonn.simpleml.emf.containingClassOrNull
import de.unibonn.simpleml.emf.containingLambdaOrNull
import de.unibonn.simpleml.emf.containingWorkflowStepOrNull
import de.unibonn.simpleml.emf.lambdaYieldsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.emf.variantsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssignee
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpression
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStatement
import de.unibonn.simpleml.simpleML.SmlTemplateStringPart
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.stdlib.isPure
import org.eclipse.emf.ecore.EObject

// Argument ------------------------------------------------------------------------------------------------------------

fun SmlArgument.isNamed() = this.parameter != null
fun SmlArgument.isPositional() = this.parameter == null

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

            return argumentList.parametersOrNull()?.getOrNull(thisIndex)
        }
    }
}

// ArgumentList --------------------------------------------------------------------------------------------------------

fun SmlArgumentList.parametersOrNull(): List<SmlParameter>? {
    when (val parent = this.eContainer()) {
        is SmlAnnotationUse -> {
            if (parent.annotation.eIsProxy()) {
                return null
            }

            return parent.annotation.parametersOrEmpty()
        }
        is SmlCall -> return parent.parametersOrNull()
    }

    return emptyList()
}

// Call ----------------------------------------------------------------------------------------------------------------

fun SmlCall.callableOrNull(): EObject? {
    return when (val maybeCallable = this.maybeCallable()) {
        is CallableResult.Callable -> maybeCallable.callable
        else -> null
    }
}

sealed class CallableResult {
    object Unresolvable : CallableResult()
    object NotCallable : CallableResult()
    class Callable(val callable: EObject) : CallableResult()
}

fun SmlCall.maybeCallable(): CallableResult {
    val visited = mutableSetOf<EObject>()
    var current: EObject? = this.receiver
    while (current != null && current !in visited) {
        visited += current

        current = when {
            current.eIsProxy() -> return CallableResult.Unresolvable
            current.isCallable() -> return CallableResult.Callable(current)
            current is SmlCall -> {
                val results = current.resultsOrNull()
                if (results == null || results.size != 1) {
                    return CallableResult.Unresolvable
                }

                results.first()
            }
            current is SmlAssignee -> current.assignedOrNull()
            current is SmlMemberAccess -> current.member.declaration
            current is SmlParameter -> return when (val typeOrNull = current.type) {
                null -> CallableResult.Unresolvable
                is SmlCallableType -> CallableResult.Callable(typeOrNull)
                else -> CallableResult.NotCallable
            }
            current is SmlReference -> current.declaration
            current is SmlResult -> return when (val typeOrNull = current.type) {
                null -> CallableResult.Unresolvable
                is SmlCallableType -> CallableResult.Callable(typeOrNull)
                else -> CallableResult.NotCallable
            }
            else -> return CallableResult.NotCallable
        }
    }

    return CallableResult.Unresolvable
}

fun SmlCall.isRecursive(): Boolean {
    val containingWorkflowStep = this.containingWorkflowStepOrNull() ?: return false
    val containingLambda = this.containingLambdaOrNull()

    val origin = mutableSetOf<EObject>(containingWorkflowStep)
    if (containingLambda != null) {
        origin.add(containingLambda)
    }

    return this.isRecursive(origin, emptySet())
}

private fun SmlCall.isRecursive(origin: Set<EObject>, visited: Set<EObject>): Boolean {
    return when (val callable = this.callableOrNull()) {
        is SmlWorkflowStep -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        is SmlLambda -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        else -> false
    }
}

fun SmlCall.parametersOrNull(): List<SmlParameter>? {
    return when (val callable = this.callableOrNull()) {
        is SmlClass -> callable.parametersOrEmpty()
        is SmlEnumVariant -> callable.parametersOrEmpty()
        is SmlFunction -> callable.parametersOrEmpty()
        is SmlCallableType -> callable.parametersOrEmpty()
        is SmlLambda -> callable.parametersOrEmpty()
        is SmlWorkflowStep -> callable.parametersOrEmpty()
        else -> null
    }
}

fun SmlCall.resultsOrNull(): List<SmlDeclaration>? {
    return when (val callable = this.callableOrNull()) {
        is SmlClass -> listOf(callable)
        is SmlEnumVariant -> listOf(callable)
        is SmlFunction -> callable.resultsOrEmpty()
        is SmlCallableType -> callable.resultsOrEmpty()
        is SmlLambda -> callable.lambdaYieldsOrEmpty()
        is SmlWorkflowStep -> callable.resultsOrEmpty()
        else -> null
    }
}

// Class ---------------------------------------------------------------------------------------------------------------

fun SmlClass?.parentClassesOrEmpty() = this.parentTypesOrEmpty().mapNotNull { it.resolveToClassOrNull() }
fun SmlClass?.parentClassOrNull(): SmlClass? {
    val resolvedParentClasses = this.parentClassesOrEmpty()
    return when (resolvedParentClasses.size) {
        1 -> resolvedParentClasses.first()
        else -> null
    }
}

// Declaration ---------------------------------------------------------------------------------------------------------

fun SmlDeclaration.isStatic(): Boolean {
    return Modifiers.STATIC in this.modifiers || !this.isCompilationUnitMember() &&
        (this is SmlClass || this is SmlEnum)
}

fun SmlDeclaration.isClassMember() = this.containingClassOrNull() != null
fun SmlDeclaration.isCompilationUnitMember(): Boolean {
    return !isClassMember() &&
        (
            this is SmlAnnotation ||
                this is SmlClass ||
                this is SmlEnum ||
                this is SmlFunction ||
                this is SmlWorkflow ||
                this is SmlWorkflowStep
            )
}

// Assignee ------------------------------------------------------------------------------------------------------------

fun SmlAssignee.assignedOrNull(): EObject? {
    return when (val maybeAssigned = this.maybeAssigned()) {
        is AssignedResult.Assigned -> maybeAssigned.assigned
        else -> null
    }
}

sealed class AssignedResult {
    object Unresolved : AssignedResult()
    object NotAssigned : AssignedResult()
    class Assigned(val assigned: EObject) : AssignedResult()
}

fun SmlAssignee.maybeAssigned(): AssignedResult {
    val assignment = this.closestAncestorOrNull<SmlAssignment>() ?: return AssignedResult.Unresolved
    val expression = assignment.expression ?: return AssignedResult.NotAssigned

    val thisIndex = assignment.assigneeList.assignees.indexOf(this)
    return when (expression) {
        is SmlCall -> {
            val results = expression.resultsOrNull() ?: return AssignedResult.Unresolved
            val result = results.getOrNull(thisIndex) ?: return AssignedResult.NotAssigned
            AssignedResult.Assigned(result)
        }
        else -> when (thisIndex) {
            0 -> AssignedResult.Assigned(expression)
            else -> AssignedResult.NotAssigned
        }
    }
}

// EObject -------------------------------------------------------------------------------------------------------------

fun EObject?.isCallable() =
    this is SmlClass ||
        this is SmlEnumVariant ||
        this is SmlFunction ||
        this is SmlCallableType ||
        this is SmlLambda ||
        this is SmlWorkflowStep

// Enum ----------------------------------------------------------------------------------------------------------------

fun SmlEnum?.isConstant() = this.variantsOrEmpty().all { it.parameterList == null }

// Expression ----------------------------------------------------------------------------------------------------------

fun SmlExpression.hasSideEffects(): Boolean {
    if (this is SmlCall) {
        if (this.isRecursive()) {
            return true
        }

        val callable = this.callableOrNull()
        return callable is SmlFunction && !callable.isPure() ||
            callable is SmlWorkflowStep && !callable.isInferredPure() ||
            callable is SmlLambda && !callable.isInferredPure()
    }

    return false
}

// Function ------------------------------------------------------------------------------------------------------------

fun SmlFunction.isMethod() = this.containingClassOrNull() != null

// Import --------------------------------------------------------------------------------------------------------------

fun SmlImport.importedNameOrNull(): String? {
    return if (this.alias == null) {
        if (this.isQualified()) {
            this.importedNamespace.split(".").last()
        } else {
            null
        }
    } else {
        this.aliasName()
    }
}

fun SmlImport.isQualified() = !this.importedNamespace.endsWith(".*")
fun SmlImport.aliasName() = this.alias?.name

// Lambda --------------------------------------------------------------------------------------------------------------

fun SmlLambda.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }

// Parameter -----------------------------------------------------------------------------------------------------------

fun SmlParameter.isRequired() = this.defaultValue == null
fun SmlParameter.isOptional() = this.defaultValue != null

fun SmlParameter.usesIn(obj: EObject) = obj.descendants<SmlReference>().filter { it.declaration == this }

// Placeholder ---------------------------------------------------------------------------------------------------------

fun SmlPlaceholder.usesIn(obj: EObject): Sequence<SmlReference> {
    return obj.descendants<SmlStatement>()
        .dropWhile { it !is SmlAssignment || this !in it.placeholdersOrEmpty() }
        .drop(1)
        .flatMap { statement ->
            statement.descendants<SmlReference>()
                .filter { it.declaration == this }
        }
}

// Template string part ------------------------------------------------------------------------------------------------

fun SmlTemplateStringPart.realValue(): String {
    return this.value
        .removePrefix("}}") // TEMPLATE_STRING_INBETWEEN & TEMPLATE_STRING_END
        .removePrefix("\"") // TEMPLATE_STRING_START
        .removeSuffix("{{") // TEMPLATE_STRING_START & TEMPLATE_STRING_INBETWEEN
        .removeSuffix("\"") // TEMPLATE_STRING_END
}

// Type ----------------------------------------------------------------------------------------------------------------

fun SmlType?.resolveToClassOrNull(): SmlClass? {
    return when (this) {
        is SmlNamedType -> this.declaration as? SmlClass
        is SmlMemberType -> this.member.resolveToClassOrNull()
        else -> null
    }
}

fun SmlType?.resolveToFunctionTypeOrNull() = this as? SmlCallableType

// TypeArgument --------------------------------------------------------------------------------------------------------

fun SmlTypeArgument.isNamed() = this.typeParameter != null
fun SmlTypeArgument.isPositional() = this.typeParameter == null

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

// TypeArgumentList ----------------------------------------------------------------------------------------------------

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

        else -> return null
    }

    return null
}

fun SmlWorkflowStep.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }
