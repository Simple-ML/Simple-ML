package de.unibonn.simpleml.ide.editor.contentassist

import de.unibonn.simpleml.emf.containingClassOrNull
import de.unibonn.simpleml.emf.isClassMember
import de.unibonn.simpleml.emf.isGlobal
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.scoping.visibleGlobalDeclarationDescriptions
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.staticAnalysis.typing.Type
import de.unibonn.simpleml.staticAnalysis.typing.hasPrimitiveType
import de.unibonn.simpleml.staticAnalysis.typing.isSubstitutableFor
import de.unibonn.simpleml.staticAnalysis.typing.type
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject

class Proposals {

    /**
     * @param context
     * Any EObject in the current file, e.g. the SmlCompilationUnit. This is used to determine which declarations are
     * visible from here.
     *
     * @param declarations
     * The declarations that correspond to the result port the user clicked on or null if a new initial call should
     * be added. They should be either SmlPlaceholders or SmlResults. If multiple declarations are specified, a callable
     * must have one matching input port for each.
     *
     * @return
     * A map of URIs to EObjects (SmlClass, SmlFunction, or SmlWorkflowStep).
     */
    fun listCallables(context: EObject, declarations: List<SmlAbstractDeclaration>): Map<URI, EObject> {
        return if (declarations.isEmpty()) {
            listCallablesWithOnlyPrimitiveParameters(context)
        } else {
            listCallablesWithMatchingParameters(context, declarations)
        }
    }

    /**
     * @param context
     * Any EObject in the current file, e.g. the SmlCompilationUnit. This is used to determine which declarations are
     * visible from here.
     *
     * @param declaration
     * The declaration that corresponds to the result port the user clicked on or null if a new initial call should
     * be added. It should be either an SmlPlaceholder or an SmlResult.
     *
     * @return
     * A map of URIs to EObjects (SmlClass, SmlFunction, or SmlWorkflowStep).
     */
    @Deprecated("Use listCallables(EObject, List<SmlAbstractDeclaration>) instead.")
    fun listCallables(context: EObject, declaration: SmlAbstractDeclaration? = null): Map<URI, EObject> {
        return if (declaration == null) {
            listCallablesWithOnlyPrimitiveParameters(context)
        } else {
            listCallablesWithMatchingParameters(context, listOf(declaration))
        }
    }

    private fun listCallablesWithOnlyPrimitiveParameters(context: EObject): Map<URI, EObject> {
        return listAllReachableDeclarations(context)
            .filterValues { obj ->
                val res = when (obj) {
                    is SmlClass -> {
                        obj.parameterList != null && obj.parametersOrEmpty().all {
                            it.hasPrimitiveType()
                        }
                    }
                    is SmlFunction -> {
                        obj.isGlobal() && obj.parametersOrEmpty().all {
                            it.hasPrimitiveType()
                        }
                    }
                    is SmlStep -> {
                        obj.parametersOrEmpty().all {
                            it.hasPrimitiveType()
                        }
                    }
                    else -> false
                }

                res
            }
    }

    private fun listCallablesWithMatchingParameters(
        context: EObject,
        declarations: List<SmlAbstractDeclaration>
    ): Map<URI, EObject> {
        val requiredTypes = declarations.map { it.type() }

        return listAllReachableDeclarations(context)
            .filterValues { obj ->
                val availableTypes = when (obj) {
                    is SmlClass -> {
                        obj.parametersOrEmpty().map { it.type() }
                    }
                    is SmlFunction -> {
                        val parameterTypes = obj.parametersOrEmpty().map { it.type() }
                        if (obj.isClassMember()) {
                            parameterTypes + obj.containingClassOrNull()!!.type()
                        } else {
                            parameterTypes
                        }
                    }
                    is SmlStep -> {
                        obj.parametersOrEmpty().map { it.type() }
                    }
                    else -> return@filterValues false
                }

                typesMatch(requiredTypes, availableTypes)
            }
    }

    private fun typesMatch(requiredTypes: List<Type>, availableTypes: List<Type>): Boolean {
        if (requiredTypes.isEmpty()) {
            return true
        }

        val requiredType = requiredTypes.first()

        val matchingAvailableTypes = availableTypes.filter { requiredType.isSubstitutableFor(it) }
        if (matchingAvailableTypes.isEmpty()) {
            return false
        }

        return matchingAvailableTypes.any {
            typesMatch(requiredTypes.drop(1), availableTypes - it)
        }
    }

    private fun listAllReachableDeclarations(context: EObject): Map<URI, EObject> {
        return context.visibleGlobalDeclarationDescriptions().associate {
            it.eObjectURI to it.eObjectOrProxy
        }
    }
}
