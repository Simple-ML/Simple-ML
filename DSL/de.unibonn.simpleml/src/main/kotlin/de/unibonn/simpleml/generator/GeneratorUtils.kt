package de.unibonn.simpleml.generator

import de.unibonn.simpleml.constants.FileExtensions
import de.unibonn.simpleml.emf.compilationUnitOrNull
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource

fun URI.toUNIXString(): String {
    val uriString = this.toPlatformString(true) ?: this.toString()
    return uriString.replace("\\", "/")
}

fun Resource.baseFileName(): String {
    return uri.toUNIXString()
        .split("/")
        .last()
        .removeSuffix(FileExtensions.STUB)
        .removeSuffix(FileExtensions.TEST)
        .removeSuffix(FileExtensions.WORKFLOW)
}

fun Resource.baseGeneratedFilePath(): String {
    val compilationUnit = this.compilationUnitOrNull()
        ?: throw IllegalArgumentException("Resource does not contain a compilation unit.")

    val packagePart = compilationUnit.name.replace(".", "/")
    val filePart = this.baseFileName()
    return "$packagePart/gen_$filePart"
}
