//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//import de.unibonn.simpleml.simpleML.Expression
//
//
//class CreateUnconnectedExpressionStatement extends HasNesting implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	new(String key, CreationHandler nestedEntity) {
//		super(nestedEntity)
//	}
//
//	override create() {
//		val entity = factory.createExpressionStatement
//		entity.expression = getNestedEntity() as Expression
//		entity
//	}
//}