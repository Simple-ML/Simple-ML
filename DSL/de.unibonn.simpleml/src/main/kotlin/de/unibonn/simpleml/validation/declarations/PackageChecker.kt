package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.constant.isInStubFile
import de.unibonn.simpleml.constant.isInTestFile
import de.unibonn.simpleml.emf.importedNameOrNull
import de.unibonn.simpleml.emf.isQualified
import de.unibonn.simpleml.emf.packageMembersOrEmpty
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.scoping.externalGlobalDeclarations
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAbstractDeclaration
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.simpleML.SmlPackage
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

class PackageChecker @Inject constructor(
    private val qualifiedNameProvider: IQualifiedNameProvider
) : AbstractSimpleMLChecker() {

    @Check
    fun members(smlPackage: SmlPackage) {
        if (smlPackage.isInStubFile()) {
            smlPackage.members
                .filter { it is SmlWorkflow || it is SmlStep }
                .forEach {
                    error(
                        "A stub file must not declare workflows or steps.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.StubFileMustNotDeclareWorkflowsOrSteps
                    )
                }
        } else if (!smlPackage.isInTestFile()) {
            smlPackage.members
                .filter { it !is SmlWorkflow && it !is SmlStep }
                .forEach {
                    error(
                        "A workflow file must only declare workflows and steps.",
                        it,
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.WorkflowFileMustOnlyDeclareWorkflowsAndSteps
                    )
                }
        }
    }

    @Check
    fun uniqueNames(smlPackage: SmlPackage) {
        val namedEObjects = smlPackage.imports.filter { it.isQualified() } + smlPackage.members

        namedEObjects.duplicatesBy {
            when (it) {
                is SmlImport -> it.importedNameOrNull()
                is SmlAbstractDeclaration -> it.name
                // Should never happen
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

        val externalGlobalDeclarations = smlPackage.externalGlobalDeclarations()
        smlPackage.packageMembersOrEmpty().forEach { member ->
            val qualifiedName = member.qualifiedNameOrNull()
            if (externalGlobalDeclarations.any { it.qualifiedName == qualifiedName }) {
                error(
                    "A declaration with qualified name '$qualifiedName' exists already.",
                    member,
                    Literals.SML_ABSTRACT_DECLARATION__NAME,
                    ErrorCode.REDECLARATION_IN_OTHER_FILE
                )
            }
        }
    }
}
