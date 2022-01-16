@file:Suppress("FunctionName")

package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.emf.uniqueVariantOrNull
import de.unibonn.simpleml.emf.variantsOrEmpty
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
        fun Annotation(context: EObject) = enum(context).uniqueVariantOrFail("Annotation")
        fun Attribute(context: EObject) = enum(context).uniqueVariantOrFail("Attribute")
        fun Class(context: EObject) = enum(context).uniqueVariantOrFail("Class")
        fun CompilationUnit(context: EObject) = enum(context).uniqueVariantOrFail("CompilationUnit")
        fun Enum(context: EObject) = enum(context).uniqueVariantOrFail("Enum")
        fun EnumVariant(context: EObject) = enum(context).uniqueVariantOrFail("EnumVariant")
        fun Function(context: EObject) = enum(context).uniqueVariantOrFail("Function")
        fun LambdaResult(context: EObject) = enum(context).uniqueVariantOrFail("LambdaResult")
        fun Parameter(context: EObject) = enum(context).uniqueVariantOrFail("Parameter")
        fun Placeholder(context: EObject) = enum(context).uniqueVariantOrFail("Placeholder")
        fun Result(context: EObject) = enum(context).uniqueVariantOrFail("Result")
        fun Step(context: EObject) = enum(context).uniqueVariantOrFail("Step")
        fun TypeParameter(context: EObject) = enum(context).uniqueVariantOrFail("TypeParameter")
        fun Workflow(context: EObject) = enum(context).uniqueVariantOrFail("Workflow")

        fun enum(context: EObject): SmlEnum {
            return context.getStdlibDeclaration(StdlibPackages.lang.append("AnnotationTarget"))
        }

        fun variants(context: EObject): List<SmlEnumVariant> {
            return enum(context).variantsOrEmpty()
        }
    }
}

private fun SmlEnum.uniqueVariantOrFail(name: String): SmlEnumVariant {
    return when (val variant = uniqueVariantOrNull(name)) {
        null -> throw IllegalStateException("Enum ${this.qualifiedNameOrNull()} has no variant '$name'.")
        else -> variant
    }
}
