package de.unibonn.simpleml.validation.statements

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlLambdaYield
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlWildcard
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.utils.AssignedResult
import de.unibonn.simpleml.utils.assigneesOrEmpty
import de.unibonn.simpleml.utils.hasSideEffects
import de.unibonn.simpleml.utils.maybeAssigned
import de.unibonn.simpleml.utils.resultsOrNull
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val ASSIGNEE_WITHOUT_VALUE = "ASSIGNEE_WITHOUT_VALUE"
const val ASSIGNEE_LIST_CAN_BE_REMOVED = "ASSIGNEE_LIST_CAN_BE_REMOVED"
const val IMPLICITLY_IGNORED_RESULT_OF_CALL = "IMPLICITLY_IGNORED_RESULT_OF_CALL"
const val STATEMENT_DOES_NOTHING = "STATEMENT_DOES_NOTHING"

class AssignmentChecker : AbstractSimpleMLChecker() {

    @Check
    fun assigneeListCanBeRemoved(smlAssignment: SmlAssignment) {
        if (smlAssignment.assigneesOrEmpty().all { it is SmlWildcard }) {
            warning(
                "The left-hand side of this assignment can be removed.",
                null,
                ASSIGNEE_LIST_CAN_BE_REMOVED
            )
        }
    }

    @Check
    fun assigneeWithoutValue(smlAssignment: SmlAssignment) {
        smlAssignment.assigneesOrEmpty()
            .filter { it.maybeAssigned() == AssignedResult.NotAssigned }
            .forEach {
                error(
                    "No value is assigned to this assignee.",
                    it,
                    null,
                    ASSIGNEE_WITHOUT_VALUE
                )
            }
    }

    @Check
    fun hasNoEffect(smlAssignment: SmlAssignment) {
        if (smlAssignment.assigneesOrEmpty().any { it is SmlPlaceholder || it is SmlYield || it is SmlLambdaYield }) {
            return
        }

        if (!smlAssignment.expression.hasSideEffects()) {
            warning(
                "This statement does nothing.",
                null,
                STATEMENT_DOES_NOTHING
            )
        }
    }

    @Check
    fun ignoredResultOfCall(smlAssignment: SmlAssignment) {
        val expression = smlAssignment.expression
        if (expression is SmlCall) {
            val results = (expression.resultsOrNull() ?: listOf())
            val unassignedResults = results.drop(smlAssignment.assigneesOrEmpty().size)

            unassignedResults.forEach {
                warning(
                    "The result '${it.name}' is implicitly ignored.",
                    Literals.SML_ASSIGNMENT__ASSIGNEE_LIST,
                    IMPLICITLY_IGNORED_RESULT_OF_CALL
                )
            }
        }
    }
}
