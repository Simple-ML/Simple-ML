package de.unibonn.simpleml.web

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.stdlibAccess.StdlibAccess
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.web.server.IServiceContext
import org.eclipse.xtext.web.server.model.IWebResourceSetProvider
import org.eclipse.xtext.xbase.lib.Pair
import kotlin.math.max

/**
 * Resources that run in multi-resource mode share the same resource set.
 */
class SimpleMLResourceSetProvider @Inject constructor(
    private val stdlib: StdlibAccess,
    private val provider: Provider<ResourceSet>
) : IWebResourceSetProvider {
	
    private val multiResourcePrefix = "multi-resource"
	
    override fun get(resourceId: String?, serviceContext: IServiceContext): ResourceSet {
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
