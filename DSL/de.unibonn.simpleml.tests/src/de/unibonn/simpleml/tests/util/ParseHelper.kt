package de.unibonn.simpleml.tests.util

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.testing.util.ParseHelper
import java.nio.file.Files

typealias ResourceName = String

class ParseHelper @Inject constructor(
    private val parseHelper: ParseHelper<SmlCompilationUnit>,
    private val resourceSetProvider: Provider<ResourceSet>,
    private val stdlib: SimpleMLStdlib
) {

    fun parseProgramText(programText: String): SmlCompilationUnit? {
        return parseHelper.parse(programText)
    }

    fun parseResource(resourceName: ResourceName): SmlCompilationUnit? {
        return readProgramTextFromResource(resourceName)?.let { parseProgramText(it) }
    }

    fun parseProgramTextWithStdlib(programText: String): SmlCompilationUnit? {
        val resourceSet = resourceSetProvider.get()
        stdlib.load(resourceSet)
        return parseHelper.parse(programText, resourceSet)
    }

    fun parseResourceWithStdlib(resourceName: ResourceName): SmlCompilationUnit? {
        return readProgramTextFromResource(resourceName)?.let { parseProgramTextWithStdlib(it) }
    }

    fun parseProgramTextWithContext(programText: String, context: List<ResourceName>): SmlCompilationUnit? {
        val resourceSet = createResourceSetFromContext(context)
        return parseHelper.parse(programText, resourceSet)
    }

    fun parseResourceWithContext(resourceName: ResourceName, context: List<ResourceName>): SmlCompilationUnit? {
        return readProgramTextFromResource(resourceName)?.let { parseResourceWithContext(it, context) }
    }

    private fun readProgramTextFromResource(resourceName: ResourceName): String? {
        val resourcePath = javaClass.classLoader.getResourcePath(resourceName) ?: return null
        if (!Files.isReadable(resourcePath)) {
            return null
        }

        return Files.readString(resourcePath)
    }

    private fun createResourceSetFromContext(context: List<ResourceName>): ResourceSet {
        val result = resourceSetProvider.get()
        for (resourceName in context) {
            val resourcePath = javaClass.classLoader.getResourcePath(resourceName) ?: continue
            val resourceUri = URI.createURI(resourcePath.toString().replace("%3A", ":"))
            result
                .createResource(resourceUri)
                .load(Files.newInputStream(resourcePath), result.loadOptions)
        }
        return result
    }
}
