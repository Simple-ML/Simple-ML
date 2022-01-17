package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.containingBlockLambdaOrNull
import de.unibonn.simpleml.emf.containingStepOrNull
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.simpleML.SmlAbstractCallable
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlStep

/**
 * Returns whether this call might lead to recursion.
 */
fun SmlCall.isRecursive(): Boolean {
    val containingWorkflowStep = this.containingStepOrNull() ?: return false
    val containingLambda = this.containingBlockLambdaOrNull()

    val origin = mutableSetOf<SmlAbstractCallable>(containingWorkflowStep)
    if (containingLambda != null) {
        origin += containingLambda
    }

    return isRecursive(origin, emptySet())
}

/**
 * Returns whether this call might lead to recursion.
 */
private fun SmlCall.isRecursive(origin: Set<SmlAbstractCallable>, visited: Set<SmlAbstractObject>): Boolean {
    return when (val callable = this.callableOrNull()) {
        // TODO: calls must be in body, not in nested lambda
        // TODO: handle expression lambda
        is SmlStep -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        is SmlBlockLambda -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        else -> false
    }
}
