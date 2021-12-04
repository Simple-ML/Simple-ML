package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlBlock
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.utils.assignedOrNull
import de.unibonn.simpleml.utils.closestAncestorOrNull
import de.unibonn.simpleml.utils.usesIn
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val PLACEHOLDER_IS_RENAMING_OF_DECLARATION = "PLACEHOLDER_IS_RENAMING_OF_DECLARATION"
const val PLACEHOLDER_IS_UNUSED = "PLACEHOLDER_IS_UNUSED"

class PlaceholderChecker : AbstractSimpleMLChecker() {

    @Check
    fun renamingOfDeclaration(smlPlaceholder: SmlPlaceholder) {
        val assigned = smlPlaceholder.assignedOrNull()
        if (assigned is SmlReference) {
            val declaration = assigned.declaration
            if (declaration is SmlClass || declaration is SmlEnum || declaration is SmlFunction || declaration is SmlParameter || declaration is SmlPlaceholder)
                warning(
                    "This placeholder only provides another name for a declaration.",
                    Literals.SML_DECLARATION__NAME,
                    PLACEHOLDER_IS_RENAMING_OF_DECLARATION
                )
        }
    }

    @Check
    fun unused(smlPlaceholder: SmlPlaceholder) {
        val block = smlPlaceholder.closestAncestorOrNull<SmlBlock>() ?: return
        if (smlPlaceholder.usesIn(block).none()) {
            warning(
                "This placeholder is unused.",
                Literals.SML_DECLARATION__NAME,
                PLACEHOLDER_IS_UNUSED
            )
        }
    }
}
