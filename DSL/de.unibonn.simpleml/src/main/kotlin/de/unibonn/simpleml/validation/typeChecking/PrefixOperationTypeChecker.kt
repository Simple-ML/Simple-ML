package de.unibonn.simpleml.validation.typeChecking

import de.unibonn.simpleml.constant.SmlPrefixOperationOperator
import de.unibonn.simpleml.constant.operator
import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlPrefixOperation
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.staticAnalysis.typing.ClassType
import de.unibonn.simpleml.staticAnalysis.typing.type
import de.unibonn.simpleml.stdlibAccess.StdlibClasses
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class PrefixOperationTypeChecker : AbstractSimpleMLChecker() {

    @Check
    fun operand(smlPrefixOperation: SmlPrefixOperation) {
        val operand = smlPrefixOperation.operand

        if (operand is SmlReference && !operand.declaration.isResolved()) {
            return
        }

        val operandType = operand.type()

        when (smlPrefixOperation.operator()) {
            SmlPrefixOperationOperator.Not -> {
                val hasWrongType = operandType !is ClassType ||
                        operandType.isNullable ||
                        operandType.smlClass.qualifiedNameOrNull() != StdlibClasses.Boolean

                if (hasWrongType) {
                    error(
                        "The operand of a logical negation must be an instance of the class 'Boolean'.",
                        Literals.SML_PREFIX_OPERATION__OPERAND,
                        ErrorCode.WrongType
                    )
                }
            }
            SmlPrefixOperationOperator.Minus -> {
                val hasWrongType = operandType !is ClassType ||
                        operandType.isNullable ||
                        operandType.smlClass.qualifiedNameOrNull() !in setOf(StdlibClasses.Float, StdlibClasses.Int)

                if (hasWrongType) {
                    error(
                        "The operand of an arithmetic negation must be an instance of the class 'Float' or the class 'Int'.",
                        Literals.SML_PREFIX_OPERATION__OPERAND,
                        ErrorCode.WrongType
                    )
                }
            }
        }
    }
}
