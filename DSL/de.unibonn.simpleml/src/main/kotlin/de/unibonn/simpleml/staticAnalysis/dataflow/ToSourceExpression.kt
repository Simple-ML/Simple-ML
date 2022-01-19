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
import de.unibonn.simpleml.staticAnalysis.isPureCallable
import de.unibonn.simpleml.staticAnalysis.linking.parameterOrNull
import de.unibonn.simpleml.staticAnalysis.linking.uniqueYieldOrNull

/**
 * Tries to find the source of this expression by partially evaluating assignments, calls, member accesses, and
 * references. On success a [SmlSourceExpression] is returned, if any reference cannot be resolved `null`.
 *
 * @param stopAtImpureCall If `true`, calls to impure callable are not traversed further but returned unchanged.
 */
@OptIn(ExperimentalStdlibApi::class)
fun SmlAbstractExpression.toSourceExpressionOrNull(stopAtImpureCall: Boolean = true): SmlSourceExpression? {
    return inline.invoke(FrameData(this, emptyMap(), stopAtImpureCall))
}

private data class FrameData<T : SmlAbstractObject?>(
    val current: T,
    val substitutions: ParameterSubstitutions,
    val stopAtImpureCall: Boolean
)

@OptIn(ExperimentalStdlibApi::class)
private val inline: DeepRecursiveFunction<FrameData<SmlAbstractExpression>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        when (data.current) {

            // Base cases
            is SmlBoolean -> SmlBoundExpression(data.current, emptyMap())
            is SmlFloat -> SmlBoundExpression(data.current, emptyMap())
            is SmlInt -> SmlBoundExpression(data.current, emptyMap())
            is SmlNull -> SmlBoundExpression(data.current, emptyMap())
            is SmlString -> SmlBoundExpression(data.current, emptyMap())
            is SmlTemplateStringStart -> SmlBoundExpression(data.current, emptyMap())
            is SmlTemplateStringInner -> SmlBoundExpression(data.current, emptyMap())
            is SmlTemplateStringEnd -> SmlBoundExpression(data.current, emptyMap())
            is SmlInfixOperation -> SmlBoundExpression(data.current, data.substitutions)
            is SmlPrefixOperation -> SmlBoundExpression(data.current, data.substitutions)
            is SmlTemplateString -> SmlBoundExpression(data.current, data.substitutions)
            is SmlBlockLambda -> SmlBoundBlockLambda(
                lambda = data.current,
                substitutionsOnCreation = data.substitutions
            )
            is SmlExpressionLambda -> SmlBoundExpressionLambda(
                lambda = data.current,
                substitutionsOnCreation = data.substitutions
            )

            // Simple recursive cases
            is SmlArgument -> callRecursive(data.copy(current = data.current.value))
            is SmlParenthesizedExpression -> callRecursive(data.copy(current = data.current.expression))

            // Complex recursive cases
            is SmlCall -> inlineCall.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.stopAtImpureCall
                )
            )
            is SmlMemberAccess -> inlineMemberAccess.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.stopAtImpureCall
                )
            )
            is SmlReference -> inlineReference.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.stopAtImpureCall
                )
            )

            else -> throw IllegalArgumentException("Missing case to handle $this.")
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineCall: DeepRecursiveFunction<FrameData<SmlCall>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->

        // Inline receiver
        val receiver = inline.callRecursive(
            FrameData(
                current = data.current.receiver,
                substitutions = data.substitutions,
                stopAtImpureCall = data.stopAtImpureCall
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
                        oldSubstitutions = data.substitutions,
                        stopAtImpureCall = data.stopAtImpureCall
                    )
                )

                // Check purity
                if (data.stopAtImpureCall && !receiver.callable.isPureCallable()) {
                    return@DeepRecursiveFunction SmlBoundExpression(data.current, data.substitutions)
                }

                // TODO: must never traverse recursive stuff -> pass visited elements in data frame
                // TODO: test this

                // Run the call
                when (receiver) {
                    is SmlBoundBlockLambda -> {
                        SmlResultRecord(
                            receiver.lambda.lambdaResultsOrEmpty().map {
                                it to inlineAssignee.callRecursive(
                                    FrameData(
                                        current = it,
                                        substitutions = newSubstitutions,
                                        stopAtImpureCall = data.stopAtImpureCall
                                    )
                                )
                            }
                        ).unwrap()
                    }
                    is SmlBoundExpressionLambda -> {
                        inline.callRecursive(
                            FrameData(
                                current = receiver.lambda.result,
                                substitutions = newSubstitutions,
                                stopAtImpureCall = data.stopAtImpureCall
                            )
                        )
                    }
                    is SmlBoundStepReference -> {
                        SmlResultRecord(
                            receiver.step.resultsOrEmpty().map {
                                it to inlineAssignee.callRecursive(
                                    FrameData(
                                        current = it.uniqueYieldOrNull(),
                                        substitutions = newSubstitutions,
                                        stopAtImpureCall = data.stopAtImpureCall
                                    )
                                )
                            }
                        ).unwrap()
                    }
                }
            }
            else -> SmlBoundExpression(data.current, data.substitutions)
        }
    }

private data class BuildNewSubstitutionsFrameData(
    val receiver: SmlBoundCallable,
    val call: SmlCall,
    val oldSubstitutions: ParameterSubstitutions,
    val stopAtImpureCall: Boolean
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
                            substitutions = data.oldSubstitutions,
                            stopAtImpureCall = data.stopAtImpureCall
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
private val inlineMemberAccess: DeepRecursiveFunction<FrameData<SmlMemberAccess>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->

        // Inline receiver
        val receiver = inline.callRecursive(
            FrameData(
                current = data.current.receiver,
                substitutions = data.substitutions,
                stopAtImpureCall = data.stopAtImpureCall
            )
        )

        when (receiver) {
            is SmlResultRecord -> receiver.getSubstitutionByReferenceOrNull(data.current.member)
            else -> SmlBoundExpression(data.current, data.substitutions)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineReference: DeepRecursiveFunction<FrameData<SmlReference>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        val declaration = data.current.declaration
        when {
            !declaration.isResolved() -> null
            declaration is SmlPlaceholder -> {
                inlineAssignee.callRecursive(
                    FrameData(
                        current = declaration,
                        substitutions = data.substitutions,
                        stopAtImpureCall = data.stopAtImpureCall
                    )
                )
            }
            declaration is SmlParameter -> {
                inlineParameter.callRecursive(
                    FrameData(
                        current = declaration,
                        substitutions = data.substitutions,
                        stopAtImpureCall = data.stopAtImpureCall
                    )
                )
            }
            declaration is SmlStep -> SmlBoundStepReference(step = declaration)
            else -> SmlBoundExpression(data.current, data.substitutions)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineAssignee: DeepRecursiveFunction<FrameData<SmlAbstractAssignee?>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        val assignmentRHS = data.current
            ?.closestAncestorOrNull<SmlAssignment>()
            ?.expression
            ?: return@DeepRecursiveFunction null

        val inlinedExpression = inline.callRecursive(
            FrameData(
                current = assignmentRHS,
                substitutions = data.substitutions,
                stopAtImpureCall = data.stopAtImpureCall
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
private val inlineParameter: DeepRecursiveFunction<FrameData<SmlParameter>, SmlSourceExpression?> =
    DeepRecursiveFunction { data ->
        when {
            data.current.isVariadic -> null
            data.current in data.substitutions -> data.substitutions[data.current]
            data.current.isOptional() -> inline.callRecursive(
                FrameData(
                    current = data.current.defaultValue,
                    substitutions = data.substitutions,
                    stopAtImpureCall = data.stopAtImpureCall
                )
            )
            else -> null
        }
    }
