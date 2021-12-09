package de.unibonn.simpleml.ide

import de.unibonn.simpleml.ide.contentassist.SimpleMLIdeContentProposalProvider
import de.unibonn.simpleml.ide.symbol.SimpleMLDocumentSymbolDeprecationInfoProvider
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

    fun bindIdeContentProposalProvider(): Class<out IdeContentProposalProvider> {
        return SimpleMLIdeContentProposalProvider::class.java
    }

    fun bindHoverService(): Class<out HoverService> {
        return SimpleMLHoverService::class.java
    }
}
