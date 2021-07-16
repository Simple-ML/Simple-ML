//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//
//
//class CreateBooleanLiteral implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	boolean content
//	
//	new(boolean content) {
//		this.content = content
//	}
//	
//	override create() {
//		val entity = factory.createBooleanLiteral
//		if(content)
//			entity.^true = true
//		entity
//	}
//}