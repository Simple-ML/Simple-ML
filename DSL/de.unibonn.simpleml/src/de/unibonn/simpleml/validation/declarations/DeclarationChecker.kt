package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val DUPLICATE_MODIFIER = "DUPLICATE_MODIFIER"
const val INVALID_MODIFIER = "INVALID_MODIFIER"
const val UNNECESSARY_MODIFIER = "UNNECESSARY_MODIFIER"

class DeclarationChecker : AbstractSimpleMLChecker() {

    @Check
    fun annotationModifiers(smlAnnotation: SmlAnnotation) {
        smlAnnotation.reportInvalidModifiers("An annotation must not have this modifier.") {
            it !in setOf(SML_DEPRECATED)
        }
    }

    @Check
    fun attributeModifiers(smlAttribute: SmlAttribute) {
        if (smlAttribute.isClassMember()) {
            smlAttribute.reportInvalidModifiers("An attribute must not have this modifier.") {
                it !in setOf(SML_DEPRECATED, SML_STATIC)
            }
        }
    }

    @Check
    fun classModifiers(smlClass: SmlClass) {
        if (smlClass.isClassMember()) {
            smlClass.reportInvalidModifiers("A nested class must not have this modifier.") {
                it !in setOf(SML_DEPRECATED, SML_OPEN, SML_STATIC)
            }

            smlClass.reportUnnecessaryModifiers("A nested class is always static.") {
                it == SML_STATIC
            }
        } else if (smlClass.isCompilationUnitMember()) {
            smlClass.reportInvalidModifiers("A top-level class must not have this modifier.") {
                it !in setOf(SML_DEPRECATED, SML_OPEN)
            }
        }
    }

    @Check
    fun declarationModifiers(smlDeclaration: SmlDeclaration) {
        if (smlDeclaration.shouldCheckDeclarationModifiers()) {
            val duplicateModifiers = smlDeclaration.modifiers.duplicatesBy { it }
            smlDeclaration.modifiers
                .forEachIndexed { index, modifier ->
                    if (modifier in duplicateModifiers) {
                        error(
                            "Modifiers must be unique.",
                            Literals.SML_DECLARATION__MODIFIERS,
                            index,
                            DUPLICATE_MODIFIER,
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
                it !in setOf(SML_DEPRECATED, SML_STATIC)
            }

            smlEnum.reportUnnecessaryModifiers("A nested enum is always static.") {
                it == SML_STATIC
            }
        } else if (smlEnum.isCompilationUnitMember()) {
            smlEnum.reportInvalidModifiers("A top-level enum must not have this modifier.") {
                it !in setOf(SML_DEPRECATED)
            }
        }
    }

    @Check
    fun functionModifiers(smlFunction: SmlFunction) {
        if (smlFunction.isCompilationUnitMember()) {
            smlFunction.reportInvalidModifiers("A top-level function must not have this modifier.") {
                it !in setOf(SML_DEPRECATED, SML_PURE)
            }
        }
    }

    @Check
    fun parameterModifiers(smlParameter: SmlParameter) {
        if (true) {
            smlParameter.reportInvalidModifiers("A parameter must not have this modifier.") {
                it !in setOf(SML_DEPRECATED)
            }
        }
    }

    @Check
    fun mustNotDeprecateRequiredParameter(smlParameter: SmlParameter) {
        if (smlParameter.isRequired() && smlParameter.isDeprecated() && true) {
            val index = smlParameter.modifiers.indexOf(SML_DEPRECATED)
            error(
                "A required parameter cannot be deprecated.",
                Literals.SML_DECLARATION__MODIFIERS,
                index,
                INVALID_MODIFIER,
                SML_DEPRECATED
            )
        }
    }

    @Check
    fun resultModifiers(smlResult: SmlResult) {
        smlResult.reportInvalidModifiers("A result must not have this modifier.") {
            it !in setOf(SML_DEPRECATED)
        }
    }

    @Check
    fun typeParameterModifiers(smlTypeParameter: SmlTypeParameter) {
        if (true) {
            smlTypeParameter.reportInvalidModifiers("A type parameter must have no modifiers.") { true }
        }
    }

    @Check
    fun workflowModifiers(smlWorkflow: SmlWorkflow) {
        smlWorkflow.reportInvalidModifiers("A workflow must have no modifiers.") { true }
    }

    @Check
    fun workflowStepModifiers(smlWorkflowStep: SmlWorkflowStep) {
        smlWorkflowStep.reportInvalidModifiers("A workflow step must have no modifiers.") { true }
    }

    private fun SmlDeclaration.shouldCheckDeclarationModifiers(): Boolean {
        return this !is SmlParameter &&
                this !is SmlResult &&
                this !is SmlTypeParameter &&
                this !is SmlWorkflow &&
                this !is SmlWorkflowStep
    }

    private fun SmlDeclaration.reportInvalidModifiers(message: String, isInvalid: (modifier: String) -> Boolean) {
        this.modifiers.forEachIndexed { index, modifier ->
            if (isInvalid(modifier)) {
                error(
                    message,
                    Literals.SML_DECLARATION__MODIFIERS,
                    index,
                    INVALID_MODIFIER,
                    modifier
                )
            }
        }
    }

    private fun SmlDeclaration.reportUnnecessaryModifiers(
        message: String,
        isUnnecessary: (modifier: String) -> Boolean
    ) {
        this.modifiers.forEachIndexed { index, modifier ->
            if (isUnnecessary(modifier)) {
                info(
                    message,
                    Literals.SML_DECLARATION__MODIFIERS,
                    index,
                    UNNECESSARY_MODIFIER,
                    modifier
                )
            }
        }
    }
}
