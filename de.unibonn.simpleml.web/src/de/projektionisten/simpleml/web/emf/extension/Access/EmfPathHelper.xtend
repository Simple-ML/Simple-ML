package de.projektionisten.simpleml.web.emf.^extension.Access

import java.util.ArrayList
import java.util.List
import java.util.regex.Pattern
import org.eclipse.emf.ecore.EObject
import de.unibonn.simpleml.simpleML.SmlWorkflow

class EmfPathHelper {
	
	static def getRootNode(EObject entity) {
		var rootNode = entity
		while(rootNode.eContainer !== null)
			rootNode = rootNode.eContainer
		
		if(rootNode instanceof SmlWorkflow)
			rootNode as SmlWorkflow
		else 
			null
	}
	
	/*
	 * 
	 */
	static def getEntityFromPath(EObject rootEntity, String path) {
		val actions = createAccessActionsFromPath(path)
		
		actions.performAccess(rootEntity)
	}

	static def createAccessActionsFromPath(String path) {
		val actions = new ArrayList<AccessHandler>
		val pathElements = path.split('/')
		val pattern = Pattern.compile("(\\[[^\\[]*\\])");
		
		pathElements.forEach[
			if(it.length <= 0) return
				
			val matcher = pattern.matcher(it)
			
			if(matcher.find()) {
				val match = matcher.group
				val arrayIndex =  Integer.parseInt(match.replaceAll('(\\[|\\])', ''))
				actions.add((new ArrayAccess(matcher.replaceAll(''), arrayIndex)))
			
			} else
				actions.add(new ObjectAccess(it))
		]
		actions
	}
	
	static def performAccess(List<AccessHandler> actionList, EObject entity) {
		var currentEntity = entity
		var iterator = actionList.listIterator
	 	
	 	while(iterator.hasNext()) {
	 		currentEntity = iterator.next().access(currentEntity)
	 	}
	 	currentEntity
	}
}