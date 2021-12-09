package de.unibonn.simpleml.ide

import de.unibonn.simpleml.ide.contentassist.SimpleMLIdeContentProposalProvider
import de.unibonn.simpleml.ide.symbol.SimpleMLDocumentSymbolDeprecationInfoProvider
import de.unibonn.simpleml.ide.symbol.SimpleMLDocumentSymbolDetailsProvider
import de.unibonn.simpleml.ide.symbol.SimpleMLDocumentSymbolKindProvider
import de.unibonn.simpleml.ide.symbol.SimpleMLDocumentSymbolNameProvider
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider
import org.eclipse.xtext.ide.server.hover.HoverService
import org.eclipse.xtext.ide.server.symbol.DocumentSymbolMapper

/**
 * Use this class to register ide components.
 */
class SimpleMLIdeModule : AbstractSimpleMLIdeModule() {
    fun bindDocumentSymbolDeprecationInfoProvider(): Class<out DocumentSymbolMapper.DocumentSymbolDeprecationInfoProvider> {
        return SimpleMLDocumentSymbolDeprecationInfoProvider::class.java
    }

    fun bindDocumentSymbolDetailsProvider(): Class<out DocumentSymbolMapper.DocumentSymbolDetailsProvider> {
        return SimpleMLDocumentSymbolDetailsProvider::class.java
    }

    fun bindDocumentSymbolKindProvider(): Class<out DocumentSymbolMapper.DocumentSymbolKindProvider> {
        return SimpleMLDocumentSymbolKindProvider::class.java
    }

    fun bindDocumentSymbolNameProvider(): Class<out DocumentSymbolMapper.DocumentSymbolNameProvider> {
        return SimpleMLDocumentSymbolNameProvider::class.java
    }

    fun bindIdeContentProposalProvider(): Class<out IdeContentProposalProvider> {
        return SimpleMLIdeContentProposalProvider::class.java
    }

    fun bindHoverService(): Class<out HoverService> {
        return SimpleMLHoverService::class.java
    }
}
