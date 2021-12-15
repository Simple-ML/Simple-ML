package de.unibonn.simpleml.web

import org.eclipse.xtext.web.server.generator.DefaultContentTypeProvider
import org.eclipse.xtext.web.server.generator.GeneratorService

class SimpleMLContentTypeProvider : DefaultContentTypeProvider() {
    override fun getContentType(fileName: String): String? {
        if (GeneratorService.DEFAULT_ARTIFACT == fileName) {
            return "text/html"
        }

        return super.getContentType(fileName)
    }
}
