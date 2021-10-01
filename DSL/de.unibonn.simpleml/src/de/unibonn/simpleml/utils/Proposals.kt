package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.typing.TypeComputer
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject

class Proposals @Inject constructor(
    private val index: SimpleMLIndexExtensions,
    private val typeComputer: TypeComputer
) {

    /**
     * @param context
     * Any EObject in the current file, e.g. the SmlCompilationUnit. This is used to determine which declarations are
     * visible from here.
     *
     * @param result
     * The SmlResult that corresponds to the result port the user clicked on or null if a new initial call should be
     * added.
     *
     * @return
     * A map of URIs to EObjects (SmlClass, SmlFunction, or SmlWorkflowStep).
     */
    fun listCallables(context: EObject, result: SmlResult?): Map<URI, EObject> {
        return if (result == null) {
            listCallablesWithOnlyPrimitiveParameters(context)
        } else {
            listCallablesWithMatchingParameters(context, result)
        }
    }

    private fun listCallablesWithOnlyPrimitiveParameters(context: EObject): Map<URI, EObject> {
        return listAllReachableDeclarations(context)
            .filterValues { obj ->
                when (obj) {
                    is SmlClass -> {
                        obj.constructor != null && obj.parametersOrEmpty().all {
                            typeComputer.hasPrimitiveType(it)
                        }
                    }
                    is SmlFunction -> {
                        obj.parametersOrEmpty().all {
                            !it.isClassMember() && typeComputer.hasPrimitiveType(it)
                        }
                    }
                    is SmlWorkflowStep -> {
                        obj.parametersOrEmpty().all {
                            typeComputer.hasPrimitiveType(it)
                        }

                    }
                    else -> false
                }
            }
    }

    private fun listCallablesWithMatchingParameters(
        context: EObject,
        result: SmlResult
    ): Map<URI, EObject> {
        return listAllReachableDeclarations(context)
            .filterValues {
                (it is SmlClass && it.constructor != null) ||
                        it is SmlFunction ||
                        it is SmlWorkflowStep
            }
        // TODO filter
    }

    private fun listAllReachableDeclarations(context: EObject): Map<URI, EObject> {
        return index.visibleGlobalDeclarationDescriptions(context).associate {
            it.eObjectURI to it.eObjectOrProxy
        }
    }
}
