package de.unibonn.simpleml.scoping

import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.scoping.impl.ImportNormalizer
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider

class SimpleMLImportedNamespaceAwareLocalScopeProvider : ImportedNamespaceAwareLocalScopeProvider() {

    /**
     * Import all declarations from the listed packages implicitly, such as "simpleml.lang".
     */
    override fun getImplicitImports(ignoreCase: Boolean): List<ImportNormalizer> {
        return listOf(
            ImportNormalizer(QualifiedName.create("simpleml", "lang"), true, ignoreCase)
        )
    }

    /**
     * Import all declarations in the same package implicitly.
     *
     * See Xtext book page 278 for more information.
     */
    override fun internalGetImportedNamespaceResolvers(
        context: EObject?,
        ignoreCase: Boolean
    ): MutableList<ImportNormalizer> {
        val resolvers: MutableList<ImportNormalizer> = super.internalGetImportedNamespaceResolvers(context, ignoreCase)
        if (context is SmlCompilationUnit) {
            qualifiedNameProvider.getFullyQualifiedName(context)?.let {
                resolvers += ImportNormalizer(
                    it,
                    true,
                    ignoreCase
                )
            }
        }
        return resolvers
    }
}
