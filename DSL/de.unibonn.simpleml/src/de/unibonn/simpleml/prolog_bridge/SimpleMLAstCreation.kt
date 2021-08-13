package de.unibonn.simpleml.prolog_bridge

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlBoolean
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.resource.SaveOptions
import java.io.IOException

private fun createSmlBooleanLiteral(isTrue: Boolean): SmlBoolean {
    return SimpleMLFactory.eINSTANCE.createSmlBoolean().apply {
        this.isTrue = isTrue
    }
}

class SimpleMLAstCreation {

    @Inject
    private val resourceSetProvider: Provider<ResourceSet>? = null

    fun run() {
        val resourceSet = resourceSetProvider!!.get()
        val fileURI = URI.createFileURI("output.simpleml")
        val resource = resourceSet.createResource(fileURI)

//        resource.contents += SimpleMLFactory.eINSTANCE.createSmlCompilationUnit().apply {
//            declarations += SimpleMLFactory.eINSTANCE.createSmlWorkflow().apply {
//                name = "predictSpeeeeeeeeed"
//                statements += SimpleMLFactory.eINSTANCE.createSmlExpressionStatement().apply {
//                    expression = SimpleMLFactory.eINSTANCE.createSmlTopLevelCall().apply {
//                        arguments += SimpleMLFactory.eINSTANCE.createSmlArgument().apply {
//                            parameter = SimpleMLFactory.eINSTANCE.createSmlParameter().apply {
//                                name = "param"
//                            }
//                            value = createSmlBooleanLiteral(true)
//                        }
//                        callable = SimpleMLFactory.eINSTANCE.createSmlProcess().apply {
//                            name = "called"
//                        }
//                        lambda = SimpleMLFactory.eINSTANCE.createSmlLambda()
//                    }
//                }
//                eAdapters().add(MultiLineCommentAdapter("bla\nblabla"))
//            }
//            declarations += SimpleMLFactory.eINSTANCE.createSmlClass().apply {
//                name = "Int"
//            }
//        }

        // To create the node model, this class might be a possible starting point (did not check yet)
        // new NodeModelBuilder()
        // See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#serialization

        // We could also relax the criteria simpleml(prolog(program)) == program to
        // format(simpleml(prolog(program))) == format(program)
        // where format automatically formats the given program. Then we would not need to bother with syntactic
        // information at all and only restore comments.
        try {
            val saveOptions = SaveOptions
                    .newBuilder()
                    .format()
                    .options
                    .toOptionsMap()
            resource.save(saveOptions)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val injector = SimpleMLStandaloneSetup().createInjectorAndDoEMFRegistration()
            val creator = injector.getInstance(SimpleMLAstCreation::class.java)
            creator.run()
        }
    }
}