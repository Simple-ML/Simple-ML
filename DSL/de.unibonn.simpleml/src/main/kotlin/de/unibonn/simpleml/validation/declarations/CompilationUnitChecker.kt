package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.constant.isInStubFile
import de.unibonn.simpleml.constant.isInTestFile
import de.unibonn.simpleml.emf.memberDeclarationsOrEmpty
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class CompilationUnitChecker : AbstractSimpleMLChecker() {

    @Check
    fun members(smlCompilationUnit: SmlCompilationUnit) {
        if (smlCompilationUnit.isInStubFile()) {
            smlCompilationUnit.memberDeclarationsOrEmpty()
                .filter { it is SmlWorkflow || it is SmlWorkflowStep }
                .forEach {
                    error(
                        "A stub file must not declare workflows or workflow steps.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS
                    )
                }
        } else if (!smlCompilationUnit.isInTestFile()) {
            smlCompilationUnit.memberDeclarationsOrEmpty()
                .filter { it !is SmlPackage && it !is SmlWorkflow && it !is SmlWorkflowStep }
                .forEach {
                    error(
                        "A workflow file must only declare workflows and workflow steps.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.WORKFLOW_FILE_MUST_ONLY_DECLARE_WORKFLOWS_AND_WORKFLOW_STEPS
                    )
                }
        }
    }

    @Check
    fun packageDeclarationPosition(smlCompilationUnit: SmlCompilationUnit) {
        smlCompilationUnit.members
            .asSequence()
            .drop(1)
            .filterIsInstance<SmlPackage>()
            .forEach {
                error(
                    "The package declaration and imports must come first.",
                    it,
                    null,
                    ErrorCode.PACKAGE_MUST_COME_FIRST
                )
            }
    }

    @Check
    fun uniquePackageDeclaration(smlCompilationUnit: SmlCompilationUnit) {
        if (smlCompilationUnit.isInTestFile()) {
            return
        }

        val packageDeclarations = smlCompilationUnit.members.filterIsInstance<SmlPackage>()
        if (packageDeclarations.size > 1) {
            packageDeclarations.asSequence()
                .drop(1)
                .forEach {
                    error(
                        "A file must have only one package.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.FILE_MUST_HAVE_ONLY_ONE_PACKAGE
                    )
                }
        } else if (smlCompilationUnit.uniquePackageOrNull()?.name == null) {
            error(
                "A file must declare its package.",
                null,
                ErrorCode.FILE_MUST_DECLARE_PACKAGE
            )
        }
    }

    @Check
    fun uniqueNames(smlCompilationUnit: SmlCompilationUnit) {
        smlCompilationUnit.memberDeclarationsOrEmpty()
            .duplicatesBy { it.name }
            .forEach {
                error(
                    "A declaration with name '${it.name}' exists already in this file.",
                    it,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    ErrorCode.REDECLARATION
                )
            }
    }
}
