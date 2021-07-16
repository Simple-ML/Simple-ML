package de.unibonn.simpleml.web

import com.google.inject.Guice
import com.google.inject.Injector
import de.unibonn.simpleml.SimpleMLRuntimeModule
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.ide.SimpleMLIdeModule
import org.eclipse.xtext.util.Modules2
import org.eclipse.xtext.web.server.persistence.IResourceBaseProvider

/**
 * Initialization support for running Xtext languages in web applications.
 */
class SimpleMLWebSetup extends SimpleMLStandaloneSetup {
	
	IResourceBaseProvider resourceBaseProvider;
	
	new(IResourceBaseProvider resourceBaseProvider) {
		this.resourceBaseProvider = resourceBaseProvider;
	}
	
	override Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new SimpleMLRuntimeModule, new SimpleMLIdeModule, new SimpleMLWebModule(resourceBaseProvider)))
	}
}
