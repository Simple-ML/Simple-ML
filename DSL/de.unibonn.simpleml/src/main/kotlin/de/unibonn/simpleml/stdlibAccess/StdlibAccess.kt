package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.constant.SmlFileExtension
import de.unibonn.simpleml.scoping.visibleGlobalDeclarationDescriptions
import de.unibonn.simpleml.simpleML.SmlClass
import org.eclipse.core.runtime.FileLocator
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.naming.QualifiedName
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val cache = mutableMapOf<QualifiedName, SmlClass?>()
private val classLoader = object{}.javaClass.classLoader

fun getStdlibClass(context: EObject, qualifiedName: QualifiedName): SmlClass? {
    return cache.computeIfAbsent(qualifiedName) {
        val description = context.visibleGlobalDeclarationDescriptions()
            .find { it.qualifiedName == qualifiedName }
            ?: return@computeIfAbsent null

        var eObject = description.eObjectOrProxy
        if (eObject != null && eObject.eIsProxy()) {
            eObject = context.eResource().resourceSet.getEObject(description.eObjectURI, true)
        }

        eObject as? SmlClass
    }
}

fun ResourceSet.loadStdlib() {
    listStdlibFiles().forEach { (path, uri) ->
        createResource(uri).load(Files.newInputStream(path), loadOptions)
    }
}

fun listStdlibFiles(): Sequence<Pair<Path, URI>> {
    val resourcesUrl = classLoader.getResource("stdlib") ?: return emptySequence()
    val resourcesUri = FileLocator.resolve(resourcesUrl).toURI()

    return sequence {
        var fileSystem: FileSystem? = null
        val stdlibBase = when (resourcesUri.scheme) {

            // Without this code tests fail with a FileSystemNotFoundException since stdlib resources are in a jar
            "jar" -> {
                fileSystem = FileSystems.newFileSystem(
                    resourcesUri,
                    emptyMap<String, String>(),
                    null
                )
                fileSystem.getPath("stdlib")
            }
            else -> Paths.get(resourcesUri)
        }

        val stdlibFiles = Files.walk(stdlibBase)
            .filter { it.toString().endsWith(".${SmlFileExtension.Stub}") }

        for (path in stdlibFiles) {
            val relativePath = path.toString().replace("stdlib/", "")
            val uri = URI.createURI("$resourcesUri/$relativePath".replace("%3A", ":"))
            yield(path to uri)
        }

        fileSystem?.close()
    }
}
