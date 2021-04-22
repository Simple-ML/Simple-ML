package de.unibonn.simpleml.scoping

import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.scoping.impl.ImportNormalizer
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider

class SimpleMLImportedNamespaceAwareLocalScopeProvider: ImportedNamespaceAwareLocalScopeProvider() {
    override fun getImplicitImports(ignoreCase: Boolean): List<ImportNormalizer> {
        return listOf(
                ImportNormalizer(QualifiedName.create("simpleml", "builtin"), true, ignoreCase),
                ImportNormalizer(QualifiedName.create("simpleml", "lang"), true, ignoreCase)
        )
    }
}