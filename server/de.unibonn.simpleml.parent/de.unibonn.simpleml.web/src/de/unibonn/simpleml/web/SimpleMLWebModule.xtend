package de.unibonn.simpleml.web

import de.unibonn.simpleml.web.AbstractSimpleMLWebModule
import org.eclipse.xtext.web.server.model.IWebResourceSetProvider
import org.eclipse.xtext.web.server.persistence.IServerResourceHandler
import org.eclipse.xtext.web.server.persistence.FileResourceHandler
import org.eclipse.xtext.web.server.persistence.IResourceBaseProvider
import com.google.inject.Binder
import org.eclipse.xtext.web.server.generator.IContentTypeProvider

/**
 * Use this class to register additional components to be used within the web application.
 */
class SimpleMLWebModule extends AbstractSimpleMLWebModule {
	IResourceBaseProvider resourceBaseProvider;
	
	new(IResourceBaseProvider resourceBaseProvider) {
		this.resourceBaseProvider = resourceBaseProvider;
	}
	
	def void configureResourceBaseProvider(Binder binder) {
		if (resourceBaseProvider !== null) {
			binder.bind(IResourceBaseProvider).toInstance(resourceBaseProvider);
		}
	}
	
	override Class<? extends IContentTypeProvider> bindIContentTypeProvider() {
		return SimpleMLContentTypeProvider;
	}
	
	def Class<? extends IServerResourceHandler> bindIServerResourceHandler() {
		return FileResourceHandler;
	}
	
	def Class<? extends IWebResourceSetProvider> bindIWebResourceSetProvider() {
		return SimpleMLResourceSetProvider
	}
}
