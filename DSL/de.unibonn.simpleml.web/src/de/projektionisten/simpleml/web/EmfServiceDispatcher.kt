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
import de.unibonn.simpleml.utils.parametersOrEmpty
import de.unibonn.simpleml.utils.resultsOrEmpty
import de.unibonn.simpleml.utils.QualifiedNameProvider
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlClass
// import de.projektionisten.simpleml.web.dto.CreateAndAssociateEntityDTO
// import de.projektionisten.simpleml.web.dto.DeleteEntityDTO
// import de.projektionisten.simpleml.web.dto.AssociationDTO
import de.projektionisten.simpleml.web.dto.ParameterDTO
import de.projektionisten.simpleml.web.dto.ProcessMetadataDTO
import org.eclipse.emf.ecore.EObject


@Singleton 
class EmfServiceDispatcher @Inject constructor(
	private val serializer: ISerializer,
	private val stdLibResourceSetProvider: SimpleMLResourceSetProvider,
	private val qualifiedNameProvider: QualifiedNameProvider
): XtextServiceDispatcher() {

	private val jsonMapper: ObjectMapper
	private val jsonConverter: Gson = Gson()

	init {
		val mapper = ObjectMapper()
		val module = EMFModule()
		
			
		module.typeInfo = EcoreTypeInfo("className",
			object: ValueWriter<EClass, String> {
				override fun writeValue(value: EClass, context: SerializerProvider): String {
					return (value as EClassImpl).instanceClassName
				}
			}
		)		
		
		mapper.registerModule(module)

		val factory = JsonResourceFactory(mapper)
		this.jsonMapper = factory.getMapper()
	}
	

	protected override fun createServiceDescriptor(serviceType: String, context: IServiceContext): ServiceDescriptor {
		return when (serviceType) {
			"getEmfModel" -> 
				getEmfModel(context)
			"getProcessMetadata" ->
				getProcessMetadata(context)	
//			"getProcessProposals" ->
//				getProcessProposals(context)
//			"createEntity" ->
//				createEntity(context)
//			"deleteEntity" ->
//				deleteEntity(context)
//			"createAssociation" ->
//				createAssociation(context)
//			"deleteAssociation" ->
//				deleteAssociation(context)
			else ->
				super.createServiceDescriptor(serviceType, context)
		}
	}

	protected fun getAstRoot(context: IServiceContext): EObject {
		val resourceDocument = getResourceDocument(super.getResourceID(context), context)
		val astRoot = resourceDocument.resource.contents.get(0)
		return astRoot
	}
	
	protected fun getEmfModel(context: IServiceContext): ServiceDescriptor {
		return context.createDefaultGetServiceResult("")
	}

	protected fun getProcessMetadata(context: IServiceContext): ServiceDescriptor {
		val resourceDocument = getResourceDocument(super.getResourceID(context), context)
		val type = object: TypeToken<ArrayList<String>>(){}.getType()
		val emfPathCollection = jsonConverter.fromJson(context.getParameter("entityPathCollection"), type) as ArrayList<String>
		val result = ArrayList<ProcessMetadataDTO>();

		emfPathCollection.forEach {
			var entityName = ""
			var error = ""
			var entity = resourceDocument.resource.getEObject(it)
			val parameterMetadata = ArrayList<ParameterDTO>()
			val resultMetadata = ArrayList<ParameterDTO>()

			if(entity === null) {
				val resourceSet = stdLibResourceSetProvider.get(it, context)
				entity = resourceSet.getEObject(URI.createURI(it), true)
			}
			
			if(entity !== null) {
				when (entity) {
					is SmlFunction -> {
						(entity as SmlFunction?).parametersOrEmpty().forEach {
							parameterMetadata.add(ParameterDTO(it.name, qualifiedNameProvider.qualifiedNameOrNull(it.type)))
						}
						(entity as SmlFunction?).resultsOrEmpty().forEach {
							resultMetadata.add(ParameterDTO(it.name, qualifiedNameProvider.qualifiedNameOrNull(it.type)))
						}
						entityName = entity.name
					}
					is SmlParameter -> {
						entityName = entity.name
					}
					is SmlClass -> {
						entityName = entity.name
					}
				}
				error = ""
				
			} else {
				error = "Entity not found!"
			}
			result.add(ProcessMetadataDTO(entityName, it, error, parameterMetadata, resultMetadata))
		}
		return context.createDefaultPostServiceResult(jsonConverter.toJson(result))
	}

	protected fun XtextWebDocument.createErrorResult(text: String): ServiceDescriptor {
		val serviceDescriptor = ServiceDescriptor()
				
		serviceDescriptor.setService(fun(): EmfServiceResult {
				return EmfServiceResult(
					"",
					"",
					text,
					this.stateId,
					false)
		})
		serviceDescriptor.setHasSideEffects(false)

		return serviceDescriptor
	}

	protected fun IServiceContext.createDefaultGetServiceResult(data: String): ServiceDescriptor {
		return this.createDefaultServiceResult(data, false)
	}
		
	protected fun IServiceContext.createDefaultPostServiceResult(data: String): ServiceDescriptor {
		return this.createDefaultServiceResult(data, true)
	}

	protected fun IServiceContext.createDefaultServiceResult(data: String, sideEffects: Boolean): ServiceDescriptor {
		val resourceDocument = getResourceDocument(super.getResourceID(this), this)
		
		if(sideEffects) {
			try {		
				
				val astRoot = resourceDocument.resource.contents.get(0)
				resourceDocument.text = serializer.serialize(astRoot)
				getValidationService(this).service.apply()
			} catch(e: Exception) {
				return resourceDocument.createErrorResult("Serialization failed: " + e.message)
			}
		}
		val emfModel = jsonMapper.writeValueAsString(resourceDocument.resource)
		val serviceDescriptor = ServiceDescriptor()
				
		serviceDescriptor.setService(fun(): EmfServiceResult {
				return EmfServiceResult(
					resourceDocument.text,
					emfModel,
					data,
					resourceDocument.stateId,
					sideEffects)
		})
		serviceDescriptor.setHasSideEffects(sideEffects)

		return serviceDescriptor
	}
}
