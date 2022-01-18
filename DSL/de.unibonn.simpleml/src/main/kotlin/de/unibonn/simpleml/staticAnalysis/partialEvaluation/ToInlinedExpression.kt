package de.unibonn.simpleml.staticAnalysis.partialEvaluation

import de.unibonn.simpleml.emf.argumentsOrEmpty
import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isOptional
import de.unibonn.simpleml.simpleML.SmlAbstractAssignee
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBlockLambda
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlEnumVariant
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
 * Tries to inline this expression by partially evaluating assignments, calls, member accesses, and references. On
 * success a [SmlInlinedExpression] is returned, if any reference cannot be resolved `null`.
 *
 * @param traverseImpureCallables If `false`, calls to impure callable are not traversed further but returned unchanged.
 */
@OptIn(ExperimentalStdlibApi::class)
fun SmlAbstractExpression.toInlinedExpressionOrNull(traverseImpureCallables: Boolean = false): SmlInlinedExpression? {
    return doInline.invoke(FrameData(this, emptyMap(), traverseImpureCallables))
}

private data class FrameData<T: SmlAbstractExpression>(
    val current: T,
    val substitutions: ParameterSubstitutions,
    val traverseImpureCallables: Boolean
)

@OptIn(ExperimentalStdlibApi::class)
private val doInline =
    DeepRecursiveFunction<FrameData<SmlAbstractExpression>, SmlInlinedExpression?> { data ->
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
            is SmlCall -> doInlineCall.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.traverseImpureCallables
                )
            )
            is SmlMemberAccess -> doInlineMemberAccess.callRecursive(
                FrameData(
                    data.current,
                    data.substitutions,
                    data.traverseImpureCallables
                )
            )
            is SmlReference -> doInlineReference.callRecursive(
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
private val doInlineCall =
    DeepRecursiveFunction<FrameData<SmlCall>, SmlInlinedExpression?> { data ->
        SmlInlinedOtherExpression(data.current)
    }

@OptIn(ExperimentalStdlibApi::class)
private val doInlineMemberAccess =
    DeepRecursiveFunction<FrameData<SmlMemberAccess>, SmlInlinedExpression?> { data ->
        SmlInlinedOtherExpression(data.current)
    }

@OptIn(ExperimentalStdlibApi::class)
private val doInlineReference =
    DeepRecursiveFunction<FrameData<SmlReference>, SmlInlinedExpression?> { data ->
        SmlInlinedOtherExpression(data.current)
    }

















//private fun SmlCall.simplifyCall(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlinedExpression? {
//    val simpleReceiver = simplifyReceiver(substitutions, traverseImpureCallables)
//        ?: return SmlInlinedOtherExpression(this)
//    val newSubstitutions = buildNewSubstitutions(simpleReceiver, substitutions, traverseImpureCallables)
//
//    return when (simpleReceiver) {
//        is SmlInlineBlockLambda -> {
//            SmlInlinedRecord(
//                simpleReceiver.results.map {
//                    it to it.simplifyAssignee(newSubstitutions, traverseImpureCallables)
//                }
//            )
//        }
//        is SmlInlineExpressionLambda -> simpleReceiver.result.toInlinedExpressionOrNull(
//            newSubstitutions,
//            traverseImpureCallables
//        )
//        is SmlInlineStep -> {
//            SmlInlinedRecord(
//                simpleReceiver.results.map {
//                    it to it.uniqueYieldOrNull()?.simplifyAssignee(newSubstitutions, traverseImpureCallables)
//                }
//            )
//        }
//    }
//}
//
//private fun SmlCall.simplifyReceiver(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlineCallable? {
//    return when (val simpleReceiver = receiver.toInlinedExpressionOrNull(substitutions, traverseImpureCallables)) {
//        is SmlInlinedRecord -> simpleReceiver.unwrap() as? SmlInlineCallable
//        is SmlInlineCallable -> simpleReceiver
//        else -> return null
//    }
//}
//
//private fun SmlCall.buildNewSubstitutions(
//    simpleReceiver: SmlInlineCallable,
//    oldSubstitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): ParameterSubstitutions {
//
//    val substitutionsOnCreation = simpleReceiver.substitutionsOnCreation
//
//    val substitutionsOnCall = argumentsOrEmpty()
//        .mapNotNull {
//            when (val parameter = it.parameterOrNull()) {
//                null -> null
//                else -> parameter to it.toInlinedExpressionOrNull(oldSubstitutions, traverseImpureCallables)
//            }
//        }
//
//    return buildMap {
//        putAll(substitutionsOnCreation)
//        putAll(substitutionsOnCall)
//    }
//}
//
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
//
//private fun SmlReference.simplifyReference(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlinedExpression? {
//    return when (val declaration = this.declaration) {
////        is SmlEnumVariant -> when {
////            declaration.parametersOrEmpty().isEmpty() -> SmlConstantEnumVariant(declaration)
////            else -> null
////        }
//        is SmlPlaceholder -> declaration.simplifyAssignee(substitutions, traverseImpureCallables)
//        is SmlParameter -> declaration.simplifyParameter(substitutions, traverseImpureCallables)
//        is SmlStep -> SmlBoundStepReference(callable = declaration)
//        else -> SmlInlinedOtherExpression(this)
//    }
//}
//
//private fun SmlAbstractAssignee.simplifyAssignee(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlinedExpression? {
//    val simpleFullAssignedExpression = closestAncestorOrNull<SmlAssignment>()
//        ?.expression
//        ?.toInlinedExpressionOrNull(substitutions, traverseImpureCallables)
//        ?: return null
//
//    return when (simpleFullAssignedExpression) {
//        is SmlInlinedRecord -> simpleFullAssignedExpression.getSubstitutionByIndexOrNull(indexOrNull()) as? SmlInlinedExpression
//        else -> when {
//            indexOrNull() == 0 -> simpleFullAssignedExpression
//            else -> null
//        }
//    }
//}
//
//private fun SmlParameter.simplifyParameter(
//    substitutions: ParameterSubstitutions,
//    traverseImpureCallables: Boolean
//): SmlInlinedExpression? {
//    return when {
//        isVariadic -> null
//        this in substitutions -> substitutions[this] as? SmlInlinedExpression
//        isOptional() -> defaultValue?.toInlinedExpressionOrNull(substitutions, traverseImpureCallables)
//        else -> null
//    }
//}
