//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//
//
//class CreateIntegerLiteral implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	int content
//	
//	new(int content) {
//		this.content = content
//	}
//	
//	override create() {
//		val entity = factory.createIntegerLiteral	
//		entity.value = content
//		entity
//	}
//}