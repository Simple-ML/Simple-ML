package de.projektionisten.simpleml.web.emf.^extension.Access

import java.util.List
import org.eclipse.emf.ecore.EObject


class ArrayAccess implements AccessHandler {
	String propertyName
	int index
	
	new(String propertyName, int index) {
		this.propertyName = propertyName.toFirstUpper
		this.index = index
	}
	
	override access(EObject entity) {
		(entity.class.getMethod('get' + propertyName).invoke(entity) as List<EObject>).get(index) as EObject
	}
}