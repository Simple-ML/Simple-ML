//package de.projektionisten.simpleml.web.emf.^extension.Association
//
//import org.eclipse.emf.ecore.EObject
//import org.eclipse.emf.ecore.util.EcoreUtil
//import de.unibonn.simpleml.simpleML.ArrayLiteral
//import de.unibonn.simpleml.simpleML.Assignment
//import de.unibonn.simpleml.simpleML.Expression
//import de.unibonn.simpleml.simpleML.ExpressionStatement
//import de.unibonn.simpleml.simpleML.Literal
//import de.unibonn.simpleml.simpleML.ProcessCall
//import de.unibonn.simpleml.simpleML.Reference
//import de.unibonn.simpleml.simpleML.SmlWorkflow
//import de.projektionisten.simpleml.web.emf.^extension.Deletion.MoveToUnconnectedStatements
//import de.projektionisten.simpleml.web.emf.^extension.Access.EmfPathHelper
//import de.projektionisten.simpleml.web.emf.^extension.Creation.CreateProcessCall
//import de.projektionisten.simpleml.web.emf.^extension.Creation.Nested
//
//
//class EmfAssociationHelper {
//	static def createAssociationToRoot(EObject root, EObject entity) {
//		if(associationExists(entity, root)) {
//			//TODO: exception or message
//			return
//		}
//
//		switch entity {
//			//link to statements
//			case entity instanceof Assignment:
//				(root as SmlWorkflow).statements.add(entity as Assignment)
//			//link to unconnected statements
//			case entity instanceof ProcessCall,
//			case entity instanceof Literal:
//				(root as SmlWorkflow).statements.add(Nested.UnconnectedAssignement(entity))
//		}
//	}
//
//	static def createAssociation(EObject source, EObject target) {
//		if(associationExists(source, target)) {
//			//TODO: exception or message
//			return
//		}
//
//		val parentOfsource = source.eContainer
//		var didNothing = 0
//		switch source {
//			case source instanceof Assignment: {
//				switch target {
//					case target instanceof Assignment:
//						handleAssignment(source, target, [
//							Nested.Reference(source)
//						])
//					case target instanceof ProcessCall:
//						(target as ProcessCall).arguments.add(Nested.Reference(source))
//					case target instanceof ArrayLiteral:
//						(target as ArrayLiteral).elements.add(Nested.Reference(source))
//					//TODO: DictionaryLiteral (what about key-name)
//					default:
//						didNothing++
//				}
//			}
//			case source instanceof Expression: {
//				switch target {
//					case target instanceof Assignment:
//						handleAssignment(source, target, [
//							source
//						])
//					case target instanceof ProcessCall:
//						(target as ProcessCall).arguments.add(source as Expression)
//					case target instanceof ArrayLiteral:
//						(target as ArrayLiteral).elements.add(source as Expression)
//					//TODO: DictionaryLiteral (what about key-name)
//					default:
//						didNothing++
//				}
//			}
//		}
//		if(didNothing < 1)
//			parentOfsource.removeNestingIfNecessary
//	}
//
//	static def deleteAssociation(EObject source, EObject target) {
//		if(!associationExists(source, target)) {
//			//TODO: exception or message
//			return
//		}
//		
//		val parentOfsource = source.eContainer
//		var didNothing = 0
//		switch source {
//			case source instanceof Assignment:
//				deleteReferenceNestingIfNesessary(source, target)
//			case source instanceof Expression:
//				new MoveToUnconnectedStatements(source).execute
//			default:
//				didNothing++
//		}
//		if(didNothing < 1)
//			parentOfsource.removeNestingIfNecessary
//	}
//
//
//
//	private static def handleAssignment(EObject source, EObject target, (EObject) => EObject assignment) {
//		val rootNode = EmfPathHelper.getRootNode(target)
//		val tempValue = (target as Assignment).getValue()
//		(target as Assignment).value = assignment.apply(source) as Expression
//		storeInUnconnectedNodesIfNecessary(tempValue, rootNode)
//	}
//	
//	private static def deleteReferenceNestingIfNesessary(EObject source, EObject target) {
//		val foundReferenceNesting = target.eContents.filter[child|
//			child instanceof Reference && (child as Reference).ref === source
//		].last
//		if(foundReferenceNesting !== null) 
//			EcoreUtil.delete(foundReferenceNesting)
//	}
//
//	private static def removeNestingIfNecessary(EObject entity) {
//		switch entity {
//			case entity instanceof Assignment:
//				(entity as Assignment).value = new CreateProcessCall('NULL', null).create() as Expression
//			case entity instanceof ExpressionStatement:
//				EcoreUtil.delete(entity)
//		}
//	}
//
//	private static def storeInUnconnectedNodesIfNecessary(EObject entity, EObject rootNode) {
//		if(rootNode === null)
//			return
//			
//		switch entity {
//			case entity instanceof ProcessCall,
//			case entity instanceof Literal:
//				(rootNode as Workflow).statements.add(Nested.UnconnectedAssignement(entity))
//		}
//	}
//
//	/* 
//	 * 'source' has to be child of 'target' in data-structure
//	 */
//	private static def associationExists(EObject source, EObject target) {
//		val associations = target.eContents.filter[child|
//			switch child {
//				case child instanceof Reference:
//					if((child as Reference).ref === source)
//						return true
//				case child instanceof ExpressionStatement:
//					if((child as ExpressionStatement).expression === source)
//						return true
//				default:
//					if(child === source)
//						return true
//			}
//			return false
//		]
//		associations.length > 0
//	}
//}
