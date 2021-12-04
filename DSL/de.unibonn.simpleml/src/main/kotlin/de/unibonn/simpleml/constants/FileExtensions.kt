package de.unibonn.simpleml.constants

import de.unibonn.simpleml.utils.OriginalFilePath
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource

/**
 * Different file extensions associated with Simple-ML programs.
 */
object FileExtensions {

    /**
     * Marks the file as a stub file, which describes an external API.
     *
     * @see isInStubFile
     * @see isStubFile
     */
    const val STUB = ".stub.simpleml"

    /**
     * Marks the file as a test file, which disables some checks to simplify its use as input of test cases.
     *
     * @see isInTestFile
     * @see isTestFile
     */
    const val TEST = ".test.simpleml"

    /**
     * Marks the file as a workflow file, which can be executed by our runtime component. Note: The extension is a
     * substring of the extensions for stubs and tests.
     *
     * @see isInWorkflowFile
     * @see isWorkflowFile
     */
    const val WORKFLOW = ".simpleml"
}

/**
 * Returns whether the object is contained in stub file.
 */
fun EObject.isInStubFile() = this.eResource().isStubFile()

/**
 * Returns whether the object is contained in test file.
 */
fun EObject.isInTestFile() = this.eResource().isTestFile()

/**
 * Returns whether the object is contained in workflow file.
 */
fun EObject.isInWorkflowFile() = this.eResource().isWorkflowFile()

/**
 * Returns whether the resource represents a stub file.
 */
fun Resource.isStubFile() = this.hasExtension(FileExtensions.STUB)

/**
 * Returns whether the resource represents a test file.
 */
fun Resource.isTestFile() = this.hasExtension(FileExtensions.TEST)

/**
 * Returns whether the resource represents a workflow file.
 */
fun Resource.isWorkflowFile() = this.hasExtension(FileExtensions.WORKFLOW) && !this.isStubFile() && !this.isTestFile()

/**
 * Returns whether the resource represents a file with the given extension.
 */
private fun Resource.hasExtension(extension: String): Boolean {

    // The original file path is normally lost for dynamic tests, so they attach it as an EMF adapter
    this.eAdapters().filterIsInstance<OriginalFilePath>().firstOrNull()?.let {
        return it.path.endsWith(extension)
    }

    return this.uri.toString().endsWith(extension)
}
