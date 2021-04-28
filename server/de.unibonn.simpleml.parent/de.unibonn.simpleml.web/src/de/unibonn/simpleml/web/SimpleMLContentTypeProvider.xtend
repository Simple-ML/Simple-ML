package de.unibonn.simpleml.web

import org.eclipse.xtext.web.server.generator.DefaultContentTypeProvider;
import org.eclipse.xtext.web.server.generator.GeneratorService;

class SimpleMLContentTypeProvider extends DefaultContentTypeProvider {
	override String getContentType(String fileName) {
		if (GeneratorService.DEFAULT_ARTIFACT.equals(fileName)) {
			return "text/html";
		}
		
		return super.getContentType(fileName);
	}
}
