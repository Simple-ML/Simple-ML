package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlStep

fun SmlCall.parametersOrNull(): List<SmlParameter>? {
    return callableOrNull()?.parametersOrEmpty()
}

fun SmlCall.resultsOrNull(): List<SmlAbstractObject>? {
    return when (val callable = this.callableOrNull()) {
        is SmlClass -> listOf(callable)
        is SmlEnumVariant -> listOf(callable)
        is SmlFunction -> callable.resultsOrEmpty()
        is SmlCallableType -> callable.resultsOrEmpty()
        is SmlBlockLambda -> callable.lambdaResultsOrEmpty()
        is SmlExpressionLambda -> listOf(callable.result)
        is SmlStep -> callable.resultsOrEmpty()
        else -> null
    }
}
