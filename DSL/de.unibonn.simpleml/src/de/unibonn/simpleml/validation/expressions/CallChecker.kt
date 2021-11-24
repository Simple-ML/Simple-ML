package de.unibonn.simpleml.validation.expressions

import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val CONTEXT_OF_CALL_WITHOUT_RESULTS = "CONTEXT_OF_CALL_WITHOUT_RESULTS"
const val CONTEXT_OF_CALL_WITH_MANY_RESULTS = "CONTEXT_OF_CALL_WITH_MANY_RESULTS"
const val NO_RECURSION = "NO_RECURSION"
const val RECEIVER_MUST_BE_CALLABLE = "RECEIVER_MUST_BE_CALLABLE"
const val CALLED_CLASS_MUST_HAVE_CONSTRUCTOR = "CALLED_CLASS_MUST_HAVE_CONSTRUCTOR"

class CallChecker : AbstractSimpleMLChecker() {

    @Check
    fun context(smlCall: SmlCall) {
        val results = smlCall.resultsOrNull() ?: return
        val source = when (smlCall.receiver) {
            is SmlMemberAccess -> smlCall.receiver
            else -> smlCall
        }
        val feature = when (smlCall.receiver) {
            is SmlMemberAccess -> Literals.SML_MEMBER_ACCESS__MEMBER
            else -> Literals.SML_CHAINED_EXPRESSION__RECEIVER
        }

        if (results.isEmpty() && !smlCall.hasValidContextForCallWithoutResults()) {
            error(
                    "A call that produces no results is not allowed in this context.",
                    source,
                    feature,
                    CONTEXT_OF_CALL_WITHOUT_RESULTS
            )
        } else if (results.size > 1 && !smlCall.hasValidContextForCallWithMultipleResults()) {
            error(
                    "A call that produces multiple results is not allowed in this context.",
                    source,
                    feature,
                    CONTEXT_OF_CALL_WITH_MANY_RESULTS
            )
        }
    }

    private fun SmlCall.hasValidContextForCallWithoutResults(): Boolean {
        val context = this.eContainer()
        return context is SmlExpressionStatement
    }

    private fun SmlCall.hasValidContextForCallWithMultipleResults(): Boolean {
        val context = this.eContainer()
        return context is SmlAssignment || context is SmlExpressionStatement || context is SmlMemberAccess
    }

    @Check
    fun recursion(smlCall: SmlCall) {
        if (smlCall.isRecursive()) {
            error(
                "Recursive calls are not allowed.",
                Literals.SML_CHAINED_EXPRESSION__RECEIVER,
                NO_RECURSION
            )
        }
    }

    @Check
    fun receiver(smlCall: SmlCall) {
        when (val maybeCallable = smlCall.maybeCallable()) {
            CallableResult.NotCallable -> {
                error(
                        "This expression must not be called.",
                        Literals.SML_CHAINED_EXPRESSION__RECEIVER,
                        RECEIVER_MUST_BE_CALLABLE
                )
            }
            is CallableResult.Callable -> {
                val callable = maybeCallable.callable
                if (callable is SmlClass && callable.constructor == null) {
                    error(
                            "Cannot create an instance of a class that has no constructor.",
                            Literals.SML_CHAINED_EXPRESSION__RECEIVER,
                            CALLED_CLASS_MUST_HAVE_CONSTRUCTOR
                    )
                }
            }
        }
    }
}
