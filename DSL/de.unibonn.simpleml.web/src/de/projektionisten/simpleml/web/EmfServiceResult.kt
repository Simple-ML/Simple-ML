package de.projektionisten.simpleml.web

import org.eclipse.xtext.web.server.IServiceResult

data class EmfServiceResult(
    val fullText: String,	
	val emfModel: String,
	val info: String,
    val error: String,
    val stateId: String,
    val dirty: Boolean
): IServiceResult  {
}
