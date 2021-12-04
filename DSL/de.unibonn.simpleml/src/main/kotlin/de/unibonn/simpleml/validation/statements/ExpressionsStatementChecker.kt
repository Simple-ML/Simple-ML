package de.unibonn.simpleml.validation.statements

import de.unibonn.simpleml.simpleML.SmlExpressionStatement
import de.unibonn.simpleml.utils.hasSideEffects
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

class ExpressionsStatementChecker : AbstractSimpleMLChecker() {

    @Check
    fun hasNoEffect(smlExpressionStatement: SmlExpressionStatement) {
        if (!smlExpressionStatement.expression.hasSideEffects()) {
            warning(
                "This statement does nothing.",
                null,
                STATEMENT_DOES_NOTHING
            )
        }
    }
}
