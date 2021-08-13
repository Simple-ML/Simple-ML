//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import de.unibonn.simpleml.simpleML.SimpleMLFactory
//import org.eclipse.emf.ecore.EObject
//import de.unibonn.simpleml.simpleML.ProcessCall
//import de.unibonn.simpleml.simpleML.Literal
//import de.unibonn.simpleml.simpleML.Assignment
//
//
//class Nested {
//	static final SimpleMLFactory factory = SimpleMLFactory.eINSTANCE
//	
//	static def UnconnectedAssignement(EObject entity) {
//		val package = factory.createAssignment
//		package.name = 'temp'
//		switch entity {
//			case entity instanceof ProcessCall:
//				package.value = entity as ProcessCall
//			case entity instanceof Literal:
//				package.value= entity as Literal
//		}
//		package 
//	}
//	
//	static def Reference(EObject entity) {
//		val package = factory.createReference
//		package.ref = entity as Assignment
//		package
//	}
//}