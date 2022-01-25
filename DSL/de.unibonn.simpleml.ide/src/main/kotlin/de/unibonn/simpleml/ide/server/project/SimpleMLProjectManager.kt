package de.unibonn.simpleml.ide.server.project

import de.unibonn.simpleml.stdlibAccess.listStdlibFiles
import org.eclipse.xtext.build.IncrementalBuilder
import org.eclipse.xtext.ide.server.ProjectManager
import org.eclipse.xtext.util.CancelIndicator

class SimpleMLProjectManager : ProjectManager() {

    override fun doInitialBuild(cancelIndicator: CancelIndicator): IncrementalBuilder.Result {
        val uris = projectConfig.sourceFolders
            .flatMap { srcFolder -> srcFolder.getAllResources(fileSystemScanner) }
            .toMutableList()

        listStdlibFiles().forEach { (_, uri) ->
            uris += uri
        }

        return doBuild(uris, emptyList(), emptyList(), cancelIndicator)
    }
}
