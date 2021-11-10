package de.unibonn.simpleml.prolog_bridge.converters

import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.AttributeT
import de.unibonn.simpleml.prolog_bridge.model.facts.ClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumInstanceT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumT
import de.unibonn.simpleml.prolog_bridge.model.facts.FileS
import de.unibonn.simpleml.prolog_bridge.model.facts.FunctionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.InterfaceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ModifierT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.model.facts.SourceLocationS
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowStepT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowT
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.prolog_bridge.utils.IdManager
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumInstance
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStatement
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.instancesOrEmpty
import de.unibonn.simpleml.utils.membersOrEmpty
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.parentTypesOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import de.unibonn.simpleml.utils.statementsOrEmpty
import de.unibonn.simpleml.utils.typeParameterConstraintsOrEmpty
import de.unibonn.simpleml.utils.typeParametersOrEmpty
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class SimpleMLAstToPrologFactbase {
    private var idManager = IdManager<EObject>()

    fun createFactbase(obj: SmlCompilationUnit): PlFactbase {
        reset()

        return PlFactbase().apply {
            visitCompilationUnit(obj)
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
        }
    }

    private fun PlFactbase.visitCompilationUnit(obj: SmlCompilationUnit) = visitEObject(obj) {
        obj.imports.forEach { visitImport(it, obj.id) }
        obj.members.forEach { visitDeclaration(it, obj.id) }

        +CompilationUnitT(obj.id, obj.name, obj.imports.map { it.id }, obj.members.map { it.id })
        +FileS(obj.id, obj.eResource().uri.toUNIXString())
    }

    private fun PlFactbase.visitImport(obj: SmlImport, parentId: Id<SmlCompilationUnit>) = visitEObject(obj) {
        +ImportT(obj.id, parentId, obj.importedNamespace, obj.alias)
    }

    private fun PlFactbase.visitDeclaration(obj: SmlDeclaration, parentId: Id<EObject>): Unit =
        visitEObject(obj) {
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

                }
                is SmlResult -> {

                }
                is SmlTypeParameter -> {

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
        }


    private fun PlFactbase.visitAnnotationUse(obj: SmlAnnotationUse, parentId: Id<SmlDeclaration>) {
////       TODO obj.argumentList?.arguments?.forEach { visitArgument(it, obj.id, obj.id) }
//
//        if (obj.annotation.name != null) {
////            visitDeclaration(obj.annotation, obj.id)
//
//            // TODO: visit referenced declaration
//        } else {
//            // TODO: visit unresolved
//            val node = NodeModelUtils.getNode(obj).text.trim()
////            +AnnotationT(obj.annotation.id, parentId, node, null)
//        }
//
//        +AnnotationUseT(obj.id, parentId, idManager.nextId(), obj.argumentList?.arguments?.map { it.id })
    }

    private fun PlFactbase.visitModifier(modifier: String, target: Id<SmlDeclaration>) {
        +ModifierT(target, modifier)
    }

    private fun PlFactbase.visitType(obj: SmlType, parentId: Id<EObject>) =
        visitEObject(obj) {
        }

    private fun PlFactbase.visitTypeParameterConstraint(obj: SmlTypeParameterConstraint, parentId: Id<EObject>) =
        visitEObject(obj) {
        }

    private fun PlFactbase.visitStatement(obj: SmlStatement, parentId: Id<EObject>) =
        visitEObject(obj) {
        }


    // Helpers ---------------------------------------------------------------------------------------------------------

    private fun PlFactbase.visitEObject(obj: EObject, visitr: PlFactbase.() -> Unit) {
        if (idManager.knowsObject(obj)) {
            return
        }

        obj.id // Enforce creation of ID to ensure correct parent-child order
        visitr()
        +SourceLocationS(obj)
    }


//            is SmlAttribute -> {
//                visitType(obj.type, obj.id)
//                +AttributeT(obj.id, parentId, obj.name, obj.type?.referenceId)
//            }
//            is SmlPlaceholder -> {
//                visitPlaceholder(obj, parentId)
//            }
//            is SmlParameter -> {
//                obj.type?.let { visitType(it, obj.id) }
//                obj.defaultValue?.let { visitExpression(it, obj.id, obj.id) }
//                +ParameterT(obj.id, parentId, obj.name, obj.isVararg, obj.type?.id, obj.defaultValue?.id)
//            }
//            is SmlNamedTypeDeclaration -> {
//                    is SmlEnum -> {
//                        obj.body?.instances?.forEach { visitDeclaration(it, obj.id) }
//                        +EnumT(obj.id, parentId, obj.name, obj.body?.instances?.map { it.id })
//                    }
//                    is SmlTypeParameter -> {
//                        +TypeParameterT(obj.id, parentId, obj.name, obj.variance)
//                    }
//                    is SmlLambdaYield -> {
//                        visitLambdaYield(obj, parentId)
//                    }
//                }
//            }
//            is SmlConstructor -> {
//
//            }
//            is SmlEnumInstance -> {
//                +EnumInstanceT(obj.id, parentId, obj.name)
//            }
//            is SmlResult -> {
//                visitType(obj.type, obj.id)
//                +ResultT(obj.id, parentId, obj.name, obj.type?.id)
//            }
//            is SmlFunction -> {
//                obj.parametersOrEmpty().forEach { visitParameter(it, obj.id) }
//                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }
//                obj.typeParameterConstraintsOrEmpty().forEach { visitTypeParameterConstraint(it, obj.id) }
//                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
//
//                +FunctionT(
//                        obj.id,
//                        parentId,
//                        obj.name,
//                        obj.typeParametersOrEmpty().map { it.id },
//                        obj.parametersOrEmpty().map { it.id },
//                        obj.resultList?.results?.map { it.id },
//                        obj.typeParameterConstraintsOrEmpty().map { it.id }
//                )
//            }
//            is SmlWorkflow -> {
//                obj.statementsOrEmpty().forEach { visitStatement(it, obj.id) }
//                +WorkflowT(obj.id, parentId, obj.name, obj.body.statements.map { it.id })
//            }
//        }

//    private fun PlFactbase.visitConstructor(obj: SmlConstructor, parentId: Id) {
//        if (idManager.knowsObject(obj)) return
//
//        obj.parameterList.parameters.forEach { visitParameter(it, obj.id) }
////        +ConstructorT(obj.id, parentId, obj.parameterList.parameters.map { it.id })
//    }
//        // TODO create an unresolved reference fact and use this in place of the annotation if it cannot be resolved
////        +AnnotationUseT(obj.id, parentId, obj.annotation?.id, obj.argumentList?.arguments?.map { it.id })
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitParameter(obj: SmlParameter, parentId: Id) {
//        visitDeclaration(obj, parentId)
//    }
//
//    private fun PlFactbase.visitStatement(obj: SmlStatement, parentId: Id) {
//        when (obj) {
//            is SmlAssignment -> {
//                obj.assigneesOrEmpty().forEach { visitDoStatementAssignee(it, parentId) }
//                visitExpression(obj.expression, obj.id, obj.id)
//                +AssignmentT(obj.id, parentId, obj.assigneeList.assignees.map { it.id }, obj.expression.id)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitPlaceholder(obj: SmlPlaceholder, parentId: Id) {
//        obj.annotations.forEach { visitAnnotationUse(it, obj.id) }
//
//        +PlaceholderT(obj.id, parentId, obj.name)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitExpression(obj: SmlExpression, parentId: Id, enclosingId: Id) {
//        if (idManager.knowsObject(obj)) return
//
//        when (obj) {
//            is SmlInfixOperation -> {
//                visitExpression(obj.leftOperand, obj.id, enclosingId)
//                visitExpression(obj.rightOperand, obj.id, enclosingId)
//                +InfixOperationT(obj.id, parentId, enclosingId, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
//            }
//            is SmlPrefixOperation -> {
//                visitExpression(obj.operand, obj.id, enclosingId)
//                +PrefixOperationT(obj.id, parentId, enclosingId, obj.operator, obj.operand.id)
//            }
//            is SmlLambda -> {
//                obj.body.statements.forEach { visitStatement(it, obj.id) }
//                obj.parametersOrEmpty().forEach { visitParameter(it, parentId) }
//                +LambdaT(obj.id, parentId, enclosingId, obj.parameterList?.parameters?.map { it.id }, obj.body.statements.map { it.id })
//            }
//            is SmlNull -> {
//                +NullT(obj.id, parentId, enclosingId)
//            }
//            is SmlReference -> {
//                if (obj.declaration !is SmlAnnotationImpl) {
//                    visitDeclaration(obj.declaration, obj.id)
//                    +ReferenceT(obj.id, parentId, enclosingId, obj.declaration.id)
//                } else {
//                    // TODO should only contain the text for the name not the full node!
//                    /* NodeModelUtils.findNodesForFeature(reference, SimpleMLPackage.Literals.SML_REFERENCE__DECLARATION).forEach {
//                           println(it.text)
//                       }
//                    */
//
//                    val node = NodeModelUtils.getNode(obj)
//                    +UnresolvedT(obj.id, node.text.trim())
//                }
//            }
//            is SmlChainedExpression -> {
//                visitExpression(obj.receiver, parentId, enclosingId)
//
//                when (obj) {
//                    is SmlMemberAccess -> {
//                        visitExpression(obj.receiver, obj.id, enclosingId)
//                        visitExpression(obj.member, obj.id, enclosingId)
//                        +MemberAccessT(obj.id, parentId, enclosingId, obj.receiver.id, obj.isNullable, obj.member.id)
//                    }
//                    is SmlCall -> {
//                        obj.argumentList.arguments.forEach { visitArgument(it, obj.id, enclosingId) }
//                        obj.typeArgumentsOrEmpty().forEach { visitTypeArgument(it, obj.id, enclosingId) }
//
//                        val tal: List<Id>? = if (obj.typeArgumentList == null) {
//                            null
//                        } else obj.typeArgumentsOrEmpty().map { it.id }
//
//                        +CallT(obj.id, parentId, enclosingId, obj.receiver.id, tal, obj.argumentList.arguments.map { it.id })
//                    }
//                }
//            }
//            is SmlBoolean -> {
//                +BooleanT(obj.id, parentId, enclosingId, obj.isTrue)
//            }
//            is SmlFloat -> {
//                +FloatT(obj.id, parentId, enclosingId, obj.value)
//            }
//            is SmlInt -> {
//                +IntT(obj.id, parentId, enclosingId, obj.value)
//            }
//            is SmlString -> {
//                +StringT(obj.id, parentId, enclosingId, obj.value)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//
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
//
//    private fun PlFactbase.visitTypeArgument(obj: SmlTypeArgument, parentId: Id, enclosingId: Id) {
//        obj.typeParameter?.let { visitDeclaration(obj.typeParameter, obj.id) }
//        visitTypeArgumentValue(obj.value, obj.id, enclosingId)
//        +TypeArgumentT(obj.id, parentId, obj.typeParameter?.id, obj.value.id)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitTypeArgumentValue(obj: SmlTypeArgumentValue, parentId: Id, enclosingId: Id) {
//        when (obj) {
//            is SmlStarProjection -> {
//                +StarProjectionT(obj.id, parentId)
//            }
//            is SmlTypeProjection -> {
//                visitType(obj.type, obj.id)
//                +TypeProjectionT(obj.id, parentId, obj.variance, obj.type.id)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitLambdaYield(obj: SmlLambdaYield, parentId: Id) {
//        obj.annotations.forEach { visitAnnotationUse(it, obj.id) }
//
//        +LambdaYieldT(obj.id, parentId, obj.name)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitDoStatementAssignee(obj: SmlAssignee, parentId: Id) {
//        when (obj) {
//            is SmlWildcard -> {
//                +WildcardT(obj.id, parentId)
//            }
//            is SmlPlaceholder -> {
//                visitPlaceholder(obj, parentId)
//            }
//            is SmlYield -> {
//                visitDeclaration(obj.result, obj.referenceId!!.id)
//                +YieldT(obj.id, parentId, obj.result.id)
//            }
//            is SmlLambdaYield -> {
//                visitLambdaYield(obj, parentId)
//            }
//        }
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitTypeParameterConstraint(obj: SmlTypeParameterConstraint, parentId: Id) {
//        visitDeclaration(obj.leftOperand, obj.id)
//        visitType(obj.rightOperand, obj.id)
//
//        +TypeParameterConstraintT(obj.id, obj.eResource().id, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.visitArgument(obj: SmlArgument, parentId: Id, enclosingId: Id) {
//        if (obj.parameter != null) visitParameter(obj.parameter, parentId)
//        visitExpression(obj.value, obj.id, enclosingId)
//
//        //TODO überall nach nullables suchen und Vorkehrungen treffen
//        val para: Id?
//        if (obj.parameter == null) para = null
//        else para = obj.parameterOrNull()?.id
//
//        +ArgumentT(obj.id, parentId, enclosingId, para, obj.value.id)
//        +SourceLocationS(obj)
//    }

    // TODO the solution using null to mark unresolved references is not ideal yet since we then lose the referenced name
//  example: for call() the resulting callT fact has the callable set to null if it could not be linked to a function
    private val EObject.referenceId: Id<EObject>?
        get() {
            return if (this.eResource() == null) {
                null
            } else {
                this.id
            }
        }

    private val <T : EObject> T.id: Id<T>
        get() = idManager.assignIdIfAbsent(this)

    private fun reset() {
        idManager = IdManager()
    }

    @Suppress("FunctionName")
    private fun SourceLocationS(obj: EObject): SourceLocationS {
        val node = NodeModelUtils.getNode(obj)
        val location = NodeModelUtils.getLineAndColumn(node, node.offset)

        return SourceLocationS(obj.id, location.line, location.column, node.offset, node.totalLength)
    }

    private fun URI.toUNIXString(): String {
        return this.toFileString().replace("\\", "/")
    }
}
