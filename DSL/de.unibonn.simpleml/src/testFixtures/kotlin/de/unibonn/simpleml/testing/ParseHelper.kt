@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.testing

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.stdlibAccess.loadStdlib
import org.eclipse.core.runtime.FileLocator
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.testing.util.ParseHelper
import java.nio.file.Files
import java.nio.file.Paths

typealias ResourceName = String

class ParseHelper @Inject constructor(
    private val parseHelper: ParseHelper<SmlCompilationUnit>,
    private val resourceSetProvider: Provider<ResourceSet>
) {

    fun parseProgramTextWithStdlib(programText: String): SmlCompilationUnit? {
        val resourceSet = resourceSetProvider.get()
        resourceSet.loadStdlib()
        return parseHelper.parse(programText, resourceSet)
    }

    fun parseResource(resourceName: ResourceName): SmlCompilationUnit? {
        return readProgramTextFromResource(resourceName)?.let { parseProgramTextWithStdlib(it) }
    }

    fun parseProgramTextWithContext(programText: String, context: List<ResourceName>): SmlCompilationUnit? {
        val resourceSet = createResourceSetFromContext(context)
        resourceSet.loadStdlib()
        return parseHelper.parse(programText, resourceSet)
    }

    fun parseResourceWithContext(resourceName: ResourceName, context: List<ResourceName>): SmlCompilationUnit? {
        return readProgramTextFromResource(resourceName)?.let { parseProgramTextWithContext(it, context) }
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
            val resourceUrl = javaClass.classLoader.getResource(resourceName) ?: continue
            val resourceFileUri = FileLocator.resolve(resourceUrl).toURI()
            val resourceEmfUri = URI.createURI(resourceFileUri.toString(), false)
            val resourcePath = Paths.get(resourceFileUri)

            result
                .createResource(resourceEmfUri)
                ?.load(Files.newInputStream(resourcePath), result.loadOptions)
        }
        return result
    }
}
