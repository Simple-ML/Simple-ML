@file:Suppress("unused")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignee
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlLambdaYield
import de.unibonn.simpleml.simpleML.SmlLocalVariable
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStatement
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraintList
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.closestAncestorOrNull
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource

/* ********************************************************************************************************************
 * Accessing descendants                                                                                              *
 * ********************************************************************************************************************/

// Resource ----------------------------------------------------------------------------------------

fun Resource?.compilationUnitOrNull(): SmlCompilationUnit? {
    return this?.allContents
        ?.asSequence()
        ?.filterIsInstance<SmlCompilationUnit>()
        ?.firstOrNull()
}

// SmlAnnotation -----------------------------------------------------------------------------------

fun SmlAnnotation?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

// SmlAnnotationUse --------------------------------------------------------------------------------

fun SmlAnnotationUse?.argumentsOrEmpty(): List<SmlArgument> {
    return this?.argumentList?.arguments.orEmpty()
}

// SmlAssignment -----------------------------------------------------------------------------------

fun SmlAssignment?.assigneesOrEmpty(): List<SmlAssignee> {
    return this?.assigneeList?.assignees.orEmpty()
}

fun SmlAssignment?.lambdaYieldsOrEmpty(): List<SmlLambdaYield> {
    return this.assigneesOrEmpty().filterIsInstance<SmlLambdaYield>()
}

fun SmlAssignment?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.assigneesOrEmpty().filterIsInstance<SmlPlaceholder>()
}

fun SmlAssignment?.yieldsOrEmpty(): List<SmlYield> {
    return this.assigneesOrEmpty().filterIsInstance<SmlYield>()
}

// SmlCall -----------------------------------------------------------------------------------------

fun SmlCall?.argumentsOrEmpty(): List<SmlArgument> {
    return this?.argumentList?.arguments.orEmpty()
}

fun SmlCall?.typeArgumentsOrEmpty(): List<SmlTypeArgument> {
    return this?.typeArgumentList?.typeArguments.orEmpty()
}

// SmlCallableType ---------------------------------------------------------------------------------

fun SmlCallableType?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlCallableType?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

// SmlClass ----------------------------------------------------------------------------------------

fun SmlClass?.membersOrEmpty(): List<SmlDeclaration> {
    return this?.body?.members.orEmpty()
}

fun SmlClass?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlClass?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlClass?.typeParameterConstraintsOrEmpty(): List<SmlTypeParameterConstraint> {
    return this?.typeParameterConstraintList?.constraints.orEmpty()
}

fun SmlClass?.parentTypesOrEmpty(): List<SmlType> {
    return this?.parentTypeList?.parentTypes.orEmpty()
}

// SmlCompilationUnit ------------------------------------------------------------------------------

/**
 * Returns the unique package declaration contained in the compilation unit or null if none or multiple exist.
 */
fun SmlCompilationUnit?.packageOrNull(): SmlPackage? {
    val packages = this?.members?.filterIsInstance<SmlPackage>()

    return when (packages?.size) {
        1 -> packages[0]
        else -> null
    }
}

// SmlDeclaration ----------------------------------------------------------------------------------

fun SmlDeclaration?.annotationUsesOrEmpty(): List<SmlAnnotationUse> {
    return this?.annotationHolder?.annotations ?: this?.annotations.orEmpty()
}

// SmlEnum -----------------------------------------------------------------------------------------

fun SmlEnum?.variantsOrEmpty(): List<SmlEnumVariant> {
    return this?.body?.variants.orEmpty()
}

// SmlEnumVariant ----------------------------------------------------------------------------------

fun SmlEnumVariant?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlEnumVariant?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlEnumVariant?.typeParameterConstraintsOrEmpty(): List<SmlTypeParameterConstraint> {
    return this?.typeParameterConstraintList?.constraints.orEmpty()
}

// SmlFunction -------------------------------------------------------------------------------------

fun SmlFunction?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlFunction?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

fun SmlFunction?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlFunction?.typeParameterConstraintsOrEmpty(): List<SmlTypeParameterConstraint> {
    return this?.typeParameterConstraintList?.constraints.orEmpty()
}

// SmlLambda ---------------------------------------------------------------------------------------

fun SmlLambda?.lambdaYieldsOrEmpty(): List<SmlLambdaYield> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.lambdaYieldsOrEmpty() }
}

fun SmlLambda?.localVariablesOrEmpty(): List<SmlLocalVariable> {
    return this.parametersOrEmpty() + this.placeholdersOrEmpty()
}

fun SmlLambda?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlLambda?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlLambda?.statementsOrEmpty(): List<SmlStatement> {
    return this?.body?.statements.orEmpty()
}

// SmlNamedType ------------------------------------------------------------------------------------

fun SmlNamedType?.typeArgumentsOrEmpty(): List<SmlTypeArgument> {
    return this?.typeArgumentList?.typeArguments.orEmpty()
}

// SmlUnionType ------------------------------------------------------------------------------------

fun SmlUnionType?.typeArgumentsOrEmpty(): List<SmlTypeArgument> {
    return this?.typeArgumentList?.typeArguments.orEmpty()
}

// SmlWorkflow -------------------------------------------------------------------------------------

fun SmlWorkflow?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlWorkflow?.statementsOrEmpty(): List<SmlStatement> {
    return this?.body?.statements.orEmpty()
}

// SmlWorkflowStep ---------------------------------------------------------------------------------

fun SmlWorkflowStep?.localVariablesOrEmpty(): List<SmlLocalVariable> {
    return this.parametersOrEmpty() + this.placeholdersOrEmpty()
}

fun SmlWorkflowStep?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlWorkflowStep?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlWorkflowStep?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

fun SmlWorkflowStep?.statementsOrEmpty(): List<SmlStatement> {
    return this?.body?.statements.orEmpty()
}

/* ********************************************************************************************************************
 * Accessing ancestors                                                                                                *
 * ********************************************************************************************************************/

fun EObject?.containingClassOrNull() = this?.closestAncestorOrNull<SmlClass>()
fun EObject?.containingEnumOrNull() = this?.closestAncestorOrNull<SmlEnum>()
fun EObject?.containingCompilationUnitOrNull() = this?.closestAncestorOrNull<SmlCompilationUnit>()
fun EObject?.containingFunctionOrNull() = this?.closestAncestorOrNull<SmlFunction>()
fun EObject?.containingLambdaOrNull() = this?.closestAncestorOrNull<SmlLambda>()
fun EObject?.containingPackageOrNull() = this?.closestAncestorOrNull<SmlPackage>()
fun EObject?.containingWorkflowOrNull() = this?.closestAncestorOrNull<SmlWorkflow>()
fun EObject?.containingWorkflowStepOrNull() = this?.closestAncestorOrNull<SmlWorkflowStep>()

/* ********************************************************************************************************************
 * Accessing siblings                                                                                                 *
 * ********************************************************************************************************************/

fun SmlTypeParameterConstraintList.typeParametersOrNull(): List<SmlTypeParameter>? {
    return when (val parent = this.eContainer()) {
        is SmlClass -> parent.typeParametersOrEmpty()
        is SmlEnumVariant -> return parent.typeParametersOrEmpty()
        is SmlFunction -> parent.typeParametersOrEmpty()
        else -> null
    }
}
