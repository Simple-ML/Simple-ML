package de.unibonn.simpleml.ide.codelens

import com.google.inject.Inject
import de.unibonn.simpleml.ide.command.CommandId
import de.unibonn.simpleml.simpleML.SmlArgumentList
import org.eclipse.lsp4j.CodeLens
import org.eclipse.lsp4j.CodeLensParams
import org.eclipse.lsp4j.Command
import org.eclipse.xtext.ide.server.Document
import org.eclipse.xtext.ide.server.codelens.ICodeLensResolver
import org.eclipse.xtext.ide.server.codelens.ICodeLensService
import org.eclipse.xtext.ide.server.symbol.DocumentSymbolMapper
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.service.OperationCanceledManager
import org.eclipse.xtext.util.CancelIndicator


class SimpleMLCodeLensProvider : ICodeLensResolver, ICodeLensService {

    @Inject
    private lateinit var operationCanceledManager: OperationCanceledManager

    @Inject
    private lateinit var rangeProvider: DocumentSymbolMapper.DocumentSymbolRangeProvider

    override fun computeCodeLenses(
        document: Document,
        resource: XtextResource,
        params: CodeLensParams,
        indicator: CancelIndicator
    ): List<CodeLens> {
        return markArgumentLists(resource, indicator)
    }

    private fun markArgumentLists(
        resource: XtextResource,
        indicator: CancelIndicator
    ): MutableList<CodeLens> {

        val result = mutableListOf<CodeLens>()
        for (obj in resource.allContents) {
            operationCanceledManager.checkCanceled(indicator)

            if (obj is SmlArgumentList) {
                result += CodeLens(
                    rangeProvider.getRange(obj),
                    Command("...", CommandId.MoreParameters.toString()),
                    null
                )
            }
        }

        return result
    }

    override fun resolveCodeLens(
        document: Document,
        resource: XtextResource,
        codeLens: CodeLens,
        indicator: CancelIndicator
    ): CodeLens {
        return codeLens
    }
}
