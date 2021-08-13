//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//
//
//class CreateFloatLiteral implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	float content
//	
//	new(float content) {
//		this.content = content
//	}
//	
//	override create() {
//		val entity = factory.createFloatLiteral
//		entity.value = content
//		entity
//	}
//}