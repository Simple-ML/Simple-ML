package de.projektionisten.simpleml.web.emf.^extension.Access

import org.eclipse.emf.ecore.EObject


interface AccessHandler {
	def EObject access(EObject entity)
}