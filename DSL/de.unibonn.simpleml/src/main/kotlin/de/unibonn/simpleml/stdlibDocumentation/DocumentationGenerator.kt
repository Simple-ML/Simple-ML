package de.unibonn.simpleml.stdlibDocumentation

import de.unibonn.simpleml.emf.containingCompilationUnitOrNull
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.scoping.allGlobalDeclarations
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.stdlibAccess.descriptionOrNull
import de.unibonn.simpleml.stdlibAccess.validTargets
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
    val packagesDocumentation = packagesToDeclarations.keys
        .sorted()
        .joinToString(separator = "\n") { packageName ->
            "* [$packageName](./${packageName.replace(".", "_")}.md)"
        }

    outputDirectory.resolve("README.md").writeText(
        """
            |# Simple-ML API Documentation
            |
            |$packagesDocumentation
            |
            |$autogenWarning
            |
        """.trimMargin()
    )
}

private fun createPackageFiles(outputDirectory: Path, packagesToDeclarations: Map<String, List<EObject>>) {
    packagesToDeclarations.forEach { (packageName, globalDeclarations) ->
        createPackageFile(outputDirectory, packageName, globalDeclarations)
    }
}

private fun createPackageFile(outputDirectory: Path, packageName: String, globalDeclarations: List<EObject>) {
    outputDirectory
        .resolve("${packageName.replace(".", "_")}.md")
        .writeText(createPackageDocumentation(packageName, globalDeclarations))
}

private fun createPackageDocumentation(
    packageName: String,
    globalDeclarations: List<EObject>
) = buildString {
    val classes = globalDeclarations.filterIsInstance<SmlClass>().sortedBy { it.name }
    val globalFunctions = globalDeclarations.filterIsInstance<SmlFunction>().sortedBy { it.name }
    val enums = globalDeclarations.filterIsInstance<SmlEnum>().sortedBy { it.name }
    val annotations = globalDeclarations.filterIsInstance<SmlAnnotation>().sortedBy { it.name }

    appendLine("# Package `$packageName`")

    // Table of contents
    if (annotations.isNotEmpty() || classes.isNotEmpty() || enums.isNotEmpty() || globalFunctions.isNotEmpty()) {
        appendLine("\n## Table of Contents\n")

        classes.forEach {
            appendLine("* [Class `${it.name}`](#class-${it.name})")
        }
        globalFunctions.forEach {
            appendLine("* [Global function `${it.name}`](#global-function-${it.name})")
        }
        enums.forEach {
            appendLine("* [Enum `${it.name}`](#enum-${it.name})")
        }
        annotations.forEach {
            appendLine("* [Annotation `${it.name}`](#annotation-${it.name})")
        }
    }

    // Classes
    classes.forEach {
        appendLine(createClassDocumentation(it, nestingLevel = 2))
    }

    // Global functions
    globalFunctions.forEach {
        appendLine(createFunctionDocumentation(it, nestingLevel = 2))
    }

    // Enums
    enums.forEach {
        appendLine(createEnumDocumentation(it, nestingLevel = 2))
    }

    // Annotations
    annotations.forEach {
        appendLine(createAnnotationDocumentation(it))
    }

    appendLine("\n$autogenWarning")
}

private fun createAnnotationDocumentation(annotation: SmlAnnotation) = buildString {
    appendLine("## Annotation `${annotation.name}`")

    // Description
    if (annotation.descriptionOrNull() != null) {
        appendLine(annotation.descriptionOrNull())
    } else {
        appendLine("_No description available._")
    }

    // Parameters
    append(createParametersDocumentation(annotation.parametersOrEmpty()))

    // Targets
    appendLine("\n**Valid targets:**")
    val validTargets = annotation
        .validTargets()
        .sortedBy { it.name }
        .joinToString(separator = "\n") {
            "* ${it.name}"
        }
    append(validTargets)
}

private fun createClassDocumentation(`class`: SmlClass, nestingLevel: Int): String {
    return ""
}

private fun createEnumDocumentation(enum: SmlEnum, nestingLevel: Int): String {
    return ""
}

private fun createFunctionDocumentation(function: SmlFunction, nestingLevel: Int): String {
    return ""
}

private fun createParametersDocumentation(parameters: List<SmlParameter>) = buildString {
    appendLine("\n**Parameters:**")
}
