package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlImport
import de.unibonn.simpleml.utils.SimpleMLIndexExtensions
import de.unibonn.simpleml.utils.alias
import de.unibonn.simpleml.utils.isQualified
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

const val UNRESOLVED_IMPORTED_NAMESPACE = "UNRESOLVED_IMPORTED_NAMESPACE"
const val WILDCARD_IMPORT_WITH_ALIAS = "WILDCARD_IMPORT_WITH_ALIAS"

class ImportChecker @Inject constructor(
    private val indexExtensions: SimpleMLIndexExtensions
) : AbstractSimpleMLChecker() {

    @Check(CheckType.NORMAL)
    fun unresolvedNamespace(smlImport: SmlImport) {
        val availableNamespaces =
            indexExtensions.visibleGlobalDeclarationDescriptions(smlImport).map { it.qualifiedName }

        if (smlImport.isQualified()) {
            val importedNamespace = QualifiedName.create(
                smlImport.importedNamespace.split(".")
            )
            if (availableNamespaces.none { it == importedNamespace }) {
                error(
                    "No declaration with qualified name '$importedNamespace' exists.",
                    Literals.SML_IMPORT__IMPORTED_NAMESPACE,
                    UNRESOLVED_IMPORTED_NAMESPACE
                )
            }
        } else {
            val importedNamespace = QualifiedName.create(
                smlImport.importedNamespace.removeSuffix(".*").split(".")
            )
            if (availableNamespaces.none { it.startsWith(importedNamespace) }) {
                error(
                    "No package with qualified name '$importedNamespace' exists.",
                    Literals.SML_IMPORT__IMPORTED_NAMESPACE,
                    UNRESOLVED_IMPORTED_NAMESPACE
                )
            }
        }
    }

    @Check
    fun wildcardImportWithAlias(smlImport: SmlImport) {
        if (!smlImport.isQualified() && smlImport.alias() != null) {
            error(
                "A wildcard import must not have an alias.",
                Literals.SML_IMPORT__ALIAS_DECLARATION,
                WILDCARD_IMPORT_WITH_ALIAS
            )
        }
    }
}
