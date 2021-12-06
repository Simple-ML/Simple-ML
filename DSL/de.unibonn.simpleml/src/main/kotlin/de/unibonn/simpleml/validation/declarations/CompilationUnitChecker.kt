package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.constants.isInStubFile
import de.unibonn.simpleml.constants.isInTestFile
import de.unibonn.simpleml.emf.packageOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.REDECLARATION
import org.eclipse.xtext.validation.Check

const val STUB_FILE_MUST_DECLARE_PACKAGE = "STUB_FILE_MUST_DECLARE_PACKAGE"
const val STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS = "STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS"
const val WORKFLOW_FILE_MUST_DECLARE_PACKAGE = "WORKFLOW_FILE_MUST_DECLARE_PACKAGE"
const val WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_WORKFLOW_STEPS =
    "WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_FUNCTIONS"

class CompilationUnitChecker : AbstractSimpleMLChecker() {

    @Check
    fun members(smlCompilationUnit: SmlCompilationUnit) {
        if (smlCompilationUnit.isInStubFile()) {
            smlCompilationUnit.members
                .filter { it is SmlWorkflow || it is SmlWorkflowStep }
                .forEach {
                    error(
                        "A stub file must not declare workflows or workflow steps.",
                        it,
                        Literals.SML_DECLARATION__NAME,
                        STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS
                    )
                }
        } else if (!smlCompilationUnit.isInTestFile()) {
            smlCompilationUnit.members
                .filter { it !is SmlWorkflow && it !is SmlWorkflowStep }
                .forEach {
                    error(
                        "A workflow file must only declare workflows and workflow steps.",
                        it,
                        Literals.SML_DECLARATION__NAME,
                        WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_WORKFLOW_STEPS
                    )
                }
        }
    }

    @Check
    fun packageDeclaration(smlCompilationUnit: SmlCompilationUnit) {
        if (smlCompilationUnit.isInStubFile()) {
            if (smlCompilationUnit.packageOrNull()?.name == null) {
                error(
                    "A stub file must declare its package.",
                    null,
                    STUB_FILE_MUST_DECLARE_PACKAGE
                )
            }
        } else if (!smlCompilationUnit.isInTestFile()) {
            if (smlCompilationUnit.packageOrNull()?.name == null) {
                error(
                    "A workflow file must declare its package.",
                    null,
                    WORKFLOW_FILE_MUST_DECLARE_PACKAGE
                )
            }
        }
    }

    @Check
    fun uniqueNames(smlCompilationUnit: SmlCompilationUnit) {
        smlCompilationUnit.members
            .duplicatesBy { it.name }
            .forEach {
                error(
                    "A declaration with name '${it.name}' exists already in this file.",
                    it,
                    Literals.SML_DECLARATION__NAME,
                    REDECLARATION
                )

            }
    }
}
