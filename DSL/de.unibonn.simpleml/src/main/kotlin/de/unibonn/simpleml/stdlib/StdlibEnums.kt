@file:Suppress("MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.stdlib

import org.eclipse.xtext.naming.QualifiedName

/**
 * Important enums in the standard library.
 */
object StdlibEnums {

    /**
     * Describes declaration types that can be targeted by annotations.
     */
    val AnnotationTarget: QualifiedName = QualifiedName.create("simpleml", "lang", "AnnotationTarget")

    /**
     * Variants of the `simpleml.lang.AnnotationTarget` enum.
     */
    object AnnotationTargetVariants {
        val Annotation: QualifiedName = AnnotationTarget.append("Annotation")
        val Attribute: QualifiedName = AnnotationTarget.append("Attribute")
        val Class: QualifiedName = AnnotationTarget.append("Class")
        val CompilationUnit: QualifiedName = AnnotationTarget.append("CompilationUnit")
        val Enum: QualifiedName = AnnotationTarget.append("Enum")
        val EnumVariant: QualifiedName = AnnotationTarget.append("EnumVariant")
        val Function: QualifiedName = AnnotationTarget.append("Function")
        val LambdaResult: QualifiedName = AnnotationTarget.append("LambdaResult")
        val Parameter: QualifiedName = AnnotationTarget.append("Parameter")
        val Placeholder: QualifiedName = AnnotationTarget.append("Placeholder")
        val Result: QualifiedName = AnnotationTarget.append("Result")
        val TypeParameter: QualifiedName = AnnotationTarget.append("TypeParameter")
        val Workflow: QualifiedName = AnnotationTarget.append("Workflow")
        val WorkflowStep: QualifiedName = AnnotationTarget.append("WorkflowStep")
    }
}
