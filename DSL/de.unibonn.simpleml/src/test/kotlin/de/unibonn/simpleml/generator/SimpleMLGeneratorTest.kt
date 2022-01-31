package de.unibonn.simpleml.generator

import com.google.inject.Inject
import de.unibonn.simpleml.emf.resourceSetOrNull
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.xbase.testing.CompilationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLGeneratorTest {

    @Inject
    private lateinit var compilationTestHelper: CompilationTestHelper

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Test
    fun test() {
        val compilationUnit = parseHelper.parseResource("generator/literals/input.smltest")
        compilationTestHelper.compile(compilationUnit?.resourceSetOrNull()) { result ->
            result.allGeneratedResources.forEach {
                println(it.key)
                println(it.value)
            }
        }
    }

//    private fun compile(
//        resourceName: String,
//        acceptor: IAcceptor<CompilationTestHelper.Result>
//    ): List<Issue> {
//        val parsingResult = parseHelper.parseResourceWithStdlib(resourceName) ?: return emptyList()
//        parsingResult.eResource().eAdapters().add(OriginalFilePath(resourceName))
//        return validationHelper.validate(parsingResult)
//    }
}
