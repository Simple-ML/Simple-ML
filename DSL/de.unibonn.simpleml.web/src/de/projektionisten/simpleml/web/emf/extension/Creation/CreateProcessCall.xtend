//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import java.util.List
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//import de.unibonn.simpleml.simpleML.Expression
//
//
//class CreateProcessCall extends HasChildren implements CreationHandler {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	String name
//	
//	new(String name, List<CreationHandler> children) {
//		super(children)
//		this.name = name
//	}
//	
//	override create() {
//		val entity = factory.createProcessCall
//		entity.ref = name
//		entity.arguments.addAll((getChildEntities() as List<?>) as List<Expression>)
//		entity
//	}
//}