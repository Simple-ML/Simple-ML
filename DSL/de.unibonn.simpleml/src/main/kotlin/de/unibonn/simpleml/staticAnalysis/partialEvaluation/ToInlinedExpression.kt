package de.unibonn.simpleml.staticAnalysis.partialEvaluation

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
 * Tries to inline this expression by partially evaluating assignments, calls, member accesses, and references. On
 * success a [SmlInlinedExpression] is returned, if any reference cannot be resolved `null`.
 *
 * @param traverseImpureCallables If `false`, calls to impure callable are not traversed further but returned unchanged.
 */
@OptIn(ExperimentalStdlibApi::class)
fun SmlAbstractExpression.toInlinedExpressionOrNull(traverseImpureCallables: Boolean = false): SmlInlinedExpression? {
    return inline.invoke(FrameData(this, emptyMap(), traverseImpureCallables))
}

private data class FrameData<T : SmlAbstractObject?>(
    val current: T,
    val substitutions: ParameterSubstitutions,
    val traverseImpureCallables: Boolean
)

@OptIn(ExperimentalStdlibApi::class)
private val inline: DeepRecursiveFunction<FrameData<SmlAbstractExpression>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->
        when (data.current) {

            // Base cases
            is SmlBoolean -> SmlInlinedOtherExpression(data.current)
            is SmlFloat -> SmlInlinedOtherExpression(data.current)
            is SmlInfixOperation -> SmlInlinedOtherExpression(data.current)
            is SmlInt -> SmlInlinedOtherExpression(data.current)
            is SmlNull -> SmlInlinedOtherExpression(data.current)
            is SmlPrefixOperation -> SmlInlinedOtherExpression(data.current)
            is SmlString -> SmlInlinedOtherExpression(data.current)
            is SmlTemplateString -> SmlInlinedOtherExpression(data.current)
            is SmlTemplateStringStart -> SmlInlinedOtherExpression(data.current)
            is SmlTemplateStringInner -> SmlInlinedOtherExpression(data.current)
            is SmlTemplateStringEnd -> SmlInlinedOtherExpression(data.current)
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
                    data.traverseImpureCallables
                )
            )
            is SmlMemberAccess -> inlineMemberAccess.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.traverseImpureCallables
                )
            )
            is SmlReference -> inlineReference.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.traverseImpureCallables
                )
            )

            else -> throw IllegalArgumentException("Missing case to handle $this.")
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineCall: DeepRecursiveFunction<FrameData<SmlCall>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->

        // Inline receiver
        val receiver = inline.callRecursive(
            FrameData(
                current = data.current.receiver,
                substitutions = data.substitutions,
                traverseImpureCallables = data.traverseImpureCallables
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
                        traverseImpureCallables = data.traverseImpureCallables
                    )
                )

                // Check purity
                if (!data.traverseImpureCallables && !receiver.callable.isPureCallable()) {
                    return@DeepRecursiveFunction SmlInlinedOtherExpression(data.current)
                }

                // Run the call
                when (receiver) {
                    is SmlBoundBlockLambda -> {
                        SmlInlinedRecord(
                            receiver.lambda.lambdaResultsOrEmpty().map {
                                it to inlineAssignee.callRecursive(
                                    FrameData(
                                        current = it,
                                        substitutions = newSubstitutions,
                                        traverseImpureCallables = data.traverseImpureCallables
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
                                traverseImpureCallables = data.traverseImpureCallables
                            )
                        )
                    }
                    is SmlBoundStepReference -> {
                        SmlInlinedRecord(
                            receiver.step.resultsOrEmpty().map {
                                it to inlineAssignee.callRecursive(
                                    FrameData(
                                        current = it.uniqueYieldOrNull(),
                                        substitutions = newSubstitutions,
                                        traverseImpureCallables = data.traverseImpureCallables
                                    )
                                )
                            }
                        ).unwrap()
                    }
                }
            }
            else -> SmlInlinedOtherExpression(data.current)
        }
    }

private data class BuildNewSubstitutionsFrameData(
    val receiver: SmlBoundCallable,
    val call: SmlCall,
    val oldSubstitutions: ParameterSubstitutions,
    val traverseImpureCallables: Boolean
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
                            traverseImpureCallables = data.traverseImpureCallables
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
private val inlineMemberAccess: DeepRecursiveFunction<FrameData<SmlMemberAccess>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->
        SmlInlinedOtherExpression(data.current)
    }

//private fun SmlMemberAccess.simplifyMemberAccess(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlinedExpression? {
//    if (member.declaration is SmlEnumVariant) {
//        return member.simplifyReference(substitutions, traverseImpureCallables)
//    }
//
//    return when (val simpleReceiver = receiver.toInlinedExpressionOrNull(substitutions, traverseImpureCallables)) {
////        SmlConstantNull -> when {
////            isNullSafe -> SmlConstantNull
////            else -> null
////        }
//        is SmlInlinedRecord -> simpleReceiver.getSubstitutionByReferenceOrNull(member) as? SmlInlinedExpression
//        else -> null
//    }
//}

@OptIn(ExperimentalStdlibApi::class)
private val inlineReference: DeepRecursiveFunction<FrameData<SmlReference>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->
        val declaration = data.current.declaration
        when {
            !declaration.isResolved() -> null
            declaration is SmlPlaceholder -> {
                inlineAssignee.callRecursive(
                    FrameData(
                        current = declaration,
                        substitutions = data.substitutions,
                        traverseImpureCallables = data.traverseImpureCallables
                    )
                )
            }
            declaration is SmlParameter -> {
                inlineParameter.callRecursive(
                    FrameData(
                        current = declaration,
                        substitutions = data.substitutions,
                        traverseImpureCallables = data.traverseImpureCallables
                    )
                )
            }
            declaration is SmlStep -> SmlBoundStepReference(step = declaration)
            else -> SmlInlinedOtherExpression(data.current)
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineAssignee: DeepRecursiveFunction<FrameData<SmlAbstractAssignee?>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->
        val assignmentRHS = data.current
            ?.closestAncestorOrNull<SmlAssignment>()
            ?.expression
            ?: return@DeepRecursiveFunction null

        val inlinedExpression = inline.callRecursive(
            FrameData(
                current = assignmentRHS,
                substitutions = data.substitutions,
                traverseImpureCallables = data.traverseImpureCallables
            )
        )

        when (inlinedExpression) {
            null -> null
            is SmlInlinedRecord -> inlinedExpression.getSubstitutionByIndexOrNull(data.current.indexOrNull())
            else -> when {
                data.current.indexOrNull() == 0 -> inlinedExpression
                else -> null
            }
        }
    }

@OptIn(ExperimentalStdlibApi::class)
private val inlineParameter: DeepRecursiveFunction<FrameData<SmlParameter>, SmlInlinedExpression?> =
    DeepRecursiveFunction { data ->
        when {
            data.current.isVariadic -> null
            data.current in data.substitutions -> data.substitutions[data.current]
            data.current.isOptional() -> inline.callRecursive(
                FrameData(
                    current = data.current.defaultValue,
                    substitutions = data.substitutions,
                    traverseImpureCallables = data.traverseImpureCallables
                )
            )
            else -> null
        }
    }
