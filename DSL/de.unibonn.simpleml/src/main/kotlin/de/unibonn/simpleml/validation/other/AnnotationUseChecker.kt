package de.unibonn.simpleml.validation.other

import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.utils.parametersOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val UNNECESSARY_ARGUMENT_LIST = "UNNECESSARY_ARGUMENT_LIST"

class AnnotationUseChecker : AbstractSimpleMLChecker() {

    @Check
    fun unnecessaryArgumentList(smlAnnotationUse: SmlAnnotationUse) {
        if (smlAnnotationUse.argumentList == null) {
            return
        }

        val parametersOrNull = smlAnnotationUse.argumentList.parametersOrNull()
        if (parametersOrNull != null && parametersOrNull.isEmpty()) {
            warning(
                "Unnecessary argument list.",
                SimpleMLPackage.Literals.SML_ANNOTATION_USE__ARGUMENT_LIST,
                UNNECESSARY_ARGUMENT_LIST
            )
        }
    }
}
