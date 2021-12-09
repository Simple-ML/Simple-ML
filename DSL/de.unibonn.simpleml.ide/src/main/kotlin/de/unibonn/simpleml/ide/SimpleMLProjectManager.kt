package de.unibonn.simpleml.ide

import com.google.inject.Inject
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.xtext.build.IncrementalBuilder
import org.eclipse.xtext.ide.server.ProjectManager
import org.eclipse.xtext.util.CancelIndicator

class SimpleMLProjectManager @Inject constructor(
    private val stdlib: SimpleMLStdlib
) : ProjectManager() {

    override fun doInitialBuild(cancelIndicator: CancelIndicator): IncrementalBuilder.Result {
        println("Source files: ")
        val uris = projectConfig.sourceFolders
            .flatMap { srcFolder -> srcFolder.getAllResources(fileSystemScanner) }
            .toMutableList()

        uris.forEach { println(it.toString()) }

        println("Stdlib files: ")
        stdlib.listStdlibFiles().forEach { (_, uri) ->
            uris += uri
            println(uri.toString())
        }

        return doBuild(uris, emptyList(), emptyList(), cancelIndicator)
    }
}
