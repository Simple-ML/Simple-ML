package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.emf.containingClassOrNull
import de.unibonn.simpleml.emf.isClassMember
import de.unibonn.simpleml.emf.isCompilationUnitMember
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.typing.Type
import de.unibonn.simpleml.typing.TypeComputer
import de.unibonn.simpleml.typing.TypeConformance
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject

class Proposals @Inject constructor(
    private val index: SimpleMLIndexExtensions,
    private val typeComputer: TypeComputer,
    private val typeConformance: TypeConformance,
) {

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
                            typeComputer.hasPrimitiveType(it)
                        }
                    }
                    is SmlFunction -> {
                        obj.isCompilationUnitMember() && obj.parametersOrEmpty().all {
                            typeComputer.hasPrimitiveType(it)
                        }
                    }
                    is SmlStep -> {
                        obj.parametersOrEmpty().all {
                            typeComputer.hasPrimitiveType(it)
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
        val requiredTypes = declarations.map { typeComputer.typeOf(it) }

        return listAllReachableDeclarations(context)
            .filterValues { obj ->
                val availableTypes = when (obj) {
                    is SmlClass -> {
                        obj.parametersOrEmpty().map { typeComputer.typeOf(it) }
                    }
                    is SmlFunction -> {
                        val parameterTypes = obj.parametersOrEmpty().map { typeComputer.typeOf(it) }
                        if (obj.isClassMember()) {
                            parameterTypes + typeComputer.typeOf(obj.containingClassOrNull()!!)
                        } else {
                            parameterTypes
                        }
                    }
                    is SmlStep -> {
                        obj.parametersOrEmpty().map { typeComputer.typeOf(it) }
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

        val matchingAvailableTypes = availableTypes.filter { typeConformance.isSubstitutableFor(requiredType, it) }
        if (matchingAvailableTypes.isEmpty()) {
            return false
        }

        return matchingAvailableTypes.any {
            typesMatch(requiredTypes.drop(1), availableTypes - it)
        }
    }

    private fun listAllReachableDeclarations(context: EObject): Map<URI, EObject> {
        return index.visibleGlobalDeclarationDescriptions(context).associate {
            it.eObjectURI to it.eObjectOrProxy
        }
    }
}
