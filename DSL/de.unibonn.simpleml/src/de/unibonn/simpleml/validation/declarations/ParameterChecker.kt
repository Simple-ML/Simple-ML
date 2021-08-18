package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.utils.SML_DEPRECATED
import de.unibonn.simpleml.utils.isDeprecated
import de.unibonn.simpleml.utils.isRequired
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val PARAMETER_MUST_HAVE_TYPE = "PARAMETER_MUST_HAVE_TYPE"

class ParameterChecker : AbstractSimpleMLChecker() {

    @Check
    fun type(smlParameter: SmlParameter) {
        if (smlParameter.type == null) {
            error(
                    "A parameter must have a type.",
                    Literals.SML_DECLARATION__NAME,
                    PARAMETER_MUST_HAVE_TYPE
            )
        }
    }
}