package de.unibonn.simpleml.ide.command

import com.google.inject.Inject
import org.eclipse.lsp4j.ExecuteCommandParams
import org.eclipse.xtext.ide.server.ILanguageServerAccess
import org.eclipse.xtext.ide.server.commands.IExecutableCommandService
import org.eclipse.xtext.service.OperationCanceledManager
import org.eclipse.xtext.util.CancelIndicator

class SimpleMLExecutableCommandService : IExecutableCommandService {

    @Inject
    private lateinit var operationCanceledManager: OperationCanceledManager

    override fun initialize(): MutableList<String> {
        println("initialized")

        return mutableListOf(CommandId.MoreParameters.toString())
    }

    override fun execute(
        params: ExecuteCommandParams,
        access: ILanguageServerAccess,
        cancelIndicator: CancelIndicator
    ): Any {

        println(params)

        return when (params.command) {
            CommandId.MoreParameters.toString() -> {
                "a"
            }
            else -> {
                throw IllegalArgumentException("Unknown command '${params.command}'.")
            }
        }
    }
}
