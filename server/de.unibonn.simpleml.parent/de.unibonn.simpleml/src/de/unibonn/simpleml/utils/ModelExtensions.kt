package de.unibonn.simpleml.utils

import de.unibonn.simpleml.simpleML.*
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource

// Annotation ----------------------------------------------------------------------------------------------------------

fun SmlAnnotation?.parametersOrEmpty() = this?.parameterList?.parameters.orEmpty()


// Annotation Use ------------------------------------------------------------------------------------------------------

fun SmlAnnotationUse?.argumentsOrEmpty() = this?.argumentList?.arguments.orEmpty()


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
        is SmlAnnotationUse -> return parent.annotation.parametersOrEmpty()
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

fun SmlCall.parametersOrNull(): List<SmlParameter>? {
    return when (val callable = this.callableOrNull()) {
        is SmlClass -> callable.parametersOrEmpty()
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
        is SmlFunction -> callable.resultsOrEmpty()
        is SmlCallableType -> callable.resultsOrEmpty()
        is SmlLambda -> callable.lambdaYieldsOrEmpty()
        is SmlWorkflowStep -> callable.resultsOrEmpty()
        else -> null
    }
}

fun SmlCall?.argumentsOrEmpty() = this?.argumentList?.arguments.orEmpty()
fun SmlCall?.typeArgumentsOrEmpty() = this?.typeArgumentList?.typeArguments.orEmpty()


// ClassOrInterface ----------------------------------------------------------------------------------------------------


fun SmlClassOrInterface?.membersOrEmpty() = this?.body?.members.orEmpty()

fun SmlClassOrInterface?.parametersOrEmpty() = this?.constructor?.parameterList?.parameters.orEmpty()
fun SmlClassOrInterface?.typeParametersOrEmpty() = this?.typeParameterList?.typeParameters.orEmpty()
fun SmlClassOrInterface?.typeParameterConstraintsOrEmpty() = this?.typeParameterConstraintList?.constraints.orEmpty()

fun SmlClassOrInterface?.parentTypesOrEmpty() = this?.parentTypeList?.parentTypes.orEmpty()
fun SmlClassOrInterface?.parentClassesOrInterfacesOrEmpty() = this.parentClassesOrEmpty() + this.parentInterfacesOrEmpty()
fun SmlClassOrInterface?.parentClassesOrEmpty() = this.parentTypesOrEmpty().mapNotNull { it.resolveToClassOrNull() }
fun SmlClassOrInterface?.parentClassOrNull(): SmlClass? {
    val resolvedParentClasses = this.parentClassesOrEmpty()
    return when (resolvedParentClasses.size) {
        1 -> resolvedParentClasses.first()
        else -> null
    }
}

fun SmlClassOrInterface?.parentInterfacesOrEmpty() = this.parentTypesOrEmpty().mapNotNull { it.resolveToInterfaceOrNull() }


// Compilation Unit ----------------------------------------------------------------------------------------------------

fun SmlCompilationUnit?.membersOrEmpty() = this?.members.orEmpty()


// Declaration ---------------------------------------------------------------------------------------------------------

fun SmlDeclaration.isDeprecated() = SML_DEPRECATED in this.modifiers
fun SmlDeclaration.isOpen(): Boolean {
    return SML_OPEN in this.modifiers || this is SmlInterface || this is SmlFunction && this.isInterfaceMember()
}

fun SmlDeclaration.isOverride() = SML_OVERRIDE in this.modifiers
fun SmlDeclaration.isPure() = SML_PURE in this.modifiers
fun SmlDeclaration.isStatic(): Boolean {
    return SML_STATIC in this.modifiers || !this.isCompilationUnitMember() &&
            (this is SmlClass || this is SmlEnum || this is SmlInterface)
}

fun SmlDeclaration.isClassOrInterfaceMember() = this.containingClassOrInterfaceOrNull() != null
fun SmlDeclaration.isClassMember() = this.containingClassOrInterfaceOrNull() is SmlClass
fun SmlDeclaration.isInterfaceMember() = this.containingClassOrInterfaceOrNull() is SmlInterface
fun SmlDeclaration.isCompilationUnitMember(): Boolean {
    return !isClassOrInterfaceMember() &&
            (this is SmlAnnotation
                    || this is SmlClass
                    || this is SmlEnum
                    || this is SmlFunction
                    || this is SmlInterface
                    || this is SmlWorkflow
                    || this is SmlWorkflowStep)
}


// Assignment ----------------------------------------------------------------------------------------------------------

fun SmlAssignment.assigneesOrEmpty() = this.assigneeList?.assignees.orEmpty()
fun SmlAssignment.lambdaYieldsOrEmpty() = this.assigneesOrEmpty().filterIsInstance<SmlLambdaYield>()
fun SmlAssignment.placeholdersOrEmpty() = this.assigneesOrEmpty().filterIsInstance<SmlPlaceholder>()
fun SmlAssignment.yieldsOrEmpty() = this.assigneesOrEmpty().filterIsInstance<SmlYield>()


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

fun EObject?.containingClassOrInterfaceOrNull() = this?.closestAncestorOrNull<SmlClassOrInterface>()
fun EObject?.containingClassOrNull() = this?.closestAncestorOrNull<SmlClass>()
fun EObject?.containingEnumOrNull() = this?.closestAncestorOrNull<SmlEnum>()
fun EObject?.containingInterfaceOrNull() = this?.closestAncestorOrNull<SmlInterface>()
fun EObject?.containingCompilationUnitOrNull() = this?.closestAncestorOrNull<SmlCompilationUnit>()
fun EObject?.containingFunctionOrNull() = this?.closestAncestorOrNull<SmlFunction>()
fun EObject?.containingLambdaOrNull() = this?.closestAncestorOrNull<SmlLambda>()
fun EObject?.containingWorkflowOrNull() = this?.closestAncestorOrNull<SmlWorkflow>()

fun EObject?.isCallable() =
        this is SmlClass ||
                this is SmlFunction ||
                this is SmlCallableType ||
                this is SmlLambda ||
                this is SmlWorkflowStep

fun EObject.isInStubFile() = this.eResource().isStubFile()


// Enum ----------------------------------------------------------------------------------------------------------------

fun SmlEnum?.instancesOrEmpty() = this?.body?.instances.orEmpty()


// Expression ----------------------------------------------------------------------------------------------------------

fun SmlExpression.hasSideEffects(): Boolean {
    if (this is SmlCall) {
        val callable = this.callableOrNull()
        return callable is SmlFunction && !callable.isPure() || callable is SmlWorkflowStep && !callable.isPure()
    }

    return false
}


// Function ------------------------------------------------------------------------------------------------------------

fun SmlFunction.isMethod() = this.containingClassOrNull() != null

fun SmlFunction?.parametersOrEmpty() = this?.parameterList?.parameters.orEmpty()
fun SmlFunction?.resultsOrEmpty() = this?.resultList?.results.orEmpty()
fun SmlFunction?.typeParametersOrEmpty() = this?.typeParameterList?.typeParameters.orEmpty()
fun SmlFunction?.typeParameterConstraintsOrEmpty() = this?.typeParameterConstraintList?.constraints.orEmpty()


// Function Type -------------------------------------------------------------------------------------------------------

fun SmlCallableType?.parametersOrEmpty() = this?.parameterList?.parameters.orEmpty()
fun SmlCallableType?.resultsOrEmpty() = this?.resultList?.results.orEmpty()


// Import --------------------------------------------------------------------------------------------------------------

fun SmlImport.isQualified() = !this.importedNamespace.endsWith(".*")


// Lambda --------------------------------------------------------------------------------------------------------------

fun SmlLambda?.lambdaYieldsOrEmpty(): List<SmlLambdaYield> {
    return this.statementsOrEmpty()
            .filterIsInstance<SmlAssignment>()
            .flatMap { it.lambdaYieldsOrEmpty() }
}

fun SmlLambda?.localVariablesOrEmpty() = this.parametersOrEmpty() + this.placeholdersOrEmpty()
fun SmlLambda?.parametersOrEmpty() = this?.parameterList?.parameters.orEmpty()
fun SmlLambda?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
            .filterIsInstance<SmlAssignment>()
            .flatMap { it.placeholdersOrEmpty() }
}

fun SmlLambda?.statementsOrEmpty() = this?.body?.statements.orEmpty()


// Named Type ----------------------------------------------------------------------------------------------------------

fun SmlNamedType?.typeArgumentsOrEmpty() = this?.typeArgumentList?.typeArguments.orEmpty()


// Parameter -----------------------------------------------------------------------------------------------------------

fun SmlParameter.isRequired() = this.defaultValue == null
fun SmlParameter.isOptional() = this.defaultValue != null

fun SmlParameter.usesIn(obj: EObject) =
        obj.eAllContents()
                .asSequence()
                .filter { it is SmlReference && it.declaration == this }


// Placeholder ---------------------------------------------------------------------------------------------------------

fun SmlPlaceholder.usesIn(obj: EObject): Sequence<SmlReference> {
    return obj.eAllContents()
            .asSequence()
            .filterIsInstance<SmlStatement>()
            .dropWhile { it !is SmlAssignment || this !in it.placeholdersOrEmpty() }
            .drop(1)
            .flatMap { statement ->
                statement.eAllContents()
                        .asSequence()
                        .filterIsInstance<SmlReference>()
                        .filter { it.declaration == this }
            }
}


// Resource ------------------------------------------------------------------------------------------------------------

fun Resource?.compilationUnitOrNull() = this?.allContents
        ?.asSequence()
        ?.filterIsInstance<SmlCompilationUnit>()
        ?.firstOrNull()

fun Resource.isStubFile(): Boolean {
    this.eAdapters().filterIsInstance<OriginalFilePath>().firstOrNull()?.let {
        return it.path.endsWith(".stub.simpleml")
    }

    return this.uri.toString().endsWith(".stub.simpleml")
}


// Type ----------------------------------------------------------------------------------------------------------------

fun SmlType?.resolveToClassOrInterfaceOrNull(): SmlClassOrInterface? {
    return when (this) {
        is SmlNamedType -> this.declaration as? SmlClassOrInterface
        is SmlMemberType -> this.member.resolveToClassOrInterfaceOrNull()
        else -> null
    }
}

fun SmlType?.resolveToClassOrNull() = this.resolveToClassOrInterfaceOrNull() as? SmlClass
fun SmlType?.resolveToFunctionTypeOrNull() = this as? SmlCallableType
fun SmlType?.resolveToInterfaceOrNull() = this.resolveToClassOrInterfaceOrNull() as? SmlInterface


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
                is SmlClassOrInterface -> return callable.typeParametersOrEmpty()
                is SmlFunction -> return callable.typeParametersOrEmpty()
            }
        }
        is SmlNamedType -> {
            when (val declaration = parent.declaration) {
                is SmlClassOrInterface -> return declaration.typeParametersOrEmpty()
                is SmlFunction -> return declaration.typeParametersOrEmpty()
            }
        }

        else -> return null
    }

    return null
}

// TypeParameterConstraintList -----------------------------------------------------------------------------------------

fun SmlTypeParameterConstraintList.typeParametersOrNull(): List<SmlTypeParameter>? {
    return when (val parent = this.eContainer()) {
        is SmlClassOrInterface -> parent.typeParametersOrEmpty()
        is SmlFunction -> parent.typeParametersOrEmpty()
        else -> null
    }
}


// UnionType -----------------------------------------------------------------------------------------------------------

fun SmlUnionType?.typeArgumentsOrEmpty() = this?.typeArgumentList?.typeArguments.orEmpty()


// Workflow ------------------------------------------------------------------------------------------------------------

fun SmlWorkflow?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
            .filterIsInstance<SmlAssignment>()
            .flatMap { it.placeholdersOrEmpty() }
}

fun SmlWorkflow?.statementsOrEmpty() = this?.body?.statements.orEmpty()


// Workflow Steps ------------------------------------------------------------------------------------------------------

fun SmlWorkflowStep?.localVariablesOrEmpty() = this.parametersOrEmpty() + this.placeholdersOrEmpty()
fun SmlWorkflowStep?.parametersOrEmpty() = this?.parameterList?.parameters.orEmpty()
fun SmlWorkflowStep?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
            .filterIsInstance<SmlAssignment>()
            .flatMap { it.placeholdersOrEmpty() }
}

fun SmlWorkflowStep?.resultsOrEmpty() = this?.resultList?.results.orEmpty()
fun SmlWorkflowStep?.statementsOrEmpty() = this?.body?.statements.orEmpty()