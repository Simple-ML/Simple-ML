package de.unibonn.simpleml.test

import com.google.inject.Inject
import de.unibonn.simpleml.test.assertions.shouldHaveNoErrorsOrWarnings
import de.unibonn.simpleml.test.util.ParseHelper
import de.unibonn.simpleml.utils.OriginalFilePath
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.validation.Issue
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class StdlibTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Inject
    private lateinit var validationHelper: ValidationTestHelper

    @Inject
    private lateinit var stdlib: SimpleMLStdlib

    @TestFactory
    fun `should not have syntax or semantic errors`(): Stream<out DynamicNode> {
        return stdlib.listStdlibFiles()
            .map { (filePath, _) ->
                val program = Files.readString(filePath)
                DynamicTest.dynamicTest(filePath.toString(), filePath.toUri()) {
                    actualIssues(filePath, program).shouldHaveNoErrorsOrWarnings()
                }
            }
            .toList()
            .stream()
    }

    private fun actualIssues(filePath: Path, program: String): List<Issue> {
        val parsingResult = parseHelper.parseProgramTextWithStdlib(program) ?: return emptyList()
        parsingResult.eResource().eAdapters().add(OriginalFilePath(filePath.toString()))
        return validationHelper.validate(parsingResult)
    }
}
