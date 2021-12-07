package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

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
            info(
                "Unnecessary parameter list.",
                Literals.SML_ANNOTATION__PARAMETER_LIST,
                InfoCode.UnnecessaryParameterList
            )
        }
    }
}
