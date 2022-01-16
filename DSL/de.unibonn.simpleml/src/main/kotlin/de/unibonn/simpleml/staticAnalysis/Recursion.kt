package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.containingBlockLambdaOrNull
import de.unibonn.simpleml.emf.containingStepOrNull
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlStep
import org.eclipse.emf.ecore.EObject

fun SmlCall.isRecursive(): Boolean {
    val containingWorkflowStep = this.containingStepOrNull() ?: return false
    val containingLambda = this.containingBlockLambdaOrNull()

    val origin = mutableSetOf<SmlAbstractObject>(containingWorkflowStep)
    if (containingLambda != null) {
        origin += containingLambda
    }

    return isRecursive(origin, emptySet())
}

private fun SmlCall.isRecursive(origin: Set<SmlAbstractObject>, visited: Set<SmlAbstractObject>): Boolean {
    return when (val callable = this.callableOrNull()) {
        is SmlStep -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        is SmlBlockLambda -> callable in origin || callable !in visited && callable.descendants<SmlCall>()
            .any { it.isRecursive(origin, visited + callable) }
        else -> false
    }
}
