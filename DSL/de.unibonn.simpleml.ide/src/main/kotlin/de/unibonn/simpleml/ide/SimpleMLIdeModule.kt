package de.unibonn.simpleml.ide

import de.unibonn.simpleml.ide.contentassist.SimpleMLIdeContentProposalProvider
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider
import org.eclipse.xtext.ide.server.hover.HoverService


/**
 * Use this class to register ide components.
 */
class SimpleMLIdeModule : AbstractSimpleMLIdeModule() {
    fun bindIdeContentProposalProvider(): Class<out IdeContentProposalProvider?> {
        return SimpleMLIdeContentProposalProvider::class.java
    }

    fun bindHoverService(): Class<out HoverService?> {
        return SimpleMLHoverService::class.java
    }
}
