//package de.projektionisten.simpleml.web.emf.^extension.Deletion
//
//import org.eclipse.emf.ecore.EObject
//import de.unibonn.simpleml.simpleML.Assignment
//import de.unibonn.simpleml.simpleML.ProcessCall
//import de.projektionisten.simpleml.web.emf.^extension.Creation.CreateProcessCall
//
//
//class NullForAssignment implements DeletionHandler {
//	EObject entity
//	
//	new(EObject entity) {
//		this.entity = entity
//	}
//
//	override execute() {
//		val nullEntity = new CreateProcessCall('NULL', null).create()
//		(entity as Assignment).value = nullEntity as ProcessCall
//	}
//}