package de.unibonn.simpleml.ide.symbol

import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import org.eclipse.emf.ecore.EClass
import org.eclipse.lsp4j.SymbolKind
import org.eclipse.xtext.ide.server.symbol.DocumentSymbolMapper

class SimpleMLDocumentSymbolKindProvider : DocumentSymbolMapper.DocumentSymbolKindProvider() {
    override fun getSymbolKind(clazz: EClass): SymbolKind {
        return when (clazz) {
            Literals.SML_ANNOTATION -> SymbolKind.Interface // Not ideal but matches @interface in Java
            Literals.SML_CLASS -> SymbolKind.Class
            Literals.SML_COMPILATION_UNIT -> SymbolKind.File
            Literals.SML_ENUM -> SymbolKind.Enum
            Literals.SML_ENUM_VARIANT -> SymbolKind.EnumMember
            Literals.SML_WORKFLOW -> SymbolKind.Function
            Literals.SML_WORKFLOW_STEP -> SymbolKind.Function
            else -> super.getSymbolKind(clazz)
        }
    }
}
