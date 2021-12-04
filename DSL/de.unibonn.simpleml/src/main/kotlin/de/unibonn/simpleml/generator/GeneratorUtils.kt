package de.unibonn.simpleml.generator

import de.unibonn.simpleml.utils.SML_STUB_EXTENSION
import de.unibonn.simpleml.utils.SML_TEST_EXTENSION
import de.unibonn.simpleml.utils.SML_WORKFLOW_EXTENSION
import de.unibonn.simpleml.utils.compilationUnitOrNull
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
            .removeSuffix(SML_STUB_EXTENSION)
            .removeSuffix(SML_TEST_EXTENSION)
            .removeSuffix(SML_WORKFLOW_EXTENSION)
}

fun Resource.baseGeneratedFilePath(): String {
    val compilationUnit = this.compilationUnitOrNull()
            ?: throw IllegalArgumentException("Resource does not contain a compilation unit.")

    val packagePart = compilationUnit.name.replace(".", "/")
    val filePart = this.baseFileName()
    return "$packagePart/gen_$filePart"
}
