package de.unibonn.simpleml.tools

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtext.validation.CheckMode
import org.eclipse.xtext.validation.IResourceValidator
import org.eclipse.xtext.validation.Issue

class ASTInspector @Inject constructor(
        private val resourceSetProvider: Provider<ResourceSet>,
        private val stdlib: SimpleMLStdlib,
        private val validator: IResourceValidator
) {

    fun inspectAST(file: String): String {

        // Load resource and library
        val set = resourceSetProvider.get()
        stdlib.load(set)

        val resource = set.getResource(URI.createFileURI(file), true)
        require(resource != null) { "Could not create resource for $file." }
        require(resource.contents.isNotEmpty()) { "Resource for $file is empty." }

        // Check for syntax errors
        val issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl)
        require(issues.none { it.isSyntaxError }) { syntaxErrorMessage(issues) }

        // Configure and start the visitor
        val compilationUnit = resource.contents[0] as SmlCompilationUnit
        return stringify(compilationUnit)
    }

    private fun syntaxErrorMessage(issues: List<Issue>) = buildString {
        appendLine("Resource has syntax errors:")
        issues.filter { it.isSyntaxError }.forEach {
            appendLine("  * $it")
        }
    }

    private fun stringify(root: EObject): String = buildString {
        appendLine(root.toString())

        if (root.eContents().isNotEmpty()) {
            appendLine("Children")
            root.eContents().forEach {
                appendLine(stringify(it).trim().prependIndent("  \u2502"))
                if (it == root.eContents().last()) {
                    append("  \u2514")
                } else {
                    append("  \u251C")
                }
                appendLine("\u2500".repeat(79))
            }
        }

        if (root.eCrossReferences().isNotEmpty()) {
            appendLine("Cross-references")
            root.eCrossReferences().forEach {
                appendLine(it.toString().prependIndent("  "))
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Aborting: no path to EMF resource provided!")
        return
    }

    val injector = SimpleMLStandaloneSetup().createInjectorAndDoEMFRegistration()
    val astInspector = injector.getInstance(ASTInspector::class.java)
    println(astInspector.inspectAST(args[0]))
}