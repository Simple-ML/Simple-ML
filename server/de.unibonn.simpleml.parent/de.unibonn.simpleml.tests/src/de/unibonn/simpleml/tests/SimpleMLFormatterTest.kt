package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.tests.util.CategorizedTest
import de.unibonn.simpleml.tests.util.createDynamicTestsFromResourceFolder
import de.unibonn.simpleml.tests.util.getResourcePath
import de.unibonn.simpleml.tests.util.testDisplayName
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.formatter.FormatterTestHelper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Path
import java.util.stream.Stream

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLFormatterTest {

    @Inject
    lateinit var formatter: FormatterTestHelper

    private val separator = "-".repeat(120)

    @TestFactory
    fun `should be formatted properly`(): Stream<out DynamicNode> {
        return javaClass.classLoader
                .getResourcePath("formattingTests")
                ?.createDynamicTestsFromResourceFolder(::validateTestFile, ::createTest)
                ?: Stream.empty()
    }

    /**
     * Checks if the given program is a valid test. If there are issues a description of the issue is returned, otherwise
     * this returns null.
     */
    private fun validateTestFile(program: String): String? {
        if (separator !in program) {
            return "Did not find a separator between the original and the formatted code."
        }

        return null
    }

    private fun createTest(resourcePath: Path, filePath: Path, program: String) = sequence {
        yield(CategorizedTest(
                "correctly_formatted",
                DynamicTest.dynamicTest(testDisplayName(resourcePath, filePath), filePath.toUri()) {
                    assertFormatted(toBeFormatted(program), expectedResult(program))
                }
        ))
    }

    private fun toBeFormatted(program: String): String {
        return program.split(separator)[0].trim()
    }

    private fun expectedResult(program: String): String {
        return program.split(separator)[1].trim()
    }

    private fun assertFormatted(toBeFormatted: String, expectedResult: String) {
        formatter.assertFormatted {
            it.toBeFormatted = toBeFormatted
            it.expectation = expectedResult + System.lineSeparator()
        }
    }
}