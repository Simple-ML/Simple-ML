package de.projektionisten.simpleml.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Singleton
import de.projektionisten.simpleml.web.dto.CreateEntityDTO
import de.projektionisten.simpleml.web.dto.ParameterDTO
import de.projektionisten.simpleml.web.dto.ProcessMetadataDTO
import de.projektionisten.simpleml.web.dto.ProcessProposalsDTO
import de.projektionisten.simpleml.web.dto.EditProcessParameterDTO
import de.unibonn.simpleml.emf.createSmlImport
import de.unibonn.simpleml.emf.createSmlAssignment
import de.unibonn.simpleml.emf.createSmlPlaceholder
import de.unibonn.simpleml.emf.createSmlReference
import de.unibonn.simpleml.emf.createSmlCall
import de.unibonn.simpleml.emf.createSmlMemberAccess
import de.unibonn.simpleml.emf.createSmlArgument
import de.unibonn.simpleml.emf.isClassMember
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.stdlibAccess.descriptionOrNull
import de.unibonn.simpleml.ide.editor.contentassist.listCallablesWithMatchingParameters
import de.unibonn.simpleml.ide.editor.contentassist.listCallablesWithOnlyPrimitiveParameters
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.web.SimpleMLResourceSetProvider
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.xtext.serializer.ISerializer
import org.eclipse.xtext.web.server.IServiceContext
import org.eclipse.xtext.web.server.XtextServiceDispatcher
import org.eclipse.xtext.web.server.model.XtextWebDocument
import org.emfjson.jackson.annotations.EcoreTypeInfo
import org.emfjson.jackson.module.EMFModule
import org.emfjson.jackson.resource.JsonResourceFactory
import org.emfjson.jackson.utils.ValueWriter

@Singleton
class EmfServiceDispatcher @Inject constructor(
    private val serializer: ISerializer,
    private val stdLibResourceSetProvider: SimpleMLResourceSetProvider,
) : XtextServiceDispatcher() {

    private val jsonMapper: ObjectMapper
    private val jsonConverter: Gson = Gson()

    init {
        val mapper = ObjectMapper()
        val module = EMFModule()

        module.typeInfo = EcoreTypeInfo(
            "className",
            object : ValueWriter<EClass, String> {
                override fun writeValue(value: EClass, context: SerializerProvider): String {
                    return (value as EClassImpl).instanceClassName
                }
            }
        )

        mapper.registerModule(module)

        val factory = JsonResourceFactory(mapper)
        this.jsonMapper = factory.getMapper()
    }

    override fun createServiceDescriptor(serviceType: String, context: IServiceContext): ServiceDescriptor {
        return when (serviceType) {
            "getEmfModel" ->
                getEmfModel(context)
            "getProcessMetadata" ->
                getProcessMetadata(context)
            "getProcessProposals" ->
                getProcessProposals(context)
            "createEntity" ->
                createEntity(context)
            "editProcessParameter" ->
                editProcessParameter(context)

// 			"deleteEntity" ->
// 				deleteEntity(context)
// 			"createAssociation" ->
// 				createAssociation(context)
// 			"deleteAssociation" ->
// 				deleteAssociation(context)
            else ->
                super.createServiceDescriptor(serviceType, context)
        }
    }

    private fun getEmfModel(context: IServiceContext): ServiceDescriptor {
        getValidationService(context).service.apply()
        return context.createDefaultGetServiceResult("")
    }

    private fun getProcessMetadata(context: IServiceContext): ServiceDescriptor {
        val type = object : TypeToken<ArrayList<String>>() {}.getType()
        val emfPathCollection =
            jsonConverter.fromJson(context.getParameter("entityPathCollection"), type) as ArrayList<String>
        val result = ArrayList<ProcessMetadataDTO>()

        emfPathCollection.forEach {
            result.add(getProcessMetadataFromURI(it, context))
        }
        return context.createDefaultPostServiceResult(jsonConverter.toJson(result))
    }

    private fun getProcessProposals(context: IServiceContext): ServiceDescriptor {
        // getResourceDocument uses the FileResourceHandler, which already uses the SimpleMLResourceSetProvider internally
        val resourceDocument = getResourceDocument(this.getResourceID(context), context)
        if (resourceDocument == null) {
            // TODO: handle error in a better way; should only happen when the server is restarted but the browser is not refreshed
            throw IllegalArgumentException("Resource document not found")
        }

        val frontendId = context.getParameter("frontendId")
        val emfPath = context.getParameter("entityPath")
        val emfEntity = getEmfEntityByPath(resourceDocument, emfPath)

        if (emfEntity !is SmlAbstractDeclaration?) {
            // TODO: handle error in a better way
            throw IllegalArgumentException("EMF path must point to an SmlDeclaration")
        }

        val astRoot = resourceDocument.resource.contents[0]
        val proposals = when (emfEntity) {
            null -> listCallablesWithOnlyPrimitiveParameters(astRoot)
            else -> listCallablesWithMatchingParameters(astRoot, listOf(emfEntity))
        }
        val result = proposals.map { getProcessMetadataFromURI(it.key.toString(), context) }

        return context.createDefaultPostServiceResult(
            jsonConverter.toJson(
                ProcessProposalsDTO(
                    frontendId,
                    emfPath,
                    result
                )
            )
        )
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

    private fun createEntity(context: IServiceContext): ServiceDescriptor {
        val resourceDocument = getResourceDocument(super.getResourceID(context), context)
        val astRoot = resourceDocument.resource.contents.get(0) as SmlCompilationUnit
        val type = object : TypeToken<CreateEntityDTO>() {}.getType()
        val createEntityDTO = jsonConverter.fromJson(context.getParameter("createEntityDTO"), type) as CreateEntityDTO

        val resourceStdLib = stdLibResourceSetProvider.get(createEntityDTO.referenceIfFunktion, context)
        val functionRef = resourceStdLib.getEObject(URI.createURI(createEntityDTO.referenceIfFunktion), true)

        val assignment = when {
            createEntityDTO.associationTargetPath == "" -> {
                createSmlAssignment(
                    listOf(createSmlPlaceholder(createEntityDTO.placeholderName)),
                    createSmlCall(
                        createSmlReference(functionRef as SmlAbstractDeclaration),
                        arguments = listOf()
                    )
                )
            }
            functionRef is SmlFunction && functionRef.isClassMember() -> {
                createSmlAssignment(
                    listOf(createSmlPlaceholder(createEntityDTO.placeholderName)),
                    createSmlCall(
                        createSmlMemberAccess(
                            createSmlReference(getEmfEntityByPath(resourceDocument, createEntityDTO.associationTargetPath) as SmlAbstractDeclaration),
                            createSmlReference(functionRef as SmlAbstractDeclaration)
                        )
                    )
                )
            } 
            else -> {
                createSmlAssignment(
                    listOf(createSmlPlaceholder(createEntityDTO.placeholderName)),
                    createSmlCall(
                        createSmlReference(functionRef as SmlAbstractDeclaration),
                        arguments = listOf(
                            createSmlArgument(
                                createSmlReference(getEmfEntityByPath(resourceDocument, createEntityDTO.associationTargetPath) as SmlAbstractDeclaration)
                            )
                        )
                    )
                )
            }
        }

        // attach to root-node
        (astRoot.members[0] as SmlWorkflow).body.statements.add(assignment)

        // create import-statement if necessary
        val importExists = astRoot.imports.any {
            it.importedNamespace == functionRef.qualifiedNameOrNull().toString()
        }
        if (!importExists && !functionRef.isClassMember()) {
            astRoot.imports += createSmlImport(
                importedNamespace = functionRef.qualifiedNameOrNull().toString()
            )
        }

        return context.createDefaultPostServiceResult("")
    }

    private fun getProcessMetadataFromURI(uri: String, serviceContext: IServiceContext): ProcessMetadataDTO {
        val resourceDocument = getResourceDocument(super.getResourceID(serviceContext), serviceContext)
        var entityName = ""
        var description: String? = ""
        var error: String
        var entity = resourceDocument.resource.getEObject(uri)
        val parameterMetadata = ArrayList<ParameterDTO>()
        val resultMetadata = ArrayList<ParameterDTO>()

        if (entity === null) {
            val resourceSet = stdLibResourceSetProvider.get(uri, serviceContext)
            entity = resourceSet.getEObject(URI.createURI(uri), true)
        }

        if (entity !== null) {
            when (entity) {
                is SmlFunction -> {
                    (entity as SmlFunction?).parametersOrEmpty().forEach {
                        parameterMetadata.add(ParameterDTO(it.name, it.type.qualifiedNameOrNull()))
                    }
                    (entity as SmlFunction?).resultsOrEmpty().forEach {
                        resultMetadata.add(ParameterDTO(it.name, it.type.qualifiedNameOrNull()))
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
            description = (entity as? SmlAbstractDeclaration)?.descriptionOrNull() 
            error = ""
        } else {
            error = "Entity not found!"
        }
        return ProcessMetadataDTO(entityName, uri, error, parameterMetadata, resultMetadata, description)
    }

    private fun editProcessParameter(context: IServiceContext): ServiceDescriptor {
		val resourceDocument = getResourceDocument(super.getResourceID(context), context)
		val type = object: TypeToken<EditProcessParameterDTO>(){}.getType()
		val editProcessParameterDTO = jsonConverter.fromJson(context.getParameter("editProcessParameterDTO"), type) as EditProcessParameterDTO		
		val target = getEmfEntityByPath(resourceDocument, editProcessParameterDTO.entityPath) as SmlCall?

		if(target != null) {
			val argumentList = target.argumentList
			var argument: SmlArgument
			var valueContainer = SimpleMLFactory.eINSTANCE.createSmlString()
			val indexAndSizeDiff = editProcessParameterDTO.parameterIndex.toInt() - target.argumentList.arguments.size

			if(indexAndSizeDiff >= 0) {
				for(i in 0..indexAndSizeDiff) {
					argument = SimpleMLFactory.eINSTANCE.createSmlArgument()
					argument.value = SimpleMLFactory.eINSTANCE.createSmlNull()
					argumentList.arguments.add(argument)
				}
			}
			
			valueContainer.value = editProcessParameterDTO.value
			argument = SimpleMLFactory.eINSTANCE.createSmlArgument()
			argument.value = valueContainer
			argumentList.arguments.set(editProcessParameterDTO.parameterIndex.toInt(), argument);
		}
		return context.createDefaultPostServiceResult("")
	}


    private fun SmlAbstractType.qualifiedNameOrNull(): String? {
        return (this as? SmlNamedType)?.declaration?.qualifiedNameOrNull()?.toString()
    }

    private fun IServiceContext.createDefaultGetServiceResult(data: String): ServiceDescriptor {
        return this.createDefaultServiceResult(data, false)
    }

    private fun IServiceContext.createDefaultPostServiceResult(data: String): ServiceDescriptor {
        return this.createDefaultServiceResult(data, true)
    }

    private fun IServiceContext.createDefaultServiceResult(data: String, sideEffects: Boolean): ServiceDescriptor {
        val resourceDocument = getResourceDocument(super.getResourceID(this), this)
        val emfModel = jsonMapper.writeValueAsString(resourceDocument.resource)
        val serviceDescriptor = ServiceDescriptor()
        var error = ""

        if (sideEffects) {
            try {
                val astRoot = resourceDocument.resource.contents.get(0)
                resourceDocument.text = serializer.serialize(astRoot)
                getValidationService(this).service.apply()
            } catch (e: Exception) {
                error = "Serialization failed: " + e.message
            }
        }

        serviceDescriptor.setService(fun(): EmfServiceResult {
            return EmfServiceResult(
                resourceDocument.text,
                emfModel,
                data,
                error,
                resourceDocument.stateId,
                sideEffects
            )
        })
        serviceDescriptor.setHasSideEffects(sideEffects)

        return serviceDescriptor
    }
}
