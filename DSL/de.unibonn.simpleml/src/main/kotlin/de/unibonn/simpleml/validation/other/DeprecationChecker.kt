package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.stdlib.isDeprecated
import de.unibonn.simpleml.utils.parameterOrNull
import de.unibonn.simpleml.utils.typeParameterOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class DeprecationChecker : AbstractSimpleMLChecker() {

    @Check
    fun annotationUseReferenceDeprecatedAnnotation(smlAnnotationUse: SmlAnnotationUse) {
        val annotation = smlAnnotationUse.annotation ?: return
        if (annotation.isDeprecated()) {
            warning(
                "The used annotation is deprecated.",
                Literals.SML_ANNOTATION_USE__ANNOTATION,
                WarningCode.ReferencedDeclarationIsDeprecated
            )
        }
    }

    @Check
    fun argumentReferencesDeprecatedParameter(smlArgument: SmlArgument) {
        val parameter = smlArgument.parameterOrNull() ?: return
        if (parameter.isDeprecated()) {
            warning(
                "The corresponding parameter is deprecated.",
                null,
                WarningCode.CorrespondingParameterIsDeprecated
            )
        }
    }

    @Check
    fun namedTypeReferencesDeprecatedDeclaration(smlNamedType: SmlNamedType) {
        val declaration = smlNamedType.declaration ?: return
        if (declaration.isDeprecated()) {
            warning(
                "The referenced declaration is deprecated.",
                Literals.SML_NAMED_TYPE__DECLARATION,
                WarningCode.ReferencedDeclarationIsDeprecated
            )
        }
    }

    @Check
    fun referenceReferencesDeprecatedDeclaration(smlReference: SmlReference) {
        val declaration = smlReference.declaration ?: return
        if (declaration.isDeprecated()) {
            warning(
                "The referenced declaration is deprecated.",
                Literals.SML_REFERENCE__DECLARATION,
                WarningCode.ReferencedDeclarationIsDeprecated
            )
        }
    }

    @Check
    fun typeArgumentReferencesDeprecatedTypeParameter(smlTypeArgument: SmlTypeArgument) {
        val typeParameter = smlTypeArgument.typeParameterOrNull() ?: return
        if (typeParameter.isDeprecated()) {
            warning(
                "The corresponding type parameter is deprecated.",
                null,
                WarningCode.CorrespondingTypeParameterIsDeprecated
            )
        }
    }
}
