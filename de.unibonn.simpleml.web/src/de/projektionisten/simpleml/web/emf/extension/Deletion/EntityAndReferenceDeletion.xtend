package de.projektionisten.simpleml.web.emf.^extension.Deletion

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import de.projektionisten.simpleml.web.emf.^extension.Access.EmfPathHelper


class EntityAndReferenceDeletion implements DeletionHandler {
	EObject entity
	
	new(EObject entity) {
		this.entity = entity
	}
	
	override execute() {
		val root = EmfPathHelper.getRootNode(entity)
		val references = EcoreUtil.UsageCrossReferencer.find(entity, root)

		references.forEach[
			EcoreUtil.delete(it.EObject)
		]
		EcoreUtil.delete(entity)
	}
}