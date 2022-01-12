package de.unibonn.simpleml.emf

import org.eclipse.emf.ecore.EObject

/**
 * Returns all descendants of this [EObject] with the given type.
 */
inline fun <reified T : EObject> EObject.descendants(): Sequence<T> {
    return this.eAllContents().asSequence().filterIsInstance<T>()
}

/**
 * Returns the closest ancestor of this [EObject] with the given type or `null` if none exists. This can the this
 * [EObject] itself.
 */
inline fun <reified T : EObject> EObject.closestAncestorOrNull(): T? {
    var current: EObject? = this.eContainer()
    while (current != null && current !is T) {
        current = current.eContainer()
    }
    return current as T?
}
