//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//
//
//class CreateStringLiteral implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	String content
//	
//	new(String content) {
//		this.content = content
//	}
//	
//	override create() {
//		val entity = factory.createStringLiteral
//		entity.value = content
//		entity
//	}
//}
//	