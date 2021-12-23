package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class MemberAccessChecker : AbstractSimpleMLChecker() {

    @Check
    fun mustBeCalled(smlMemberAccess: SmlMemberAccess) {
        when (val member = smlMemberAccess.member.declaration) {
            is SmlFunction -> {
                if (!member.isStatic && smlMemberAccess.eContainer() !is SmlCall) {
                    error(
                        "An instance method must be called.",
                        Literals.SML_MEMBER_ACCESS__MEMBER,
                        ErrorCode.INSTANCE_METHOD_MUST_BE_CALLED
                    )
                }
            }
            is SmlEnumVariant -> {
                val mustBeInstantiated =
                    member.typeParametersOrEmpty().isNotEmpty() || member.parametersOrEmpty().isNotEmpty()
                if (mustBeInstantiated && smlMemberAccess.eContainer() !is SmlCall) {
                    error(
                        "An enum variant with parameters or type parameters must be instantiated.",
                        Literals.SML_MEMBER_ACCESS__MEMBER,
                        ErrorCode.ENUM_VARIANT_MUST_BE_INSTANTIATED
                    )
                }
            }
        }
    }
}
