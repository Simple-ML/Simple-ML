package de.projektionisten.simpleml.web.emf.^extension.Deletion

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil


class EntityDeletion implements DeletionHandler {
	EObject entity

	new(EObject entity) {
		this.entity = entity
	}
	
	override execute() {
		EcoreUtil.delete(entity)
	}
}