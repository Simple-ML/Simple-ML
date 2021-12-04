package de.unibonn.simpleml.utils

/**
 * Different file extensions associated with Simple-ML programs.
 */
object FileExtensions {

    /**
     * Marks the file as a stub file, which describes an external API.
     *
     * @see isStubFile
     */
    const val STUB = ".stub.simpleml"

    /**
     * Marks the file as a test file, which disables some checks to simplify its use as input of test cases.
     *
     * @see isTestFile
     */
    const val TEST = ".test.simpleml"

    /**
     * Marks the file as a workflow file, which can be executed by our runtime component. Note: The extension is a
     * substring of the extensions for stubs and tests.
     *
     * @see isWorkflowFile
     */
    const val WORKFLOW = ".simpleml"
}

/**
 * Possible modifiers of declarations.
 */
object Modifiers {

    /**
     * Allows classes to be subclassed and methods to be overridden.
     *
     * @see isOpen
     */
    const val OPEN = "open"

    /**
     * The method overrides a superclass method.
     *
     * @see isOverride
     */
    const val OVERRIDE = "override"

    /**
     * Allows using API elements in a static context.
     *
     * @see isStatic
     */
    const val STATIC = "static"
}
