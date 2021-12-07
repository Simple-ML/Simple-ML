package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.targetOrNull
import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.stdlib.StdlibAnnotations
import de.unibonn.simpleml.stdlib.StdlibEnums.AnnotationTargetVariants
import de.unibonn.simpleml.stdlib.uniqueAnnotationUseOrNull
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.isResolved
import de.unibonn.simpleml.utils.parametersOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val MISSING_ARGUMENT_LIST = "MISSING_ARGUMENT_LIST"
const val UNNECESSARY_ARGUMENT_LIST = "UNNECESSARY_ARGUMENT_LIST"
const val WRONG_TARGET = "WRONG_TARGET"
const val DUPLICATE_TARGET = "DUPLICATE_TARGET"

class AnnotationUseChecker : AbstractSimpleMLChecker() {

    @Check
    fun duplicateTargetInTargetAnnotation(smlAnnotationUse: SmlAnnotationUse) {
        val annotation = smlAnnotationUse.annotation
        if (!annotation.isResolved() || annotation.fullyQualifiedName() != StdlibAnnotations.Target) {
            return
        }

        smlAnnotationUse
            .argumentsOrEmpty()
            .map { it.value }
            .filterIsInstance<SmlMemberAccess>()
            .duplicatesBy { it.member.declaration.fullyQualifiedName() }
            .forEach {
                warning(
                    "This annotation target is used multiple times.",
                    it,
                    null,
                    DUPLICATE_TARGET
                )
            }
    }

    @Check
    fun missingArgumentList(smlAnnotationUse: SmlAnnotationUse) {
        if (smlAnnotationUse.argumentList != null) {
            return
        }

        val annotation = smlAnnotationUse.annotation
        if (!annotation.isResolved()) {
            return
        }

        val parameters = smlAnnotationUse.annotation.parametersOrEmpty()
        if (parameters.isNotEmpty()) {
            error(
                "Missing argument list.",
                Literals.SML_ANNOTATION_USE__ANNOTATION,
                MISSING_ARGUMENT_LIST
            )
        }
    }

    @Check
    fun target(smlAnnotationUse: SmlAnnotationUse) {

        // Get target of annotation use
        val actualTarget = smlAnnotationUse.targetOrNull() ?: return

        // Get legal targets of used annotation
        val annotation = smlAnnotationUse.annotation
        if (!annotation.isResolved()) {
            return
        }

        val targetAnnotationUse = annotation.uniqueAnnotationUseOrNull(StdlibAnnotations.Target) ?: return
        val legalTargets = targetAnnotationUse
            .argumentsOrEmpty()
            .asSequence()
            .map { it.value }
            .filterIsInstance<SmlMemberAccess>()
            .mapNotNull { it.member.declaration.fullyQualifiedName() }
            .toSet()

        // Compare actual and legal targets
        val wrongTarget: String? = when {
            actualTarget is SmlAnnotation && AnnotationTargetVariants.Annotation !in legalTargets -> {
                "an annotation"
            }
            actualTarget is SmlAttribute && AnnotationTargetVariants.Attribute !in legalTargets -> {
                "an attribute"
            }
            actualTarget is SmlClass && AnnotationTargetVariants.Class !in legalTargets -> {
                "a class"
            }
            actualTarget is SmlPackage && AnnotationTargetVariants.CompilationUnit !in legalTargets -> {
                "a compilation unit"
            }
            actualTarget is SmlEnum && AnnotationTargetVariants.Enum !in legalTargets -> {
                "an enum"
            }
            actualTarget is SmlEnumVariant && AnnotationTargetVariants.EnumVariant !in legalTargets -> {
                "an enum variant"
            }
            actualTarget is SmlFunction && AnnotationTargetVariants.Function !in legalTargets -> {
                "a function"
            }
            actualTarget is SmlParameter && AnnotationTargetVariants.Parameter !in legalTargets -> {
                "a parameter"
            }
            actualTarget is SmlResult && AnnotationTargetVariants.Result !in legalTargets -> {
                "a result"
            }
            actualTarget is SmlTypeParameter && AnnotationTargetVariants.TypeParameter !in legalTargets -> {
                "a type parameter"
            }
            actualTarget is SmlWorkflow && AnnotationTargetVariants.Workflow !in legalTargets -> {
                "a workflow"
            }
            actualTarget is SmlWorkflowStep && AnnotationTargetVariants.WorkflowStep !in legalTargets -> {
                "a workflow step"
            }
            else -> null
        }

        // Show error
        if (wrongTarget != null) {
            error(
                "This annotation cannot be applied to $wrongTarget.",
                null,
                WRONG_TARGET
            )
        }
    }

    @Check
    fun unnecessaryArgumentList(smlAnnotationUse: SmlAnnotationUse) {
        if (smlAnnotationUse.argumentList == null) {
            return
        }

        val parametersOrNull = smlAnnotationUse.argumentList.parametersOrNull()
        if (parametersOrNull != null && parametersOrNull.isEmpty()) {
            warning(
                "Unnecessary argument list.",
                Literals.SML_ANNOTATION_USE__ARGUMENT_LIST,
                UNNECESSARY_ARGUMENT_LIST
            )
        }
    }
}
