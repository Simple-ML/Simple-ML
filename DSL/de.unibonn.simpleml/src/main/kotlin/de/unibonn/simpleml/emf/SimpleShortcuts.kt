@file:Suppress("unused")

/**
 * Contains shortcuts that simplify working with the EMF model. Since most of these are very straightforward, unit tests
 * are usually not required.
 */

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractCompilationUnitMember
import de.unibonn.simpleml.simpleML.SmlAbstractConstraint
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractLocalVariable
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlAbstractProtocolTerm
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlAnnotationCallHolder
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlConstraintList
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
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
import de.unibonn.simpleml.utils.uniqueOrNull
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/* ********************************************************************************************************************
 * Accessing descendants                                                                                              *
 * ********************************************************************************************************************/

// Resource ----------------------------------------------------------------------------------------

fun Resource.compilationUnitOrNull(): SmlCompilationUnit? {
    return this.allContents
        ?.asSequence()
        ?.filterIsInstance<SmlCompilationUnit>()
        ?.firstOrNull()
}

// SmlAbstractCallable -----------------------------------------------------------------------------

fun SmlAbstractCallable?.parametersOrEmpty(): List<SmlParameter> {
    return this?.parameterList?.parameters.orEmpty()
}

// SmlAbstractDeclaration --------------------------------------------------------------------------

fun SmlAbstractDeclaration?.annotationCallsOrEmpty(): List<SmlAnnotationCall> {
    return this?.annotationCallHolder?.annotationCalls ?: this?.annotationCalls.orEmpty()
}

// SmlAnnotation -----------------------------------------------------------------------------------

fun SmlAnnotation?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

// SmlAnnotationCall -------------------------------------------------------------------------------

fun SmlAnnotationCall?.argumentsOrEmpty(): List<SmlArgument> {
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

// SmlBlockLambda ----------------------------------------------------------------------------------

fun SmlBlockLambda?.lambdaResultsOrEmpty(): List<SmlBlockLambdaResult> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.lambdaResultsOrEmpty() }
}

fun SmlBlockLambda?.localVariablesOrEmpty(): List<SmlAbstractLocalVariable> {
    return this.parametersOrEmpty() + this.placeholdersOrEmpty()
}

fun SmlBlockLambda?.placeholdersOrEmpty(): List<SmlPlaceholder> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.placeholdersOrEmpty() }
}

fun SmlBlockLambda?.statementsOrEmpty(): List<SmlAbstractStatement> {
    return this?.body?.statements.orEmpty()
}

// SmlCall -----------------------------------------------------------------------------------------

fun SmlCall?.argumentsOrEmpty(): List<SmlArgument> {
    return this?.argumentList?.arguments.orEmpty()
}

fun SmlCall?.typeArgumentsOrEmpty(): List<SmlTypeArgument> {
    return this?.typeArgumentList?.typeArguments.orEmpty()
}

// SmlCallableType ---------------------------------------------------------------------------------

fun SmlCallableType?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

// SmlClass ----------------------------------------------------------------------------------------

fun SmlClass?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
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

fun SmlClass.uniqueProtocolOrNull(): SmlProtocol? {
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
fun SmlCompilationUnit.uniquePackageOrNull(): SmlPackage? {
    return this.members?.filterIsInstance<SmlPackage>()?.uniqueOrNull()
}

// SmlEnum -----------------------------------------------------------------------------------------

fun SmlEnum?.variantsOrEmpty(): List<SmlEnumVariant> {
    return this?.body?.variants.orEmpty()
}

// SmlEnumVariant ----------------------------------------------------------------------------------

fun SmlEnumVariant?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlEnumVariant?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

// SmlFunction -------------------------------------------------------------------------------------

fun SmlFunction?.resultsOrEmpty(): List<SmlResult> {
    return this?.resultList?.results.orEmpty()
}

fun SmlFunction?.typeParametersOrEmpty(): List<SmlTypeParameter> {
    return this?.typeParameterList?.typeParameters.orEmpty()
}

fun SmlFunction?.constraintsOrEmpty(): List<SmlAbstractConstraint> {
    return this?.constraintList?.constraints.orEmpty()
}

// SmlImport ---------------------------------------------------------------------------------------

fun SmlImport.aliasNameOrNull(): String? {
    return this.alias?.name
}

fun SmlImport.importedNameOrNull(): String? {
    return when (alias) {
        null -> when {
            isQualified() -> importedNamespace.split(".").last()
            else -> null
        }
        else -> aliasNameOrNull()
    }
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

fun SmlProtocol.termOrNull(): SmlAbstractProtocolTerm? {
    return this.body?.term
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

fun SmlStep?.yieldsOrEmpty(): List<SmlYield> {
    return this.statementsOrEmpty()
        .filterIsInstance<SmlAssignment>()
        .flatMap { it.yieldsOrEmpty() }
}

/* ********************************************************************************************************************
 * Accessing ancestors                                                                                                *
 * ********************************************************************************************************************/

fun EObject.containingBlockLambdaOrNull() = this.closestAncestorOrNull<SmlBlockLambda>()
fun EObject.containingCallableOrNull() = this.closestAncestorOrNull<SmlAbstractCallable>()
fun EObject.containingClassOrNull() = this.closestAncestorOrNull<SmlClass>()
fun EObject.containingCompilationUnitOrNull() = this.closestAncestorOrNull<SmlCompilationUnit>()
fun EObject.containingDeclarationOrNull() = this.closestAncestorOrNull<SmlAbstractDeclaration>()
fun EObject.containingEnumOrNull() = this.closestAncestorOrNull<SmlEnum>()
fun EObject.containingExpressionLambdaOrNull() = this.closestAncestorOrNull<SmlExpressionLambda>()
fun EObject.containingFunctionOrNull() = this.closestAncestorOrNull<SmlFunction>()
fun EObject.containingPackageOrNull() = this.closestAncestorOrNull<SmlPackage>()
fun EObject.containingProtocolOrNull() = this.closestAncestorOrNull<SmlProtocol>()
fun EObject.containingStepOrNull() = this.closestAncestorOrNull<SmlStep>()
fun EObject.containingWorkflowOrNull() = this.closestAncestorOrNull<SmlWorkflow>()

fun SmlAnnotationCall.targetOrNull(): SmlAbstractDeclaration? {
    return when (val declaration = this.containingDeclarationOrNull() ?: return null) {
        is SmlAnnotationCallHolder -> declaration.containingDeclarationOrNull()
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

/* ********************************************************************************************************************
 * Checks                                                                                                             *
 * ********************************************************************************************************************/

// SmlAbstractDeclaration --------------------------------------------------------------------------

fun SmlAbstractDeclaration.isClassMember(): Boolean {
    return containingClassOrNull() != null
}

fun SmlAbstractDeclaration.isCompilationUnitMember(): Boolean {
    return !isClassMember() && this is SmlAbstractCompilationUnitMember
}

@OptIn(ExperimentalContracts::class)
fun SmlAbstractDeclaration?.isResolved(): Boolean {
    contract {
        returns(true) implies (this@isResolved != null)
    }

    return (this != null) && !this.eIsProxy()
}

// SmlArgument -------------------------------------------------------------------------------------

fun SmlArgument.isNamed() = parameter != null
fun SmlArgument.isPositional() = parameter == null

// SmlEnum -----------------------------------------------------------------------------------------

fun SmlEnum.isConstant(): Boolean {
    return variantsOrEmpty().all { it.parametersOrEmpty().isEmpty() }
}

// SmlFunction -----------------------------------------------------------------------------------

fun SmlFunction.isMethod() = containingClassOrNull() != null

// SmlImport ---------------------------------------------------------------------------------------

fun SmlImport.isQualified() = !importedNamespace.endsWith(".*")
fun SmlImport.isWildcard() = importedNamespace.endsWith(".*")

// SmlParameter ------------------------------------------------------------------------------------

fun SmlParameter.isRequired() = defaultValue == null && !isVariadic
fun SmlParameter.isOptional() = defaultValue != null

// SmlTypeArgument ---------------------------------------------------------------------------------

fun SmlTypeArgument.isNamed() = typeParameter != null
fun SmlTypeArgument.isPositional() = typeParameter == null

/* ********************************************************************************************************************
 * Conversions                                                                                                        *
 * ********************************************************************************************************************/

// SmlAbstractDeclaration --------------------------------------------------------------------------

fun SmlAbstractDeclaration.asResolvedOrNull(): SmlAbstractDeclaration? {
    return when {
        isResolved() -> this
        else -> null
    }
}
