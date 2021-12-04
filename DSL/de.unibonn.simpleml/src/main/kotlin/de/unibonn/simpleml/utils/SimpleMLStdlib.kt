package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClass
import org.eclipse.core.runtime.FileLocator
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SimpleMLStdlib @Inject constructor(
    private val indexExtensions: SimpleMLIndexExtensions
) {

    private val cache = mutableMapOf<String, SmlClass?>()

    fun getClass(context: EObject, qualifiedName: String): SmlClass? {
        return cache.computeIfAbsent(qualifiedName) {
            val description = indexExtensions.visibleGlobalDeclarationDescriptions(context)
                .find { it.qualifiedName.toString() == qualifiedName }
                ?: return@computeIfAbsent null

            var eObject = description.eObjectOrProxy
            if (eObject != null && eObject.eIsProxy()) {
                eObject = context.eResource().resourceSet.getEObject(description.eObjectURI, true)
            }

            eObject as? SmlClass
        }
    }

    fun load(resourceSet: ResourceSet) {
        listStdlibFiles().forEach { (path, uri) ->
            resourceSet.createResource(uri)
                .load(Files.newInputStream(path), resourceSet.loadOptions)
        }
    }

    fun listStdlibFiles(): Sequence<Pair<Path, URI>> {
        val resourcesUrl = javaClass.classLoader.getResource("stubs") ?: return emptySequence()
        val resourcesUri = FileLocator.resolve(resourcesUrl).toURI()

        return sequence {
            var fileSystem: FileSystem? = null
            val stdlibBase = when (resourcesUri.scheme) {

                // Without this code Maven tests fail with a FileSystemNotFoundException since stdlib resources are in a jar
                "jar" -> {
                    fileSystem =
                        FileSystems.newFileSystem(resourcesUri, emptyMap<String, String>(), null)
                    fileSystem.getPath("stubs")
                }
                else -> Paths.get(resourcesUri)
            }

            val stdlibFiles = Files.walk(stdlibBase)
                .filter { it.toString().endsWith(".stub.simpleml") }

            for (path in stdlibFiles) {
                val relativePath = path.toString().replace("stubs/", "")
                val uri = URI.createURI("$resourcesUri/$relativePath".replace("%3A", ":"))
                yield(path to uri)
            }

            fileSystem?.close()
        }
    }
}
