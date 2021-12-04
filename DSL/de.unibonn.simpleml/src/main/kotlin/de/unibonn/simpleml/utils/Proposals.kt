package de.unibonn.simpleml.utils

import com.google.inject.Inject
import de.unibonn.simpleml.emf.containingClassOrNull
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
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
                    is SmlWorkflowStep -> {
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
        result: SmlResult
    ): Map<URI, EObject> {
        val resultType = typeComputer.typeOf(result)

        return listAllReachableDeclarations(context)
            .filterValues { obj ->
                when (obj) {
                    is SmlClass -> {
                        obj.parameterList != null && obj.parametersOrEmpty().any {
                            typeConformance.isSubstitutableFor(resultType, typeComputer.typeOf(it))
                        }
                    }
                    is SmlFunction -> {
                        val hasMatchingParameter =
                            obj.parametersOrEmpty().any {
                                typeConformance.isSubstitutableFor(resultType, typeComputer.typeOf(it))
                            }
                        if (hasMatchingParameter) {
                            return@filterValues true
                        }

                        val containingClass = obj.containingClassOrNull() ?: return@filterValues false

                        return@filterValues typeConformance.isSubstitutableFor(resultType, containingClass)
                    }
                    is SmlWorkflowStep -> {
                        obj.parametersOrEmpty().any {
                            typeConformance.isSubstitutableFor(resultType, typeComputer.typeOf(it))
                        }
                    }
                    else -> false
                }
            }
    }

    private fun listAllReachableDeclarations(context: EObject): Map<URI, EObject> {
        return index.visibleGlobalDeclarationDescriptions(context).associate {
            it.eObjectURI to it.eObjectOrProxy
        }
    }
}
