package de.unibonn.simpleml.ide

import com.google.inject.Inject
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.emf.common.util.URI
import org.eclipse.xtext.build.IncrementalBuilder
import org.eclipse.xtext.ide.server.ProjectManager
import org.eclipse.xtext.util.CancelIndicator
import java.nio.file.Files
import java.nio.file.Path

class SimpleMLProjectManager @Inject constructor(
    private val stdlib: SimpleMLStdlib
) : ProjectManager() {

    override fun doInitialBuild(cancelIndicator: CancelIndicator): IncrementalBuilder.Result {
        val uris = projectConfig.sourceFolders
            .flatMap { srcFolder -> srcFolder.getAllResources(fileSystemScanner) }
            .toMutableList()

//        val resourcesUrl = stdlib.javaClass.classLoader.getResource("simpleml")
//        if (resourcesUrl != null) {
//            println(resourcesUrl.toString())
//            val resourcesUri = FileLocator.resolve(resourcesUrl).toURI()
//            println(resourcesUri.toString())
//            var fileSystem: FileSystem? = null
//            val stdlibBase = when (resourcesUri.scheme) {
//
//                // Without this code Maven tests fail with a FileSystemNotFoundException since stdlib resources are in a jar
//                "jar" -> {
//                    fileSystem = FileSystems.newFileSystem(resourcesUri, emptyMap<String, String>(), null)
//                    fileSystem.getPath("simpleml")
//                }
//                else -> Paths.get(resourcesUri)
//            }
//            println(stdlibBase)
//
//            Files.walk(stdlibBase)
//                .filter { it.toString().endsWith(".stub.simpleml") }
//                .forEach {
//                    uris += URI.createFileURI(it.toString())
//                }
//        }

        // TODO: must be configurable within the editor + we need a solution for the web setup + standalone (tests)
        //  think about the relation between the DSL and the stdlib stubs (one or multiple repos)
        Files.walk(Path.of("C:\\Users\\Lars\\Documents\\Repositories\\Simple-ML\\stdlib\\stubs"))
            .filter { it.toString().endsWith(".stub.simpleml") }
            .forEach {
                println(it.toString())
                println(URI.createFileURI(it.toString()).toString())
                uris += URI.createFileURI(it.toString())
            }

        uris.forEach {
            println(it.toString())
        }

        return doBuild(uris, emptyList(), emptyList(), cancelIndicator)
    }
}


