/*
 * generated by Xtext 2.18.0.M3
 */
package de.unibonn.simpleml.web

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.xtext.util.DisposableRegistry
import org.eclipse.xtext.web.servlet.XtextServlet
import org.eclipse.xtext.web.server.XtextServiceDispatcher
import org.eclipse.xtext.web.servlet.HttpServiceContext
import org.eclipse.xtext.web.server.InvalidRequestException
import org.eclipse.xtext.web.server.persistence.ResourceBaseProviderImpl
import de.projektionisten.simpleml.web.EmfServiceDispatcher

/**
 * Deploy this class into a servlet container to enable DSL-specific services.
 */
@WebServlet(name = 'XtextServices', urlPatterns = '/xtext-service/*')
class SimpleMLServlet extends XtextServlet {
	
	DisposableRegistry disposableRegistry
	
	override init() {
		super.init()
		val resourceBaseProvider = new ResourceBaseProviderImpl("./dsl_files");
		val injector = new SimpleMLWebSetup(resourceBaseProvider).createInjectorAndDoEMFRegistration()
		disposableRegistry = injector.getInstance(DisposableRegistry)
	}
	
	override destroy() {
		if (disposableRegistry !== null) {
			disposableRegistry.dispose()
			disposableRegistry = null
		}
		super.destroy()
	}
	
	override protected XtextServiceDispatcher.ServiceDescriptor getService(HttpServletRequest request) throws InvalidRequestException {
		val serviceContext = new HttpServiceContext(request)
		val injector = getInjector(serviceContext)
		val serviceDispatcher = injector.getInstance(EmfServiceDispatcher)
		val service = serviceDispatcher.getService(serviceContext)
		return service
	}
	
	override protected void service(HttpServletRequest req, HttpServletResponse resp) {
 		resp.activateCORS(req.getHeader("Origin"))
 		super.service(req, resp)
	}

	def activateCORS(HttpServletResponse resp, String origin) {
		resp.addHeader("Access-Control-Allow-Origin", origin);
		resp.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
		resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Set-Cookie, *");
		resp.addHeader("Access-Control-Allow-Credentials", "true")
	}
}
