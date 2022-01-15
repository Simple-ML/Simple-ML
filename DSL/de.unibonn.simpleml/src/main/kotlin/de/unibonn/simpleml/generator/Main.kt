package de.unibonn.simpleml.generator

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.stdlibAccess.StdlibAccess
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.generator.GeneratorContext
import org.eclipse.xtext.generator.GeneratorDelegate
import org.eclipse.xtext.generator.JavaIoFileSystemAccess
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtext.validation.CheckMode
import org.eclipse.xtext.validation.IResourceValidator
import java.lang.IllegalArgumentException

@Suppress("unused")
class Main @Inject constructor(
    private val fileAccess: JavaIoFileSystemAccess,
    private val generator: GeneratorDelegate,
    private val resourceSetProvider: Provider<ResourceSet>,
    private val stdlib: StdlibAccess,
    private val validator: IResourceValidator
) {

    fun runGenerator(files: List<String>) {

        // Load the resources
        val set = resourceSetProvider.get()
        files.forEach {
            set.getResource(URI.createFileURI(it), true)
                ?: throw IllegalArgumentException("Could not create resource for $it.")
        }

        // Load the library
        stdlib.load(set)

        // Configure the generator
        fileAccess.setOutputPath("src-gen/")
        val context = GeneratorContext().apply {
            cancelIndicator = CancelIndicator.NullImpl
        }

        // Generate all resources
        set.resources.forEach { resource ->

            // Validate the resource
            val issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl)
            if (issues.any { it.severity == Severity.ERROR }) {
                issues.forEach { println(it) }
                return
            }

            // Start the generator
            generator.generate(resource, fileAccess, context)
        }

        println("Code generation finished.")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Aborting: no path to EMF resource provided!")
        return
    }
    val injector = SimpleMLStandaloneSetup().createInjectorAndDoEMFRegistration()
    val main = injector.getInstance(Main::class.java)
    main.runGenerator(args.toList())
}
