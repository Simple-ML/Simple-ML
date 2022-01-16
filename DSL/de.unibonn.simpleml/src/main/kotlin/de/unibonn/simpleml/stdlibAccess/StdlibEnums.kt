package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.emf.uniqueVariantOrNull
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import org.eclipse.emf.ecore.EObject

/**
 * Important enums in the standard library.
 */
object StdlibEnum {

    /**
     * Describes declaration types that can be targeted by annotations.
     */
    object AnnotationTarget {
        lateinit var Annotation: SmlEnumVariant
        lateinit var Attribute: SmlEnumVariant
        lateinit var Class: SmlEnumVariant
        lateinit var CompilationUnit: SmlEnumVariant
        lateinit var Enum: SmlEnumVariant
        lateinit var EnumVariant: SmlEnumVariant
        lateinit var Function: SmlEnumVariant
        lateinit var LambdaResult: SmlEnumVariant
        lateinit var Parameter: SmlEnumVariant
        lateinit var Placeholder: SmlEnumVariant
        lateinit var Result: SmlEnumVariant
        lateinit var Step: SmlEnumVariant
        lateinit var TypeParameter: SmlEnumVariant
        lateinit var Workflow: SmlEnumVariant

        val variants
            get() = listOf(
                Annotation,
                Attribute,
                Class,
                CompilationUnit,
                Enum,
                EnumVariant,
                Function,
                LambdaResult,
                Parameter,
                Placeholder,
                Result,
                Step,
                TypeParameter,
                Workflow
            )
    }
}

/**
 * Loads the important enums in the standard library.
 */
internal fun EObject.loadStdlibEnum() {
    loadAnnotationTarget()
}

private fun EObject.loadAnnotationTarget() {
    getStdlibDeclaration<SmlEnum>(StdlibPackages.lang.append("AnnotationTarget")).let {
        StdlibEnum.AnnotationTarget.Annotation = it.uniqueVariantOrFail("Annotation")
        StdlibEnum.AnnotationTarget.Attribute = it.uniqueVariantOrFail("Attribute")
        StdlibEnum.AnnotationTarget.Class = it.uniqueVariantOrFail("Class")
        StdlibEnum.AnnotationTarget.CompilationUnit = it.uniqueVariantOrFail("CompilationUnit")
        StdlibEnum.AnnotationTarget.Enum = it.uniqueVariantOrFail("Enum")
        StdlibEnum.AnnotationTarget.EnumVariant = it.uniqueVariantOrFail("EnumVariant")
        StdlibEnum.AnnotationTarget.Function = it.uniqueVariantOrFail("Function")
        StdlibEnum.AnnotationTarget.LambdaResult = it.uniqueVariantOrFail("LambdaResult")
        StdlibEnum.AnnotationTarget.Parameter = it.uniqueVariantOrFail("Parameter")
        StdlibEnum.AnnotationTarget.Placeholder = it.uniqueVariantOrFail("Placeholder")
        StdlibEnum.AnnotationTarget.Result = it.uniqueVariantOrFail("Result")
        StdlibEnum.AnnotationTarget.Step = it.uniqueVariantOrFail("Step")
        StdlibEnum.AnnotationTarget.TypeParameter = it.uniqueVariantOrFail("TypeParameter")
        StdlibEnum.AnnotationTarget.Workflow = it.uniqueVariantOrFail("Workflow")
    }
}

private fun SmlEnum.uniqueVariantOrFail(name: String): SmlEnumVariant {
    return when (val variant = uniqueVariantOrNull(name)) {
        null -> throw IllegalStateException("Enum ${this.qualifiedNameOrNull()} has no variant '$name'.")
        else -> variant
    }
}
