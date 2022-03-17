package de.unibonn.simpleml.stdlibDocumentation

import de.unibonn.simpleml.emf.containingCompilationUnitOrNull
import de.unibonn.simpleml.scoping.allGlobalDeclarations
import org.eclipse.emf.ecore.EObject
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

private val autogenWarning = """
    |----------
    |
    |**This file was created automatically. Do not change it manually!**
""".trimMargin()

/**
 * Generates documentation for all declarations that are visible from the given context.
 *
 * @receiver The context.
 * @param outputDirectory Where to place the created files.
 */
fun EObject.generateDocumentation(outputDirectory: Path) {
    outputDirectory.createDirectories()

    val packagesToDeclarations: Map<String, List<EObject>> = allGlobalDeclarations()
        .map { it.eObjectOrProxy }
        .filter { it.containingCompilationUnitOrNull()?.name != null }
        .groupBy { it.containingCompilationUnitOrNull()!!.name }

    createReadme(outputDirectory, packagesToDeclarations)
    createPackageFiles(outputDirectory, packagesToDeclarations)
}

private fun createReadme(outputDirectory: Path, packagesToDeclarations: Map<String, List<EObject>>) {
    val packageDescriptions = packagesToDeclarations.keys
        .sorted()
        .joinToString(separator = "\n") { packageName ->
            "* [$packageName](./${packageName.replace(".", "_")}.md)"
        }

    outputDirectory.resolve("README.md").writeText(
        """
            |# Simple-ML API Documentation
            |
            |$packageDescriptions
            |
            |$autogenWarning
        """.trimMargin()
    )
}

private fun createPackageFiles(outputDirectory: Path, packagesToDeclarations: Map<String, List<EObject>>) {
    packagesToDeclarations.forEach { (packageName, globalDeclarations) ->
        createPackageFile(outputDirectory, packageName, globalDeclarations)
    }
}

private fun createPackageFile(outputDirectory: Path, packageName: String, globalDeclarations: List<EObject>) {
    outputDirectory.resolve("${packageName.replace(".", "_")}.md").writeText(
        """
            |# Package `$packageName`
            |
            |$autogenWarning
        """.trimMargin()
    )
}
