package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.stdlibAccess.isPure

fun SmlAbstractExpression.hasSideEffects(): Boolean {
    if (this is SmlCall) {
        if (this.isRecursive()) {
            return true
        }

        val callable = this.callableOrNull()
        return callable is SmlFunction && !callable.isPure() ||
            callable is SmlStep && !callable.isInferredPure() ||
            callable is SmlBlockLambda && !callable.isInferredPure()
    }

    return false
}

fun SmlAbstractCallable.isPure(): Boolean {
    // TODO
    return false
}

fun SmlStep.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }

fun SmlBlockLambda.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }

fun SmlExpressionLambda.isInferredPure() = this.descendants<SmlCall>().none { it.hasSideEffects() }
