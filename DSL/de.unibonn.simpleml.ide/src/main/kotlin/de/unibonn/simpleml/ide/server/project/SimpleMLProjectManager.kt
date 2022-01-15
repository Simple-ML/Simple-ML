package de.unibonn.simpleml.ide.server.project

import com.google.inject.Inject
import de.unibonn.simpleml.stdlibAccess.StdlibAccess
import org.eclipse.xtext.build.IncrementalBuilder
import org.eclipse.xtext.ide.server.ProjectManager
import org.eclipse.xtext.util.CancelIndicator

class SimpleMLProjectManager @Inject constructor(
    private val stdlib: StdlibAccess
) : ProjectManager() {

    override fun doInitialBuild(cancelIndicator: CancelIndicator): IncrementalBuilder.Result {
        val uris = projectConfig.sourceFolders
            .flatMap { srcFolder -> srcFolder.getAllResources(fileSystemScanner) }
            .toMutableList()

        stdlib.listStdlibFiles().forEach { (_, uri) ->
            uris += uri
        }

        return doBuild(uris, emptyList(), emptyList(), cancelIndicator)
    }
}
