package de.unibonn.simpleml.serializer

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.serializer.impl.Serializer

internal object SerializerExtensionsInjectionTarget {

    @Inject
    lateinit var serializer: Serializer
}

/**
 * Serializes a subtree of the EMF model. Formatting only works if a compilation unit is passed as the receiver.
 *
 * @receiver The root of the subtree.
 * @return A result object indicating success (formatted/unformatted) or failure.
 */
fun EObject.serializeToString(): SerializeToStringResult {
    if (this is SmlCompilationUnit && this.eResource() == null) {
        XtextResource().contents += this
    }

    return try {
        val string = SerializerExtensionsInjectionTarget.serializer.serialize(this)
        when (this) {
            is SmlCompilationUnit -> SerializeToStringResult.Formatted(string)
            else -> SerializeToStringResult.Unformatted(string)
        }
    } catch (e: RuntimeException) {
        SerializeToStringResult.WrongEmfModelFailure(e.message ?: "")
    }
}

/**
 * Result of calling [serializeToString].
 */
sealed interface SerializeToStringResult {

    /**
     * Serialization was successful.
     */
    sealed interface Success : SerializeToStringResult {

        /**
         * The DSL program code.
         */
        val code: String
    }

    /**
     * Serialization was successful and the result could be formatted.
     */
    class Formatted(override val code: String) : Success

    /**
     * Serialization was successful but the result could not be formatted.
     */
    class Unformatted(override val code: String) : Success

    /**
     * Something went wrong while serializing the object.
     */
    sealed interface Failure : SerializeToStringResult {

        /**
         * A message that describes the failure.
         */
        val message: String
    }

    /**
     * The EMF model is not configured correctly.
     */
    class WrongEmfModelFailure(override val message: String) : Failure
}
