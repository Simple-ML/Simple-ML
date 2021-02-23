package de.unibonn.simpleml.utils

import org.eclipse.emf.ecore.EObject

inline fun <reified T : EObject> EObject.closestAncestorOrNull(): T? {
    var current: EObject? = this.eContainer()
    while (current != null && current !is T) {
        current = current.eContainer()
    }
    return current as T?
}

inline fun <reified T : EObject> EObject.descendants(): Sequence<T> {
    return this.eAllContents().asSequence().filterIsInstance<T>()
}