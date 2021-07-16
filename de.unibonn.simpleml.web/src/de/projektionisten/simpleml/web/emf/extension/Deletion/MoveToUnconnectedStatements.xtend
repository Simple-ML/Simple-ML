//package de.projektionisten.simpleml.web.emf.^extension.Deletion
//
//import org.eclipse.emf.ecore.EObject
//import de.unibonn.simpleml.simpleML.SmlWorkflow
//import de.projektionisten.simpleml.web.emf.^extension.Access.EmfPathHelper
//import de.projektionisten.simpleml.web.emf.^extension.Creation.Nested
//
//
//class MoveToUnconnectedStatements implements DeletionHandler {
//	EObject entity
//
//	new(EObject entity) {
//		this.entity = entity
//	}
//	
//	override execute() {
//		val rootNode = EmfPathHelper.getRootNode(entity)
//		val packedEntity = Nested.UnconnectedAssignement(entity)
//		(rootNode as SmlWorkflow).statements.add(packedEntity)
//	}
//}