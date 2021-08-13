//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//import de.unibonn.simpleml.simpleML.Expression
//
//
//class CreateAssignment extends HasNesting implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	String name
//	
//	new(String name, CreationHandler nestedEntity) {
//		super(nestedEntity)
//		this.name = name
//	}
//	
//	override create() {
//		val entity = factory.createAssignment
//		
//		entity.name = name
//		entity.value = getNestedEntity() as Expression
//		entity
//	}
//}