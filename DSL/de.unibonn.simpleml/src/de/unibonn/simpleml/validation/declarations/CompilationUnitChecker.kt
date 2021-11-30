package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.SimpleMLIndexExtensions
import de.unibonn.simpleml.utils.isInStubFile
import de.unibonn.simpleml.utils.isInTestFile
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

const val REDECLARATION_IN_OTHER_FILE = "REDECLARATION_IN_OTHER_FILE"
const val STUB_FILE_MUST_DECLARE_PACKAGE = "STUB_FILE_MUST_DECLARE_PACKAGE"
const val STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS = "STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS"
const val WORKFLOW_FILE_MUST_DECLARE_PACKAGE = "WORKFLOW_FILE_MUST_DECLARE_PACKAGE"
const val WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_WORKFLOW_STEPS =
    "WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_FUNCTIONS"

class CompilationUnitChecker @Inject constructor(
    private val indexExtensions: SimpleMLIndexExtensions,
    private val qualifiedNameProvider: IQualifiedNameProvider
) : AbstractSimpleMLChecker() {

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
            if (smlCompilationUnit.name == null) {
                error(
                    "A stub file must declare its package.",
                    Literals.SML_COMPILATION_UNIT__NAME,
                    STUB_FILE_MUST_DECLARE_PACKAGE
                )
            }
        } else {
            if (smlCompilationUnit.name == null) {
                error(
                    "A workflow file must declare its package.",
                    Literals.SML_COMPILATION_UNIT__NAME,
                    WORKFLOW_FILE_MUST_DECLARE_PACKAGE
                )
            }
        }
    }

    @Check
    fun uniqueNames(smlCompilationUnit: SmlCompilationUnit) {
        smlCompilationUnit.members
            .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this file." }
    }

    @Check(CheckType.NORMAL)
    fun uniqueNamesAcrossFiles(smlCompilationUnit: SmlCompilationUnit) {

        // Since the stdlib is automatically loaded into a workspace every declaration would be marked as a duplicate
        // when editing the stdlib
        if (smlCompilationUnit.name.startsWith("simpleml")) {
            return
        }

        val externalGlobalDeclarations =
            indexExtensions.visibleExternalGlobalDeclarationDescriptions(smlCompilationUnit)
        smlCompilationUnit.members.forEach {
            val qualifiedName = qualifiedNameProvider.getFullyQualifiedName(it)
            if (qualifiedName in externalGlobalDeclarations) {
                error(
                    "A declaration with qualified name '$qualifiedName' exists already.",
                    it,
                    Literals.SML_DECLARATION__NAME,
                    REDECLARATION_IN_OTHER_FILE
                )
            }
        }
    }
}
