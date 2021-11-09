package de.unibonn.simpleml.prolog_bridge.converters

import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.FileS
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.model.facts.SourceLocationS
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.prolog_bridge.utils.IdManager
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.parametersOrEmpty
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class SimpleMLAstToPrologFactbase {
    private var idManager = IdManager<EObject>()

    fun createFactbase(obj: SmlCompilationUnit): PlFactbase {
        reset()

        return PlFactbase().apply {
            handleCompilationUnit(obj)
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

    private fun PlFactbase.handleCompilationUnit(obj: SmlCompilationUnit) = handleEObject(obj) {
        obj.imports.forEach { handleImport(it, obj.id) }
        obj.members.forEach { handleCompilationUnitMember(it, obj.id) }

        +CompilationUnitT(obj.id, obj.name, obj.imports.map { it.id }, obj.members.map { it.id })
        +FileS(obj.id, obj.eResource().uri.toUNIXString())
    }

    private fun PlFactbase.handleImport(obj: SmlImport, parentId: Id<SmlCompilationUnit>) = handleEObject(obj) {
        +ImportT(obj.id, parentId, obj.importedNamespace, obj.alias)
    }

    private fun PlFactbase.handleCompilationUnitMember(obj: SmlDeclaration, parentId: Id<SmlCompilationUnit>) =
        handleEObject(obj) {
            obj.annotations.forEach { handleAnnotationUse(it, obj.id) }
            obj.modifiers.forEach { handleModifier(it, obj.id) }

            when (obj) {
                is SmlAnnotation -> {
                    obj.parametersOrEmpty().forEach { handleParameter(it, obj.id) }

                    +AnnotationT(obj.id, parentId, obj.name, obj.parametersOrEmpty().map { it.id })
                }
                is SmlClass -> {

                }
                is SmlEnum -> {

                }
                is SmlFunction -> {

                }
                is SmlInterface -> {

                }
                is SmlWorkflow -> {

                }
                is SmlWorkflowStep -> {

                }
            }
        }


    private fun PlFactbase.handleAnnotationUse(obj: SmlAnnotationUse, parentId: Id<SmlDeclaration>) {
//        if (idManager.knowsObject(obj)) return
//
//        obj.argumentList?.arguments?.forEach { handleArgument(it, obj.id, obj.id) }
//        if (obj.annotation.name != null) {
//            handleDeclaration(obj.annotation, obj.id)
//        } else {
//            val node = NodeModelUtils.getNode(obj).text.trim()
//            +AnnotationT(obj.annotation.id, parentId, node, null)
//        }
    }

    private fun PlFactbase.handleModifier(modifier: String, target: Id<SmlDeclaration>) {

    }

    private fun PlFactbase.handleParameter(obj: SmlParameter, parentId: Id<EObject>) =
        handleEObject(obj) {
        }

    private fun PlFactbase.handleEObject(obj: EObject, handler: PlFactbase.() -> Unit) {
        if (idManager.knowsObject(obj)) {
            return
        }

        obj.id // Enforce creation of ID to ensure correct parent-child order
        handler()
        +SourceLocationS(obj)
    }


//            is SmlAttribute -> {
//                handleType(obj.type, obj.id)
//                +AttributeT(obj.id, parentId, obj.name, obj.type?.referenceId)
//            }
//            is SmlPlaceholder -> {
//                handlePlaceholder(obj, parentId)
//            }
//            is SmlParameter -> {
//                obj.type?.let { handleType(it, obj.id) }
//                obj.defaultValue?.let { handleExpression(it, obj.id, obj.id) }
//                +ParameterT(obj.id, parentId, obj.name, obj.isVararg, obj.type?.id, obj.defaultValue?.id)
//            }
//            is SmlNamedTypeDeclaration -> {
//                when (obj) {
//                    is SmlClassOrInterface -> {
//                        obj.body?.members?.forEach { handleDeclaration(it, parentId) }
//                        obj.constructor?.let { handleConstructor(obj.constructor, obj.id) }
//                        obj.parentTypeList?.parentTypes?.forEach { handleType(it, parentId) }
//                        obj.typeParameterConstraintsOrEmpty().forEach { handleTypeParameterConstraint(it, parentId) }
//                        obj.typeParameterList?.typeParameters?.forEach { handleDeclaration(it, parentId) }
//
////                        when (obj) {
////                            is SmlClass -> {
////                                +ClassT(
////                                        obj.id,
////                                        parentId,
////                                        obj.name,
////                                        obj.typeParametersOrEmpty().map { it.id },
////                                        obj.constructor?.id,
////                                        obj.parentTypesOrEmpty().map { it.id },
////                                        obj.typeParameterConstraintsOrEmpty().map { it.id },
////                                        obj.membersOrEmpty().map { it.id }
////                                )
////                            }
////                            is SmlInterface -> {
////                                +InterfaceT(
////                                        obj.id,
////                                        parentId,
////                                        obj.name,
////                                        obj.typeParametersOrEmpty().map { it.id },
////                                        obj.constructor?.id,
////                                        obj.parentTypesOrEmpty().map { it.id },
////                                        obj.typeParameterConstraintsOrEmpty().map { it.id },
////                                        obj.membersOrEmpty().map { it.id }
////                                )
////                            }
////                        }
//                    }
//                    is SmlEnum -> {
//                        obj.body?.instances?.forEach { handleDeclaration(it, obj.id) }
//                        +EnumT(obj.id, parentId, obj.name, obj.body?.instances?.map { it.id })
//                    }
//                    is SmlTypeParameter -> {
//                        +TypeParameterT(obj.id, parentId, obj.name, obj.variance)
//                    }
//                    is SmlLambdaYield -> {
//                        handleLambdaYield(obj, parentId)
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
//                handleType(obj.type, obj.id)
//                +ResultT(obj.id, parentId, obj.name, obj.type?.id)
//            }
//            is SmlFunction -> {
//                obj.parametersOrEmpty().forEach { handleParameter(it, obj.id) }
//                obj.resultsOrEmpty().forEach { handleDeclaration(it, obj.id) }
//                obj.typeParameterConstraintsOrEmpty().forEach { handleTypeParameterConstraint(it, obj.id) }
//                obj.typeParametersOrEmpty().forEach { handleDeclaration(it, obj.id) }
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
//                obj.statementsOrEmpty().forEach { handleStatement(it, obj.id) }
//                +WorkflowT(obj.id, parentId, obj.name, obj.body.statements.map { it.id })
//            }
//        }

//    private fun PlFactbase.handleConstructor(obj: SmlConstructor, parentId: Id) {
//        if (idManager.knowsObject(obj)) return
//
//        obj.parameterList.parameters.forEach { handleParameter(it, obj.id) }
////        +ConstructorT(obj.id, parentId, obj.parameterList.parameters.map { it.id })
//    }
//        // TODO create an unresolved reference fact and use this in place of the annotation if it cannot be resolved
////        +AnnotationUseT(obj.id, parentId, obj.annotation?.id, obj.argumentList?.arguments?.map { it.id })
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleParameter(obj: SmlParameter, parentId: Id) {
//        handleDeclaration(obj, parentId)
//    }
//
//    private fun PlFactbase.handleStatement(obj: SmlStatement, parentId: Id) {
//        when (obj) {
//            is SmlAssignment -> {
//                obj.assigneesOrEmpty().forEach { handleDoStatementAssignee(it, parentId) }
//                handleExpression(obj.expression, obj.id, obj.id)
//                +AssignmentT(obj.id, parentId, obj.assigneeList.assignees.map { it.id }, obj.expression.id)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handlePlaceholder(obj: SmlPlaceholder, parentId: Id) {
//        obj.annotations.forEach { handleAnnotationUse(it, obj.id) }
//
//        +PlaceholderT(obj.id, parentId, obj.name)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleExpression(obj: SmlExpression, parentId: Id, enclosingId: Id) {
//        if (idManager.knowsObject(obj)) return
//
//        when (obj) {
//            is SmlInfixOperation -> {
//                handleExpression(obj.leftOperand, obj.id, enclosingId)
//                handleExpression(obj.rightOperand, obj.id, enclosingId)
//                +InfixOperationT(obj.id, parentId, enclosingId, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
//            }
//            is SmlPrefixOperation -> {
//                handleExpression(obj.operand, obj.id, enclosingId)
//                +PrefixOperationT(obj.id, parentId, enclosingId, obj.operator, obj.operand.id)
//            }
//            is SmlLambda -> {
//                obj.body.statements.forEach { handleStatement(it, obj.id) }
//                obj.parametersOrEmpty().forEach { handleParameter(it, parentId) }
//                +LambdaT(obj.id, parentId, enclosingId, obj.parameterList?.parameters?.map { it.id }, obj.body.statements.map { it.id })
//            }
//            is SmlNull -> {
//                +NullT(obj.id, parentId, enclosingId)
//            }
//            is SmlReference -> {
//                if (obj.declaration !is SmlAnnotationImpl) {
//                    handleDeclaration(obj.declaration, obj.id)
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
//                handleExpression(obj.receiver, parentId, enclosingId)
//
//                when (obj) {
//                    is SmlMemberAccess -> {
//                        handleExpression(obj.receiver, obj.id, enclosingId)
//                        handleExpression(obj.member, obj.id, enclosingId)
//                        +MemberAccessT(obj.id, parentId, enclosingId, obj.receiver.id, obj.isNullable, obj.member.id)
//                    }
//                    is SmlCall -> {
//                        obj.argumentList.arguments.forEach { handleArgument(it, obj.id, enclosingId) }
//                        obj.typeArgumentsOrEmpty().forEach { handleTypeArgument(it, obj.id, enclosingId) }
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
//    private fun PlFactbase.handleType(obj: SmlType?, parentId: Id) {
//        if (obj == null) return
//        when (obj) {
//            is SmlCallableType -> {
//                obj.parameterList.parameters.forEach { handleParameter(it, obj.id) }
//                obj.resultList.results.forEach { handleDeclaration(it, obj.id) }
//
//                +CallableTypeT(obj.id, parentId, obj.parameterList.parameters.map { it.id }, obj.resultList.results.map { it.id })
//            }
//            is SmlMemberType -> {
//                handleType(obj.member, obj.id)
//                handleType(obj.receiver, obj.id)
//
//                +MemberTypeT(obj.id, parentId, obj.receiver.id, obj.member.id)
//            }
//            is SmlNamedType -> {
//                handleDeclaration(obj.declaration, obj.id)
//                obj.typeArgumentsOrEmpty().forEach { handleTypeArgument(it, obj.id, obj.id) }
//
//                +NamedTypeT(obj.id, parentId, obj.declaration.id, obj.typeArgumentsOrEmpty().map { it.id }, obj.isNullable)
//            }
//            is SmlUnionType -> {
//                obj.typeArgumentList.typeArguments.forEach { handleTypeArgument(it, obj.id, obj.id) }
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
//    private fun PlFactbase.handleTypeArgument(obj: SmlTypeArgument, parentId: Id, enclosingId: Id) {
//        obj.typeParameter?.let { handleDeclaration(obj.typeParameter, obj.id) }
//        handleTypeArgumentValue(obj.value, obj.id, enclosingId)
//        +TypeArgumentT(obj.id, parentId, obj.typeParameter?.id, obj.value.id)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleTypeArgumentValue(obj: SmlTypeArgumentValue, parentId: Id, enclosingId: Id) {
//        when (obj) {
//            is SmlStarProjection -> {
//                +StarProjectionT(obj.id, parentId)
//            }
//            is SmlTypeProjection -> {
//                handleType(obj.type, obj.id)
//                +TypeProjectionT(obj.id, parentId, obj.variance, obj.type.id)
//            }
//        }
//
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleLambdaYield(obj: SmlLambdaYield, parentId: Id) {
//        obj.annotations.forEach { handleAnnotationUse(it, obj.id) }
//
//        +LambdaYieldT(obj.id, parentId, obj.name)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleDoStatementAssignee(obj: SmlAssignee, parentId: Id) {
//        when (obj) {
//            is SmlWildcard -> {
//                +WildcardT(obj.id, parentId)
//            }
//            is SmlPlaceholder -> {
//                handlePlaceholder(obj, parentId)
//            }
//            is SmlYield -> {
//                handleDeclaration(obj.result, obj.referenceId!!.id)
//                +YieldT(obj.id, parentId, obj.result.id)
//            }
//            is SmlLambdaYield -> {
//                handleLambdaYield(obj, parentId)
//            }
//        }
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleTypeParameterConstraint(obj: SmlTypeParameterConstraint, parentId: Id) {
//        handleDeclaration(obj.leftOperand, obj.id)
//        handleType(obj.rightOperand, obj.id)
//
//        +TypeParameterConstraintT(obj.id, obj.eResource().id, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
//        +SourceLocationS(obj)
//    }
//
//    private fun PlFactbase.handleArgument(obj: SmlArgument, parentId: Id, enclosingId: Id) {
//        if (obj.parameter != null) handleParameter(obj.parameter, parentId)
//        handleExpression(obj.value, obj.id, enclosingId)
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
