package de.unibonn.simpleml.web

import com.google.inject.Binder
import org.eclipse.xtext.web.server.generator.IContentTypeProvider
import org.eclipse.xtext.web.server.model.IWebResourceSetProvider
import org.eclipse.xtext.web.server.persistence.FileResourceHandler
import org.eclipse.xtext.web.server.persistence.IResourceBaseProvider
import org.eclipse.xtext.web.server.persistence.IServerResourceHandler

/**
 * Use this class to register additional components to be used within the web application.
 */
class SimpleMLWebModule(private var resourceBaseProvider: IResourceBaseProvider?) : AbstractSimpleMLWebModule() {
    fun configureResourceBaseProvider(binder: Binder) {
        if (resourceBaseProvider !== null) {
            binder.bind(IResourceBaseProvider::class.java).toInstance(resourceBaseProvider)
        }
    }

    override fun bindIContentTypeProvider(): Class<out IContentTypeProvider> {
        return SimpleMLContentTypeProvider::class.java
    }

    fun bindIServerResourceHandler(): Class<out IServerResourceHandler> {
        return FileResourceHandler::class.java
    }

    fun bindIWebResourceSetProvider(): Class<out IWebResourceSetProvider> {
        return SimpleMLResourceSetProvider::class.java
    }
}
