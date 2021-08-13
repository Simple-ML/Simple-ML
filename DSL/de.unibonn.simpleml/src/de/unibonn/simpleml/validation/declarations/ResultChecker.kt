package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val RESULT_MUST_HAVE_TYPE = "RESULT_MUST_HAVE_TYPE"

class ResultChecker : AbstractSimpleMLChecker() {

    @Check
    fun type(smlResult: SmlResult) {
        if (smlResult.type == null) {
            error(
                    "A result must have a type.",
                    Literals.SML_DECLARATION__NAME,
                    RESULT_MUST_HAVE_TYPE
            )
        }
    }
}