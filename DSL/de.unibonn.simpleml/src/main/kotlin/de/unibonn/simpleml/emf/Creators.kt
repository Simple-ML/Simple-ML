@file:Suppress("unused")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAnnotationUseHolder
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssigneeList
import de.unibonn.simpleml.simpleML.SmlBlock
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlImportAlias
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.simpleML.SmlParentTypeList
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlParenthesizedType
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlResultList
import de.unibonn.simpleml.simpleML.SmlStarProjection
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraintList
import de.unibonn.simpleml.simpleML.SmlTypeParameterList
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWildcard
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.XtextResource

private val factory = SimpleMLFactory.eINSTANCE

/**
 * Returns a new [Resource].
 *
 * This can be useful to serialize EObjects that were initialized with the creators in this file rather than generated
 * by the parser, since serialization requires EObjects to be contained in a resource.
 */
fun createSmlDummyResource(
    fileName: String,
    fileExtension: FileExtension,
    compilationUnit: SmlCompilationUnit
): Resource {
    val uri = URI.createURI("dummy:/$fileName.${fileExtension.extension}")
    return XtextResource(uri).apply {
        this.contents += compilationUnit
    }
}

///**
// * Returns a new object of class [SmlAnnotation].
// */
//fun createSmlAnnotation(): SmlAnnotation
//
///**
// * Returns a new object of class [SmlAnnotationUse].
// */
//fun createSmlAnnotationUse(): SmlAnnotationUse

/**
 * Returns a new object of class [SmlAnnotationUseHolder].
 */
private fun createSmlAnnotationUseHolder(annotations: List<SmlAnnotationUse>): SmlAnnotationUseHolder {
    return factory.createSmlAnnotationUseHolder().apply {
        this.annotations += annotations
    }
}

///**
// * Returns a new object of class [SmlArgument].
// */
//fun createSmlArgument(): SmlArgument

/**
 * Returns a new object of class [SmlArgumentList].
 */
private fun createSmlArgumentList(arguments: List<SmlArgument>): SmlArgumentList {
    return factory.createSmlArgumentList().apply {
        this.arguments += arguments
    }
}

/**
 * Returns a new object of class [SmlAssigneeList].
 */
private fun createSmlAssigneeList(assignees: List<SmlAbstractAssignee>): SmlAssigneeList {
    return factory.createSmlAssigneeList().apply {
        this.assignees += assignees
    }
}

///**
// * Returns a new object of class [SmlAssignment].
// */
//fun createSmlAssignment(): SmlAssignment
//
///**
// * Returns a new object of class [SmlAttribute].
// */
//fun createSmlAttribute(): SmlAttribute

/**
 * Returns a new object of class [SmlBlock].
 */
private fun createSmlBlock(statements: List<SmlAbstractStatement>): SmlBlock {
    return factory.createSmlBlock().apply {
        this.statements += statements
    }
}

/**
 * Returns a new object of class [SmlBoolean].
 */
fun createSmlBoolean(value: Boolean): SmlBoolean {
    return factory.createSmlBoolean().apply {
        this.isTrue = value
    }
}

///**
// * Returns a new object of class [SmlCall].
// */
//fun createSmlCall(): SmlCall
//
///**
// * Returns a new object of class [SmlCallableType].
// */
//fun createSmlCallableType(): SmlCallableType
//
///**
// * Returns a new object of class [SmlClass].
// */
//fun createSmlClass(): SmlClass
//
///**
// * Returns a new object of class [SmlClassBody].
// */
//private fun createSmlClassBody(): SmlClassBody
//
///**
// * Returns a new object of class [SmlCompilationUnit].
// */
//fun createSmlCompilationUnit(): SmlCompilationUnit

/**
 * Returns a new object of class [SmlEnum].
 */
fun createSmlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    init: SmlEnum.() -> Unit
): SmlEnum {
    return factory.createSmlEnum().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.init()
    }
}

/**
 * Returns a new object of class [SmlEnumVariant].
 */
fun createSmlEnumVariant(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null
): SmlEnumVariant {
    return factory.createSmlEnumVariant().apply {
        this.name = name
        this.annotations += annotations
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.typeParameterConstraintList = createSmlTypeParameterConstraintList(typeParameterConstraints)
    }
}

/**
 * Adds a new object of class [SmlEnumVariant] to the receiver.
 */
fun SmlEnum.smlEnumVariant(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    typeParameterConstraint: List<SmlTypeParameterConstraint> = emptyList()
) {
    if (this.body == null) {
        this.body = factory.createSmlEnumBody()
    }

    this.body.variants += createSmlEnumVariant(name, annotations, typeParameters, parameters, typeParameterConstraint)
}

///**
// * Returns a new object of class [SmlExpressionStatement].
// */
//fun createSmlExpressionStatement(): SmlExpressionStatement

/**
 * Returns a new object of class [SmlFloat].
 */
fun createSmlFloat(value: Double): SmlFloat {
    return factory.createSmlFloat().apply {
        this.value = value
    }
}

///**
// * Returns a new object of class [SmlFunction].
// */
//fun createSmlFunction(): SmlFunction
//
///**
// * Returns a new object of class [SmlImport].
// */
//fun createSmlImport(): SmlImport

/**
 * Returns a new object of class [SmlImportAlias].
 */
private fun createSmlImportAlias(name: String): SmlImportAlias {
    return factory.createSmlImportAlias().apply {
        this.name = name
    }
}

///**
// * Returns a new object of class [SmlInfixOperation].
// */
//fun createSmlInfixOperation(): SmlInfixOperation

/**
 * Returns a new object of class [SmlInt].
 */
fun createSmlInt(value: Int): SmlInt {
    return factory.createSmlInt().apply {
        this.value = value
    }
}

///**
// * Returns a new object of class [SmlLambda].
// */
//fun createSmlLambda(): SmlLambda
//
///**
// * Returns a new object of class [SmlLambdaResult].
// */
//fun createSmlLambdaResult(): SmlLambdaResult
//
///**
// * Returns a new object of class [SmlMemberAccess].
// */
//fun createSmlMemberAccess(): SmlMemberAccess
//
///**
// * Returns a new object of class [SmlMemberType].
// */
//fun createSmlMemberType(): SmlMemberType
//
///**
// * Returns a new object of class [SmlNamedType].
// */
//fun createSmlNamedType(): SmlNamedType

/**
 * Returns a new object of class [SmlNull].
 */
fun createSmlNull(): SmlNull {
    return factory.createSmlNull()
}

///**
// * Returns a new object of class [SmlPackage].
// */
//fun createSmlPackage(): SmlPackage
//
///**
// * Returns a new object of class [SmlParameter].
// */
//fun createSmlParameter(): SmlParameter

/**
 * Returns a new object of class [SmlParameterList] or `null` if the parameter is `null`.
 */
private fun createSmlParameterList(parameters: List<SmlParameter>?): SmlParameterList? {
    if (parameters == null) {
        return null
    }

    return factory.createSmlParameterList().apply {
        this.parameters += parameters
    }
}

/**
 * Returns a new object of class [SmlParenthesizedExpression].
 */
fun createSmlParenthesizedExpression(expression: SmlAbstractExpression): SmlParenthesizedExpression {
    return factory.createSmlParenthesizedExpression().apply {
        this.expression = expression
    }
}

/**
 * Returns a new object of class [SmlParenthesizedType].
 */
fun createSmlParenthesizedType(type: SmlAbstractType): SmlParenthesizedType {
    return factory.createSmlParenthesizedType().apply {
        this.type = type
    }
}

/**
 * Returns a new object of class [SmlParentTypeList].
 */
private fun createSmlParentTypeList(parentTypes: List<SmlAbstractType>): SmlParentTypeList {
    return factory.createSmlParentTypeList().apply {
        this.parentTypes += parentTypes
    }
}

///**
// * Returns a new object of class [SmlPlaceholder].
// */
//fun createSmlPlaceholder(): SmlPlaceholder
//
///**
// * Returns a new object of class [SmlPrefixOperation].
// */
//fun createSmlPrefixOperation(): SmlPrefixOperation
//
///**
// * Returns a new object of class [SmlReference].
// */
//fun createSmlReference(): SmlReference
//
///**
// * Returns a new object of class [SmlResult].
// */
//fun createSmlResult(): SmlResult

/**
 * Returns a new object of class [SmlResultList].
 */
private fun createSmlResultList(results: List<SmlResult>): SmlResultList {
    return factory.createSmlResultList().apply {
        this.results += results
    }
}

/**
 * Returns a new object of class [SmlStarProjection].
 */
fun createSmlStarProjection(): SmlStarProjection {
    return factory.createSmlStarProjection()
}

/**
 * Returns a new object of class [SmlString].
 */
fun createSmlString(value: String): SmlString {
    return factory.createSmlString().apply {
        this.value = value
    }
}

///**
// * Returns a new object of class [SmlTemplateString].
// */
//fun createSmlTemplateString(): SmlTemplateString
//
///**
// * Returns a new object of class [SmlTemplateStringPart].
// */
//fun createSmlTemplateStringPart(): SmlTemplateStringPart
//
///**
// * Returns a new object of class [SmlTypeArgument].
// */
//fun createSmlTypeArgument(): SmlTypeArgument

/**
 * Returns a new object of class [SmlTypeArgumentList].
 */
private fun createSmlTypeArgumentList(typeArguments: List<SmlTypeArgument>): SmlTypeArgumentList {
    return factory.createSmlTypeArgumentList().apply {
        this.typeArguments += typeArguments
    }
}

///**
// * Returns a new object of class [SmlTypeParameter].
// */
//fun createSmlTypeParameter(): SmlTypeParameter

/**
 * Returns a new object of class [SmlTypeParameterList] or `null` if the parameter is `null`.
 */
private fun createSmlTypeParameterList(typeParameters: List<SmlTypeParameter>?): SmlTypeParameterList? {
    if (typeParameters == null) {
        return null
    }

    return factory.createSmlTypeParameterList().apply {
        this.typeParameters += typeParameters
    }
}

///**
// * Returns a new object of class [SmlTypeParameterConstraint].
// */
//fun createSmlTypeParameterConstraint(): SmlTypeParameterConstraint

/**
 * Returns a new object of class [SmlTypeParameterConstraintList] or `null` if the parameter is `null`.
 */
private fun createSmlTypeParameterConstraintList(constraints: List<SmlTypeParameterConstraint>?): SmlTypeParameterConstraintList? {
    if (constraints == null) {
        return null
    }

    return factory.createSmlTypeParameterConstraintList().apply {
        this.constraints += constraints
    }
}

///**
// * Returns a new object of class [SmlTypeProjection].
// */
//fun createSmlTypeProjection(): SmlTypeProjection

/**
 * Returns a new object of class [SmlUnionType].
 */
fun createSmlUnionType(typeArguments: List<SmlTypeArgument>): SmlUnionType {
    return factory.createSmlUnionType().apply {
        this.typeArgumentList = createSmlTypeArgumentList(typeArguments)
    }
}

/**
 * Returns a new object of class [SmlWildcard].
 */
fun createSmlWildcard(): SmlWildcard {
    return factory.createSmlWildcard()
}

///**
// * Returns a new object of class [SmlWorkflow].
// */
//fun createSmlWorkflow(): SmlWorkflow
//
///**
// * Returns a new object of class [SmlWorkflowStep].
// */
//fun createSmlWorkflowStep(): SmlWorkflowStep
//
///**
// * Returns a new object of class [SmlYield].
// */
//fun createSmlYield(): SmlYield
