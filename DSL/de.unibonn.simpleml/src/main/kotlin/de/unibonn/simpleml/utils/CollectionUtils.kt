package de.unibonn.simpleml.utils

import kotlin.math.max

/**
 * Creates a list of all elements in the iterable that the labeler maps to the same label as at least one other element.
 */
fun <I, K> Iterable<I>.duplicatesBy(labeler: (I) -> K): List<I> {
    return this
            .groupBy(labeler)
            .values
            .filter { it.size > 1 }
            .flatten()
}

/**
 * Maps corresponding elements of the left and right list using the given zipper. The shorter list is padded with nulls
 * at the end, so the resulting list has the same length as the longer list.
 */
fun <L, R, O> outerZipBy(left: List<L>, right: List<R>, zipper: (L?, R?) -> O): List<O> {
    val maxSize = max(left.size, right.size)
    return (0 until maxSize).map { i ->
        zipper(left.getOrNull(i), right.getOrNull(i))
    }
}
