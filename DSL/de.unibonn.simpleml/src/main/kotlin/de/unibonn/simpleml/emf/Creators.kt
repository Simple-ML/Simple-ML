@file:Suppress("unused")

package de.unibonn.simpleml.emf

import de.unibonn.simpleml.constant.SmlFileExtension
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.constant.SmlProtocolQuantifiedTermQuantifier
import de.unibonn.simpleml.constant.SmlProtocolTokenClassValue
import de.unibonn.simpleml.constant.SmlTypeParameterConstraintOperator
import de.unibonn.simpleml.constant.SmlVariance
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractClassMember
import de.unibonn.simpleml.simpleML.SmlAbstractCompilationUnitMember
import de.unibonn.simpleml.simpleML.SmlAbstractConstraint
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractNamedTypeDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlAbstractPackageMember
import de.unibonn.simpleml.simpleML.SmlAbstractProtocolTerm
import de.unibonn.simpleml.simpleML.SmlAbstractProtocolToken
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAbstractTypeArgumentValue
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAnnotationUseHolder
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssigneeList
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlConstraintList
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
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlProtocol
import de.unibonn.simpleml.simpleML.SmlProtocolAlternative
import de.unibonn.simpleml.simpleML.SmlProtocolComplement
import de.unibonn.simpleml.simpleML.SmlProtocolParenthesizedTerm
import de.unibonn.simpleml.simpleML.SmlProtocolQuantifiedTerm
import de.unibonn.simpleml.simpleML.SmlProtocolReference
import de.unibonn.simpleml.simpleML.SmlProtocolReferenceList
import de.unibonn.simpleml.simpleML.SmlProtocolSequence
import de.unibonn.simpleml.simpleML.SmlProtocolSubterm
import de.unibonn.simpleml.simpleML.SmlProtocolTokenClass
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlResultList
import de.unibonn.simpleml.simpleML.SmlStarProjection
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeParameterList
import de.unibonn.simpleml.simpleML.SmlTypeProjection
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWildcard
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlStep
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
    fileExtension: SmlFileExtension,
    compilationUnit: SmlCompilationUnit
): Resource {
    val uri = URI.createURI("dummy:/$fileName.${fileExtension.extension}")
    return XtextResource(uri).apply {
        this.contents += compilationUnit
    }
}

/**
 * Returns a new [Resource].
 *
 * This can be useful to serialize EObjects that were initialized with the creators in this file rather than generated
 * by the parser, since serialization requires EObjects to be contained in a resource.
 */
fun createSmlDummyResource(
    fileName: String,
    fileExtension: SmlFileExtension,
    init: SmlCompilationUnit.() -> Unit = {}
): Resource {
    val uri = URI.createURI("dummy:/$fileName.${fileExtension.extension}")
    return XtextResource(uri).apply {
        this.contents += createSmlCompilationUnit(init = init)
    }
}

/**
 * Returns a new object of class [SmlAnnotation].
 */
fun createSmlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList()
): SmlAnnotation {
    return factory.createSmlAnnotation().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.parameterList = createSmlParameterList(parameters.ifEmpty { null })
    }
}

/**
 * Adds a new object of class [SmlAnnotation] to the receiver.
 */
fun SmlCompilationUnit.smlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList()
) {
    this.addMember(createSmlAnnotation(name, annotations, parameters))
}

/**
 * Adds a new object of class [SmlAnnotation] to the receiver.
 */
fun SmlPackage.smlAnnotation(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList()
) {
    this.addMember(createSmlAnnotation(name, annotations, parameters))
}

/**
 * Returns a new object of class [SmlAnnotationUse].
 */
fun createSmlAnnotationUse(annotation: SmlAnnotation, arguments: List<SmlArgument> = emptyList()): SmlAnnotationUse {
    return factory.createSmlAnnotationUse().apply {
        this.annotation = annotation
        this.argumentList = createSmlArgumentList(arguments.ifEmpty { null })
    }
}

/**
 * Returns a new object of class [SmlAnnotationUse] that points to an annotation with the given name.
 */
fun createSmlAnnotationUse(annotationName: String, arguments: List<SmlArgument> = emptyList()): SmlAnnotationUse {
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
 *
 * @throws IllegalArgumentException If no assignees are passed.
 */
fun createSmlAssignment(assignees: List<SmlAbstractAssignee>, expression: SmlAbstractExpression): SmlAssignment {
    if (assignees.isEmpty()) {
        throw IllegalArgumentException("Must have at least one assignee.")
    }

    return factory.createSmlAssignment().apply {
        this.assigneeList = createSmlAssigneeList(assignees)
        this.expression = expression
    }
}

/**
 * Adds a new object of class [SmlAssignment] to the receiver.
 */
fun SmlLambda.smlAssignment(assignees: List<SmlAbstractAssignee>, expression: SmlAbstractExpression) {
    this.addStatement(createSmlAssignment(assignees, expression))
}

/**
 * Adds a new object of class [SmlAssignment] to the receiver.
 */
fun SmlWorkflow.smlAssignment(assignees: List<SmlAbstractAssignee>, expression: SmlAbstractExpression) {
    this.addStatement(createSmlAssignment(assignees, expression))
}

/**
 * Adds a new object of class [SmlAssignment] to the receiver.
 */
fun SmlStep.smlAssignment(assignees: List<SmlAbstractAssignee>, expression: SmlAbstractExpression) {
    this.addStatement(createSmlAssignment(assignees, expression))
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
 * Returns a new object of class [SmlBoolean].
 */
fun createSmlBoolean(value: Boolean): SmlBoolean {
    return factory.createSmlBoolean().apply {
        this.isTrue = value
    }
}

/**
 * Returns a new object of class [SmlCall].
 */
fun createSmlCall(
    receiver: SmlAbstractExpression,
    typeArguments: List<SmlTypeArgument> = emptyList(),
    arguments: List<SmlArgument> = emptyList()
): SmlCall {
    return factory.createSmlCall().apply {
        this.receiver = receiver
        this.typeArgumentList = createSmlTypeArgumentList(typeArguments)
        this.argumentList = createSmlArgumentList(arguments)
    }
}

/**
 * Returns a new object of class [SmlCallableType].
 */
fun createSmlCallableType(parameters: List<SmlParameter>, results: List<SmlResult>): SmlCallableType {
    return factory.createSmlCallableType().apply {
        this.parameterList = createSmlParameterList(parameters)
        this.resultList = createSmlResultList(results)
    }
}

/**
 * Returns a new object of class [SmlClass].
 */
fun createSmlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null, // null and emptyList() are semantically different
    parentTypes: List<SmlAbstractType> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList(),
    protocol: SmlProtocol? = null,
    members: List<SmlAbstractClassMember> = emptyList(),
    init: SmlClass.() -> Unit = {}
): SmlClass {
    return factory.createSmlClass().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.parentTypeList = createSmlParentTypeList(parentTypes)
        this.constraintList = createSmlConstraintList(constraints)
        protocol?.let { addMember(it) }
        members.forEach { addMember(it) }
        this.init()
    }
}

/**
 * Adds a new object of class [SmlClass] to the receiver.
 */
fun SmlClass.smlClass(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList(),
    protocol: SmlProtocol? = null,
    members: List<SmlAbstractClassMember> = emptyList(),
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            constraints,
            protocol,
            members,
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList(),
    protocol: SmlProtocol? = null,
    members: List<SmlAbstractClassMember> = emptyList(),
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            constraints,
            protocol,
            members,
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null,
    parentTypes: List<SmlAbstractType> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList(),
    protocol: SmlProtocol? = null,
    members: List<SmlAbstractClassMember> = emptyList(),
    init: SmlClass.() -> Unit = {}
) {
    this.addMember(
        createSmlClass(
            name,
            annotations,
            typeParameters,
            parameters,
            parentTypes,
            constraints,
            protocol,
            members,
            init
        )
    )
}

/**
 * Adds a new member to the receiver.
 */
private fun SmlClass.addMember(member: SmlAbstractObject) {
    if (this.body == null) {
        this.body = factory.createSmlClassBody()
    }

    this.body.members += member
}

/**
 * Returns a new object of class [SmlCompilationUnit].
 */
fun createSmlCompilationUnit(
    members: List<SmlAbstractCompilationUnitMember> = emptyList(),
    init: SmlCompilationUnit.() -> Unit = {}
): SmlCompilationUnit {
    return factory.createSmlCompilationUnit().apply {
        members.forEach { addMember(it) }
        init()
    }
}

/**
 * Adds a new member to the receiver.
 */
private fun SmlCompilationUnit.addMember(member: SmlAbstractCompilationUnitMember) {
    this.members += member
}

/**
 * Returns a new object of class [SmlConstraintList] or `null` if the list of constraints is empty.
 */
private fun createSmlConstraintList(constraints: List<SmlAbstractConstraint>): SmlConstraintList? {
    if (constraints.isEmpty()) {
        return null
    }

    return factory.createSmlConstraintList().apply {
        this.constraints += constraints
    }
}

/**
 * Returns a new object of class [SmlEnum].
 */
fun createSmlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    variants: List<SmlEnumVariant> = emptyList(),
    init: SmlEnum.() -> Unit = {}
): SmlEnum {
    return factory.createSmlEnum().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        variants.forEach { addVariant(it) }
        this.init()
    }
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlClass.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    variants: List<SmlEnumVariant> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, variants, init))
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlCompilationUnit.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    variants: List<SmlEnumVariant> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, variants, init))
}

/**
 * Adds a new object of class [SmlEnum] to the receiver.
 */
fun SmlPackage.smlEnum(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    variants: List<SmlEnumVariant> = emptyList(),
    init: SmlEnum.() -> Unit = {}
) {
    this.addMember(createSmlEnum(name, annotations, variants, init))
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null, // null and emptyList() are semantically different
    constraints: List<SmlAbstractConstraint> = emptyList()
): SmlEnumVariant {
    return factory.createSmlEnumVariant().apply {
        this.name = name
        this.annotations += annotations
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.constraintList = createSmlConstraintList(constraints)
    }
}

/**
 * Adds a new object of class [SmlEnumVariant] to the receiver.
 */
fun SmlEnum.smlEnumVariant(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter>? = null,
    constraints: List<SmlAbstractConstraint> = emptyList()
) {
    this.addVariant(createSmlEnumVariant(name, annotations, typeParameters, parameters, constraints))
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
 * Adds a new object of class [SmlExpressionStatement] to the receiver.
 */
fun SmlLambda.smlExpressionStatement(expression: SmlAbstractExpression) {
    this.addStatement(createSmlExpressionStatement(expression))
}

/**
 * Adds a new object of class [SmlExpressionStatement] to the receiver.
 */
fun SmlWorkflow.smlExpressionStatement(expression: SmlAbstractExpression) {
    this.addStatement(createSmlExpressionStatement(expression))
}

/**
 * Adds a new object of class [SmlExpressionStatement] to the receiver.
 */
fun SmlStep.smlExpressionStatement(expression: SmlAbstractExpression) {
    this.addStatement(createSmlExpressionStatement(expression))
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList()
): SmlFunction {
    return factory.createSmlFunction().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.isStatic = isStatic
        this.typeParameterList = createSmlTypeParameterList(typeParameters)
        this.parameterList = createSmlParameterList(parameters)
        this.resultList = createSmlResultList(results.ifEmpty { null })
        this.constraintList = createSmlConstraintList(constraints)
    }
}

/**
 * Adds a new object of class [SmlFunction] to the receiver.
 */
fun SmlClass.smlFunction(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    isStatic: Boolean = false,
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList()
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            constraints
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList()
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            constraints
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
    typeParameters: List<SmlTypeParameter> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    constraints: List<SmlAbstractConstraint> = emptyList()
) {
    this.addMember(
        createSmlFunction(
            name,
            annotations,
            isStatic,
            typeParameters,
            parameters,
            results,
            constraints
        )
    )
}

/**
 * Returns a new object of class [SmlImport].
 */
fun createSmlImport(importedNamespace: String, alias: String? = null): SmlImport {
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
    operator: SmlInfixOperationOperator,
    rightOperand: SmlAbstractExpression
): SmlInfixOperation {
    return factory.createSmlInfixOperation().apply {
        this.leftOperand = leftOperand
        this.operator = operator.operator
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
fun createSmlLambda(
    parameters: List<SmlParameter> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlLambda.() -> Unit = {}
): SmlLambda {
    return factory.createSmlLambda().apply {
        this.parameterList = createSmlParameterList(parameters.ifEmpty { null })
        this.body = factory.createSmlBlock()
        statements.forEach { addStatement(it) }
        this.init()
    }
}

/**
 * Adds a new statement to the receiver.
 */
private fun SmlLambda.addStatement(statement: SmlAbstractStatement) {
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
    typeArguments: List<SmlTypeArgument> = emptyList(),
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
    members: List<SmlAbstractPackageMember> = emptyList(),
    init: SmlPackage.() -> Unit = {}
): SmlPackage {
    return factory.createSmlPackage().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.imports += imports
        members.forEach { addMember(it) }
        this.init()
    }
}

/**
 * Adds a new object of class [SmlPackage] to the receiver.
 */
fun SmlCompilationUnit.smlPackage(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    imports: List<SmlImport> = emptyList(),
    members: List<SmlAbstractPackageMember> = emptyList(),
    init: SmlPackage.() -> Unit = {}
) {
    this.addMember(createSmlPackage(name, annotations, imports, members, init))
}

/**
 * Adds a new member to the receiver.
 */
private fun SmlPackage.addMember(member: SmlAbstractPackageMember) {
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
 * Returns a new object of class [SmlParentTypeList] or `null` if the list of parent type is empty.
 */
private fun createSmlParentTypeList(parentTypes: List<SmlAbstractType>): SmlParentTypeList? {
    if (parentTypes.isEmpty()) {
        return null
    }

    return factory.createSmlParentTypeList().apply {
        this.parentTypes += parentTypes
    }
}

/**
 * Returns a new object of class [SmlPlaceholder].
 */
fun createSmlPlaceholder(name: String, annotations: List<SmlAnnotationUse>): SmlPlaceholder {
    return factory.createSmlPlaceholder().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
    }
}

/**
 * Returns a new object of class [SmlPrefixOperation].
 */
fun createSmlPrefixOperation(operator: SmlPrefixOperationOperator, operand: SmlAbstractExpression): SmlPrefixOperation {
    return factory.createSmlPrefixOperation().apply {
        this.operator = operator.operator
        this.operand = operand
    }
}

/**
 * Returns a new object of class [SmlProtocol].
 */
fun createSmlProtocol(
    subterms: List<SmlProtocolSubterm> = emptyList(),
    term: SmlAbstractProtocolTerm? = null,
    init: SmlProtocol.() -> Unit = {}
): SmlProtocol {
    return factory.createSmlProtocol().apply {
        this.body = factory.createSmlProtocolBody()
        subterms.forEach { addSubterm(it) }
        this.body.term = term
        this.init()
    }
}

/**
 * Adds a new object of class [SmlProtocol] to the receiver.
 */
fun SmlClass.smlProtocol(
    subterms: List<SmlProtocolSubterm> = emptyList(),
    term: SmlAbstractProtocolTerm? = null,
    init: SmlProtocol.() -> Unit = {}
) {
    this.addMember(createSmlProtocol(subterms, term, init))
}

/**
 * Adds a new subterm to the receiver.
 */
private fun SmlProtocol.addSubterm(subterm: SmlProtocolSubterm) {
    if (this.body == null) {
        this.body = factory.createSmlProtocolBody()
    }

    if (this.body.subtermList == null) {
        this.body.subtermList = factory.createSmlProtocolSubtermList()
    }

    this.body.subtermList.subterms += subterm
}

/**
 * Returns a new object of class [SmlProtocolAlternative].
 */
fun createSmlProtocolAlternative(terms: List<SmlAbstractProtocolTerm>): SmlProtocolAlternative {
    if (terms.size < 2) {
        throw IllegalArgumentException("Must have at least two terms.")
    }

    return factory.createSmlProtocolAlternative().apply {
        this.terms += terms
    }
}

/**
 * Returns a new object of class [SmlProtocolComplement].
 */
fun createSmlProtocolComplement(
    universe: SmlProtocolTokenClass? = null,
    references: List<SmlProtocolReference> = emptyList()
): SmlProtocolComplement {
    return factory.createSmlProtocolComplement().apply {
        this.universe = universe
        this.referenceList = createSmlProtocolReferenceList(references)
    }
}

/**
 * Returns a new object of class [SmlProtocolParenthesizedTerm].
 */
fun createSmlProtocolParenthesizedTerm(term: SmlAbstractProtocolTerm): SmlProtocolParenthesizedTerm {
    return factory.createSmlProtocolParenthesizedTerm().apply {
        this.term = term
    }
}

/**
 * Returns a new object of class [SmlProtocolQuantifiedTerm].
 */
fun createSmlProtocolQuantifiedTerm(
    term: SmlAbstractProtocolTerm,
    quantifier: SmlProtocolQuantifiedTermQuantifier
): SmlProtocolQuantifiedTerm {
    return factory.createSmlProtocolQuantifiedTerm().apply {
        this.term = term
        this.quantifier = quantifier.quantifier
    }
}

/**
 * Returns a new object of class [SmlProtocolReference].
 */
fun createSmlProtocolReference(token: SmlAbstractProtocolToken): SmlProtocolReference {
    return factory.createSmlProtocolReference().apply {
        this.token = token
    }
}

/**
 * Returns a new object of class [SmlProtocolReferenceList] or `null` if the list of protocols is empty.
 */
private fun createSmlProtocolReferenceList(references: List<SmlProtocolReference>): SmlProtocolReferenceList? {
    if (references.isEmpty()) {
        return null
    }

    return factory.createSmlProtocolReferenceList().apply {
        this.references += references
    }
}

/**
 * Returns a new object of class [SmlProtocolSequence].
 *
 * @throws IllegalArgumentException If `terms.size < 2`.
 */
fun createSmlProtocolSequence(terms: List<SmlAbstractProtocolTerm>): SmlProtocolSequence {
    if (terms.size < 2) {
        throw IllegalArgumentException("Must have at least two terms.")
    }

    return factory.createSmlProtocolSequence().apply {
        this.terms += terms
    }
}

/**
 * Returns a new object of class [SmlProtocolSubterm].
 */
fun createSmlProtocolSubterm(name: String, term: SmlAbstractProtocolTerm): SmlProtocolSubterm {
    return factory.createSmlProtocolSubterm().apply {
        this.name = name
        this.term = term
    }
}

/**
 * Returns a new object of class [SmlProtocolSubterm].
 */
fun SmlProtocol.smlProtocolSubterm(name: String, term: SmlAbstractProtocolTerm) {
    this.addSubterm(createSmlProtocolSubterm(name, term))
}

/**
 * Returns a new object of class [SmlProtocolTokenClass].
 */
fun createSmlProtocolTokenClass(value: SmlProtocolTokenClassValue): SmlProtocolTokenClass {
    return factory.createSmlProtocolTokenClass().apply {
        this.value = value.value
    }
}

/**
 * Returns a new object of class [SmlReference].
 */
fun createSmlReference(declaration: SmlAbstractDeclaration): SmlReference {
    return factory.createSmlReference().apply {
        this.declaration = declaration
    }
}

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
 * Returns a new object of class [SmlStep].
 */
fun createSmlStep(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlStep.() -> Unit = {}
): SmlStep {
    return factory.createSmlStep().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.parameterList = createSmlParameterList(parameters)
        this.resultList = createSmlResultList(results.ifEmpty { null })
        this.body = factory.createSmlBlock()
        statements.forEach { addStatement(it) }
        this.init()
    }
}

/**
 * Adds a new object of class [SmlStep] to the receiver.
 */
fun SmlCompilationUnit.smlStep(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlStep.() -> Unit = {}
) {
    this.addMember(createSmlStep(name, annotations, parameters, results, statements, init))
}

/**
 * Adds a new object of class [SmlStep] to the receiver.
 */
fun SmlPackage.smlStep(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    parameters: List<SmlParameter> = emptyList(),
    results: List<SmlResult> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlStep.() -> Unit = {}
) {
    this.addMember(createSmlStep(name, annotations, parameters, results, statements, init))
}

/**
 * Adds a new statement to the receiver.
 */
private fun SmlStep.addStatement(statement: SmlAbstractStatement) {
    if (this.body == null) {
        this.body = factory.createSmlBlock()
    }

    this.body.statements += statement
}

/**
 * Returns a new object of class [SmlString].
 */
fun createSmlString(value: String): SmlString {
    return factory.createSmlString().apply {
        this.value = value
    }
}

/**
 * Returns a new object of class [SmlTemplateString]. String parts should not include delimiters (`"`, `{{`, `}}`).
 * Template expressions are inserted between the string parts.
 *
 * @throws IllegalArgumentException If `stringParts.size < 2`.
 * @throws IllegalArgumentException If `templateExpressions` is empty.
 * @throws IllegalArgumentException If `stringsParts.size` != `templateExpressions.size + 1`.
 */
fun createSmlTemplateString(
    stringParts: List<String>,
    templateExpressions: List<SmlAbstractExpression>
): SmlTemplateString {

    // One of the first two checks is sufficient but this allows better error messages.
    if (stringParts.size < 2) {
        throw IllegalArgumentException("Must have at least two string parts.")
    } else if (templateExpressions.isEmpty()) {
        throw IllegalArgumentException("Must have at least one template expression.")
    } else if (stringParts.size != templateExpressions.size + 1) {
        throw IllegalArgumentException("Must have exactly one more string part than there are template expressions.")
    }

    return factory.createSmlTemplateString().apply {
        stringParts.forEachIndexed { index, value ->

            // Next template string part
            this.expressions += when (index) {
                0 -> {
                    factory.createSmlTemplateStringStart().apply {
                        this.value = value
                    }
                }
                stringParts.size - 1 -> {
                    factory.createSmlTemplateStringEnd().apply {
                        this.value = value
                    }
                }
                else -> {
                    factory.createSmlTemplateStringInner().apply {
                        this.value = value
                    }
                }
            }

            // Next template expression
            if (index < templateExpressions.size) {
                this.expressions += templateExpressions[index]
            }
        }

        this.expressions += expressions
    }
}

/**
 * Returns a new object of class [SmlTypeArgument].
 */
fun createSmlTypeArgument(
    value: SmlAbstractTypeArgumentValue,
    typeParameter: SmlTypeParameter? = null
): SmlTypeArgument {
    return factory.createSmlTypeArgument().apply {
        this.value = value
        this.typeParameter = typeParameter
    }
}

/**
 * Returns a new object of class [SmlTypeArgument] that points to a type parameter with the given name.
 */
fun createSmlTypeArgument(
    value: SmlAbstractTypeArgumentValue,
    typeParameterName: String
): SmlTypeArgument {
    return createSmlTypeArgument(
        value,
        createSmlTypeParameter(typeParameterName)
    )
}

/**
 * Returns a new object of class [SmlTypeArgumentList] or `null` if the list of type arguments is empty.
 */
private fun createSmlTypeArgumentList(typeArguments: List<SmlTypeArgument>): SmlTypeArgumentList? {
    if (typeArguments.isEmpty()) {
        return null
    }

    return factory.createSmlTypeArgumentList().apply {
        this.typeArguments += typeArguments
    }
}

/**
 * Returns a new object of class [SmlTypeParameter].
 */
fun createSmlTypeParameter(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    variance: SmlVariance = SmlVariance.Invariant
): SmlTypeParameter {
    return factory.createSmlTypeParameter().apply {
        this.name = name
        this.annotations += annotations
        this.variance = variance.variance
    }
}

/**
 * Returns a new object of class [SmlTypeParameterList] or `null` if the list of type parameters is empty.
 */
private fun createSmlTypeParameterList(typeParameters: List<SmlTypeParameter>): SmlTypeParameterList? {
    if (typeParameters.isEmpty()) {
        return null
    }

    return factory.createSmlTypeParameterList().apply {
        this.typeParameters += typeParameters
    }
}

/**
 * Returns a new object of class [SmlTypeParameterConstraint].
 */
fun createSmlTypeParameterConstraint(
    leftOperand: SmlTypeParameter,
    operator: SmlTypeParameterConstraintOperator,
    rightOperand: SmlAbstractType
): SmlTypeParameterConstraint {
    return factory.createSmlTypeParameterConstraint().apply {
        this.leftOperand = leftOperand
        this.operator = operator.operator
        this.rightOperand = rightOperand
    }
}

/**
 * Returns a new object of class [SmlTypeParameterConstraint] that points to a type parameter with the given name.
 */
fun createSmlTypeParameterConstraint(
    leftOperandName: String,
    operator: SmlTypeParameterConstraintOperator,
    rightOperand: SmlAbstractType
): SmlTypeParameterConstraint {
    return createSmlTypeParameterConstraint(
        createSmlTypeParameter(leftOperandName),
        operator,
        rightOperand
    )
}

/**
 * Returns a new object of class [SmlTypeProjection].
 */
fun createSmlTypeProjection(type: SmlAbstractType, variance: SmlVariance = SmlVariance.Invariant): SmlTypeProjection {
    return factory.createSmlTypeProjection().apply {
        this.type = type
        this.variance = variance.variance
    }
}

/**
 * Returns a new object of class [SmlUnionType].
 *
 * @throws IllegalArgumentException If no type arguments are passed.
 */
fun createSmlUnionType(typeArguments: List<SmlTypeArgument>): SmlUnionType {
    if (typeArguments.isEmpty()) {
        throw IllegalArgumentException("Must have at least one type argument.")
    }

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

/**
 * Returns a new object of class [SmlWorkflow].
 */
fun createSmlWorkflow(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlWorkflow.() -> Unit = {}
): SmlWorkflow {
    return factory.createSmlWorkflow().apply {
        this.name = name
        this.annotationUseHolder = createSmlAnnotationUseHolder(annotations)
        this.body = factory.createSmlBlock()
        statements.forEach { addStatement(it) }
        this.init()
    }
}

/**
 * Adds a new object of class [SmlWorkflow] to the receiver.
 */
fun SmlCompilationUnit.smlWorkflow(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlWorkflow.() -> Unit = {}
) {
    this.addMember(createSmlWorkflow(name, annotations, statements, init))
}

/**
 * Adds a new object of class [SmlWorkflow] to the receiver.
 */
fun SmlPackage.smlWorkflow(
    name: String,
    annotations: List<SmlAnnotationUse> = emptyList(),
    statements: List<SmlAbstractStatement> = emptyList(),
    init: SmlWorkflow.() -> Unit = {}
) {
    this.addMember(createSmlWorkflow(name, annotations, statements, init))
}

/**
 * Adds a new statement to the receiver.
 */
private fun SmlWorkflow.addStatement(statement: SmlAbstractStatement) {
    if (this.body == null) {
        this.body = factory.createSmlBlock()
    }

    this.body.statements += statement
}

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
