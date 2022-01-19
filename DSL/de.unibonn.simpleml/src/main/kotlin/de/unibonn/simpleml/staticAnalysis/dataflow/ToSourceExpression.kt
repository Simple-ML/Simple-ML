package de.unibonn.simpleml.staticAnalysis.dataflow

import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isOptional
import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.emf.lambdaResultsOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlExpressionLambda
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.staticAnalysis.indexOrNull
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.linking.uniqueYieldOrNull

/**
 * Tries to find the source of this expression by partially evaluating assignments, calls, member accesses, and
 * references. On success a [SmlSourceExpression] is returned, if any reference cannot be resolved `null`.
 */
@OptIn(ExperimentalStdlibApi::class)
fun SmlAbstractExpression.toSourceExpressionOrNull(): SmlSourceExpression? {
    return inlineAndUnwrap(FrameData(this, emptySet(), emptyMap()))
}

private data class FrameData<T : SmlAbstractObject?>(
    val current: T,
    val visited: Set<SmlAbstractObject>,
    val substitutions: ParameterSubstitutions
)

@OptIn(ExperimentalStdlibApi::class)
private val inlineAndUnwrap: DeepRecursiveFunction<FrameData<SmlAbstractExpression>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        when (val source = inline.callRecursive(data)) {
            is SmlResultRecord -> source.unwrap()
            else -> source
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inline: DeepRecursiveFunction<FrameData<SmlAbstractExpression>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        if (data.current in data.visited) {
            return@DeepRecursiveFunction null
        }

        when (data.current) {

            // Base cases
            is SmlBoolean -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlFloat -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlInt -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlNull -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlString -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlTemplateStringStart -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlTemplateStringInner -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlTemplateStringEnd -> SmlBoundOtherExpression(data.current, emptyMap())
            is SmlInfixOperation -> SmlBoundOtherExpression(data.current, data.substitutions)
            is SmlPrefixOperation -> SmlBoundOtherExpression(data.current, data.substitutions)
            is SmlTemplateString -> SmlBoundOtherExpression(data.current, data.substitutions)
            is SmlBlockLambda -> SmlBoundBlockLambda(
                lambda = data.current,
                substitutionsOnCreation = data.substitutions
            )
            is SmlExpressionLambda -> SmlBoundExpressionLambda(
                lambda = data.current,
                substitutionsOnCreation = data.substitutions
            )

            // Simple recursive cases
            is SmlArgument -> callRecursive(
                FrameData(
                    current = data.current.value,
                    visited = data.visited + data.current,
                    substitutions = data.substitutions
                )
            )
            is SmlParenthesizedExpression -> callRecursive(
                FrameData(
                    current = data.current.expression,
                    visited = data.visited + data.current,
                    substitutions = data.substitutions
                )
            )

            // Complex recursive cases
            is SmlCall -> callToSource.callRecursive(
                FrameData(
                    data.current,
                    data.visited,
                    data.substitutions
                )
            )
            is SmlMemberAccess -> memberAccessToSource.callRecursive(
                FrameData(
                    data.current,
                    data.visited,
                    data.substitutions
                )
            )
            is SmlReference -> referenceToSource.callRecursive(
                FrameData(
                    data.current,
                    data.visited,
                    data.substitutions
                )
            )

            else -> throw IllegalArgumentException("Missing case to handle $this.")
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val callToSource: DeepRecursiveFunction<FrameData<SmlCall>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->

        // Inline receiver
        val receiver = inlineAndUnwrap.callRecursive(
            FrameData(
                current = data.current.receiver,
                visited = data.visited,
                substitutions = data.substitutions
            )
        )

        when (receiver) {
            null -> null
            is SmlBoundCallable -> {

                // Build new substitutions
                val newSubstitutions = buildNewSubstitutions.callRecursive(
                    BuildNewSubstitutionsFrameData(
                        receiver = receiver,
                        call = data.current,
                        visited = data.visited,
                        oldSubstitutions = data.substitutions
                    )
                )

                // Run the call
                when (receiver) {
                    is SmlBoundBlockLambda -> {
                        SmlResultRecord(
                            receiver.lambda.lambdaResultsOrEmpty().map {
                                it to assigneeToSource.callRecursive(
                                    FrameData(
                                        current = it,
                                        visited = data.visited,
                                        substitutions = newSubstitutions
                                    )
                                )
                            }
                        )
                    }
                    is SmlBoundExpressionLambda -> {
                        inline.callRecursive(
                            FrameData(
                                current = receiver.lambda.result,
                                visited = data.visited,
                                substitutions = newSubstitutions
                            )
                        )
                    }
                    is SmlBoundStepReference -> {
                        SmlResultRecord(
                            receiver.step.resultsOrEmpty().map {
                                it to assigneeToSource.callRecursive(
                                    FrameData(
                                        current = it.uniqueYieldOrNull(),
                                        visited = data.visited,
                                        substitutions = newSubstitutions
                                    )
                                )
                            }
                        )
                    }
                }
            }
            else -> SmlBoundOtherExpression(data.current, data.substitutions)
        }
    }

private data class BuildNewSubstitutionsFrameData(
    val receiver: SmlBoundCallable,
    val call: SmlCall,
    val visited: Set<SmlAbstractObject>,
    val oldSubstitutions: ParameterSubstitutions
)

@OptIn(ExperimentalStdlibApi::class)
private val buildNewSubstitutions: DeepRecursiveFunction<BuildNewSubstitutionsFrameData, ParameterSubstitutions> =
    DeepRecursiveFunction { data ->
        val substitutionsOnCreation = data.receiver.substitutionsOnCreation
        val substitutionsOnCall = data.call.argumentsOrEmpty()
            .mapNotNull {
                when (val parameter = it.parameterOrNull()) {
                    null -> null
                    else -> parameter to inline.callRecursive(
                        FrameData(
                            current = it,
                            visited = data.visited,
                            substitutions = data.oldSubstitutions
                        )
                    )
                }
            }

        buildMap {
            putAll(substitutionsOnCreation)
            putAll(substitutionsOnCall)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val memberAccessToSource: DeepRecursiveFunction<FrameData<SmlMemberAccess>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->

        // Inline receiver
        val receiver = inline.callRecursive(
            FrameData(
                current = data.current.receiver,
                visited = data.visited,
                substitutions = data.substitutions
            )
        )

        when (receiver) {
            null -> null
            is SmlResultRecord -> receiver.getSubstitutionByReferenceOrNull(data.current.member)
            else -> SmlBoundOtherExpression(data.current, data.substitutions)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val referenceToSource: DeepRecursiveFunction<FrameData<SmlReference>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        val declaration = data.current.declaration
        when {
            !declaration.isResolved() -> null
            declaration is SmlPlaceholder -> {
                assigneeToSource.callRecursive(
                    FrameData(
                        current = declaration,
                        visited = data.visited,
                        substitutions = data.substitutions
                    )
                )
            }
            declaration is SmlParameter -> {
                parameterToSource.callRecursive(
                    FrameData(
                        current = declaration,
                        visited = data.visited,
                        substitutions = data.substitutions
                    )
                )
            }
            declaration is SmlStep -> SmlBoundStepReference(step = declaration)
            else -> SmlBoundOtherExpression(data.current, data.substitutions)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val assigneeToSource: DeepRecursiveFunction<FrameData<SmlAbstractAssignee?>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        val assignmentRHS = data.current
            ?.closestAncestorOrNull<SmlAssignment>()
            ?.expression
            ?: return@DeepRecursiveFunction null

        val inlinedExpression = inline.callRecursive(
            FrameData(
                current = assignmentRHS,
                visited = data.visited,
                substitutions = data.substitutions
            )
        )

        when (inlinedExpression) {
            null -> null
            is SmlResultRecord -> inlinedExpression.getSubstitutionByIndexOrNull(data.current.indexOrNull())
            else -> when {
                data.current.indexOrNull() == 0 -> inlinedExpression
                else -> null
            }
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val parameterToSource: DeepRecursiveFunction<FrameData<SmlParameter>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        when {
            data.current.isVariadic -> null
            data.current in data.substitutions -> data.substitutions[data.current]
            data.current.isOptional() -> inline.callRecursive(
                FrameData(
                    current = data.current.defaultValue,
                    visited = data.visited,
                    substitutions = data.substitutions
                )
            )
            else -> null
        }
    }
