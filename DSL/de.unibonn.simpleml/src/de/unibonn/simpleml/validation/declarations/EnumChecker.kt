package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.instancesOrEmpty
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val UNNECESSARY_ENUM_BODY = "UNNECESSARY_ENUM_BODY"

class EnumChecker : AbstractSimpleMLChecker() {

    @Check
    fun body(smlEnum: SmlEnum) {
        if (smlEnum.body != null && smlEnum.instancesOrEmpty().isEmpty()) {
            warning(
                    "Unnecessary enum body.",
                    Literals.SML_ENUM__BODY,
                    UNNECESSARY_ENUM_BODY
            )
        }
    }

    @Check
    fun uniqueNames(smlEnum: SmlEnum) {
        smlEnum.instancesOrEmpty()
                .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this enum." }
    }
}