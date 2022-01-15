@file:Suppress("unused")

package de.unibonn.simpleml.utils

import de.unibonn.simpleml.staticAnalysis.callableOrNull
import de.unibonn.simpleml.emf.aliasNameOrNull
import de.unibonn.simpleml.emf.assigneesOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.containingBlockLambdaOrNull
import de.unibonn.simpleml.emf.containingStepOrNull
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.isClassMember
import de.unibonn.simpleml.emf.isNamed
import de.unibonn.simpleml.emf.isQualified
import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.memberDeclarationsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.emf.yieldsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlResultList
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.staticAnalysis.isRecursive
import de.unibonn.simpleml.stdlibAccess.isPure
import org.eclipse.emf.ecore.EObject

// Argument ------------------------------------------------------------------------------------------------------------

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

// ArgumentList --------------------------------------------------------------------------------------------------------

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

// Call ----------------------------------------------------------------------------------------------------------------

fun SmlCall.parametersOrNull(): List<SmlParameter>? {
    return callableOrNull()?.parametersOrEmpty()
}

fun SmlCall.resultsOrNull(): List<SmlAbstractDeclaration>? {
    return when (val callable = this.callableOrNull()) {
        is SmlClass -> listOf(callable)
        is SmlEnumVariant -> listOf(callable)
        is SmlFunction -> callable.resultsOrEmpty()
        is SmlCallableType -> callable.resultsOrEmpty()
        is SmlBlockLambda -> callable.lambdaResultsOrEmpty()
        is SmlStep -> callable.resultsOrEmpty()
        else -> null
    }
}

// Class ---------------------------------------------------------------------------------------------------------------

fun SmlClass?.inheritedNonStaticMembersOrEmpty(): Set<SmlAbstractDeclaration> {
    return this?.parentTypesOrEmpty()
        ?.mapNotNull { it.classOrNull() }
        ?.flatMap { it.memberDeclarationsOrEmpty() }
        ?.filter { it is SmlAttribute && !it.isStatic || it is SmlFunction && !it.isStatic }
        ?.toSet()
        .orEmpty()
}

fun SmlClass?.parentClassesOrEmpty() = this.parentTypesOrEmpty().mapNotNull { it.classOrNull() }
fun SmlClass?.parentClassOrNull(): SmlClass? {
    val resolvedParentClasses = this.parentClassesOrEmpty()
    return when (resolvedParentClasses.size) {
        1 -> resolvedParentClasses.first()
        else -> null
    }
}

// Assignee ------------------------------------------------------------------------------------------------------------

fun SmlAbstractAssignee.assignedOrNull(): EObject? {
    return when (val maybeAssigned = this.maybeAssigned()) {
        is AssignedResult.Assigned -> maybeAssigned.assigned
        else -> null
    }
}

sealed interface AssignedResult {
    object Unresolved : AssignedResult
    object NotAssigned : AssignedResult
    sealed class Assigned : AssignedResult {
        abstract val assigned: EObject
    }

    class AssignedExpression(override val assigned: SmlAbstractExpression) : Assigned()
    class AssignedDeclaration(override val assigned: SmlAbstractDeclaration) : Assigned()
}

fun SmlAbstractAssignee.maybeAssigned(): AssignedResult {
    val assignment = this.closestAncestorOrNull<SmlAssignment>() ?: return AssignedResult.Unresolved
    val expression = assignment.expression ?: return AssignedResult.NotAssigned

    val thisIndex = assignment.assigneeList.assignees.indexOf(this)
    return when (expression) {
        is SmlCall -> {
            val results = expression.resultsOrNull() ?: return AssignedResult.Unresolved
            val result = results.getOrNull(thisIndex) ?: return AssignedResult.NotAssigned
            AssignedResult.AssignedDeclaration(result)
        }
        else -> when (thisIndex) {
            0 -> AssignedResult.AssignedExpression(expression)
            else -> AssignedResult.NotAssigned
        }
    }
}

fun SmlAbstractAssignee.indexOrNull(): Int? {
    val assignment = closestAncestorOrNull<SmlAssignment>() ?: return null
    return assignment.assigneesOrEmpty().indexOf(this)
}

// Import --------------------------------------------------------------------------------------------------------------

fun SmlImport.importedNameOrNull(): String? {
    return if (this.alias == null) {
        if (this.isQualified()) {
            this.importedNamespace.split(".").last()
        } else {
            null
        }
    } else {
        this.aliasNameOrNull()
    }
}

fun SmlParameter.usesIn(obj: EObject) = obj.descendants<SmlReference>().filter { it.declaration == this }

// Placeholder ---------------------------------------------------------------------------------------------------------

fun SmlPlaceholder.usesIn(obj: EObject): Sequence<SmlReference> {
    return obj.descendants<SmlAbstractStatement>()
        .dropWhile { it !is SmlAssignment || this !in it.placeholdersOrEmpty() }
        .drop(1)
        .flatMap { statement ->
            statement.descendants<SmlReference>()
                .filter { it.declaration == this }
        }
}

// Result --------------------------------------------------------------------------------------------------------------

fun SmlResult.uniqueYieldOrNull(): SmlYield? {
    val yields = yieldsOrEmpty()
    return when (yields.size) {
        1 -> yields.first()
        else -> null
    }
}

fun SmlResult.yieldsOrEmpty(): List<SmlYield> {
    val resultList = closestAncestorOrNull<SmlResultList>() ?: return emptyList()
    val step = resultList.eContainer() as? SmlStep ?: return emptyList()

    return step.yieldsOrEmpty().filter { it.result == this }
}

// Step ----------------------------------------------------------------------------------------------------------------

fun SmlStep.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }

// Type ----------------------------------------------------------------------------------------------------------------

sealed interface ClassResult {
    object Unresolvable : ClassResult
    object NotAClass : ClassResult
    class Class(val `class`: SmlClass) : ClassResult
}

fun SmlAbstractType?.maybeClass(): ClassResult {
    return when (this) {
        is SmlNamedType -> {
            val declaration = this.declaration
            if (declaration.isResolved()) {
                if (declaration is SmlClass) {
                    ClassResult.Class(declaration)
                } else {
                    ClassResult.NotAClass
                }
            } else {
                ClassResult.Unresolvable
            }
        }
        is SmlMemberType -> this.member.maybeClass()
        else -> ClassResult.Unresolvable
    }
}

fun SmlAbstractType?.classOrNull(): SmlClass? {
    return when (val result = this.maybeClass()) {
        is ClassResult.Class -> result.`class`
        else -> null
    }
}

// TypeArgument --------------------------------------------------------------------------------------------------------

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
    }

    return null
}

// SmlAbstractDeclaration --------------------------------------------------------------------------

fun SmlAbstractDeclaration.isInferredStatic(): Boolean {
    return when {
        !this.isClassMember() -> false
        this is SmlClass || this is SmlEnum -> true
        this is SmlAttribute && this.isStatic -> true
        this is SmlFunction && this.isStatic -> true
        else -> false
    }
}

// SmlBlockLambda ----------------------------------------------------------------------------------

fun SmlBlockLambda.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }


// SmlExpression -----------------------------------------------------------------------------------

fun SmlAbstractExpression.hasSideEffects(): Boolean {
    if (this is SmlCall) {
        if (this.isRecursive()) {
            return true
        }

        val callable = this.callableOrNull()
        return callable is SmlFunction && !callable.isPure() ||
                callable is SmlStep && !callable.isInferredPure() ||
                callable is SmlBlockLambda && !callable.isInferredPure()
    }

    return false
}

// SmlExpressionLambda -----------------------------------------------------------------------------

fun SmlExpressionLambda.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }
