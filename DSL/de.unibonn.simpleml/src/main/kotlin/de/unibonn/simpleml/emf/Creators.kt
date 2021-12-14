@file:Suppress("unused")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractClassMember
import de.unibonn.simpleml.simpleML.SmlAbstractCompilationUnitMember
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractNamedTypeDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractPackageMember
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAnnotationUseHolder
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssigneeList
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBlock
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlImportAlias
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlLambdaResult
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.simpleML.SmlParentTypeList
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlParenthesizedType
import de.unibonn.simpleml.simpleML.SmlReference
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
import de.unibonn.simpleml.simpleML.SmlYield
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

/**
 * Returns a new object of class [SmlAnnotation].
 */
fun createSmlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter>? = null
): SmlAnnotation {
    return factory.createSmlAnnotation().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.parameterList = createSmlParameterList(parameters)
    }
}

/**
 * Adds a new object of class [SmlAnnotation] to the receiver.
 */
fun SmlCompilationUnit.smlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter>? = null
) {
    this.addMember(createSmlAnnotation(name, annotations, parameters))
}

/**
 * Adds a new object of class [SmlAnnotation] to the receiver.
 */
fun SmlPackage.smlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter>? = null
) {
    this.addMember(createSmlAnnotation(name, annotations, parameters))
}

/**
 * Returns a new object of class [SmlAnnotationUse].
 */
fun createSmlAnnotationUse(annotation: SmlAnnotation, arguments: List<SmlArgument>? = null): SmlAnnotationUse {
    return factory.createSmlAnnotationUse().apply {
        this.annotation = annotation
        this.argumentList = createSmlArgumentList(arguments)
    }
}

/**
 * Returns a new object of class [SmlAnnotationUse] that points to an annotation with the given name.
 */
fun createSmlAnnotationUse(annotationName: String, arguments: List<SmlArgument>? = null): SmlAnnotationUse {
    return createSmlAnnotationUse(
        createSmlAnnotation(annotationName),
        arguments
    )
}

/**
 * Returns a new object of class [SmlAnnotationUseHolder].
 */
private fun createSmlAnnotationUseHolder(annotations: List<SmlAnnotationUse>): SmlAnnotationUseHolder {
    return factory.createSmlAnnotationUseHolder().apply {
        this.annotations += annotations
    }
}

/**
 * Returns a new object of class [SmlArgument].
 */
fun createSmlArgument(value: SmlAbstractExpression, parameter: SmlParameter? = null): SmlArgument {
    return factory.createSmlArgument().apply {
        this.value = value
        this.parameter = parameter
    }
}

/**
 * Returns a new object of class [SmlArgument] that points to a parameter with the given name.
 */
fun createSmlArgument(value: SmlAbstractExpression, parameterName: String): SmlArgument {
    return createSmlArgument(
        value,
        createSmlParameter(parameterName)
    )
}

/**
 * Returns a new object of class [SmlArgumentList] or `null` if the parameter is `null`.
 */
private fun createSmlArgumentList(arguments: List<SmlArgument>?): SmlArgumentList? {
    if (arguments == null) {
        return null
    }

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

/**
 * Returns a new object of class [SmlAssignment].
 */
fun createSmlAssignment(assignees: List<SmlAbstractAssignee>, expression: SmlAbstractExpression): SmlAssignment {
    return factory.createSmlAssignment().apply {
        this.assigneeList = createSmlAssigneeList(assignees)
        this.expression = expression
    }
}

/**
 * Returns a new object of class [SmlAttribute].
 */
fun createSmlAttribute(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    type: SmlAbstractType? = null
): SmlAttribute {
    return factory.createSmlAttribute().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.isStatic = isStatic
        this.type = type
    }
}

/**
 * Adds a new object of class [SmlAttribute] to the receiver.
 */
fun SmlClass.smlAttribute(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    type: SmlAbstractType? = null
) {
    this.addMember(createSmlAttribute(name, annotations, isStatic, type))
}

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

/**
 * Returns a new object of class [SmlClass].
 */
fun createSmlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null,
    init: SmlClass.() -> Unit = {}
): SmlClass {
    return factory.createSmlClass().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.parentTypeList = createSmlParentTypeList(parentTypes)
        this.typeParameterConstraintList = createSmlTypeParameterConstraintList(typeParameterConstraints)
        this.init()
    }
}

/**
 * Adds a new object of class [SmlClass] to the receiver.
 */
fun SmlClass.smlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null,
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            typeParameterConstraints,
            init
        )
    )
}

/**
 * Adds a new object of class [SmlClass] to the receiver.
 */
fun SmlCompilationUnit.smlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null,
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            typeParameterConstraints,
            init
        )
    )
}

/**
 * Adds a new object of class [SmlClass] to the receiver.
 */
fun SmlPackage.smlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null,
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            typeParameterConstraints,
            init
        )
    )
}

/**
 * Adds a new member to the receiver.
 */
private fun SmlClass.addMember(member: SmlAbstractClassMember) {
    if (this.body == null) {
        this.body = factory.createSmlClassBody()
    }

    this.body.members += member
}

/**
 * Returns a new object of class [SmlCompilationUnit].
 */
fun createSmlCompilationUnit(init: SmlCompilationUnit.() -> Unit = {}): SmlCompilationUnit {
    return factory.createSmlCompilationUnit().apply(init)
}

/**
 * Adds a new member to the receiver.
 */
private fun SmlCompilationUnit.addMember(member: SmlAbstractCompilationUnitMember) {
    this.members += member
}

/**
 * Returns a new object of class [SmlEnum].
 */
fun createSmlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    init: SmlEnum.() -> Unit = {}
): SmlEnum {
    return factory.createSmlEnum().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.init()
    }
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlClass.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, init))
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlCompilationUnit.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, init))
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlPackage.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, init))
}

/**
 * Adds a new variant to the receiver.
 */
private fun SmlEnum.addVariant(variant: SmlEnumVariant) {
    if (this.body == null) {
        this.body = factory.createSmlEnumBody()
    }

    this.body.variants += variant
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
    this.addVariant(createSmlEnumVariant(name, annotations, typeParameters, parameters, typeParameterConstraint))
}

/**
 * Returns a new object of class [SmlExpressionStatement].
 */
fun createSmlExpressionStatement(expression: SmlAbstractExpression): SmlExpressionStatement {
    return factory.createSmlExpressionStatement().apply {
        this.expression = expression
    }
}

/**
 * Returns a new object of class [SmlFloat].
 */
fun createSmlFloat(value: Double): SmlFloat {
    return factory.createSmlFloat().apply {
        this.value = value
    }
}

/**
 * Returns a new object of class [SmlFunction].
 */
fun createSmlFunction(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null
): SmlFunction {
    return factory.createSmlFunction().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.isStatic = isStatic
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.resultList = createSmlResultList(results)
        this.typeParameterConstraintList = createSmlTypeParameterConstraintList(typeParameterConstraints)
    }
}

/**
 * Adds a new object of class [SmlFunction] to the receiver.
 */
fun SmlClass.smlFunction(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            typeParameterConstraints
        )
    )
}

/**
 * Adds a new object of class [SmlFunction] to the receiver.
 */
fun SmlCompilationUnit.smlFunction(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            typeParameterConstraints
        )
    )
}

/**
 * Adds a new object of class [SmlFunction] to the receiver.
 */
fun SmlPackage.smlFunction(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    typeParameters: List<SmlTypeParameter>? = null,
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult>? = null,
    typeParameterConstraints: List<SmlTypeParameterConstraint>? = null
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            typeParameterConstraints
        )
    )
}

/**
 * Returns a new object of class [SmlImport].
 */
fun createSmlImport(importedNamespace: String, alias: String?): SmlImport {
    return factory.createSmlImport().apply {
        this.importedNamespace = importedNamespace
        this.alias = createSmlImportAlias(alias)
    }
}

/**
 * Returns a new object of class [SmlImportAlias] or `null` if the parameter is `null`.
 */
private fun createSmlImportAlias(name: String?): SmlImportAlias? {
    if (name == null) {
        return null
    }

    return factory.createSmlImportAlias().apply {
        this.name = name
    }
}

/**
 * Returns a new object of class [SmlInfixOperation].
 */
fun createSmlInfixOperation(
    leftOperand: SmlAbstractExpression,
    operator: String,
    rightOperand: SmlAbstractExpression
): SmlInfixOperation {
    return factory.createSmlInfixOperation().apply {
        this.leftOperand = leftOperand
        this.operator = operator
        this.rightOperand = rightOperand
    }
}

/**
 * Returns a new object of class [SmlInt].
 */
fun createSmlInt(value: Int): SmlInt {
    return factory.createSmlInt().apply {
        this.value = value
    }
}

/**
 * Returns a new object of class [SmlLambda].
 */
fun createSmlLambda(parameters: List<SmlParameter>? = null, init: SmlLambda.() -> Unit = {}): SmlLambda {
    return factory.createSmlLambda().apply {
        this.parameterList = createSmlParameterList(parameters)
        this.body = factory.createSmlBlock()
        this.init()
    }
}

/**
 * Adds a new statement to the receiver.
 */
fun SmlLambda.addStatement(statement: SmlAbstractStatement) {
    if (this.body == null) {
        this.body = factory.createSmlBlock()
    }

    this.body.statements += statement
}

/**
 * Returns a new object of class [SmlLambdaResult].
 */
fun createSmlLambdaResult(name: String, annotations: List<SmlAnnotationUse> = emptyList()): SmlLambdaResult {
    return factory.createSmlLambdaResult().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
    }
}

/**
 * Returns a new object of class [SmlMemberAccess].
 */
fun createSmlMemberAccess(
    receiver: SmlAbstractExpression,
    member: SmlReference,
    isNullSafe: Boolean = false
): SmlMemberAccess {
    return factory.createSmlMemberAccess().apply {
        this.receiver = receiver
        this.member = member
        this.isNullSafe = isNullSafe
    }
}

/**
 * Returns a new object of class [SmlMemberType].
 */
fun createSmlMemberType(receiver: SmlAbstractType, member: SmlNamedType): SmlMemberType {
    return factory.createSmlMemberType().apply {
        this.receiver = receiver
        this.member = member
    }
}

/**
 * Returns a new object of class [SmlNamedType].
 */
fun createSmlNamedType(
    declaration: SmlAbstractNamedTypeDeclaration,
    typeArguments: List<SmlTypeArgument>? = null,
    isNullable: Boolean = false
): SmlNamedType {
    return factory.createSmlNamedType().apply {
        this.declaration = declaration
        this.typeArgumentList = createSmlTypeArgumentList(typeArguments)
        this.isNullable = isNullable
    }
}

/**
 * Returns a new object of class [SmlNull].
 */
fun createSmlNull(): SmlNull {
    return factory.createSmlNull()
}

/**
 * Returns a new object of class [SmlPackage].
 */
fun createSmlPackage(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    imports: List<SmlImport> = emptyList(),
    init: SmlPackage.() -> Unit = {}
): SmlPackage {
    return factory.createSmlPackage().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.imports += imports
        this.init()
    }
}

/**
 * Returns a new object of class [SmlPackage].
 */
fun SmlCompilationUnit.smlPackage(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    imports: List<SmlImport> = emptyList(),
    init: SmlPackage.() -> Unit = {}
) {
    this.addMember(createSmlPackage(name, annotations, imports, init))
}

/**
 * Adds a new member to the receiver.
 */
fun SmlPackage.addMember(member: SmlAbstractPackageMember) {
    this.members += member
}

/**
 * Returns a new object of class [SmlParameter].
 */
fun createSmlParameter(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isVariadic: Boolean = false,
    type: SmlAbstractType? = null,
    defaultValue: SmlAbstractExpression? = null
): SmlParameter {
    return factory.createSmlParameter().apply {
        this.name = name
        this.annotations += annotations
        this.isVariadic = isVariadic
        this.type = type
        this.defaultValue = defaultValue
    }
}

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
 * Returns a new object of class [SmlParentTypeList] or `null` if the parameter is `null`.
 */
private fun createSmlParentTypeList(parentTypes: List<SmlAbstractType>?): SmlParentTypeList? {
    if (parentTypes == null) {
        return null
    }

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

/**
 * Returns a new object of class [SmlResult].
 */
fun createSmlResult(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    type: SmlAbstractType? = null
): SmlResult {
    return factory.createSmlResult().apply {
        this.name = name
        this.annotations += annotations
        this.type = type
    }
}

/**
 * Returns a new object of class [SmlResultList] or `null` if the parameter is `null`.
 */
private fun createSmlResultList(results: List<SmlResult>?): SmlResultList? {
    if (results == null) {
        return null
    }

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
 * Returns a new object of class [SmlTypeArgumentList] or `null` if the parameter is `null`.
 */
private fun createSmlTypeArgumentList(typeArguments: List<SmlTypeArgument>?): SmlTypeArgumentList? {
    if (typeArguments == null) {
        return null
    }

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
private fun createSmlTypeParameterConstraintList(typeParameterConstraints: List<SmlTypeParameterConstraint>?): SmlTypeParameterConstraintList? {
    if (typeParameterConstraints == null) {
        return null
    }

    return factory.createSmlTypeParameterConstraintList().apply {
        this.constraints += typeParameterConstraints
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

/**
 * Returns a new object of class [SmlYield].
 */
fun createSmlYield(result: SmlResult): SmlYield {
    return factory.createSmlYield().apply {
        this.result = result
    }
}

/**
 * Returns a new object of class [SmlYield] that points to a result with the given name.
 */
fun createSmlYield(resultName: String): SmlYield {
    return createSmlYield(createSmlResult(resultName))
}
