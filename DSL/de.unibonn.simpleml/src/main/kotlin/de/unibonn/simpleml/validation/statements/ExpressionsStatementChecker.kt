package de.unibonn.simpleml.validation.statements

import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.staticAnalysis.isPureExpression
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.WarningCode
import org.eclipse.xtext.validation.Check

class ExpressionsStatementChecker : AbstractSimpleMLChecker() {

    @Check
    fun hasNoEffect(smlExpressionStatement: SmlExpressionStatement) {
        if (smlExpressionStatement.expression.isPureExpression()) {
            warning(
                "This statement does nothing.",
                null,
                WarningCode.StatementDoesNothing
            )
        }
    }
}
