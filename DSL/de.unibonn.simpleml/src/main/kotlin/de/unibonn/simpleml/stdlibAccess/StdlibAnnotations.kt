@file:Suppress("MemberVisibilityCanBePrivate", "FunctionName")

package de.unibonn.simpleml.stdlibAccess

import de.unibonn.simpleml.emf.annotationCallsOrEmpty
import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationCall
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantEnumVariant
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantExpression
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.SmlConstantString
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.utils.uniqueOrNull
import org.eclipse.emf.ecore.EObject

/**
 * Important annotations in the standard library.
 */
object StdlibAnnotation {

    /**
     * The declaration should no longer be used.
     *
     * @see isDeprecated
     */
    fun Deprecated(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Deprecated"))
    }

    /**
     * The purpose of a declaration.
     *
     * @see descriptionOrNull
     */
    fun Description(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Description"))
    }

    /**
     * The qualified name of the corresponding module in Python.
     *
     * @see pythonModuleOrNull
     */
    fun PythonModule(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("PythonModule"))
    }

    /**
     * The name of the corresponding API element in Python.
     *
     * @see pythonNameOrNull
     */
    fun PythonName(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("PythonName"))
    }

    /**
     * The function returns the same results for the same arguments and has no side effects.
     *
     * @see isPure
     */
    fun Pure(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Pure"))
    }

    /**
     * The annotation can be used multiple times for the same declaration.
     *
     * @see isRepeatable
     */
    fun Repeatable(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Repeatable"))
    }

    /**
     * The version in which a declaration was added.
     *
     * @see sinceVersionOrNull
     */
    fun Since(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Since"))
    }

    /**
     * The annotation can target only a subset of declaration types.
     *
     * @see validTargets
     */
    fun Target(context: EObject): SmlAnnotation {
        return context.getStdlibDeclaration(StdlibPackages.lang.append("Target"))
    }
}

/**
 * Returns all calls of the given annotation.
 */
fun SmlAbstractDeclaration.annotationCallsOrEmpty(annotation: SmlAnnotation): List<SmlAnnotationCall> {
    return this.annotationCallsOrEmpty().filter { it.annotation == annotation }
}

/**
 * Returns the unique use of the given annotation or `null` if none or multiple exist.
 */
fun SmlAbstractDeclaration.uniqueAnnotationCallOrNull(annotation: SmlAnnotation): SmlAnnotationCall? {
    return this.annotationCallsOrEmpty(annotation).uniqueOrNull()
}

/**
 * Returns the description attached to the declaration with a `simpleml.lang.Description` annotation.
 */
fun SmlAbstractDeclaration.descriptionOrNull(): String? {
    val value = annotationCallArgumentValueOrNull(StdlibAnnotation.Description(this), "description")
    return (value as? SmlConstantString)?.value
}

/**
 * Checks if the declaration is annotated with the `simpleml.lang.Deprecated` annotation.
 */
fun SmlAbstractDeclaration.isDeprecated(): Boolean {
    return hasAnnotationCallTo(StdlibAnnotation.Deprecated(this))
}

/**
 * Checks if the function is annotated with the `simpleml.lang.Pure` annotation.
 */
fun SmlFunction.isPure(): Boolean {
    return hasAnnotationCallTo(StdlibAnnotation.Pure(this))
}

/**
 * Checks if the annotation is annotated with the `simpleml.lang.Repeatable` annotation.
 */
fun SmlAnnotation.isRepeatable(): Boolean {
    return hasAnnotationCallTo(StdlibAnnotation.Repeatable(this))
}

/**
 * Returns the qualified name of the Python module that corresponds to this compilation unit. It is attached to the
 * compilation unit with a `simpleml.lang.PythonModule` annotation.
 */
fun SmlCompilationUnit.pythonModuleOrNull(): String? {
    val value = uniquePackageOrNull()?.annotationCallArgumentValueOrNull(
        StdlibAnnotation.PythonModule(this),
        "qualifiedName"
    )
    return (value as? SmlConstantString)?.value
}

/**
 * Returns the name of the Python API element that corresponds to this declaration. It is attached to the declaration
 * with a `simpleml.lang.PythonName` annotation.
 */
fun SmlAbstractDeclaration.pythonNameOrNull(): String? {
    val value = annotationCallArgumentValueOrNull(StdlibAnnotation.PythonName(this), "name")
    return (value as? SmlConstantString)?.value
}

/**
 * Returns the version when the declaration was added. This is attached to the declaration with a `simpleml.lang.Since`
 * annotation.
 */
fun SmlAbstractDeclaration.sinceVersionOrNull(): String? {
    val value = annotationCallArgumentValueOrNull(StdlibAnnotation.Since(this), "version")
    return (value as? SmlConstantString)?.value
}

/**
 * Returns the possible targets of this annotation.
 */
fun SmlAnnotation.validTargets(): List<SmlEnumVariant> {
    val targetAnnotationCall = uniqueAnnotationCallOrNull(StdlibAnnotation.Target(this))
        ?: return StdlibEnum.AnnotationTarget.variants(this)

    return targetAnnotationCall
        .argumentsOrEmpty()
        .asSequence()
        .mapNotNull { it.value.toConstantExpressionOrNull() }
        .filterIsInstance<SmlConstantEnumVariant>()
        .map { it.value }
        .filter { it in StdlibEnum.AnnotationTarget.variants(this) }
        .toList()
}

/**
 * Returns whether this [SmlAbstractDeclaration] has at least one call to the given annotation.
 */
private fun SmlAbstractDeclaration.hasAnnotationCallTo(annotation: SmlAnnotation): Boolean {
    return annotationCallsOrEmpty().any { it.annotation == annotation }
}

/**
 * Finds the unique call to the given annotation and looks up the value assigned to the parameter with the given name.
 */
private fun SmlAbstractDeclaration.annotationCallArgumentValueOrNull(
    annotation: SmlAnnotation,
    parameterName: String
): SmlConstantExpression? {
    return uniqueAnnotationCallOrNull(annotation)
        .argumentsOrEmpty()
        .uniqueOrNull { it.parameterOrNull()?.name == parameterName }
        ?.toConstantExpressionOrNull()
}
