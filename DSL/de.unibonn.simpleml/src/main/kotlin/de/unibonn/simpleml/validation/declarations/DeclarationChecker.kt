package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.constants.Modifiers
import de.unibonn.simpleml.emf.annotationUsesOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedName
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.stdlib.StdlibAnnotations
import de.unibonn.simpleml.stdlib.isMultiUse
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.isClassMember
import de.unibonn.simpleml.utils.isCompilationUnitMember
import de.unibonn.simpleml.utils.isRequired
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

class DeclarationChecker : AbstractSimpleMLChecker() {

    @Check
    fun annotationCardinality(smlDeclaration: SmlAbstractDeclaration) {
        smlDeclaration.annotationUsesOrEmpty()
            .filter { it.annotation != null && !it.annotation.eIsProxy() && !it.annotation.isMultiUse() }
            .duplicatesBy { it.annotation.fullyQualifiedName() }
            .forEach {
                error(
                    "This annotation can only be used once.",
                    it,
                    null,
                    ErrorCode.ANNOTATION_IS_SINGLE_USE
                )
            }
    }

    @Check
    fun annotationModifiers(smlAnnotation: SmlAnnotation) {
        smlAnnotation.reportInvalidModifiers("An annotation must have no modifiers.") { true }
    }

    @Check
    fun attributeModifiers(smlAttribute: SmlAttribute) {
        if (smlAttribute.isClassMember()) {
            smlAttribute.reportInvalidModifiers("An attribute must not have this modifier.") {
                it !in setOf(Modifiers.STATIC)
            }
        }
    }

    @Check
    fun classModifiers(smlClass: SmlClass) {
        if (smlClass.isClassMember()) {
            smlClass.reportInvalidModifiers("A nested class must not have this modifier.") {
                it !in setOf(Modifiers.OPEN, Modifiers.STATIC)
            }

            smlClass.reportUnnecessaryModifiers("A nested class is always static.") {
                it == Modifiers.STATIC
            }
        } else if (smlClass.isCompilationUnitMember()) {
            smlClass.reportInvalidModifiers("A top-level class must not have this modifier.") {
                it !in setOf(Modifiers.OPEN)
            }
        }
    }

    @Check
    fun declarationModifiers(smlDeclaration: SmlAbstractDeclaration) {
        if (smlDeclaration.shouldCheckDeclarationModifiers()) {
            val duplicateModifiers = smlDeclaration.modifiers.duplicatesBy { it }
            smlDeclaration.modifiers
                .forEachIndexed { index, modifier ->
                    if (modifier in duplicateModifiers) {
                        error(
                            "Modifiers must be unique.",
                            Literals.SML_ABSTRACT_DECLARATION__MODIFIERS,
                            index,
                            ErrorCode.DUPLICATE_MODIFIER,
                            modifier
                        )
                    }
                }
        }
    }

    @Check
    fun enumModifiers(smlEnum: SmlEnum) {
        if (smlEnum.isClassMember()) {
            smlEnum.reportInvalidModifiers("A nested enum must not have this modifier.") {
                it !in setOf(Modifiers.STATIC)
            }

            smlEnum.reportUnnecessaryModifiers("A nested enum is always static.") {
                it == Modifiers.STATIC
            }
        } else if (smlEnum.isCompilationUnitMember()) {
            smlEnum.reportInvalidModifiers("A top-level enum must have no modifiers.") { true }
        }
    }

    @Check
    fun enumVariantModifiers(smlEnumVariant: SmlEnumVariant) {
        smlEnumVariant.reportInvalidModifiers("An enum variant must have no modifiers.") { true }
    }

    @Check
    fun functionModifiers(smlFunction: SmlFunction) {
        if (smlFunction.isCompilationUnitMember()) {
            smlFunction.reportInvalidModifiers("A top-level function must have no modifiers.") { true }
        }
    }

    @Check
    fun parameterModifiers(smlParameter: SmlParameter) {
        smlParameter.reportInvalidModifiers("A parameter must have no modifiers.") { true }
    }

    @Check
    fun mustNotDeprecateRequiredParameter(smlParameter: SmlParameter) {
        if (smlParameter.isRequired()) {
            val deprecatedAnnotationOrNull = smlParameter.annotationUsesOrEmpty().firstOrNull {
                it.annotation.fullyQualifiedName() == StdlibAnnotations.Deprecated
            }

            if (deprecatedAnnotationOrNull != null) {
                error(
                    "A required parameter cannot be deprecated.",
                    deprecatedAnnotationOrNull,
                    null,
                    ErrorCode.DEPRECATED_REQUIRED_PARAMETER
                )
            }
        }
    }

    @Check
    fun resultModifiers(smlResult: SmlResult) {
        smlResult.reportInvalidModifiers("A result must have no modifiers.") { true }
    }

    @Check
    fun typeParameterModifiers(smlTypeParameter: SmlTypeParameter) {
        smlTypeParameter.reportInvalidModifiers("A type parameter must have no modifiers.") { true }
    }

    @Check
    fun workflowModifiers(smlWorkflow: SmlWorkflow) {
        smlWorkflow.reportInvalidModifiers("A workflow must have no modifiers.") { true }
    }

    @Check
    fun workflowStepModifiers(smlWorkflowStep: SmlWorkflowStep) {
        smlWorkflowStep.reportInvalidModifiers("A workflow step must have no modifiers.") { true }
    }

    private fun SmlAbstractDeclaration.shouldCheckDeclarationModifiers(): Boolean {
        return this !is SmlParameter &&
            this !is SmlResult &&
            this !is SmlTypeParameter &&
            this !is SmlWorkflow &&
            this !is SmlWorkflowStep
    }

    private fun SmlAbstractDeclaration.reportInvalidModifiers(message: String, isInvalid: (modifier: String) -> Boolean) {
        this.modifiers.forEachIndexed { index, modifier ->
            if (isInvalid(modifier)) {
                error(
                    message,
                    Literals.SML_ABSTRACT_DECLARATION__MODIFIERS,
                    index,
                    ErrorCode.INVALID_MODIFIER,
                    modifier
                )
            }
        }
    }

    private fun SmlAbstractDeclaration.reportUnnecessaryModifiers(
        message: String,
        isUnnecessary: (modifier: String) -> Boolean
    ) {
        this.modifiers.forEachIndexed { index, modifier ->
            if (isUnnecessary(modifier)) {
                info(
                    message,
                    Literals.SML_ABSTRACT_DECLARATION__MODIFIERS,
                    index,
                    InfoCode.UnnecessaryModifier,
                    modifier
                )
            }
        }
    }
}
