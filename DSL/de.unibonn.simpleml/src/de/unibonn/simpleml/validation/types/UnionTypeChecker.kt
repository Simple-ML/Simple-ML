package de.unibonn.simpleml.validation.types

import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val UNION_TYPE_WITHOUT_TYPE_ARGUMENTS = "UNION_TYPE_WITHOUT_TYPE_ARGUMENTS"
const val UNION_TYPE_WITH_ONE_TYPE_ARGUMENT = "UNION_TYPE_WITH_ONE_TYPE_ARGUMENT"

class UnionTypeChecker : AbstractSimpleMLChecker() {

    @Check
    fun numberOfTypeArguments(smlUnionType: SmlUnionType) {
        when (smlUnionType.typeArgumentsOrEmpty().size) {
            0 -> {
                error(
                        "A union type must have least one type argument.",
                        null,
                        UNION_TYPE_WITHOUT_TYPE_ARGUMENTS
                )
            }
            1 -> {
                warning(
                        "A union type with one type argument is equivalent to the the type argument itself.",
                        null,
                        UNION_TYPE_WITH_ONE_TYPE_ARGUMENT
                )
            }
        }
    }
}