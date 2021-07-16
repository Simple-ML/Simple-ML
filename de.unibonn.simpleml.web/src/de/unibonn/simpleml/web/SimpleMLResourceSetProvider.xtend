package de.unibonn.simpleml.web

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.model.IWebResourceSetProvider;
import de.unibonn.simpleml.utils.SimpleMLStdlib

/**
 * Resources that run in multi-resource mode share the same resource set.
 */
class SimpleMLResourceSetProvider implements IWebResourceSetProvider {
	
	public static final String MULTI_RESOURCE_PREFIX = "multi-resource"
	
	@Inject
	SimpleMLStdlib stdlib
		
	@Inject 
	Provider<ResourceSet> provider
	
	override ResourceSet get(String resourceId, IServiceContext serviceContext) {
		if (resourceId !== null && resourceId.startsWith(MULTI_RESOURCE_PREFIX)) {
			val pathEnd = Math.max(resourceId.indexOf('/'), MULTI_RESOURCE_PREFIX.length())
			return serviceContext.getSession().get(Pair.of(ResourceSet, resourceId.substring(0, pathEnd))) [
				val resourceSet = provider.get()
				stdlib.load(resourceSet)
				resourceSet
				
			]
		} else {
			val resourceSet = provider.get()
			stdlib.load(resourceSet)
			return resourceSet
		}
	}
}
