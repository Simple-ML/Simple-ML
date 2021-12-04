package de.unibonn.simpleml.constants

import de.unibonn.simpleml.simpleML.SmlDeclaration

/**
 * Possible modifiers of declarations.
 */
object Modifiers {

    /**
     * Allows classes to be subclassed and methods to be overridden.
     *
     * @see hasOpenModifier
     */
    const val OPEN = "open"

    /**
     * The method overrides a superclass method.
     *
     * @see hasOverrideModifier
     */
    const val OVERRIDE = "override"

    /**
     * Allows using API elements in a static context.
     *
     * @see hasStaticModifier
     */
    const val STATIC = "static"
}

/**
 * The declaration is explicitly marked with the `open` modifier.
 */
fun SmlDeclaration.hasOpenModifier() = Modifiers.OPEN in this.modifiers

/**
 * The declaration is explicitly marked with the `override` modifier.
 */
fun SmlDeclaration.hasOverrideModifier() = Modifiers.OVERRIDE in this.modifiers

/**
 * The declaration is explicitly marked with the `static` modifier.
 */
fun SmlDeclaration.hasStaticModifier() = Modifiers.STATIC in this.modifiers
