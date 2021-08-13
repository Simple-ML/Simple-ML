package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.utils.isStatic
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val INSTANCE_METHOD_MUST_BE_CALLED = "INSTANCE_METHOD_MUST_BE_CALLED"

class MemberAccessChecker : AbstractSimpleMLChecker() {

    @Check
    fun instanceMethodMustBeCalled(smlMemberAccess: SmlMemberAccess) {
        val member = smlMemberAccess.member.declaration
        if (member is SmlFunction && !member.isStatic() && smlMemberAccess.eContainer() !is SmlCall) {
            error(
                    "An instance method must be called.",
                    Literals.SML_MEMBER_ACCESS__MEMBER,
                    INSTANCE_METHOD_MUST_BE_CALLED
            )
        }
    }
}