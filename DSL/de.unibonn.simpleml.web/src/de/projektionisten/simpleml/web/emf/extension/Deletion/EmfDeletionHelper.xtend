//package de.projektionisten.simpleml.web.emf.^extension.Deletion
//
//import java.util.ArrayList
//import java.util.List
//import org.eclipse.emf.ecore.EObject
//import de.unibonn.simpleml.simpleML.Assignment
//import de.unibonn.simpleml.simpleML.SmlCall
//import de.unibonn.simpleml.simpleML.ExpressionStatement
//import de.unibonn.simpleml.simpleML.Reference
//import de.unibonn.simpleml.simpleML.Literal
//import de.unibonn.simpleml.simpleML.ArrayLiteral
//import de.unibonn.simpleml.simpleML.DictionaryLiteral
//import de.unibonn.simpleml.simpleML.Seconds
//
//class EmfDeletionHelper {
//	
//	static def delteEntity(EObject entity) {
//		val actions = createDeletionStrategie(entity)
//		actions.executeDeletion	
//	}
//	
//	static def createDeletionStrategie(EObject entity) {
//		val actions = new ArrayList<DeletionHandler>
//		val _entity = adjustEntityPointer(entity)
//		
//		_entity.eContents.forEach[child|
//			actions.add(child.childDeletionStrategie)
//		]
//
//		actions.add(_entity.selfDeletionStrategie)
//		actions.add(_entity.parentDeletionStrategie)
//		actions
//	}
//
//	static def getParentDeletionStrategie(EObject entity) {
//		if(entity.eContainer === null)
//			new DoNothing
//			
//		val parent = entity.eContainer
//		switch parent {
//			case parent instanceof Assignment:
//				new NullForAssignment(parent)
//			case parent instanceof ExpressionStatement:
//				new EntityDeletion(parent)
//			case parent instanceof SmlCall:
//				new DoNothing
//			case parent instanceof ArrayLiteral:
//				new EntityDeletionIfEmptyContainer(parent)
//			case parent instanceof DictionaryLiteral:
//				new EntityDeletionIfEmptyContainer(parent)
//			default:
//				new DoNothing
//		}
//	}
//	
//	static def getSelfDeletionStrategie(EObject entity) {
//		switch entity {
//			case entity instanceof Assignment:
//				new EntityAndReferenceDeletion(entity)
//			case entity instanceof ProcessCall:
//				new EntityDeletion(entity)
//			case entity instanceof Literal:
//				new EntityDeletion(entity)
//			default:
//				new DoNothing
//		}
//	}
//
//	static def getChildDeletionStrategie(EObject entity) {
//		switch entity {
//			case entity instanceof ProcessCall:
//				new MoveToUnconnectedStatements(entity)
//			case entity instanceof Reference:
//				new EntityDeletion(entity)
//			case entity instanceof ArrayLiteral:
//				new MoveToUnconnectedStatements(entity)
//			case entity instanceof DictionaryLiteral:
//				new MoveToUnconnectedStatements(entity)
//			case entity instanceof Literal:
//				new EntityDeletion(entity)
//			default:
//				new DoNothing
//		}
//	}
//	
//	static def adjustEntityPointer(EObject entity) {
//		switch entity {
//			case entity instanceof Seconds:
//				entity.eContainer
//			default:
//				entity
//		}
//	}
//	
//	static def executeDeletion(List<DeletionHandler> deleteActions) {
//		var iterator = deleteActions.listIterator
//	 	
//	 	while(iterator.hasNext()) {
//	 		iterator.next().execute()
//	 	}
//	}
//}