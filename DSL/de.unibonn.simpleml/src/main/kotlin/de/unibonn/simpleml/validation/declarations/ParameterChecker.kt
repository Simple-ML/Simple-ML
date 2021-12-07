package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val PARAMETER_MUST_HAVE_TYPE = "PARAMETER_MUST_HAVE_TYPE"

class ParameterChecker : AbstractSimpleMLChecker() {

    @Check
    fun type(smlParameter: SmlParameter) {
        if (smlParameter.type == null) {
            error(
                "A parameter must have a type.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                PARAMETER_MUST_HAVE_TYPE
            )
        }
    }
}
