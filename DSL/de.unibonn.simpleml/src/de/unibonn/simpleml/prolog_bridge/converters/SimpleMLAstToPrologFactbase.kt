package de.unibonn.simpleml.prolog_bridge.converters

import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationUseT
import de.unibonn.simpleml.prolog_bridge.model.facts.ArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AssignmentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AttributeT
import de.unibonn.simpleml.prolog_bridge.model.facts.BooleanT
import de.unibonn.simpleml.prolog_bridge.model.facts.CallT
import de.unibonn.simpleml.prolog_bridge.model.facts.ClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumInstanceT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumT
import de.unibonn.simpleml.prolog_bridge.model.facts.ExpressionStatementT
import de.unibonn.simpleml.prolog_bridge.model.facts.FileS
import de.unibonn.simpleml.prolog_bridge.model.facts.FloatT
import de.unibonn.simpleml.prolog_bridge.model.facts.FunctionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.InfixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.IntT
import de.unibonn.simpleml.prolog_bridge.model.facts.InterfaceT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaYieldT
import de.unibonn.simpleml.prolog_bridge.model.facts.MemberAccessT
import de.unibonn.simpleml.prolog_bridge.model.facts.ModifierT
import de.unibonn.simpleml.prolog_bridge.model.facts.NullT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.model.facts.PlaceholderT
import de.unibonn.simpleml.prolog_bridge.model.facts.PrefixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.ReferenceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ResultT
import de.unibonn.simpleml.prolog_bridge.model.facts.SourceLocationS
import de.unibonn.simpleml.prolog_bridge.model.facts.StarProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.StringT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.UnresolvedT
import de.unibonn.simpleml.prolog_bridge.model.facts.WildcardT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowStepT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowT
import de.unibonn.simpleml.prolog_bridge.model.facts.YieldT
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.prolog_bridge.utils.IdManager
import de.unibonn.simpleml.simpleML.SimpleMLPackage
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
import de.unibonn.simpleml.simpleML.SmlTypeArgumentValue
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeProjection
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWildcard
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.argumentsOrEmpty
import de.unibonn.simpleml.utils.assigneesOrEmpty
import de.unibonn.simpleml.utils.instancesOrEmpty
import de.unibonn.simpleml.utils.membersOrEmpty
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.parentTypesOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import de.unibonn.simpleml.utils.statementsOrEmpty
import de.unibonn.simpleml.utils.typeArgumentsOrEmpty
import de.unibonn.simpleml.utils.typeParameterConstraintsOrEmpty
import de.unibonn.simpleml.utils.typeParametersOrEmpty
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class SimpleMLAstToPrologFactbase {
    private var idManager = IdManager<EObject>()

    fun createFactbase(compilationUnits: List<SmlCompilationUnit>): PlFactbase {
        reset()

        return PlFactbase().apply {

            // Enforce order of IDs
            compilationUnits.forEach { compilationUnit ->
                compilationUnit.id
                compilationUnit.eAllContents().asSequence().forEach { obj ->
                    obj.id
                }
            }

            // Create facts
            compilationUnits.forEach {
                visitCompilationUnit(it)
            }
        }
    }

//    fun createFactbase(obj: SmlCompilationUnit): PlFactbase {
//        reset()
//
//        return PlFactbase().apply {
//            visitCompilationUnit(obj)
//
//            val node = NodeModelUtils.getNode(obj)
//
//            node.asTreeIterable.forEach {
//                println(it)
//                if (it is HiddenLeafNode) {
//                    val grammarElement = it.grammarElement
//                    if (grammarElement is TerminalRule && (grammarElement.name == "SL_COMMENT")) {
//                        println(grammarElement)
//                        println(it.text.replace("^//\\s*".toRegex(), ""))
//                        println(NodeModelUtils.getLineAndColumn(it, it.offset))
//                        println(it.offset)
//                    }
//                    if (grammarElement is TerminalRule && (grammarElement.name == "ML_COMMENT")) {
//                        println(grammarElement)
//                        println(it.text)
//                    }
//                }
//            }
//        }
//    }

    // *****************************************************************************************************************
    // Declarations
    // ****************************************************************************************************************/

    private fun PlFactbase.visitCompilationUnit(obj: SmlCompilationUnit) {
        obj.imports.forEach { this.visitImport(it, obj.id) }
        obj.members.forEach { this.visitDeclaration(it, obj.id) }

        +CompilationUnitT(obj.id, obj.name, obj.imports.map { it.id }, obj.members.map { it.id })
        +FileS(obj.id, obj.eResource().uri.toUNIXString())
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitImport(obj: SmlImport, parentId: Id<SmlCompilationUnit>) {
        +ImportT(obj.id, parentId, obj.importedNamespace, obj.alias)
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitDeclaration(obj: SmlDeclaration, parentId: Id<EObject>) {
        obj.annotations.forEach { visitAnnotationUse(it, obj.id) }
        obj.modifiers.forEach { visitModifier(it, obj.id) }

        when (obj) {
            is SmlAnnotation -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +AnnotationT(obj.id, parentId, obj.name, obj.parameterList?.parameters?.map { it.id })
            }
            is SmlAttribute -> {
                obj.type?.let { visitType(it, obj.id) }

                +AttributeT(obj.id, parentId, obj.name, obj.type?.id)
            }
            is SmlClass -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parentTypesOrEmpty().forEach { visitType(it, obj.id) }
                obj.typeParameterConstraintsOrEmpty().forEach { visitTypeParameterConstraint(it, obj.id) }
                obj.membersOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +ClassT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.constructor?.parameterList?.parameters?.map { it.id },
                    obj.parentTypeList?.parentTypes?.map { it.id },
                    obj.typeParameterConstraintList?.constraints?.map { it.id },
                    obj.body?.members?.map { it.id }
                )
            }
            is SmlEnum -> {
                obj.instancesOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +EnumT(obj.id, parentId, obj.name, obj.body?.instances?.map { it.id })
            }
            is SmlEnumInstance -> {
                +EnumInstanceT(obj.id, parentId, obj.name)
            }
            is SmlFunction -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.typeParameterConstraintsOrEmpty().forEach { visitTypeParameterConstraint(it, obj.id) }

                +FunctionT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.parametersOrEmpty().map { it.id },
                    obj.resultList?.results?.map { it.id },
                    obj.typeParameterConstraintList?.constraints?.map { it.id },
                )
            }
            is SmlInterface -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parentTypesOrEmpty().forEach { visitType(it, obj.id) }
                obj.typeParameterConstraintsOrEmpty().forEach { visitTypeParameterConstraint(it, obj.id) }
                obj.membersOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +InterfaceT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.constructor?.parameterList?.parameters?.map { it.id },
                    obj.parentTypeList?.parentTypes?.map { it.id },
                    obj.typeParameterConstraintList?.constraints?.map { it.id },
                    obj.body?.members?.map { it.id }
                )
            }
            is SmlParameter -> {
                obj.type?.let { visitType(it, obj.id) }
                obj.defaultValue?.let { visitExpression(it, obj.id, obj.id) }

                +ParameterT(obj.id, parentId, obj.name, obj.isVararg, obj.type?.id, obj.defaultValue?.id)
            }
            is SmlResult -> {
                obj.type?.let { visitType(it, obj.id) }

                +ResultT(obj.id, parentId, obj.name, obj.type?.id)
            }
            is SmlTypeParameter -> {
                +TypeParameterT(obj.id, parentId, obj.name, obj.variance)
            }
            is SmlWorkflow -> {
                obj.statementsOrEmpty().forEach { visitStatement(it, obj.id) }

                +WorkflowT(obj.id, parentId, obj.name, obj.statementsOrEmpty().map { it.id })
            }
            is SmlWorkflowStep -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.statementsOrEmpty().forEach { visitStatement(it, obj.id) }

                +WorkflowStepT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.parametersOrEmpty().map { it.id },
                    obj.resultList?.results?.map { it.id },
                    obj.statementsOrEmpty().map { it.id }
                )
            }
        }

        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitAnnotationUse(obj: SmlAnnotationUse, parentId: Id<SmlDeclaration>) {
        visitCrossReference(obj, SimpleMLPackage.Literals.SML_ANNOTATION_USE__ANNOTATION, obj.annotation)
        obj.argumentsOrEmpty().forEach { visitExpression(it, obj.id, obj.id) }

        +AnnotationUseT(obj.id, parentId, obj.annotation.id, obj.argumentList?.arguments?.map { it.id })
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitModifier(modifier: String, target: Id<SmlDeclaration>) {
        +ModifierT(target, modifier)
    }


    // *****************************************************************************************************************
    // Statements
    // ****************************************************************************************************************/

    private fun PlFactbase.visitStatement(obj: SmlStatement, parentId: Id<EObject>) {
        when (obj) {
            is SmlAssignment -> {
                obj.assigneesOrEmpty().forEach { this.visitAssignee(it, obj.id) }
                this.visitExpression(obj.expression, obj.id, obj.id)

                +AssignmentT(obj.id, parentId, obj.assigneesOrEmpty().map { it.id }, obj.expression.id)
            }
            is SmlExpressionStatement -> {
                this.visitExpression(obj.expression, obj.id, obj.id)

                +ExpressionStatementT(obj.id, parentId, obj.expression.id)
            }
        }

        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitAssignee(obj: SmlAssignee, parentId: Id<SmlAssignment>) {
        when (obj) {
            is SmlLambdaYield -> {
                +LambdaYieldT(obj.id, parentId, obj.name)
            }
            is SmlPlaceholder -> {
                +PlaceholderT(obj.id, parentId, obj.name)
            }
            is SmlWildcard -> {
                +WildcardT(obj.id, parentId)
            }
            is SmlYield -> {
                visitCrossReference(obj, SimpleMLPackage.Literals.SML_YIELD__RESULT, obj.result)

                +YieldT(obj.id, parentId, obj.result.id)
            }
        }

        visitSourceLocation(obj)
    }

    // *****************************************************************************************************************
    // Expressions
    // ****************************************************************************************************************/

    private fun PlFactbase.visitExpression(obj: SmlExpression, parentId: Id<EObject>, enclosingId: Id<EObject>) {
        when (obj) {
            is SmlArgument -> {
                obj.parameter?.let { visitCrossReference(obj, SimpleMLPackage.Literals.SML_ARGUMENT__PARAMETER, it) }
                visitExpression(obj.value, obj.id, enclosingId)

                +ArgumentT(obj.id, parentId, enclosingId, obj.parameter?.id, obj.value.id)
            }
            is SmlBoolean -> {
                +BooleanT(obj.id, parentId, enclosingId, obj.isTrue)
            }
            is SmlCall -> {
                visitExpression(obj.receiver, obj.id, enclosingId)
                obj.typeArgumentsOrEmpty().forEach { visitTypeArgument(it, obj.id) }
                obj.argumentsOrEmpty().forEach { visitExpression(it, obj.id, enclosingId) }

                +CallT(
                    obj.id,
                    parentId,
                    enclosingId,
                    obj.receiver.id,
                    obj.typeArgumentList?.typeArguments?.map { it.id },
                    obj.argumentsOrEmpty().map { it.id }
                )
            }
            is SmlFloat -> {
                +FloatT(obj.id, parentId, enclosingId, obj.value)
            }
            is SmlInfixOperation -> {
                visitExpression(obj.leftOperand, obj.id, enclosingId)
                visitExpression(obj.rightOperand, obj.id, enclosingId)

                +InfixOperationT(obj.id, parentId, enclosingId, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
            }
            is SmlInt -> {
                +IntT(obj.id, parentId, enclosingId, obj.value)
            }
            is SmlLambda -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.statementsOrEmpty().forEach { visitStatement(it, obj.id) }

                +LambdaT(
                    obj.id,
                    parentId,
                    enclosingId,
                    obj.parameterList?.parameters?.map { it.id },
                    obj.statementsOrEmpty().map { it.id }
                )
            }
            is SmlMemberAccess -> {
                visitExpression(obj.receiver, obj.id, enclosingId)
                visitExpression(obj.member, obj.id, enclosingId)

                +MemberAccessT(obj.id, parentId, enclosingId, obj.receiver.id, obj.isNullable, obj.member.id)
            }
            is SmlNull -> {
                +NullT(obj.id, parentId, enclosingId)
            }
            is SmlPrefixOperation -> {
                visitExpression(obj.operand, obj.id, enclosingId)

                +PrefixOperationT(obj.id, parentId, enclosingId, obj.operator, obj.operand.id)
            }
            is SmlReference -> {
                visitCrossReference(obj, SimpleMLPackage.Literals.SML_REFERENCE__DECLARATION, obj.declaration)

                +ReferenceT(obj.id, parentId, enclosingId, obj.declaration.id)
            }
            is SmlString -> {
                +StringT(obj.id, parentId, enclosingId, obj.value)
            }
        }

        visitSourceLocation(obj)
    }


    // *****************************************************************************************************************
    // Types
    // ****************************************************************************************************************/

    private fun PlFactbase.visitType(obj: SmlType, parentId: Id<EObject>) {
        when (obj) {
            is SmlCallableType -> {

            }
            is SmlMemberType -> {

            }
            is SmlNamedType -> {

            }
            is SmlThisType -> {

            }
            is SmlUnionType -> {

            }
        }

        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitTypeArgument(obj: SmlTypeArgument, parentId: Id<EObject>) {
        obj.typeParameter?.let {
            visitCrossReference(
                obj,
                SimpleMLPackage.Literals.SML_TYPE_ARGUMENT__TYPE_PARAMETER,
                obj.typeParameter
            )
        }
        visitTypeArgumentValue(obj.value, obj.id)

        +TypeArgumentT(obj.id, parentId, obj.typeParameter?.id, obj.value.id)
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitTypeArgumentValue(obj: SmlTypeArgumentValue, parentId: Id<SmlTypeArgument>) {
        when (obj) {
            is SmlStarProjection -> {
                +StarProjectionT(obj.id, parentId)
            }
            is SmlTypeProjection -> {
                visitType(obj.type, obj.id)

                +TypeProjectionT(obj.id, parentId, obj.variance, obj.type.id)
            }
        }

        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitTypeParameterConstraint(obj: SmlTypeParameterConstraint, parentId: Id<EObject>) {
        visitSourceLocation(obj)
    }


    // *****************************************************************************************************************
    // Other
    // ****************************************************************************************************************/

    private fun PlFactbase.visitSourceLocation(obj: EObject) {
        val node = NodeModelUtils.getNode(obj)
        val location = NodeModelUtils.getLineAndColumn(node, node.offset)

        +SourceLocationS(obj.id, location.line, location.column, node.offset, node.totalLength)
    }

    private fun PlFactbase.visitCrossReference(source: EObject, edge: EReference, target: EObject) {
        if (!idManager.knowsObject(target)) {
            val name = getReferencedName(source, edge)
            +UnresolvedT(target.id, name)
        }
    }

    private fun getReferencedName(eObject: EObject, eReference: EReference): String {
        return NodeModelUtils
            .findNodesForFeature(eObject, eReference)
            .joinToString("") { it.text }
    }


    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

//    private fun PlFactbase.visitType(obj: SmlType?, parentId: Id) {
//        if (obj == null) return
//        when (obj) {
//            is SmlCallableType -> {
//                obj.parameterList.parameters.forEach { visitParameter(it, obj.id) }
//                obj.resultList.results.forEach { visitDeclaration(it, obj.id) }
//
//                +CallableTypeT(obj.id, parentId, obj.parameterList.parameters.map { it.id }, obj.resultList.results.map { it.id })
//            }
//            is SmlMemberType -> {
//                visitType(obj.member, obj.id)
//                visitType(obj.receiver, obj.id)
//
//                +MemberTypeT(obj.id, parentId, obj.receiver.id, obj.member.id)
//            }
//            is SmlNamedType -> {
//                visitDeclaration(obj.declaration, obj.id)
//                obj.typeArgumentsOrEmpty().forEach { visitTypeArgument(it, obj.id, obj.id) }
//
//                +NamedTypeT(obj.id, parentId, obj.declaration.id, obj.typeArgumentsOrEmpty().map { it.id }, obj.isNullable)
//            }
//            is SmlUnionType -> {
//                obj.typeArgumentList.typeArguments.forEach { visitTypeArgument(it, obj.id, obj.id) }
//
//                +UnionTypeT(obj.id, parentId, obj.typeArgumentList.typeArguments.map { it.id })
//            }
//            is SmlThisType -> {
//                +ThisTypeT(obj.id, parentId)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//    private fun PlFactbase.visitTypeParameterConstraint(obj: SmlTypeParameterConstraint, parentId: Id) {
//        visitDeclaration(obj.leftOperand, obj.id)
//        visitType(obj.rightOperand, obj.id)
//
//        +TypeParameterConstraintT(obj.id, obj.eResource().id, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
//        +SourceLocationS(obj)
//    }

    private val <T : EObject> T.id: Id<T>
        get() = idManager.assignIdIfAbsent(this)

    private fun reset() {
        idManager = IdManager()
    }

    private fun URI.toUNIXString(): String {
        return this.toFileString().replace("\\", "/")
    }
}
