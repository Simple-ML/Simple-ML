package de.unibonn.simpleml.prolog_bridge.model.facts

import de.unibonn.simpleml.prolog_bridge.model.facts.PlTerm.Companion.fromJavaRepresentation
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignee
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlClassOrInterface
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumInstance
import de.unibonn.simpleml.simpleML.SmlExpression
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlLambdaYield
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStarProjection
import de.unibonn.simpleml.simpleML.SmlStatement
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlThisType
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeProjection
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWildcard
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import org.eclipse.emf.ecore.EObject

/**
 * Represents generic Prolog facts.
 *
 * @param factName
 * The name of this fact.
 *
 * @param arguments
 * The arguments of this fact. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class PlFact(factName: String, vararg arguments: Any?) {

    /**
     * The name of this fact as a Prolog atom.
     */
    val factName: PlAtom = PlAtom(factName)

    /**
     * The arguments of this fact as a list of Prolog terms.
     */
    val plArguments: List<PlTerm> = fromJavaRepresentation(listOf(*arguments))

    /**
     * The number of arguments of this fact.
     */
    private val arity = arguments.size

    /**
     * The functor of this fact, i.e. factName/arity.
     *
     * **Example:** If the name is "example" and the number of arguments (= arity) is 3, the functor is "example/3".
     */
    val functor: String = "$factName/$arity"

    override fun toString() = plArguments.joinToString(prefix = "$factName(", postfix = ").") { it.toString() }
}

/**
 * Prolog facts that have their own ID and a reference to the fact for their logical parent.
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param otherArguments
 * Arguments of this fact beyond the ID. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class Node(factName: String, id: Id<EObject>, vararg otherArguments: Any?) :
    PlFact(factName, id, *otherArguments) {

    /**
     * The ID of this fact.
     */
    abstract val id: Id<EObject>
}

/**
 * Prolog facts that have their own ID and a reference to the fact for their logical parent.
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent.
 *
 * @param otherArguments
 * Arguments of this fact beyond ID and parent. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class NodeWithParent(factName: String, id: Id<EObject>, parent: Id<EObject>, vararg otherArguments: Any?) :
    Node(factName, id, parent, *otherArguments) {

    /**
     * The ID of the fact for the logical parent.
     */
    abstract val parent: Id<EObject>
}


/**********************************************************************************************************************
 * Compilation Unit
 **********************************************************************************************************************/

/**
 * This Prolog fact represents compilations units.
 *
 * @param id
 * The ID of this fact.
 *
 * @param package
 * The package of this compilation unit or null if it does not declare a package.
 *
 * @param imports
 * The IDs of the importT facts for the imports.
 *
 * @param members
 * The IDs of the facts for the members.
 */
data class CompilationUnitT(
    override val id: Id<SmlCompilationUnit>,
    val `package`: String?,
    val imports: List<Id<SmlImport>>,
    val members: List<Id<SmlDeclaration>>
) : Node("compilationUnitT", id, `package`, imports, members) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents imports.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the compilationUnitT fact for the containing compilation unit.
 *
 * @param importedNamespace
 * The qualified name of the imported namespace.
 *
 * @param alias
 * The alias the namespace should be imported under or null if no alias is specified.
 */
data class ImportT(
    override val id: Id<SmlImport>,
    override val parent: Id<SmlCompilationUnit>,
    val importedNamespace: String,
    val alias: String?
) :
    NodeWithParent("importT", id, parent, importedNamespace, alias) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Declarations
 **********************************************************************************************************************/

/**
 * Prolog facts that have their own ID, a reference to the fact for their logical parent, and a name (which is the name
 * of the declaration and unrelated to the factName).
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent.
 *
 * @param name
 * The name of the declaration.
 *
 * @param otherArguments
 * Arguments of this fact beyond ID, parent, and name. Arguments can either be `null`, booleans, IDs, number, strings or
 * lists.
 */
sealed class DeclarationT(
    factName: String,
    id: Id<SmlDeclaration>,
    parent: Id<EObject>, // SmlClassOrInterface | SmlCompilationUnit
    name: String,
    vararg otherArguments: Any?
) :
    NodeWithParent(factName, id, parent, name, *otherArguments) {

    /**
     * The name of the declaration.
     */
    abstract val name: String
}

/**
 * This Prolog fact represents annotations.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the compilationUnitT fact for the containing compilation unit.
 *
 * @param name
 * The name of the annotation.
 *
 * @param parameters
 * The list of parameters or null. Each element in the list is the ID of a parameterT fact for the respective parameter.
 * Note that an empty list is used for an annotation with an empty parameter list, e.g. `annotation A()`, while null is
 * used for an annotation with no parameter list at all, like `annotation B`.
 */
data class AnnotationT(
    override val id: Id<SmlAnnotation>,
    override val parent: Id<SmlCompilationUnit>,
    override val name: String,
    val parameters: List<Id<SmlParameter>>?
) : DeclarationT("annotationT", id, parent, name, parameters) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents attributes of a class or interface.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the classT/interfaceT fact for the containing class or interface.
 *
 * @param name
 * The name of the attribute.
 *
 * @param type
 * The ID of the fact for the type of the attribute or null if no type was specified.
 */
data class AttributeT(
    override val id: Id<SmlAttribute>,
    override val parent: Id<SmlClassOrInterface>,
    override val name: String,
    val type: Id<SmlType>?
) :
    DeclarationT("attributeT", id, parent, name, type) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents classes.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the containing compilation unit, class or interface.
 *
 * @param name
 * The name of the class.
 *
 * @param typeParameters
 * The list of type parameters or null. Each element in the list is the ID of a typeParameterT fact for the respective
 * type parameter. Note that an empty list is used for a class with an empty type parameter list, e.g. `class A<>`,
 * while null is used for a class with no type parameter list at all, like `class B`.
 *
 * @param parameters
 * The list of parameters or null. Each element in the list is the ID of a parameterT fact for the respective parameter.
 * Note that an empty list is used for a class with a constructor with an empty parameter list, e.g. `class A()`, while
 * null is used for a class with no constructor at all, like `class B`.
 *
 * @param parentTypes
 * The IDs of the facts for the parent types of this class or null if the class has no parent types. Note that the
 * grammar forbids the use of the keyword `sub` without any parent types afterwards, so this will never be set to an
 * empty list.
 *
 * @param typeParameterConstraints
 * The IDs of the typeParameterConstraintT facts for the type parameter constraints of this class or null if the class
 * has no type parameter constraints. Note that the grammar forbids the use of the keyword `where` without any type
 * parameter constraints afterwards, so this will never be set to an empty list.
 *
 * @param members
 * The list of class members or null. Each element in the list is the ID of the fact for the respective member. Note
 * that an empty list is used for a class with an empty body, e.g. `class A {}`, while null is used for a class without
 * a body, like `class B`.
 */
data class ClassT(
    override val id: Id<SmlClass>,
    override val parent: Id<EObject>, // SmlClassOrInterface | SmlCompilationUnit
    override val name: String,
    val typeParameters: List<Id<SmlTypeParameter>>?,
    val parameters: List<Id<SmlParameter>>?,
    val parentTypes: List<Id<SmlType>>?,
    val typeParameterConstraints: List<Id<SmlTypeParameterConstraint>>?,
    val members: List<Id<SmlDeclaration>>?
) : DeclarationT(
    "classT",
    id,
    parent,
    name,
    typeParameters,
    parameters,
    parentTypes,
    typeParameterConstraints,
    members
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents enums.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the containing compilation unit, class or interface.
 *
 * @param name
 * The name of the enum.
 *
 * @param instances
 * The list of instances or null. Each element in the list is the ID of the enumInstanceT fact for the respective
 * instance. Note that an empty list is used for an enum with an empty body, e.g. `enum A {}`, while null is used for
 * an enum without a body, like `enum B`.
 */
data class EnumT(
    override val id: Id<SmlEnum>,
    override val parent: Id<EObject>, // SmlClassOrInterface | SmlCompilationUnit
    override val name: String,
    val instances: List<Id<SmlEnumInstance>>?
) :
    DeclarationT("enumT", id, parent, name, instances) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents enum instances.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the enumT fact for the containing enum.
 *
 * @param name
 * The name of the enum instance.
 */
data class EnumInstanceT(
    override val id: Id<SmlEnumInstance>,
    override val parent: Id<SmlEnum>,
    override val name: String
) :
    DeclarationT("enumInstanceT", id, parent, name) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents functions.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the containing compilation unit, class or interface.
 *
 * @param name
 * The name of the function.
 *
 * @param typeParameters
 * The list of type parameters or null. Each element in the list is the ID of a typeParameterT fact for the respective
 * type parameter. Note that an empty list is used for a function with an empty type parameter list, e.g. `fun a<>()`,
 * while null is used for a function with no type parameter list at all, like `fun b()`.
 *
 * @param parameters
 * The IDs of the parameterT facts for the parameters of the function. The grammar requires the list to be there so this
 * is never null.
 *
 * @param results
 * The list of result or null. Each element in the list is the ID of a resultT fact for the respective result. Note that
 * an empty list is used for a function with an empty result list, e.g. `fun a() -> ()`, while null is used for a
 * function with no result list at all, like `fun b()`.
 *
 * @param typeParameterConstraints
 * The IDs of the typeParameterConstraintT facts for the type parameter constraints of this function or null if the
 * function has no type parameter constraints. Note that the grammar forbids the use of the keyword `where` without any
 * type parameter constraints afterwards, so this will never be set to an empty list.
 */
data class FunctionT(
    override val id: Id<SmlFunction>,
    override val parent: Id<EObject>, // SmlClassOrInterface | SmlCompilationUnit
    override val name: String,
    val typeParameters: List<Id<SmlTypeParameter>>?,
    val parameters: List<Id<SmlParameter>>,
    val results: List<Id<SmlResult>>?,
    val typeParameterConstraints: List<Id<SmlTypeParameterConstraint>>?
) : DeclarationT(
    "functionT",
    id,
    parent,
    name,
    typeParameters,
    parameters,
    results,
    typeParameterConstraints
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents interfaces.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the containing compilation unit, class or interface.
 *
 * @param name
 * The name of the interface.
 *
 * @param typeParameters
 * The list of type parameters or null. Each element in the list is the ID of a typeParameterT fact for the respective
 * type parameter. Note that an empty list is used for a interface with an empty type parameter list, e.g.
 * `interface A<>`, while null is used for a interface with no type parameter list at all, like `interface B`.
 *
 * @param parameters
 * The list of parameters or null. Each element in the list is the ID of a parameterT fact for the respective parameter.
 * Note that an empty list is used for a interface with a constructor with an empty parameter list, e.g.
 * `interface A()`, while null is used for a interface with no constructor at all, like `interface B`.
 *
 * @param parentTypes
 * The IDs of the facts for the parent types of this interface or null if the interface has no parent types. Note that
 * the grammar forbids the use of the keyword `sub` without any parent types afterwards, so this will never be set to an
 * empty list.
 *
 * @param typeParameterConstraints
 * The IDs of the typeParameterConstraintT facts for the type parameter constraints of this interface or null if the
 * interface has no type parameter constraints. Note that the grammar forbids the use of the keyword `where` without any
 * type parameter constraints afterwards, so this will never be set to an empty list.
 *
 * @param members
 * The list of interface members or null. Each element in the list is the ID of the fact for the respective member. Note
 * that an empty list is used for an interface with an empty body, e.g. `interface A {}`, while null is used for a
 * interface without a body, like `interface B`.
 */
data class InterfaceT(
    override val id: Id<SmlInterface>,
    override val parent: Id<EObject>, // SmlClassOrInterface | SmlCompilationUnit
    override val name: String,
    val typeParameters: List<Id<SmlTypeParameter>>?,
    val parameters: List<Id<SmlParameter>>?,
    val parentTypes: List<Id<SmlType>>?,
    val typeParameterConstraints: List<Id<SmlTypeParameterConstraint>>?,
    val members: List<Id<SmlDeclaration>>?
) : DeclarationT(
    "interfaceT",
    id,
    parent,
    name,
    typeParameters,
    parameters,
    parentTypes,
    typeParameterConstraints,
    members
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents yields in a lambda.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the assignmentT fact for the containing assignment.
 *
 * @param name
 * The name of the yielded result.
 */
data class LambdaYieldT(
    override val id: Id<SmlLambdaYield>,
    override val parent: Id<SmlLambda>,
    override val name: String
) :
    DeclarationT("lambdaYieldT", id, parent, name) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents parameters.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. an annotation.
 *
 * @param name
 * The name of the parameter.
 *
 * @param isVariadic
 * Whether this parameter is variadic.
 *
 * @param type
 * The ID of the fact for the type or null if no type is specified.
 *
 * @param defaultValue
 * The ID of the fact for the default value or null if the parameter is required and has no default value.
 */
data class ParameterT(
    override val id: Id<SmlParameter>,
    override val parent: Id<EObject>,
    override val name: String,
    val isVariadic: Boolean,
    val type: Id<SmlType>?,
    val defaultValue: Id<SmlExpression>?
) : DeclarationT(
    "parameterT",
    id,
    parent,
    name,
    isVariadic,
    type,
    defaultValue
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents placeholder declarations.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the assignmentT fact for the containing assignment.
 *
 * @param name
 * The name of the placeholder.
 */
data class PlaceholderT(
    override val id: Id<SmlPlaceholder>,
    override val parent: Id<SmlAssignment>,
    override val name: String
) :
    DeclarationT("placeholderT", id, parent, name) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents results.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a function.
 *
 * @param name
 * The name of the result.
 *
 * @param type
 * The ID of the fact for the type or null if no type is specified.
 */
data class ResultT(
    override val id: Id<SmlResult>,
    override val parent: Id<EObject>,
    override val name: String,
    val type: Id<SmlType>?
) :
    DeclarationT("resultT", id, parent, name, type) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents type parameters.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a class.
 *
 * @param name
 * The name of the type parameter.
 *
 * @param variance
 * The variance of this type parameter ("in" for contravariance, "out" for covariance, or `null` for invariance).
 */
data class TypeParameterT(
    override val id: Id<SmlTypeParameter>,
    override val parent: Id<EObject>,
    override val name: String,
    val variance: String?
) : DeclarationT("typeParameterT", id, parent, name, variance) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents workflows.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the compilationUnitT fact for the containing compilation unit.
 *
 * @param name
 * The name of the workflow.
 *
 * @param statements
 * The IDs of the facts for the statements in the workflow body. The grammar requires the body to be there so this is
 * never null.
 */
data class WorkflowT(
    override val id: Id<SmlWorkflow>,
    override val parent: Id<SmlCompilationUnit>,
    override val name: String,
    val statements: List<Id<SmlStatement>>
) : DeclarationT("workflowT", id, parent, name, statements) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents workflow steps.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the compilationUnitT fact for the containing compilation unit.
 *
 * @param name
 * The name of the workflow step.
 *
 * @param parameters
 * The IDs of the parameterT facts for the parameters of the workflow step. The grammar requires the list to be there so
 * this is never null.
 *
 * @param results
 * The list of result or null. Each element in the list is the ID of a resultT fact for the respective result. Note that
 * an empty list is used for a workflow step with an empty result list, e.g. `step a() -> () {}`, while null is used
 * for a workflow step with no result list at all, like `step b() {}`.
 *
 * @param statements
 * The IDs of the facts for the statements in the body of the workflow step. The grammar requires the body to be there
 * so this is never null.
 */
data class WorkflowStepT(
    override val id: Id<SmlWorkflowStep>,
    override val parent: Id<SmlCompilationUnit>,
    override val name: String,
    val parameters: List<Id<SmlParameter>>,
    val results: List<Id<SmlResult>>?,
    val statements: List<Id<SmlStatement>>
) : DeclarationT(
    "workflowStepT",
    id,
    parent,
    name,
    parameters,
    results,
    statements
) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Statements
 **********************************************************************************************************************/

/**
 * Prolog facts for statements.
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent.
 *
 * @param otherArguments
 * Arguments of this fact beyond ID and parent. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class StatementT(
    factName: String,
    id: Id<SmlStatement>,
    parent: Id<EObject>, // SmlLambda | SmlWorkflow | SmlWorkflowStep
    vararg otherArguments: Any?
) :
    NodeWithParent(factName, id, parent, *otherArguments)

/**
 * This Prolog fact represents assignments.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, such as a workflow.
 *
 * @param assignees
 * The assignees of this assignment (has at least one).
 *
 * @param expression
 * The ID of the fact for the expression on the right-hand side of this assignment.
 */
data class AssignmentT(
    override val id: Id<SmlAssignment>,
    override val parent: Id<EObject>,
    val assignees: List<Id<SmlAssignee>>,
    val expression: Id<SmlExpression>
) :
    StatementT("assignmentT", id, parent, assignees, expression) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents expression statements.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, such as a workflow.
 *
 * @param expression
 * The ID of the fact for the expression that is evaluated.
 */
data class ExpressionStatementT(
    override val id: Id<SmlExpressionStatement>,
    override val parent: Id<EObject>,
    val expression: Id<SmlExpression>
) :
    StatementT("expressionStatementT", id, parent, expression) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Expressions
 **********************************************************************************************************************/

/**
 * Prolog facts for expressions.
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param otherArguments
 * Arguments of this fact beyond ID, parent, and enclosing. Arguments can either be `null`, booleans, IDs, number,
 * strings or lists.
 */
sealed class ExpressionT(
    factName: String,
    id: Id<EObject>, // SmlArgument | SmlExpression
    parent: Id<EObject>,
    enclosing: Id<EObject>,
    vararg otherArguments: Any?
) :
    NodeWithParent(factName, id, parent, enclosing, *otherArguments) {

    /**
     * The ID of the fact for closest ancestor that is not an expression. This is usually a statement but can also be a
     * parameter if the expression is its default value.
     */
    abstract val enclosing: Id<EObject>
}

/**
 * This Prolog fact represents arguments for annotation uses or calls.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression. This is either a statement or an annotation use.
 *
 * @param parameter
 * If the argument is named, this is the ID of the parameterT fact for the referenced parameter or an unresolvedT
 * fact if the parameter could not be resolved. If the argument is purely positional this is null.
 *
 * @param value
 * The ID of the fact for the expression that represents the passed value.
 */
data class ArgumentT(
    override val id: Id<SmlArgument>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val parameter: Id<EObject>?, // SmlParameter or new ID if unresolved
    val value: Id<SmlExpression>
) : ExpressionT("argumentT", id, parent, enclosing, parameter, value) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents boolean literals.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param value
 * The value of the literal.
 */
data class BooleanT(
    override val id: Id<SmlBoolean>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val value: Boolean
) :
    ExpressionT("booleanT", id, parent, enclosing, value) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents calls.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. another call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param receiver
 * The ID of the fact for the callable that is called.
 *
 * @param typeArguments
 * The list of type arguments or null. Each element in the list is the ID of a typeArgumentT fact for the respective
 * type argument. Note that an empty list is used for a call with an empty type argument list, e.g. `a<>()`, while null
 * is used for a call with no type argument list at all, like `b()`.
 *
 * @param arguments
 * The IDs of the argumentT facts for the arguments of the call. The grammar requires the list to be there so this
 * is never null.
 */
data class CallT(
    override val id: Id<SmlCall>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val receiver: Id<SmlExpression>,
    val typeArguments: List<Id<SmlTypeArgument>>?,
    val arguments: List<Id<SmlArgument>>
) : ExpressionT(
    "callT",
    id,
    parent,
    enclosing,
    receiver,
    typeArguments,
    arguments
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents float literals.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param value
 * The value of the literal.
 */
data class FloatT(
    override val id: Id<SmlFloat>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val value: Double
) :
    ExpressionT("floatT", id, parent, enclosing, value) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents infix operations.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param leftOperand
 * The ID of the fact for the expression that is used as the left operand.
 *
 * @param operator
 * The operator of this operation.
 *
 * @param rightOperand
 * The ID of the fact for the expression that is used as the right operand.
 */
data class InfixOperationT(
    override val id: Id<SmlInfixOperation>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val leftOperand: Id<SmlExpression>,
    val operator: String,
    val rightOperand: Id<SmlExpression>
) : ExpressionT(
    "infixOperationT",
    id,
    parent,
    enclosing,
    leftOperand,
    operator,
    rightOperand
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents integer literals.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param value
 * The value of the literal.
 */
data class IntT(
    override val id: Id<SmlInt>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val value: Int
) :
    ExpressionT("intT", id, parent, enclosing, value) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents lambdas.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param parameters
 * The list of parameters or null. Each element in the list is the ID of a parameterT fact for the respective parameter.
 * Note that an empty list is used for a call with an empty parameter list, e.g. `lambda a() {}`, while null is used
 * for a lambda with no parameter list at all, like `lambda b {}`.
 *
 * @param statements
 * The IDs of the facts for the statements in the body of the lambda. The grammar requires the body to be there
 * so this is never null.
 */
data class LambdaT(
    override val id: Id<SmlLambda>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val parameters: List<Id<SmlParameter>>?,
    val statements: List<Id<SmlStatement>>
) : ExpressionT(
    "lambdaT",
    id,
    parent,
    enclosing,
    parameters,
    statements
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents member accesses.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param receiver
 * The ID of the fact for the receiver of the member access.
 *
 * @param isNullSafe
 * Whether this member access is null safe.
 *
 * @param member
 * The ID of the referenceT fact for the accessed member.
 */
data class MemberAccessT(
    override val id: Id<SmlMemberAccess>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val receiver: Id<SmlExpression>,
    val isNullSafe: Boolean,
    val member: Id<SmlReference>
) : ExpressionT(
    "memberAccessT",
    id,
    parent,
    enclosing,
    receiver,
    isNullSafe,
    member
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represent the `null` literal.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 */
data class NullT(override val id: Id<SmlNull>, override val parent: Id<EObject>, override val enclosing: Id<EObject>) :
    ExpressionT("nullT", id, parent, enclosing) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents parenthesized expressions.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param expression
 * The ID of the fact for the expression inside the parentheses.
 */
data class ParenthesizedExpression(
    override val id: Id<SmlExpression>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val expression: Id<SmlExpression>
) : ExpressionT("parenthesizedExpressionT", id, parent, enclosing, expression) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents prefix operations.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param operator
 * The operator of this operation.
 *
 * @param operand
 * The ID of the fact for the expression that is used as the operand.
 */
data class PrefixOperationT(
    override val id: Id<SmlPrefixOperation>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val operator: String,
    val operand: Id<SmlExpression>
) : ExpressionT(
    "prefixOperationT",
    id,
    parent,
    enclosing,
    operator,
    operand
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents references.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param symbol
 * The ID of the fact for the referenced symbol or an unresolvedT fact if the reference could not be resolved.
 */
data class ReferenceT(
    override val id: Id<SmlReference>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val symbol: Id<EObject> // SmlDeclaration or new ID if unresolved
) :
    ExpressionT("referenceT", id, parent, enclosing, symbol) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents string literals.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param enclosing
 * The ID of the fact for closest ancestor that is not an expression.
 *
 * @param value
 * The value of the literal.
 */
data class StringT(
    override val id: Id<SmlString>,
    override val parent: Id<EObject>,
    override val enclosing: Id<EObject>,
    val value: String
) :
    ExpressionT("stringT", id, parent, enclosing, value) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Types
 **********************************************************************************************************************/

/**
 * Prolog facts for types.
 *
 * @param factName
 * The name of this fact.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent.
 *
 * @param otherArguments
 * Arguments of this fact beyond ID and parent. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class TypeT(factName: String, id: Id<SmlType>, parent: Id<EObject>, vararg otherArguments: Any?) :
    NodeWithParent(factName, id, parent, *otherArguments)

/**
 * This Prolog fact represents callable types.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 *
 * @param parameters
 * The IDs of the parameterT facts for the parameters of the callable type. The grammar requires the list to be there so
 * this is never null.
 *
 * @param results
 * The IDs of the resultT facts for the results of the callable type. The grammar requires the list to be there so this
 * is never null.
 */
data class CallableTypeT(
    override val id: Id<SmlCallableType>,
    override val parent: Id<EObject>,
    val parameters: List<Id<SmlParameter>>,
    val results: List<Id<SmlResult>>
) : TypeT("callableTypeT", id, parent, parameters, results) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents member types.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 *
 * @param receiver
 * The ID of the fact for the receiver of the member type.
 *
 * @param member
 * The ID of the namedTypeT fact for the accessed member type.
 */
data class MemberTypeT(
    override val id: Id<SmlMemberType>,
    override val parent: Id<EObject>,
    val receiver: Id<SmlType>,
    val member: Id<SmlNamedType>
) :
    TypeT("memberTypeT", id, parent, receiver, member) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents named types like classes.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 *
 * @param declaration
 * The ID of the fact for the declaration that is used as the type or an unresolvedT fact if the declaration could not
 * be resolved.
 *
 * @param typeArguments
 * The list of type arguments or null. Each element in the list is the ID of a typeArgumentT fact for the respective
 * type argument. Note that an empty list is used for a named type with an empty type argument list, e.g. `A<>`, while
 * null is used for a named type with no type argument list at all, like `B`.
 *
 * @param isNullable
 * Whether `null` is a valid instance of the type.
 */
data class NamedTypeT(
    override val id: Id<SmlNamedType>,
    override val parent: Id<EObject>,
    val declaration: Id<EObject>, // SmlDeclaration or new ID if unresolved
    val typeArguments: List<Id<SmlTypeArgument>>?,
    val isNullable: Boolean
) : TypeT(
    "namedTypeT",
    id,
    parent,
    declaration,
    typeArguments,
    isNullable
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents parenthesized types.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 *
 * @param type
 * The ID of the fact for the type inside the parentheses.
 */
data class ParenthesizedType(override val id: Id<SmlType>, override val parent: Id<EObject>, val type: Id<SmlType>) :
    TypeT("parenthesizedTypeT", id, parent, type) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents the `this` type that can be used in interfaces.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 */
data class ThisTypeT(override val id: Id<SmlThisType>, override val parent: Id<EObject>) :
    TypeT("thisTypeT", id, parent) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents union types.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a parameter.
 *
 * @param typeArguments
 * The IDs of the typeArgumentT facts for the type arguments of this union type. Note that the grammar requires the list
 * to be there (can be empty) so this will never be `null`.
 */
data class UnionTypeT(
    override val id: Id<SmlUnionType>,
    override val parent: Id<EObject>,
    val typeArguments: List<Id<SmlTypeArgument>>
) :
    TypeT("unionTypeT", id, parent, typeArguments) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Other
 **********************************************************************************************************************/

/**
 * This Prolog fact represents annotation uses.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the annotated declaration.
 *
 * @param annotation
 * The ID of the annotationT fact for the referenced annotation or an unresolvedT fact if the annotation could
 * not be resolved.
 *
 * @param arguments
 * The list of arguments or null. Each element in the list is the ID of an argumentT fact for the respective argument.
 * Note that an empty list is used for an annotation use with an empty argument list, e.g. `@A()`, while null is used
 * for an annotation use without an argument list, like `@B`.
 */
data class AnnotationUseT(
    override val id: Id<SmlAnnotationUse>,
    override val parent: Id<EObject>,
    val annotation: Id<EObject>, // SmlAnnotation or new ID if unresolved
    val arguments: List<Id<SmlArgument>>?
) :
    NodeWithParent("annotationUseT", id, parent, annotation, arguments) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents star projections `*`.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the containing typeArgumentT fact.
 */
data class StarProjectionT(override val id: Id<SmlStarProjection>, override val parent: Id<SmlTypeArgument>) :
    NodeWithParent("starProjectionT", id, parent) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents type arguments.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a call.
 *
 * @param typeParameter
 * If the type argument is named, this is the ID of the typeParameterT fact for the referenced type parameter or an
 * unresolvedT fact if the type parameter could not be resolved. If the type argument is purely positional this is null.
 *
 * @param value
 * The ID of the fact for the type that represents the passed value.
 */
data class TypeArgumentT(
    override val id: Id<SmlTypeArgument>,
    override val parent: Id<EObject>,
    val typeParameter: Id<EObject>?, // SmlTypeParameter or new ID if unresolved
    val value: Id<SmlType>
) :
    NodeWithParent("typeArgumentT", id, parent, typeParameter, value) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents type parameter constraints.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the fact for the logical parent, e.g. a class.
 *
 * @param leftOperand
 * The ID of the typeParameterT fact for the type parameter that is used as the left operand or an unresolvedT fact if
 * the type parameter could not be resolved.
 *
 * @param operator
 * The operator of this operation.
 *
 * @param rightOperand
 * The ID of the fact for the type that is used as the right operand.
 */
data class TypeParameterConstraintT(
    override val id: Id<SmlTypeParameterConstraint>,
    override val parent: Id<EObject>,
    val leftOperand: Id<EObject>, // SmlTypeParameter or new ID if unresolved
    val operator: String,
    val rightOperand: Id<SmlType>
) : NodeWithParent(
    "typeParameterConstraintT",
    id,
    parent,
    leftOperand,
    operator,
    rightOperand
) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents type projections.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the containing typeArgumentT fact.
 *
 * @param variance
 * The variance of this type projection ("in" for contravariance, "out" for covariance, or `null` for invariance).
 *
 * @param type
 * The ID of the fact for the type to use for projection.
 */
data class TypeProjectionT(
    override val id: Id<SmlTypeProjection>,
    override val parent: Id<SmlTypeArgument>,
    val variance: String?,
    val type: Id<SmlType>
) :
    NodeWithParent("typeProjectionT", id, parent, variance, type) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents cross-references that could not be resolved. It is used so the name of the referenced
 * declaration can be retrieved.
 *
 * @param id
 * The ID of this fact.
 *
 * @param name
 * The name of the referenced declaration.
 */
data class UnresolvedT(override val id: Id<EObject>, val name: String) : Node("unresolvedT", id, name) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents wildcards in an assignment, which discard the assigned value.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the assignmentT fact for the containing assignment.
 */
data class WildcardT(override val id: Id<SmlWildcard>, override val parent: Id<SmlAssignment>) :
    NodeWithParent("wildcardT", id, parent) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact represents yields.
 *
 * @param id
 * The ID of this fact.
 *
 * @param parent
 * The ID of the assignmentT fact for the containing assignment.
 *
 * @param result
 * The ID of the resultT fact for the referenced result or an unresolvedT fact if the result could not be
 * resolved.
 */
data class YieldT(
    override val id: Id<SmlYield>,
    override val parent: Id<SmlAssignment>,
    val result: Id<EObject> // SmlResult or new ID if unresolved
) :
    NodeWithParent("yieldT", id, parent, result) {
    override fun toString() = super.toString()
}


/**********************************************************************************************************************
 * Relations
 **********************************************************************************************************************/

/**
 * Prolog facts that add additional information to nodes and do not have their own ID.
 *
 * @param factName
 * The name of this fact.
 *
 * @param target
 * The ID of the node that should be enhanced.
 *
 * @param otherArguments
 * The arguments of this fact. Arguments can either be `null`, booleans, IDs, number, strings or lists.
 */
sealed class Relation(factName: String, target: Id<EObject>, vararg otherArguments: Any?) :
    PlFact(factName, target, *otherArguments) {

    /**
     * The ID of the node that should be enhanced.
     */
    abstract val target: Id<EObject>
}

/**
 * This Prolog fact represents modifiers.
 *
 * @param target
 * The ID of the fact for the modified declaration.
 *
 * @param modifier
 * The modifier, for example "deprecated" or "open".
 */
data class ModifierT(override val target: Id<SmlDeclaration>, val modifier: String) :
    Relation("modifierT", target, modifier) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact stores the file path of a compilation unit.
 *
 * @param target
 * The ID of the fact for the respective compilation unit.
 *
 * @param path
 * The file path of the compilation unit.
 */
data class FileS(override val target: Id<SmlCompilationUnit>, val path: String) : Relation("fileS", target, path) {
    override fun toString() = super.toString()
}

/**
 * This Prolog fact stores the location of the source code for some node in a source file.
 *
 * @param target
 * The ID of the fact for the respective node.
 *
 * @param offset
 * The total offset of start of the source code for the node from the start of the file.
 *
 * @param line
 * The line where the start of the source code for the node is located.
 *
 * @param column
 * The column where the start of the source code for the node is located.
 *
 * @param length
 * The length the source code for the node.
 */
data class SourceLocationS(
    override val target: Id<EObject>,
    val offset: Int,
    val line: Int,
    val column: Int,
    val length: Int
) :
    Relation("sourceLocationS", target, offset, line, column, length) {
    override fun toString() = super.toString()
}
