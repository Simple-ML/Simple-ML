package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.constants.isInStubFile
import de.unibonn.simpleml.constants.isInTestFile
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.utils.SimpleMLIndexExtensions
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.importedNameOrNull
import de.unibonn.simpleml.utils.isQualified
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

const val REDECLARATION_IN_OTHER_FILE = "REDECLARATION_IN_OTHER_FILE"
const val NO_BACKTICKS_IN_PACKAGE_NAME = "NO_BACKTICKS_IN_PACKAGE_NAME"

class PackageChecker @Inject constructor(
    private val indexExtensions: SimpleMLIndexExtensions,
    private val qualifiedNameProvider: IQualifiedNameProvider
) : AbstractSimpleMLChecker() {

    @Check
    fun members(smlPackage: SmlPackage) {
        if (smlPackage.isInStubFile()) {
            smlPackage.members
                .filter { it is SmlWorkflow || it is SmlWorkflowStep }
                .forEach {
                    error(
                        "A stub file must not declare workflows or workflow steps.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.STUB_FILE_MUST_NOT_DECLARE_WORKFLOWS
                    )
                }
        } else if (!smlPackage.isInTestFile()) {
            smlPackage.members
                .filter { it !is SmlWorkflow && it !is SmlWorkflowStep }
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
    fun name(smlPackage: SmlPackage) {
        if (smlPackage.name.contains("`")) {
            error(
                "The package name must not contain backticks.",
                Literals.SML_ABSTRACT_DECLARATION__NAME,
                NO_BACKTICKS_IN_PACKAGE_NAME
            )
        }
    }

    @Check
    fun uniqueNames(smlPackage: SmlPackage) {
        val namedEObjects = smlPackage.imports.filter { it.isQualified() } + smlPackage.members

        namedEObjects.duplicatesBy {
            when (it) {
                is SmlImport -> it.importedNameOrNull()
                is SmlAbstractDeclaration -> it.name
                else -> throw AssertionError("$it is neither an import nor a declaration.")
            }
        }.forEach {
            when {
                it is SmlImport && it.alias == null -> {
                    error(
                        "A declaration with name '${it.importedNameOrNull()}' exists already in this file.",
                        it,
                        Literals.SML_IMPORT__IMPORTED_NAMESPACE,
                        ErrorCode.REDECLARATION
                    )
                }
                it is SmlImport && it.alias != null -> {
                    error(
                        "A declaration with name '${it.importedNameOrNull()}' exists already in this file.",
                        it.alias,
                        Literals.SML_IMPORT_ALIAS__NAME,
                        ErrorCode.REDECLARATION
                    )
                }
                it is SmlAbstractDeclaration -> {
                    error(
                        "A declaration with name '${it.name}' exists already in this file.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.REDECLARATION
                    )
                }
            }
        }
    }

    @Check(CheckType.NORMAL)
    fun uniqueNamesAcrossFiles(smlPackage: SmlPackage) {

        // Since the stdlib is automatically loaded into a workspace, every declaration would be marked as a duplicate
        // when editing the stdlib
        if (smlPackage.isInStubFile() && smlPackage.name.startsWith("simpleml")) {
            return
        }

        val externalGlobalDeclarations =
            indexExtensions.visibleExternalGlobalDeclarationDescriptions(smlPackage)

        smlPackage.members.forEach {
            val qualifiedName = qualifiedNameProvider.getFullyQualifiedName(it)
            if (qualifiedName in externalGlobalDeclarations) {
                error(
                    "A declaration with qualified name '$qualifiedName' exists already.",
                    it,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    REDECLARATION_IN_OTHER_FILE
                )
            }
        }
    }
}
