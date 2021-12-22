package de.unibonn.simpleml.prolog_bridge.converters

import de.unibonn.simpleml.emf.annotationUsesOrEmpty
import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.assigneesOrEmpty
import de.unibonn.simpleml.emf.constraintsOrEmpty
import de.unibonn.simpleml.emf.memberDeclarationsOrEmpty
import de.unibonn.simpleml.emf.membersOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.emf.referencesOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.emf.statementsOrEmpty
import de.unibonn.simpleml.emf.subtermsOrEmpty
import de.unibonn.simpleml.emf.termOrNull
import de.unibonn.simpleml.emf.typeArgumentsOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.emf.variantsOrEmpty
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationT
import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationUseT
import de.unibonn.simpleml.prolog_bridge.model.facts.ArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AssignmentT
import de.unibonn.simpleml.prolog_bridge.model.facts.AttributeT
import de.unibonn.simpleml.prolog_bridge.model.facts.BooleanT
import de.unibonn.simpleml.prolog_bridge.model.facts.CallT
import de.unibonn.simpleml.prolog_bridge.model.facts.CallableTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.ClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.CompilationUnitT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumT
import de.unibonn.simpleml.prolog_bridge.model.facts.EnumVariantT
import de.unibonn.simpleml.prolog_bridge.model.facts.ExpressionStatementT
import de.unibonn.simpleml.prolog_bridge.model.facts.FloatT
import de.unibonn.simpleml.prolog_bridge.model.facts.FunctionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ImportT
import de.unibonn.simpleml.prolog_bridge.model.facts.InfixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.IntT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaResultT
import de.unibonn.simpleml.prolog_bridge.model.facts.LambdaT
import de.unibonn.simpleml.prolog_bridge.model.facts.MemberAccessT
import de.unibonn.simpleml.prolog_bridge.model.facts.MemberTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.NamedTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.NullT
import de.unibonn.simpleml.prolog_bridge.model.facts.PackageT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParenthesizedExpressionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ParenthesizedTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.model.facts.PlaceholderT
import de.unibonn.simpleml.prolog_bridge.model.facts.PrefixOperationT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolAlternativeT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolComplementT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolParenthesizedTermT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolQuantifiedTermT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolReferenceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolSequenceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolSubtermT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolT
import de.unibonn.simpleml.prolog_bridge.model.facts.ProtocolTokenClassT
import de.unibonn.simpleml.prolog_bridge.model.facts.ReferenceT
import de.unibonn.simpleml.prolog_bridge.model.facts.ResourceS
import de.unibonn.simpleml.prolog_bridge.model.facts.ResultT
import de.unibonn.simpleml.prolog_bridge.model.facts.SourceLocationS
import de.unibonn.simpleml.prolog_bridge.model.facts.StarProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.StepT
import de.unibonn.simpleml.prolog_bridge.model.facts.StringT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringEndT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringInnerT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringStartT
import de.unibonn.simpleml.prolog_bridge.model.facts.TemplateStringT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeArgumentT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeParameterConstraintT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeParameterT
import de.unibonn.simpleml.prolog_bridge.model.facts.TypeProjectionT
import de.unibonn.simpleml.prolog_bridge.model.facts.UnionTypeT
import de.unibonn.simpleml.prolog_bridge.model.facts.UnresolvedT
import de.unibonn.simpleml.prolog_bridge.model.facts.WildcardT
import de.unibonn.simpleml.prolog_bridge.model.facts.WorkflowT
import de.unibonn.simpleml.prolog_bridge.model.facts.YieldT
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.prolog_bridge.utils.IdManager
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractConstraint
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlAbstractProtocolTerm
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlAbstractTypeArgumentValue
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlImport
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
import de.unibonn.simpleml.simpleML.SmlProtocolSequence
import de.unibonn.simpleml.simpleML.SmlProtocolSubterm
import de.unibonn.simpleml.simpleML.SmlProtocolTokenClass
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStarProjection
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeProjection
import de.unibonn.simpleml.simpleML.SmlUnionType
import de.unibonn.simpleml.simpleML.SmlWildcard
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.aliasName
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class AstToPrologFactbase {
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

    // *****************************************************************************************************************
    // Declarations
    // ****************************************************************************************************************/

    private fun PlFactbase.visitCompilationUnit(obj: SmlCompilationUnit) {
        obj.memberDeclarationsOrEmpty().forEach { this.visitDeclaration(it, obj.id) }

        +CompilationUnitT(
            obj.id,
            obj.memberDeclarationsOrEmpty().map { it.id }
        )
        +ResourceS(obj.id, obj.eResource().uri.toString())
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitDeclaration(obj: SmlAbstractDeclaration, parentId: Id<SmlAbstractObject>) {
        obj.annotationUsesOrEmpty().forEach { visitAnnotationUse(it, obj.id) }

        when (obj) {
            is SmlAnnotation -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.constraintsOrEmpty().forEach { visitConstraint(it, obj.id) }

                +AnnotationT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.parameterList?.parameters?.map { it.id },
                    obj.constraintList?.constraints?.map { it.id }
                )
            }
            is SmlAttribute -> {
                obj.type?.let { visitType(it, obj.id) }

                +AttributeT(obj.id, parentId, obj.name, obj.isStatic, obj.type?.id)
            }
            is SmlClass -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parentTypesOrEmpty().forEach { visitType(it, obj.id) }
                obj.constraintsOrEmpty().forEach { visitConstraint(it, obj.id) }
                obj.membersOrEmpty().forEach { visitClassMember(it, obj.id) }

                +ClassT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.parameterList?.parameters?.map { it.id },
                    obj.parentTypeList?.parentTypes?.map { it.id },
                    obj.constraintList?.constraints?.map { it.id },
                    obj.body?.members?.map { it.id }
                )
            }
            is SmlEnum -> {
                obj.variantsOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +EnumT(obj.id, parentId, obj.name, obj.body?.variants?.map { it.id })
            }
            is SmlEnumVariant -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.constraintsOrEmpty().forEach { visitConstraint(it, obj.id) }

                +EnumVariantT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.parameterList?.parameters?.map { it.id },
                    obj.constraintList?.constraints?.map { it.id },
                )
            }
            is SmlFunction -> {
                obj.typeParametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.constraintsOrEmpty().forEach { visitConstraint(it, obj.id) }

                +FunctionT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.isStatic,
                    obj.typeParameterList?.typeParameters?.map { it.id },
                    obj.parametersOrEmpty().map { it.id },
                    obj.resultList?.results?.map { it.id },
                    obj.constraintList?.constraints?.map { it.id },
                )
            }
            is SmlPackage -> {
                obj.imports.forEach { this.visitImport(it, obj.id) }
                obj.memberDeclarationsOrEmpty().forEach { this.visitDeclaration(it, obj.id) }

                +PackageT(
                    obj.id,
                    parentId,
                    obj.name,
                    obj.imports.map { it.id },
                    obj.memberDeclarationsOrEmpty().map { it.id }
                )
            }
            is SmlParameter -> {
                obj.type?.let { visitType(it, obj.id) }
                obj.defaultValue?.let { visitExpression(it, obj.id, obj.id) }

                +ParameterT(obj.id, parentId, obj.name, obj.isVariadic, obj.type?.id, obj.defaultValue?.id)
            }
            is SmlProtocolSubterm -> {
                visitProtocolTerm(obj.term, obj.id, obj.id)

                +ProtocolSubtermT(obj.id, parentId, obj.name, obj.term.id)
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
            is SmlStep -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.statementsOrEmpty().forEach { visitStatement(it, obj.id) }

                +StepT(
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

    private fun PlFactbase.visitAnnotationUse(obj: SmlAnnotationUse, parentId: Id<SmlAbstractDeclaration>) {
        visitCrossReference(obj, SimpleMLPackage.Literals.SML_ANNOTATION_USE__ANNOTATION, obj.annotation)
        obj.argumentsOrEmpty().forEach { visitExpression(it, obj.id, obj.id) }

        +AnnotationUseT(obj.id, parentId, obj.annotation.id, obj.argumentList?.arguments?.map { it.id })
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitImport(obj: SmlImport, parentId: Id<SmlPackage>) {
        +ImportT(obj.id, parentId, obj.importedNamespace, obj.aliasName())
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitClassMember(obj: SmlAbstractObject, parentId: Id<SmlClass>) {
        when (obj) {
            is SmlAbstractDeclaration -> visitDeclaration(obj, parentId)
            is SmlProtocol -> visitProtocol(obj, parentId)
        }
    }

    private fun PlFactbase.visitProtocol(obj: SmlProtocol, parentId: Id<SmlClass>) {
        obj.subtermsOrEmpty().forEach { visitDeclaration(it, obj.id) }
        obj.termOrNull()?.let { visitProtocolTerm(it, obj.id, obj.id) }

        +ProtocolT(
            obj.id,
            parentId,
            obj.body?.subtermList?.subterms?.map { it.id },
            obj.body?.term?.id
        )
        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitProtocolTerm(
        obj: SmlAbstractProtocolTerm,
        parentId: Id<SmlAbstractObject>,
        enclosingId: Id<SmlAbstractObject>
    ) {
        when (obj) {
            is SmlProtocolAlternative -> {
                obj.terms.forEach { visitProtocolTerm(it, obj.id, enclosingId) }

                +ProtocolAlternativeT(obj.id, parentId, enclosingId, obj.terms.map { it.id })
            }
            is SmlProtocolComplement -> {
                obj.universe?.let { visitProtocolTerm(obj.universe, obj.id, enclosingId) }
                obj.referencesOrEmpty().forEach { visitProtocolTerm(it, obj.id, enclosingId) }

                +ProtocolComplementT(
                    obj.id,
                    parentId,
                    enclosingId,
                    obj.universe?.id,
                    obj.referenceList?.references?.map { it.id }
                )
            }
            is SmlProtocolParenthesizedTerm -> {
                visitProtocolTerm(obj.term, obj.id, enclosingId)

                +ProtocolParenthesizedTermT(obj.id, parentId, enclosingId, obj.term.id)
            }
            is SmlProtocolQuantifiedTerm -> {
                visitProtocolTerm(obj.term, obj.id, enclosingId)

                +ProtocolQuantifiedTermT(obj.id, parentId, enclosingId, obj.term.id, obj.quantifier)
            }
            is SmlProtocolReference -> {
                visitCrossReference(obj, SimpleMLPackage.Literals.SML_PROTOCOL_REFERENCE__TOKEN, obj.token)

                +ProtocolReferenceT(obj.id, parentId, enclosingId, obj.token.id)
            }
            is SmlProtocolSequence -> {
                obj.terms.forEach { visitProtocolTerm(it, obj.id, enclosingId) }

                +ProtocolSequenceT(obj.id, parentId, enclosingId, obj.terms.map { it.id })
            }
            is SmlProtocolTokenClass -> {
                +ProtocolTokenClassT(obj.id, parentId, enclosingId, obj.value)
            }
        }

        visitSourceLocation(obj)
    }

    // *****************************************************************************************************************
    // Statements
    // ****************************************************************************************************************/

    private fun PlFactbase.visitStatement(obj: SmlAbstractStatement, parentId: Id<SmlAbstractObject>) {
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

    private fun PlFactbase.visitAssignee(obj: SmlAbstractAssignee, parentId: Id<SmlAssignment>) {
        when (obj) {
            is SmlLambdaResult -> {
                obj.annotationUsesOrEmpty().forEach { visitAnnotationUse(it, obj.id) }

                +LambdaResultT(obj.id, parentId, obj.name)
            }
            is SmlPlaceholder -> {
                obj.annotationUsesOrEmpty().forEach { visitAnnotationUse(it, obj.id) }

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

    private fun PlFactbase.visitExpression(
        obj: SmlAbstractExpression,
        parentId: Id<SmlAbstractObject>,
        enclosingId: Id<SmlAbstractObject>
    ) {
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

                +MemberAccessT(obj.id, parentId, enclosingId, obj.receiver.id, obj.isNullSafe, obj.member.id)
            }
            is SmlNull -> {
                +NullT(obj.id, parentId, enclosingId)
            }
            is SmlParenthesizedExpression -> {
                visitExpression(obj.expression, obj.id, enclosingId)

                +ParenthesizedExpressionT(obj.id, parentId, enclosingId, obj.expression.id)
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
            is SmlTemplateString -> {
                obj.expressions.forEach { visitExpression(it, obj.id, enclosingId) }

                +TemplateStringT(obj.id, parentId, enclosingId, obj.expressions.map { it.id })
            }
            is SmlTemplateStringStart -> {
                +TemplateStringStartT(obj.id, parentId, enclosingId, obj.value)
            }
            is SmlTemplateStringInner -> {
                +TemplateStringInnerT(obj.id, parentId, enclosingId, obj.value)
            }
            is SmlTemplateStringEnd -> {
                +TemplateStringEndT(obj.id, parentId, enclosingId, obj.value)
            }
        }

        visitSourceLocation(obj)
    }

    // *****************************************************************************************************************
    // Types
    // ****************************************************************************************************************/

    private fun PlFactbase.visitType(obj: SmlAbstractType, parentId: Id<SmlAbstractObject>) {
        when (obj) {
            is SmlCallableType -> {
                obj.parametersOrEmpty().forEach { visitDeclaration(it, obj.id) }
                obj.resultsOrEmpty().forEach { visitDeclaration(it, obj.id) }

                +CallableTypeT(
                    obj.id,
                    parentId,
                    obj.parametersOrEmpty().map { it.id },
                    obj.resultsOrEmpty().map { it.id }
                )
            }
            is SmlMemberType -> {
                visitType(obj.receiver, obj.id)
                visitType(obj.member, obj.id)

                +MemberTypeT(obj.id, parentId, obj.receiver.id, obj.member.id)
            }
            is SmlNamedType -> {
                visitCrossReference(obj, SimpleMLPackage.Literals.SML_NAMED_TYPE__DECLARATION, obj.declaration)
                obj.typeArgumentsOrEmpty().forEach { visitTypeArgument(it, obj.id) }

                +NamedTypeT(
                    obj.id,
                    parentId,
                    obj.declaration.id,
                    obj.typeArgumentList?.typeArguments?.map { it.id },
                    obj.isNullable
                )
            }
            is SmlParenthesizedType -> {
                visitType(obj.type, obj.id)

                +ParenthesizedTypeT(obj.id, parentId, obj.type.id)
            }
            is SmlUnionType -> {
                obj.typeArgumentsOrEmpty().forEach { visitTypeArgument(it, obj.id) }

                +UnionTypeT(obj.id, parentId, obj.typeArgumentsOrEmpty().map { it.id })
            }
        }

        visitSourceLocation(obj)
    }

    private fun PlFactbase.visitTypeArgument(obj: SmlTypeArgument, parentId: Id<SmlAbstractObject>) {
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

    private fun PlFactbase.visitTypeArgumentValue(obj: SmlAbstractTypeArgumentValue, parentId: Id<SmlTypeArgument>) {
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

    private fun PlFactbase.visitConstraint(
        obj: SmlAbstractConstraint,
        parentId: Id<SmlAbstractObject>
    ) {
        when (obj) {
            is SmlTypeParameterConstraint -> {
                visitCrossReference(
                    obj,
                    SimpleMLPackage.Literals.SML_TYPE_PARAMETER_CONSTRAINT__LEFT_OPERAND,
                    obj.leftOperand
                )
                visitType(obj.rightOperand, obj.id)

                +TypeParameterConstraintT(obj.id, parentId, obj.leftOperand.id, obj.operator, obj.rightOperand.id)
            }
        }

        visitSourceLocation(obj)
    }

    // *****************************************************************************************************************
    // Other
    // ****************************************************************************************************************/

    private fun PlFactbase.visitSourceLocation(obj: SmlAbstractObject) {
        val uriHash = EcoreUtil2.getURI(obj).toString().split("#").last()
        val node = NodeModelUtils.getNode(obj)
        val location = NodeModelUtils.getLineAndColumn(node, node.offset)

        +SourceLocationS(
            obj.id,
            uriHash,
            node.offset,
            location.line,
            location.column,
            node.length
        )
    }

    private fun PlFactbase.visitCrossReference(source: SmlAbstractObject, edge: EReference, target: SmlAbstractObject) {
        if (!idManager.knowsObject(target)) {
            val name = getReferencedName(source, edge)
            +UnresolvedT(target.id, name)
        }
    }

    private fun getReferencedName(obj: SmlAbstractObject, eReference: EReference): String {
        return NodeModelUtils
            .findNodesForFeature(obj, eReference)
            .joinToString("") { it.text }
    }

    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

    private val <T : EObject> T.id: Id<T>
        get() = idManager.assignIdIfAbsent(this)

    private fun reset() {
        idManager = IdManager()
    }
}
