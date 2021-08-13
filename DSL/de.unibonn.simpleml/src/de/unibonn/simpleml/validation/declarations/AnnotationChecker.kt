package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

class AnnotationChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlAnnotation: SmlAnnotation) {
        smlAnnotation.parametersOrEmpty().reportDuplicateNames {
            "A parameter with name '${it.name}' exists already in this annotation."
        }
    }
}