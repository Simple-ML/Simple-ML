package de.projektionisten.simpleml.web.emf.^extension.Creation

import java.util.List
import java.util.ArrayList
import org.eclipse.emf.ecore.EObject


interface CreationHandler {
	def EObject create()
}

class HasChildren {
	List<CreationHandler> children
	
	new(List<CreationHandler> children) {
		this.children = children
	}
	
	def getChildEntities() {
		if(children === null)
			return new ArrayList<EObject>()
			
		val iterator = children.iterator
		val createdObjects = new ArrayList<EObject>
		
			
		while(iterator.hasNext()) {
			createdObjects.addAll(iterator.next().create())
		}
		createdObjects
	}
}

class HasNesting {
	CreationHandler nestedEntity
	
	new(CreationHandler nestedEntity) {
		this.nestedEntity = nestedEntity
	}
	
	def getNestedEntity() {
		if(nestedEntity === null)
			return null
		
		nestedEntity.create()
	}
	
}