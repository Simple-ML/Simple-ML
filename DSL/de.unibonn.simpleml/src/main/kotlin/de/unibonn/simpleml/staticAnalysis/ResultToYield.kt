package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.yieldsOrEmpty
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlResultList
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.uniqueOrNull

fun SmlResult.uniqueYieldOrNull(): SmlYield? {
    return yieldsOrEmpty().uniqueOrNull()
}

fun SmlResult.yieldsOrEmpty(): List<SmlYield> {
    val resultList = closestAncestorOrNull<SmlResultList>() ?: return emptyList()
    val step = resultList.eContainer() as? SmlStep ?: return emptyList()

    return step.yieldsOrEmpty().filter { it.result == this }
}
