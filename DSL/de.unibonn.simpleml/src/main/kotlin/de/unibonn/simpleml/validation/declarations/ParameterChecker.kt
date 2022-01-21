package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAbstractLambda
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.staticAnalysis.partialEvaluation.toConstantExpressionOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

class ParameterChecker : AbstractSimpleMLChecker() {

    @Check
    fun type(smlParameter: SmlParameter) {
        val parameterList = smlParameter.closestAncestorOrNull<SmlParameterList>() ?: return
        if (smlParameter.type == null && parameterList.eContainer() !is SmlAbstractLambda) {
            error(
                "A parameter must have a type.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                ErrorCode.ParameterMustHaveType
            )
        }
    }

    @Check(CheckType.NORMAL)
    fun defaultValueMustBeConstant(smlParameter: SmlParameter) {
        val defaultValue = smlParameter.defaultValue ?: return
        if (defaultValue.toConstantExpressionOrNull() == null) {
            error(
                "Default values of parameters must be constant.",
                Literals.SML_PARAMETER__DEFAULT_VALUE,
                ErrorCode.MustBeConstant
            )
        }
    }
}
