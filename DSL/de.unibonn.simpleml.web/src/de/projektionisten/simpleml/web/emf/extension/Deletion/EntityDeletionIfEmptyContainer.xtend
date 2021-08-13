//package de.projektionisten.simpleml.web.emf.^extension.Deletion
//
//import java.util.List
//import org.eclipse.emf.ecore.EObject
//import org.eclipse.emf.ecore.util.EcoreUtil
//import de.unibonn.simpleml.simpleML.ArrayLiteral
//import de.unibonn.simpleml.simpleML.DictionaryLiteral
//import de.projektionisten.simpleml.web.emf.^extension.Deletion.EmfDeletionHelper
//
//
//class EntityDeletionIfEmptyContainer implements DeletionHandler {
//	EObject entity
//	
//	new(EObject entity) {
//		this.entity = entity
//	}
//	
//	override execute() {
//		var delete = false			
//		switch entity {
//			case entity instanceof ArrayLiteral:
//				delete = !hasChildren((entity as ArrayLiteral).elements as List<?> as List<EObject>)
//			case entity instanceof DictionaryLiteral:
//				delete = !hasChildren((entity as DictionaryLiteral).properties as List<?> as List<EObject>)
//		}
//		if(delete) {
//			EmfDeletionHelper.getParentDeletionStrategie(entity).execute()
//			EcoreUtil.delete(entity)
//		}
//	}
//	
//	private def hasChildren(List<EObject> children) {
//		children.size > 0			
//	}
//}
