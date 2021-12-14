package de.unibonn.simpleml.generator

import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.emf.compilationUnitOrNull
import de.unibonn.simpleml.emf.uniquePackageOrNull
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource

fun URI.toUNIXString(): String {
    val uriString = this.toPlatformString(true) ?: this.toString()
    return uriString.replace("\\", "/")
}

fun Resource.baseFileName(): String {
    return this.uri.toUNIXString()
        .split("/")
        .last()
        .removeSuffix(".${FileExtension.STUB}")
        .removeSuffix(".${FileExtension.TEST}")
        .removeSuffix(".${FileExtension.FLOW}")
}

fun Resource.baseGeneratedFilePath(): String {
    val compilationUnit = this.compilationUnitOrNull()
        ?: throw IllegalArgumentException("Resource does not contain a compilation unit.")

    val packagePart = compilationUnit.uniquePackageOrNull()
        ?.name
        ?.replace(".", "/")
        ?: "."
    val filePart = this.baseFileName()
    return "$packagePart/gen_$filePart"
}
