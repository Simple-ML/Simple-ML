package de.projektionisten.simpleml.web.emf.^extension.Access

import org.eclipse.emf.ecore.EObject


class ObjectAccess implements AccessHandler {
	String propertyName
	
	new(String propertyName) {
		this.propertyName = propertyName.toFirstUpper
	}
	
	override access(EObject entity) {
		entity.class.getMethod('get' + propertyName).invoke(entity) as EObject
	}
}