package de.projektionisten.simpleml.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Singleton
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.xtext.serializer.ISerializer
import org.eclipse.xtext.web.server.IServiceContext
import org.eclipse.xtext.web.server.XtextServiceDispatcher
import org.eclipse.xtext.web.server.model.XtextWebDocument
import org.emfjson.jackson.annotations.EcoreTypeInfo
import org.emfjson.jackson.module.EMFModule
import org.emfjson.jackson.resource.JsonResourceFactory
import org.emfjson.jackson.utils.ValueWriter
import de.projektionisten.simpleml.web.dto.CreateAndAssociateEntityDTO
import de.projektionisten.simpleml.web.dto.DeleteEntityDTO
import de.projektionisten.simpleml.web.dto.AssociationDTO

@Singleton class EmfServiceDispatcher extends XtextServiceDispatcher {
	@Inject ISerializer serializer
	
	ObjectMapper jsonMapper
	Gson jsonConverter
	
	new() {
		val mapper = new ObjectMapper()
		val module = new EMFModule()
		
		module.typeInfo = new EcoreTypeInfo('className',
			new ValueWriter<EClass, String>() {
				override writeValue(EClass value, SerializerProvider context) {
					return (value as EClassImpl).instanceClassName
				}
			}
		)		
		
		mapper.registerModule(module)

		val factory = new JsonResourceFactory(mapper)
		jsonMapper = factory.getMapper()
		jsonConverter = new Gson()
	}
	
	protected override ServiceDescriptor createServiceDescriptor(String serviceType, IServiceContext context) {
		switch serviceType {
			case 'getEmfModel': 
				getEmfModel(context)
			case 'getProcessProposals':
				getProcessProposals(context)
			case 'createEntity':
				createEntity(context)
			case 'deleteEntity':
				deleteEntity(context)
			case 'createAssociation':
				createAssociation(context)
			case 'deleteAssociation':
				deleteAssociation(context)
			default:
				super.createServiceDescriptor(serviceType, context)
		}
	}

	protected def getAstRoot(IServiceContext context) {
		val resourceDocument = getResourceDocument(context.resourceID, context)
		val astRoot = resourceDocument.resource.contents.get(0)
		astRoot
	}
	
	protected def getEmfModel(IServiceContext context) {
		context.createStandardGetServiceResult
	}
	
	protected def getProcessProposals(IServiceContext context) {
		
		val resourceDocument = getResourceDocument(context.resourceID, context)
//		val processDefinitions = processDefinitions.processes
		// TODO: get Processdefinitions

		new ServiceDescriptor => [
			service = [
				new EmfServiceResult(
//					jsonConverter.toJson(processDefinitions.values),
					"",
					null,
					null,
					resourceDocument.stateId,
					false)
			]
			hasSideEffects = false
		]
	}
	
	protected def createEntity(IServiceContext context) {
		val type = new TypeToken<CreateAndAssociateEntityDTO>(){}.getType()
		val createAndAssociateEntityDTO = jsonConverter.fromJson(context.getParameter("createEntityDTO"), type) as CreateAndAssociateEntityDTO
		val astRoot = getAstRoot(context)
		
		EmfServiceCollection.createEntity(astRoot, createAndAssociateEntityDTO.entity, createAndAssociateEntityDTO.target)
		
		context.createStandardPostServiceResult
	}
	
	protected def deleteEntity(IServiceContext context) {
		val type = new TypeToken<DeleteEntityDTO>(){}.getType()
		val deleteEntityDTO = jsonConverter.fromJson(context.getParameter("deleteEntityDTO"), type)
		val astRoot = getAstRoot(context)
		
		EmfServiceCollection.deleteEntity(astRoot, deleteEntityDTO)
		
		context.createStandardPostServiceResult
	}
	
	protected def createAssociation(IServiceContext context) {
		val type = new TypeToken<AssociationDTO>(){}.getType()
		val associationDTO = jsonConverter.fromJson(context.getParameter("associationDTO"), type)
		val astRoot = getAstRoot(context)	

		EmfServiceCollection.createAssociation(astRoot, associationDTO)
			
		context.createStandardPostServiceResult
	}
	
	protected def deleteAssociation(IServiceContext context) {
		val type = new TypeToken<AssociationDTO>(){}.getType()
		val associationDTO = jsonConverter.fromJson(context.getParameter("associationDTO"), type)
		val astRoot = getAstRoot(context)	

		EmfServiceCollection.deleteAssociation(astRoot, associationDTO)
			
		context.createStandardPostServiceResult
	}
	
	
	//TODO: put in own class
	protected def createInfoResult(XtextWebDocument resourceDocument, String text) {
		new ServiceDescriptor => [
			service = [
				new EmfServiceResult(
					null,
					null,
					text,
					resourceDocument.stateId,
					false)
			]
			hasSideEffects = false
		]
	}
	
	protected def createErrorResult(XtextWebDocument resourceDocument, String text) {
		new ServiceDescriptor => [
			service = [
				new EmfServiceResult(
					null,
					null,
					text,
					resourceDocument.stateId,
					false)
			]
			hasSideEffects = false
		]
	}
	
	protected def createStandardGetServiceResult(IServiceContext context) {
		createStandardServiceResult(context, false)
	}
		
	protected def createStandardPostServiceResult(IServiceContext context) {
		createStandardServiceResult(context, true)
	}

	protected def createStandardServiceResult(IServiceContext context, boolean sideEffects) {
		val resourceDocument = getResourceDocument(context.resourceID, context)
		
		if(sideEffects) {
			try {		
				
				val astRoot = resourceDocument.resource.contents.get(0)
				resourceDocument.text = serializer.serialize(astRoot)
				context.validationService.service.apply
			} catch(Exception e) {
				return resourceDocument.createErrorResult("Serialization failed: " + e.message)
			}
		}
		val emfModel = jsonMapper.writeValueAsString(resourceDocument.resource)
		new ServiceDescriptor => [
			service = [
				new EmfServiceResult(
					resourceDocument.text,
					emfModel,
					null,
					resourceDocument.stateId,
					sideEffects)
			]
			hasSideEffects = sideEffects
		]
	}
}

	