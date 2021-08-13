package de.unibonn.simpleml.web

import com.google.inject.Inject
import com.google.inject.Provider
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.xbase.lib.Pair
import org.eclipse.xtext.web.server.model.IWebResourceSetProvider
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.xtext.web.server.IServiceContext
import kotlin.math.max

/**
 * Resources that run in multi-resource mode share the same resource set.
 */
class SimpleMLResourceSetProvider @Inject constructor(
	private val stdlib: SimpleMLStdlib,
	private val provider: Provider<ResourceSet>
): IWebResourceSetProvider {
	
	private val multiResourcePrefix = "multi-resource"
	
	override fun  get( resourceId: String?,  serviceContext: IServiceContext): ResourceSet {
		return if (resourceId !== null && resourceId.startsWith(multiResourcePrefix)) {
			val pathEnd = max(resourceId.indexOf('/'), multiResourcePrefix.length)
			serviceContext.session.get(Pair.of(ResourceSet::class.java, resourceId.substring(0, pathEnd))) {
				val resourceSet = provider.get()
				stdlib.load(resourceSet)
				resourceSet
			}
		} else {
			val resourceSet = provider.get()
			stdlib.load(resourceSet)
			resourceSet
		}
	}
}
