package de.projektionisten.simpleml.web

import java.util.ArrayList
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Singleton
import org.eclipse.emf.ecore.EObject
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
import de.unibonn.simpleml.utils.Proposals
import de.unibonn.simpleml.utils.containingCompilationUnitOrNull
import de.unibonn.simpleml.simpleML.*
import de.projektionisten.simpleml.web.dto.CreateEntityDTO
import de.projektionisten.simpleml.web.dto.ParameterDTO
import de.projektionisten.simpleml.web.dto.ProcessMetadataDTO
import de.projektionisten.simpleml.web.dto.ProcessProposalsDTO
import org.eclipse.xtext.web.server.persistence.IServerResourceHandler
import java.lang.IllegalArgumentException


@Singleton
class EmfServiceDispatcher @Inject constructor(
	private val serializer: ISerializer,
	private val stdLibResourceSetProvider: SimpleMLResourceSetProvider,
	private val qualifiedNameProvider: QualifiedNameProvider,
	private val proposals: Proposals
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
			"getProcessProposals" ->
				getProcessProposals(context)
			"createEntity" ->
				createEntity(context)
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



	protected fun getEmfModel(context: IServiceContext): ServiceDescriptor {
		return context.createDefaultGetServiceResult("")
	}

	protected fun getProcessMetadata(context: IServiceContext): ServiceDescriptor {
		val type = object: TypeToken<ArrayList<String>>(){}.getType()
		val emfPathCollection = jsonConverter.fromJson(context.getParameter("entityPathCollection"), type) as ArrayList<String>
		val result = ArrayList<ProcessMetadataDTO>();

		emfPathCollection.forEach {
			result.add(getProcessMetadataFromURI(it, context))
		}
		return context.createDefaultPostServiceResult(jsonConverter.toJson(result))
	}

	protected fun getProcessProposals(context: IServiceContext): ServiceDescriptor {
		// getResourceDocument uses the FileResourceHandler, which already uses the SimpleMLResourceSetProvider internally
		val resourceDocument = getResourceDocument(this.getResourceID(context), context)
		if (resourceDocument == null) {
			// TODO: handle error in a better way; should only happen when the server is restarted but the browser is not refreshed
			throw IllegalArgumentException("Resource document not found")
		}

		val frontendId = context.getParameter("frontendId")
		val emfPath = context.getParameter("entityPath")
		println(emfPath)
		val emfEntity = getEmfEntityByPath(resourceDocument, emfPath)
		println(emfEntity)

		if (emfEntity !is SmlDeclaration?) {
			// TODO: handle error in a better way
			throw IllegalArgumentException("EMF path must point to an SmlDeclaration")
		}

		val astRoot = resourceDocument.resource.contents[0]
		val proposals = proposals.listCallables(astRoot, emfEntity)
		val result = proposals.map { getProcessMetadataFromURI(it.key.toString(), context) }

		return context.createDefaultPostServiceResult(jsonConverter.toJson(ProcessProposalsDTO(frontendId, emfPath, result)))
	}

	private fun getEmfEntityByPath(resourceDocument: XtextWebDocument, emfPath: String?): EObject? {
		if (emfPath.isNullOrEmpty()) {
			return null
		}

		val localEmfObject = resourceDocument.resource.getEObject(emfPath)
		if (localEmfObject != null) {
			return localEmfObject
		}

		return resourceDocument.resource.resourceSet.getEObject(URI.createURI(emfPath), false)
	}

	protected fun createEntity(context: IServiceContext): ServiceDescriptor {
		val resourceDocument = getResourceDocument(super.getResourceID(context), context)
		val astRoot = resourceDocument.resource.contents.get(0) as SmlCompilationUnit
		val type = object: TypeToken<CreateEntityDTO>(){}.getType()
		val createEntityDTO = jsonConverter.fromJson(context.getParameter("createEntityDTO"), type) as CreateEntityDTO

		val resourceStdLib = stdLibResourceSetProvider.get(createEntityDTO.referenceIfFunktion, context)
		val functionRef = resourceStdLib.getEObject(URI.createURI(createEntityDTO.referenceIfFunktion), true)

		if(createEntityDTO.associationTargetPath.isNullOrEmpty()) {
			val assignment = SimpleMLFactory.eINSTANCE.createSmlAssignment()
			val assigneeList = SimpleMLFactory.eINSTANCE.createSmlAssigneeList()
			val placeholder = SimpleMLFactory.eINSTANCE.createSmlPlaceholder()
			val call = SimpleMLFactory.eINSTANCE.createSmlCall()
			val argumentList = SimpleMLFactory.eINSTANCE.createSmlArgumentList()
			val reference = SimpleMLFactory.eINSTANCE.createSmlReference()
			val import = SimpleMLFactory.eINSTANCE.createSmlImport()
			var importExists = false

			when(functionRef) {
				is SmlFunction -> {
					reference.declaration = functionRef as SmlFunction
				}
				is SmlClass -> {
					reference.declaration = functionRef as SmlClass
				}
			}

			// create entity
			call.receiver = reference
			call.argumentList = argumentList

			placeholder.name = createEntityDTO.placeholderName
			assigneeList.assignees.add(placeholder)

			assignment.expression = call
			assignment.assigneeList = assigneeList

			(astRoot.members[0] as SmlWorkflow).body.statements.add(assignment)

			// create import-statement if necessary
			astRoot.imports.forEach {
				if(it.importedNamespace == reference.declaration.containingCompilationUnitOrNull()?.name + "." + reference.declaration.name) {
					importExists = true
				}
			}
			if(!importExists) {
				import.importedNamespace = reference.declaration.containingCompilationUnitOrNull()?.name + "." + reference.declaration.name
				astRoot.imports.add(import)
			}
		} else {

		}

		return context.createDefaultPostServiceResult("")
	}





	private fun getProcessMetadataFromURI(uri: String, serviceContext: IServiceContext): ProcessMetadataDTO {
		val resourceDocument = getResourceDocument(super.getResourceID(serviceContext), serviceContext)
        var entityName = ""
        var error = ""
        var entity = resourceDocument.resource.getEObject(uri)
        val parameterMetadata = ArrayList<ParameterDTO>()
        val resultMetadata = ArrayList<ParameterDTO>()

        if(entity === null) {
            val resourceSet = stdLibResourceSetProvider.get(uri, serviceContext)
            entity = resourceSet.getEObject(URI.createURI(uri), true)
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
        return ProcessMetadataDTO(entityName, uri, error, parameterMetadata, resultMetadata)
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
