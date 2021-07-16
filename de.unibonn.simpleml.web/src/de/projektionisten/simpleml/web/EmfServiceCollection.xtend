package de.projektionisten.simpleml.web

import org.eclipse.emf.ecore.EObject
import de.projektionisten.simpleml.web.dto.CreateEntityDTO
import de.projektionisten.simpleml.web.dto.DeleteEntityDTO
import de.projektionisten.simpleml.web.dto.AssociationDTO
import de.projektionisten.simpleml.web.exception.EmfException
//import de.projektionisten.simpleml.web.emf.^extension.Creation.EmfCreationHelper
//import de.projektionisten.simpleml.web.emf.^extension.Association.EmfAssociationHelper
//import de.projektionisten.simpleml.web.emf.^extension.Access.EmfPathHelper
//import de.projektionisten.simpleml.web.emf.^extension.Deletion.EmfDeletionHelper

class EmfServiceCollection {
	static def createRoot() {
		
	}
	
	static def createEntity(EObject astRoot, CreateEntityDTO createEntityDTO, String targetEntityPath) {
//		val createdEntity = EmfCreationHelper.createEntity(CreateEntityDTO.convert(createEntityDTO))
//		EmfAssociationHelper.createAssociationToRoot(astRoot, createdEntity)
//
//		if(targetEntityPath !== null) {
//			val targetEntity = EmfPathHelper.getEntityFromPath(astRoot, targetEntityPath)
//			EmfAssociationHelper.createAssociation(targetEntity, createdEntity)
//		}
	}
	
	static def deleteEntity(EObject astRoot, DeleteEntityDTO deleteEntityDTO) {
//		if(astRoot === null)
//			throw new EmfException
//		
//		//TODO: validate path possibly throw error
//		
//		val entity = EmfPathHelper.getEntityFromPath(astRoot, deleteEntityDTO.entityPath)
//		EmfDeletionHelper.delteEntity(entity)
	}
	
	static def createAssociation(EObject astRoot, AssociationDTO associationDTO) {
//		val sourceEntity = EmfPathHelper.getEntityFromPath(astRoot, associationDTO.source)	
//		val targetEntity = EmfPathHelper.getEntityFromPath(astRoot, associationDTO.target)	
//			
//		EmfAssociationHelper.createAssociation(sourceEntity, targetEntity)
	}
	
	static def deleteAssociation(EObject astRoot, AssociationDTO associationDTO) {
//		val sourceEntity = EmfPathHelper.getEntityFromPath(astRoot, associationDTO.source)	
//		val targetEntity = EmfPathHelper.getEntityFromPath(astRoot, associationDTO.target)	
//			
//		EmfAssociationHelper.deleteAssociation(sourceEntity, targetEntity)
	}	
}