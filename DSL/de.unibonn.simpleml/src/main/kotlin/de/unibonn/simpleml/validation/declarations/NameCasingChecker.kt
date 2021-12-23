package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class NameCasingChecker : AbstractSimpleMLChecker() {

    @Check
    fun segmentsShouldBeLowercase(smlPackage: SmlPackage) {
        val hasInvalidSegments = smlPackage.name
            .split('.')
            .filter { it.isNotEmpty() }
            .any { !it.first().isLowerCase() }

        if (hasInvalidSegments) {
            warning(
                "All segments of the qualified name of a package should start with a lowercase letter.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                WarningCode.SegmentsShouldBeLowercase
            )
        }
    }

    @Check
    fun annotationNamesShouldStartWithUppercaseLetter(smlAnnotation: SmlAnnotation) {
        smlAnnotation.nameShouldStartWithUppercaseLetter("annotations")
    }

    @Check
    fun attributeNamesShouldStartWithLowercaseLetter(smlAttribute: SmlAttribute) {
        smlAttribute.nameShouldStartWithLowercaseLetter("attributes")
    }

    @Check
    fun classNamesShouldStartWithUppercaseLetter(smlClass: SmlClass) {
        smlClass.nameShouldStartWithUppercaseLetter("classes")
    }

    @Check
    fun enumNamesShouldStartWithUppercaseLetter(smlEnum: SmlEnum) {
        smlEnum.nameShouldStartWithUppercaseLetter("enums")
    }

    @Check
    fun enumVariantNamesShouldStartWithUppercaseLetter(smlEnumVariant: SmlEnumVariant) {
        smlEnumVariant.nameShouldStartWithUppercaseLetter("enum variants")
    }

    @Check
    fun functionNamesShouldStartWithLowercaseLetter(smlFunction: SmlFunction) {
        smlFunction.nameShouldStartWithLowercaseLetter("functions")
    }

    @Check
    fun parameterNamesShouldStartWithLowercaseLetter(smlParameter: SmlParameter) {
        smlParameter.nameShouldStartWithLowercaseLetter("parameters")
    }

    @Check
    fun placeholderNamesShouldStartWithLowercaseLetter(smlPlaceholder: SmlPlaceholder) {
        smlPlaceholder.nameShouldStartWithLowercaseLetter("placeholders")
    }

    @Check
    fun resultNamesShouldStartWithLowercaseLetter(smlResult: SmlResult) {
        smlResult.nameShouldStartWithLowercaseLetter("results")
    }

    @Check
    fun stepNamesShouldStartWithLowercaseLetter(smlStep: SmlStep) {
        smlStep.nameShouldStartWithLowercaseLetter("steps")
    }

    @Check
    fun workflowNamesShouldStartWithLowercaseLetter(smlWorkflow: SmlWorkflow) {
        smlWorkflow.nameShouldStartWithLowercaseLetter("workflows")
    }

    private fun SmlAbstractDeclaration.nameShouldStartWithUppercaseLetter(declarationType: String) {
        if (!this.name.isNullOrEmpty() && !this.name.first().isUpperCase()) {
            warning(
                "Names of $declarationType should start with an uppercase letter.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                WarningCode.NameShouldBeUpperCase
            )
        }
    }

    private fun SmlAbstractDeclaration.nameShouldStartWithLowercaseLetter(declarationType: String) {
        if (!this.name.isNullOrEmpty() && !this.name.first().isLowerCase()) {
            warning(
                "Names of $declarationType should start with a lowercase letter.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                WarningCode.NameShouldBeUpperCase
            )
        }
    }
}
