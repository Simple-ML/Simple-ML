package de.unibonn.simpleml.web

import de.projektionisten.simpleml.web.EmfServiceDispatcher
import org.eclipse.xtext.util.DisposableRegistry
import org.eclipse.xtext.web.server.XtextServiceDispatcher
import org.eclipse.xtext.web.server.persistence.ResourceBaseProviderImpl
import org.eclipse.xtext.web.servlet.HttpServiceContext
import org.eclipse.xtext.web.servlet.XtextServlet
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Deploy this class into a servlet container to enable DSL-specific services.
 */
@WebServlet(name = "XtextServices", urlPatterns = ["/xtext-service/*"])
class SimpleMLServlet : XtextServlet() {

    private var disposableRegistry: DisposableRegistry? = null

    override fun init() {
        super.init()
        val resourceBaseProvider = ResourceBaseProviderImpl("./dsl_files")
        val injector = SimpleMLWebSetup(resourceBaseProvider).createInjectorAndDoEMFRegistration()
        disposableRegistry = injector.getInstance(DisposableRegistry::class.java)
    }

    override fun destroy() {
        disposableRegistry?.dispose()
        disposableRegistry = null
        super.destroy()
    }

    override fun getService(request: HttpServletRequest): XtextServiceDispatcher.ServiceDescriptor {
        val serviceContext = HttpServiceContext(request)
        val injector = getInjector(serviceContext)
        val serviceDispatcher = injector.getInstance(EmfServiceDispatcher::class.java)
        return serviceDispatcher.getService(serviceContext)
    }

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        activateCORS(resp, req.getHeader("Origin"))
        super.service(req, resp)
    }

    private fun activateCORS(resp: HttpServletResponse, origin: String?) {
        if (origin != null) {
            resp.addHeader("Access-Control-Allow-Origin", origin)
            resp.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE")
            resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Set-Cookie, *")
            resp.addHeader("Access-Control-Allow-Credentials", "true")
        }
    }
}
