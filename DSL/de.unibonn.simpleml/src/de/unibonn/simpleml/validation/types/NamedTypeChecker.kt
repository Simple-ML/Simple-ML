package de.unibonn.simpleml.validation.types

import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.utils.typeParametersOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val NAMED_TYPE_MISSING_TYPE_ARGUMENT_LIST = "NAMED_TYPE_MISSING_TYPE_ARGUMENT_LIST"

class NamedTypeChecker : AbstractSimpleMLChecker() {

    @Check
    fun missingTypeArgumentList(smlNamedType: SmlNamedType) {
        if (smlNamedType.typeArgumentList != null) {
            return
        }

        val typeParameters = when (val declaration = smlNamedType.declaration) {
            is SmlClass -> declaration.typeParametersOrEmpty()
            else -> return
        }

        if (typeParameters.isNotEmpty()) {
            error(
                    "This type is generic and, therefore, requires a list of type arguments.",
                    SimpleMLPackage.Literals.SML_NAMED_TYPE__DECLARATION,
                    NAMED_TYPE_MISSING_TYPE_ARGUMENT_LIST
            )
        }
    }
}
