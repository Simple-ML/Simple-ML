@file:Suppress("unused")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractConstraint
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractLocalVariable
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlAbstractProtocolTerm
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAnnotationUseHolder
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlConstraintList
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlProtocol
import de.unibonn.simpleml.simpleML.SmlProtocolBody
import de.unibonn.simpleml.simpleML.SmlProtocolComplement
import de.unibonn.simpleml.simpleML.SmlProtocolReference
import de.unibonn.simpleml.simpleML.SmlProtocolSubterm
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.closestAncestorOrNull
import de.unibonn.simpleml.utils.uniqueOrNull
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

// SmlAbstractDeclaration --------------------------------------------------------------------------

fun SmlAbstractDeclaration?.annotationUsesOrEmpty(): List<SmlAnnotationUse> {
    return this?.annotationUseHolder?.annotations ?: this?.annotations.orEmpty()
}

// SmlAnnotation -----------------------------------------------------------------------------------

fun SmlAnnotation?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlAnnotation?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

// SmlAnnotationUse --------------------------------------------------------------------------------

fun SmlAnnotationUse?.argumentsOrEmpty(): List<SmlArgument> {
    return this?.argumentList?.arguments.orEmpty()
}

// SmlAssignment -----------------------------------------------------------------------------------

fun SmlAssignment?.assigneesOrEmpty(): List<SmlAbstractAssignee> {
    return this?.assigneeList?.assignees
        ?.filterIsInstance<SmlAbstractAssignee>()
        .orEmpty()
}

fun SmlAssignment?.lambdaResultsOrEmpty(): List<SmlBlockLambdaResult> {
    return this.assigneesOrEmpty().filterIsInstance<SmlBlockLambdaResult>()
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

fun SmlClass?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlClass?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlClass?.parentTypesOrEmpty(): List<SmlAbstractType> {
    return this?.parentTypeList?.parentTypes.orEmpty()
}

fun SmlClass?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

fun SmlClass?.membersOrEmpty(): List<SmlAbstractObject> {
    return this?.body?.members.orEmpty()
}

fun SmlClass?.memberDeclarationsOrEmpty(): List<SmlAbstractDeclaration> {
    return this?.body?.members
        ?.filterIsInstance<SmlAbstractDeclaration>()
        .orEmpty()
}

fun SmlClass?.protocolsOrEmpty(): List<SmlProtocol> {
    return this?.body?.members
        ?.filterIsInstance<SmlProtocol>()
        .orEmpty()
}

fun SmlClass?.uniqueProtocolOrNull(): SmlProtocol? {
    return this.protocolsOrEmpty().uniqueOrNull()
}

// SmlCompilationUnit ------------------------------------------------------------------------------

fun SmlCompilationUnit?.memberDeclarationsOrEmpty(): List<SmlAbstractDeclaration> {
    return this?.members
        ?.filterIsInstance<SmlAbstractDeclaration>()
        .orEmpty()
}

/**
 * Returns the unique package declaration contained in the compilation unit or `null` if none or multiple exist.
 */
fun SmlCompilationUnit?.uniquePackageOrNull(): SmlPackage? {
    return this?.members?.filterIsInstance<SmlPackage>()?.uniqueOrNull()
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

fun SmlEnumVariant?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
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

fun SmlFunction?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

// SmlLambda ---------------------------------------------------------------------------------------

fun SmlBlockLambda?.lambdaResultsOrEmpty(): List<SmlBlockLambdaResult> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.lambdaResultsOrEmpty() }
}

fun SmlBlockLambda?.localVariablesOrEmpty(): List<SmlAbstractLocalVariable> {
    return this.parametersOrEmpty() + this.placeholdersOrEmpty()
}

fun SmlBlockLambda?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlBlockLambda?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlBlockLambda?.statementsOrEmpty(): List<SmlAbstractStatement> {
    return this?.body?.statements.orEmpty()
}

// SmlNamedType ------------------------------------------------------------------------------------

fun SmlNamedType?.typeArgumentsOrEmpty(): List<SmlTypeArgument> {
    return this?.typeArgumentList?.typeArguments.orEmpty()
}

// SmlPackage --------------------------------------------------------------------------------------

fun SmlPackage?.memberDeclarationsOrEmpty(): List<SmlAbstractDeclaration> {
    return this?.members
        ?.filterIsInstance<SmlAbstractDeclaration>()
        .orEmpty()
}

// SmlProtocol -------------------------------------------------------------------------------------

fun SmlProtocol?.subtermsOrEmpty(): List<SmlProtocolSubterm> {
    return this?.body.subtermsOrEmpty()
}

fun SmlProtocol?.termOrNull(): SmlAbstractProtocolTerm? {
    return this?.body?.term
}

// SmlProtocolBody ---------------------------------------------------------------------------------

fun SmlProtocolBody?.subtermsOrEmpty(): List<SmlProtocolSubterm> {
    return this?.subtermList?.subterms.orEmpty()
}

// SmlProtocolComplement ---------------------------------------------------------------------------

fun SmlProtocolComplement?.referencesOrEmpty(): List<SmlProtocolReference> {
    return this?.referenceList?.references.orEmpty()
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

fun SmlWorkflow?.statementsOrEmpty(): List<SmlAbstractStatement> {
    return this?.body?.statements.orEmpty()
}

// SmlWorkflowStep ---------------------------------------------------------------------------------

fun SmlStep?.localVariablesOrEmpty(): List<SmlAbstractLocalVariable> {
    return this.parametersOrEmpty() + this.placeholdersOrEmpty()
}

fun SmlStep?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

fun SmlStep?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlStep?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

fun SmlStep?.statementsOrEmpty(): List<SmlAbstractStatement> {
    return this?.body?.statements.orEmpty()
}

/* ********************************************************************************************************************
 * Accessing ancestors                                                                                                *
 * ********************************************************************************************************************/

fun EObject?.containingClassOrNull() = this?.closestAncestorOrNull<SmlClass>()
fun EObject?.containingDeclarationOrNull() = this?.closestAncestorOrNull<SmlAbstractDeclaration>()
fun EObject?.containingEnumOrNull() = this?.closestAncestorOrNull<SmlEnum>()
fun EObject?.containingCompilationUnitOrNull() = this?.closestAncestorOrNull<SmlCompilationUnit>()
fun EObject?.containingFunctionOrNull() = this?.closestAncestorOrNull<SmlFunction>()
fun EObject?.containingLambdaOrNull() = this?.closestAncestorOrNull<SmlBlockLambda>()
fun EObject?.containingPackageOrNull() = this?.closestAncestorOrNull<SmlPackage>()
fun EObject?.containingProtocolOrNull() = this?.closestAncestorOrNull<SmlProtocol>()
fun EObject?.containingWorkflowOrNull() = this?.closestAncestorOrNull<SmlWorkflow>()
fun EObject?.containingWorkflowStepOrNull() = this?.closestAncestorOrNull<SmlStep>()

fun SmlAnnotationUse?.targetOrNull(): SmlAbstractDeclaration? {
    return when (val declaration = this.containingDeclarationOrNull() ?: return null) {
        is SmlAnnotationUseHolder -> declaration.containingDeclarationOrNull()
        else -> declaration
    }
}

/* ********************************************************************************************************************
 * Accessing siblings                                                                                                 *
 * ********************************************************************************************************************/

fun SmlConstraintList.typeParametersOrNull(): List<SmlTypeParameter>? {
    return when (val parent = this.eContainer()) {
        is SmlClass -> parent.typeParametersOrEmpty()
        is SmlEnumVariant -> return parent.typeParametersOrEmpty()
        is SmlFunction -> parent.typeParametersOrEmpty()
        else -> null
    }
}
