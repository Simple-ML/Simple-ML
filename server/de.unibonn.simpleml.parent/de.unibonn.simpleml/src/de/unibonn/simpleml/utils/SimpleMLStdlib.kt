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
import java.nio.file.Paths

private const val LIB_PACKAGE = "simpleml.lang"
const val LIB_ANY = "${LIB_PACKAGE}.Any"
const val LIB_BOOLEAN = "${LIB_PACKAGE}.Boolean"
const val LIB_FLOAT = "${LIB_PACKAGE}.Float"
const val LIB_INT = "${LIB_PACKAGE}.Int"
const val LIB_STRING = "${LIB_PACKAGE}.String"

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
        val resourcesUrl = javaClass.classLoader.getResource("simpleml") ?: return
        val resourcesUri = FileLocator.resolve(resourcesUrl).toURI()

        var fileSystem: FileSystem? = null
        val stdlibBase = when (resourcesUri.scheme) {

            // Without this code Maven tests fail with a FileSystemNotFoundException since stdlib resources are in a jar
            "jar" -> {
                fileSystem = FileSystems.newFileSystem(resourcesUri, emptyMap<String, String>(), null)
                fileSystem.getPath("simpleml")
            }
            else -> Paths.get(resourcesUri)
        }

        Files.walk(stdlibBase)
                .filter { it.toString().endsWith(".stub.simpleml") }
                .forEach {
                    resourceSet.createResource(URI.createFileURI(it.toString()))
                            .load(Files.newInputStream(it), resourceSet.loadOptions)
                }

        fileSystem?.close()
    }
}
