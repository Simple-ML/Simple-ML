package de.unibonn.simpleml.validation

import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.utils.duplicatesBy
import org.eclipse.xtext.validation.EValidatorRegistrar

const val REDECLARATION = "REDECLARATION"

abstract class AbstractSimpleMLChecker : AbstractSimpleMLValidator() {
    override fun register(registrar: EValidatorRegistrar) {
        // This is overridden to prevent duplicate validation errors.
    }

    protected fun List<SmlAbstractDeclaration>.reportDuplicateNames(message: (SmlAbstractDeclaration) -> String) {
        this.duplicatesBy { it.name }
            .forEach { error(message(it), it, SimpleMLPackage.Literals.SML_ABSTRACT_DECLARATION__NAME, REDECLARATION) }
    }
}
