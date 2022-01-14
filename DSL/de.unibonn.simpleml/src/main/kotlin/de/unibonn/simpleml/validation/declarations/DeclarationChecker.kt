package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.annotationCallsOrEmpty
import de.unibonn.simpleml.naming.fullyQualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.stdlibAccess.StdlibAnnotations
import de.unibonn.simpleml.stdlibAccess.isRepeatable
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.isRequired
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class DeclarationChecker : AbstractSimpleMLChecker() {

    @Check
    fun annotationCardinality(smlDeclaration: SmlAbstractDeclaration) {
        smlDeclaration.annotationCallsOrEmpty()
            .filter { it.annotation != null && !it.annotation.eIsProxy() && !it.annotation.isRepeatable() }
            .duplicatesBy { it.annotation.fullyQualifiedNameOrNull() }
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
    fun mustNotDeprecateRequiredParameter(smlParameter: SmlParameter) {
        if (smlParameter.isRequired()) {
            val deprecatedAnnotationOrNull = smlParameter.annotationCallsOrEmpty().firstOrNull {
                it.annotation.fullyQualifiedNameOrNull() == StdlibAnnotations.Deprecated
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
}
