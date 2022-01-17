package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.isRequired
import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.targetOrNull
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlBlockLambdaResult
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.staticAnalysis.linking.parametersOrNull
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.stdlibAccess.StdlibAnnotations
import de.unibonn.simpleml.stdlibAccess.StdlibEnums.AnnotationTarget
import de.unibonn.simpleml.stdlibAccess.validTargets
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import de.unibonn.simpleml.validation.codes.InfoCode
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class AnnotationCallChecker : AbstractSimpleMLChecker() {

    @Check
    fun duplicateTargetInTargetAnnotation(smlAnnotationCall: SmlAnnotationCall) {
        val annotation = smlAnnotationCall.annotation
        if (!annotation.isResolved() || annotation.qualifiedNameOrNull() != StdlibAnnotations.Target) {
            return
        }

        smlAnnotationCall
            .argumentsOrEmpty()
            .map { it.value }
            .filterIsInstance<SmlMemberAccess>()
            .duplicatesBy { it.member.declaration.qualifiedNameOrNull() }
            .forEach {
                warning(
                    "This annotation target is used multiple times.",
                    it,
                    null,
                    WarningCode.DuplicateTarget
                )
            }
    }

    @Check
    fun missingArgumentList(smlAnnotationCall: SmlAnnotationCall) {
        if (smlAnnotationCall.argumentList != null) {
            return
        }

        val annotation = smlAnnotationCall.annotation
        if (!annotation.isResolved()) {
            return
        }

        val parameters = smlAnnotationCall.annotation.parametersOrEmpty()
        if (parameters.any { it.isRequired() }) {
            error(
                "Missing argument list.",
                Literals.SML_ANNOTATION_CALL__ANNOTATION,
                ErrorCode.MISSING_ARGUMENT_LIST
            )
        }
    }

    @Check
    fun target(smlAnnotationCall: SmlAnnotationCall) {

        // Get target of annotation use
        val actualTarget = smlAnnotationCall.targetOrNull() ?: return

        // Get legal targets of used annotation
        val annotation = smlAnnotationCall.annotation
        if (!annotation.isResolved()) {
            return
        }

        val legalTargets = annotation.validTargets()

        // Compare actual and legal targets
        val wrongTarget: String? = when {
            actualTarget is SmlAnnotation && AnnotationTarget.Annotation !in legalTargets -> {
                "an annotation"
            }
            actualTarget is SmlAttribute && AnnotationTarget.Attribute !in legalTargets -> {
                "an attribute"
            }
            actualTarget is SmlClass && AnnotationTarget.Class !in legalTargets -> {
                "a class"
            }
            actualTarget is SmlPackage && AnnotationTarget.CompilationUnit !in legalTargets -> {
                "a compilation unit"
            }
            actualTarget is SmlEnum && AnnotationTarget.Enum !in legalTargets -> {
                "an enum"
            }
            actualTarget is SmlEnumVariant && AnnotationTarget.EnumVariant !in legalTargets -> {
                "an enum variant"
            }
            actualTarget is SmlFunction && AnnotationTarget.Function !in legalTargets -> {
                "a function"
            }
            actualTarget is SmlBlockLambdaResult && AnnotationTarget.LambdaResult !in legalTargets -> {
                "a lambda result"
            }
            actualTarget is SmlParameter && AnnotationTarget.Parameter !in legalTargets -> {
                "a parameter"
            }
            actualTarget is SmlPlaceholder && AnnotationTarget.Placeholder !in legalTargets -> {
                "a placeholder"
            }
            actualTarget is SmlResult && AnnotationTarget.Result !in legalTargets -> {
                "a result"
            }
            actualTarget is SmlTypeParameter && AnnotationTarget.TypeParameter !in legalTargets -> {
                "a type parameter"
            }
            actualTarget is SmlWorkflow && AnnotationTarget.Workflow !in legalTargets -> {
                "a workflow"
            }
            actualTarget is SmlStep && AnnotationTarget.Step !in legalTargets -> {
                "a step"
            }
            else -> null
        }

        // Show error
        if (wrongTarget != null) {
            error(
                "This annotation cannot be applied to $wrongTarget.",
                null,
                ErrorCode.WRONG_TARGET
            )
        }
    }

    @Check
    fun unnecessaryArgumentList(smlAnnotationCall: SmlAnnotationCall) {
        if (smlAnnotationCall.argumentList == null || smlAnnotationCall.argumentsOrEmpty().isNotEmpty()) {
            return
        }

        val parametersOrNull = smlAnnotationCall.argumentList.parametersOrNull()
        if (parametersOrNull != null && parametersOrNull.none { it.isRequired() }) {
            info(
                "Unnecessary argument list.",
                Literals.SML_ABSTRACT_CALL__ARGUMENT_LIST,
                InfoCode.UnnecessaryArgumentList
            )
        }
    }

    @Check
    fun argumentsMustBeConstant(smlAnnotationCall: SmlAnnotationCall) {
        smlAnnotationCall.argumentsOrEmpty().forEach {
            if (it.value?.toConstantExpressionOrNull() == null) {
                error(
                    "Arguments in annotation call must be constant.",
                    it,
                    Literals.SML_ARGUMENT__VALUE,
                    ErrorCode.MustBeConstant
                )
            }
        }
    }
}
