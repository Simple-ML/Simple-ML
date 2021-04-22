package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.prolog_bridge.Main
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import org.eclipse.core.runtime.FileLocator
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.resource.IResourceDescription
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths


private const val LIB_PACKAGE = "simpleml.lang"
const val LIB_ANY = "${LIB_PACKAGE}.Any"
const val LIB_BOOLEAN = "${LIB_PACKAGE}.Boolean"
const val LIB_FLOAT = "${LIB_PACKAGE}.Float"
const val LIB_INT = "${LIB_PACKAGE}.Int"
const val LIB_STRING = "${LIB_PACKAGE}.String"

private val factory = SimpleMLFactory.eINSTANCE

object BuiltinClasses {

    /**
     * open class Any
     */
    val Any: SmlClass = factory.createSmlClass().apply {
        modifiers += SML_OPEN
        name = "Any"
    }

    /**
     * class Boolean
     */
    val Boolean: SmlClass = factory.createSmlClass().apply {
        name = "Boolean"
    }

    /**
     * open class Number
     */
    val Number: SmlClass = factory.createSmlClass().apply {
        modifiers += SML_OPEN
        name = "Number"
    }

    /**
     * class Int sub Number
     */
    val Int: SmlClass = factory.createSmlClass().apply {
        name = "Int"
        parentTypeList = factory.createSmlParentTypeList().apply {
            parentTypes += factory.createSmlNamedType().apply {
                declaration = Number
            }
        }
    }

    /**
     * class Float sub Number
     */
    val Float: SmlClass = factory.createSmlClass().apply {
        name = "Float"
        parentTypeList = factory.createSmlParentTypeList().apply {
            parentTypes += factory.createSmlNamedType().apply {
                declaration = Number
            }
        }
    }

    /**
     * class String
     */
    val String: SmlClass = factory.createSmlClass().apply {
        name = "String"
    }

    /**
     * class Nothing
     */
    val Nothing: SmlClass = factory.createSmlClass().apply {
        name = "Nothing"
    }
}

class SimpleMLStdlib @Inject constructor(
    private val indexExtensions: SimpleMLIndexExtensions
) {

    private val cache = mutableMapOf<String, SmlClass?>()

    fun getClass(context: EObject, qualifiedName: String): SmlClass? {
//        return cache.computeIfAbsent(qualifiedName) {
//            val description = indexExtensions.visibleGlobalDeclarationDescriptions(context)
//                    .find { it.qualifiedName.toString() == qualifiedName }
//                    ?: return@computeIfAbsent null
//
//            var eObject = description.eObjectOrProxy
//            if (eObject != null && eObject.eIsProxy()) {
//                eObject = context.eResource().resourceSet.getEObject(description.eObjectURI, true)
//            }
//
//            eObject as? SmlClass
//        }

        return when (qualifiedName) {
            LIB_ANY -> BuiltinClasses.Any
            LIB_BOOLEAN -> BuiltinClasses.Boolean
            LIB_INT -> BuiltinClasses.Int
            LIB_FLOAT -> BuiltinClasses.Float
            LIB_STRING -> BuiltinClasses.String
            else -> null
        }
    }

    fun load(resourceSet: ResourceSet) {
        val resourcesUrl = javaClass.classLoader.getResource("simpleml") ?: return
        val resourcesUri = FileLocator.resolve(resourcesUrl).toURI()

        var fileSystem: FileSystem? = null
        val stdlibBase = when (resourcesUri.scheme) {

            // Without this code Maven tests fail with a FileSystemNotFoundException since stdlib resources are in a jar
            "jar" -> {
                fileSystem = FileSystems.newFileSystem(resourcesUri, emptyMap<String, String>(), null)
                fileSystem.getPath("simpleml")
            }
            else -> Paths.get(resourcesUri)
        }

        Files.walk(stdlibBase)
            .filter { it.toString().endsWith(".stub.simpleml") }
            .forEach {
                resourceSet.createResource(URI.createFileURI(it.toString()))
                    .load(Files.newInputStream(it), resourceSet.loadOptions)
            }

        val builtin = resourceSet.createResource(URI.createURI("dummy:/simpleml/builtin.stub.simpleml"))
        builtin.contents += builtinCompilationUnit()

        fileSystem?.close()
    }

    fun builtinCompilationUnit(): SmlCompilationUnit {
        return factory.createSmlCompilationUnit().apply {
            name = "simpleml.builtin"
            members += listOf(
                BuiltinClasses.Any,
                BuiltinClasses.Boolean,
                BuiltinClasses.Number,
                BuiltinClasses.Int,
                BuiltinClasses.Float,
                BuiltinClasses.String,
                BuiltinClasses.Nothing
            )
        }
    }
}
