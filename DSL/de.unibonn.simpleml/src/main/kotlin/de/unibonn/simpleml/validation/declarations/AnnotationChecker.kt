package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.utils.parametersOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.other.UNNECESSARY_ARGUMENT_LIST
import org.eclipse.xtext.validation.Check

const val UNNECESSARY_PARAMETER_LIST = "UNNECESSARY_PARAMETER_LIST"

class AnnotationChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlAnnotation: SmlAnnotation) {
        smlAnnotation.parametersOrEmpty().reportDuplicateNames {
            "A parameter with name '${it.name}' exists already in this annotation."
        }
    }

    @Check
    fun unnecessaryParameterList(smlAnnotation: SmlAnnotation) {
        if (smlAnnotation.parameterList != null && smlAnnotation.parametersOrEmpty().isEmpty()) {
            warning(
                "Unnecessary parameter list.",
                SimpleMLPackage.Literals.SML_ANNOTATION__PARAMETER_LIST,
                UNNECESSARY_PARAMETER_LIST
            )
        }
    }
}
