package de.projektionisten.simpleml.web

import java.util.ArrayList
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
import org.eclipse.emf.common.util.URI
import org.emfjson.jackson.annotations.EcoreTypeInfo
import org.emfjson.jackson.module.EMFModule
import org.emfjson.jackson.resource.JsonResourceFactory
import org.emfjson.jackson.utils.ValueWriter
import de.unibonn.simpleml.web.SimpleMLResourceSetProvider
import de.unibonn.simpleml.utils.ModelExtensionsKt
import de.unibonn.simpleml.utils.QualifiedNameProvider
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlClass
import de.projektionisten.simpleml.web.dto.CreateAndAssociateEntityDTO
import de.projektionisten.simpleml.web.dto.DeleteEntityDTO
import de.projektionisten.simpleml.web.dto.AssociationDTO
import de.projektionisten.simpleml.web.emf.dto.ParameterDTO
import de.projektionisten.simpleml.web.emf.dto.ProcessMetadataDTO

@Singleton class EmfServiceDispatcher extends XtextServiceDispatcher {
	@Inject ISerializer serializer
	@Inject SimpleMLResourceSetProvider stdLibResourceSetProvider
	@Inject QualifiedNameProvider qualifiedNameProvider

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
			case 'getProcessMetadata':
				getProcessMetadata(context)	
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
		context.createDefaultGetServiceResult('')
	}
	
	protected def getProcessMetadata(IServiceContext context) {
		val resourceDocument = getResourceDocument(context.resourceID, context)
		val type = new TypeToken<ArrayList<String>>(){}.getType()
		val emfPathCollection = jsonConverter.fromJson(context.getParameter('entityPathCollection'), type) as ArrayList<String>
		val result = new ArrayList<ProcessMetadataDTO>();

		emfPathCollection.forEach[
			var entityName = ''
			var error = '';
			var entity = resourceDocument.resource.getEObject(it)
			val parameterMetadata = new ArrayList<ParameterDTO>()
			val resultMetadata = new ArrayList<ParameterDTO>()

			if(entity === null) {
				val resourceSet = stdLibResourceSetProvider.get(it, context)
				entity = resourceSet.getEObject(URI.createURI(it), true)
			}
			if(entity !== null) {
				switch entity {
					case entity instanceof SmlFunction: {
						ModelExtensionsKt.parametersOrEmpty((entity as SmlFunction)).forEach[
							parameterMetadata.add(new ParameterDTO(it.name, qualifiedNameProvider.qualifiedNameOrNull(it.type)))
						]
						ModelExtensionsKt.resultsOrEmpty((entity as SmlFunction)).forEach[
							resultMetadata.add(new ParameterDTO(it.name, qualifiedNameProvider.qualifiedNameOrNull(it.type)))
						]
						entityName = (entity as SmlFunction).name
					}
					case entity instanceof SmlParameter: {
						entityName = (entity as SmlParameter).name
					}
					case entity instanceof SmlClass: {
						entityName = (entity as SmlClass).name
					}
				}
				error = ''
				
			} else {
				error = 'Entity not found!'
			}
			result.add(new ProcessMetadataDTO(entityName, it, error, parameterMetadata, resultMetadata))
		]
		context.createDefaultPostServiceResult(jsonConverter.toJson(result))
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
		
		context.createDefaultPostServiceResult('')
	}
	
	protected def deleteEntity(IServiceContext context) {
		val type = new TypeToken<DeleteEntityDTO>(){}.getType()
		val deleteEntityDTO = jsonConverter.fromJson(context.getParameter("deleteEntityDTO"), type)
		val astRoot = getAstRoot(context)
		
		EmfServiceCollection.deleteEntity(astRoot, deleteEntityDTO)
		
		context.createDefaultPostServiceResult('')
	}
	
	protected def createAssociation(IServiceContext context) {
		val type = new TypeToken<AssociationDTO>(){}.getType()
		val associationDTO = jsonConverter.fromJson(context.getParameter("associationDTO"), type)
		val astRoot = getAstRoot(context)	

		EmfServiceCollection.createAssociation(astRoot, associationDTO)
			
		context.createDefaultPostServiceResult('')
	}
	
	protected def deleteAssociation(IServiceContext context) {
		val type = new TypeToken<AssociationDTO>(){}.getType()
		val associationDTO = jsonConverter.fromJson(context.getParameter("associationDTO"), type)
		val astRoot = getAstRoot(context)	

		EmfServiceCollection.deleteAssociation(astRoot, associationDTO)
			
		context.createDefaultPostServiceResult('')
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
	
	protected def createDefaultGetServiceResult(IServiceContext context, String data) {
		createDefaultServiceResult(context, data, false)
	}
		
	protected def createDefaultPostServiceResult(IServiceContext context, String data) {
		createDefaultServiceResult(context, data, true)
	}

	protected def createDefaultServiceResult(IServiceContext context, String data, boolean sideEffects) {
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
					data,
					resourceDocument.stateId,
					sideEffects)
			]
			hasSideEffects = sideEffects
		]
	}
}

	