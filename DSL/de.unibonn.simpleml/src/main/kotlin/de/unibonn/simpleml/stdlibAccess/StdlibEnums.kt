@file:Suppress("MemberVisibilityCanBePrivate")

package de.unibonn.simpleml.stdlibAccess

import org.eclipse.xtext.naming.QualifiedName

/**
 * Important enums in the standard library.
 */
object StdlibEnums {

    /**
     * Describes declaration types that can be targeted by annotations.
     */
    enum class AnnotationTarget {
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
        Workflow;

        companion object {
            val qualifiedName: QualifiedName = QualifiedName.create("simpleml", "lang", "AnnotationTarget")

            fun valueOfOrNull(name: String): AnnotationTarget? {
                return values().firstOrNull { it.name == name }
            }
        }
    }
}
