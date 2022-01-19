package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.immediateCalls
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.stdlibAccess.isPure

/**
 * Whether this [SmlAbstractExpression] is pure and, thus, can be removed.
 *
 * @param resultIfUnknown What to return if neither purity nor impurity can be proven. Note that external functions are
 * still always assumed to be impure unless they are marked with `@Pure`.
 */
fun SmlAbstractExpression.isPureExpression(resultIfUnknown: Boolean = false): Boolean {
    return when (this) {
        is SmlCall -> !isRecursive() && callableOrNull().isPureCallable(resultIfUnknown)
        else -> true
    }
}

/**
 * Whether this [SmlAbstractCallable] is pure, so calls to this can be removed.
 *
 * @param resultIfUnknown What to return if neither purity nor impurity can be proven. Note that external functions are
 * still always assumed to be impure unless they are marked with `@Pure`.
 */
fun SmlAbstractCallable?.isPureCallable(resultIfUnknown: Boolean = false): Boolean {
    // TODO build a proper call graph

    return when (this) {
        null -> resultIfUnknown

        is SmlAbstractLambda -> immediateCalls().all { it.isPureExpression(resultIfUnknown) }
        is SmlStep -> immediateCalls().all { it.isPureExpression(resultIfUnknown) }

        is SmlCallableType -> resultIfUnknown
        is SmlClass -> true
        is SmlEnumVariant -> true
        is SmlFunction -> isPure()

        else -> throw IllegalArgumentException("Cannot handle callable of type '${this::class.simpleName}'.")
    }
}
