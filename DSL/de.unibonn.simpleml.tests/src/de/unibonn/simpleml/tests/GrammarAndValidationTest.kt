package de.unibonn.simpleml.tests

import com.google.inject.Inject
import de.unibonn.simpleml.tests.assertions.shouldHaveNoIssue
import de.unibonn.simpleml.tests.assertions.shouldHaveNoSemanticError
import de.unibonn.simpleml.tests.assertions.shouldHaveNoSemanticInfo
import de.unibonn.simpleml.tests.assertions.shouldHaveNoSemanticWarning
import de.unibonn.simpleml.tests.assertions.shouldHaveNoSyntaxError
import de.unibonn.simpleml.tests.assertions.shouldHaveSemanticError
import de.unibonn.simpleml.tests.assertions.shouldHaveSemanticInfo
import de.unibonn.simpleml.tests.assertions.shouldHaveSemanticWarning
import de.unibonn.simpleml.tests.assertions.shouldHaveSyntaxError
import de.unibonn.simpleml.tests.util.CategorizedTest
import de.unibonn.simpleml.tests.util.ParseHelper
import de.unibonn.simpleml.tests.util.createDynamicTestsFromResourceFolder
import de.unibonn.simpleml.tests.util.getResourcePath
import de.unibonn.simpleml.tests.util.testDisplayName
import de.unibonn.simpleml.utils.OriginalFilePath
import de.unibonn.simpleml.utils.outerZipBy
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.validation.Issue
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Path
import java.util.stream.Stream

private const val SYNTAX_ERROR = "syntax_error"
private const val NO_SYNTAX_ERROR = "no_syntax_error"
private const val SEMANTIC_ERROR = "semantic_error"
private const val NO_SEMANTIC_ERROR = "no_semantic_error"
private const val SEMANTIC_WARNING = "semantic_warning"
private const val NO_SEMANTIC_WARNING = "no_semantic_warning"
private const val SEMANTIC_INFO = "semantic_info"
private const val NO_SEMANTIC_INFO = "no_semantic_info"
private const val NO_ISSUE = "no_issue"
private val validSeverities = listOf(
    SYNTAX_ERROR,
    NO_SYNTAX_ERROR,
    SEMANTIC_ERROR,
    NO_SEMANTIC_ERROR,
    SEMANTIC_WARNING,
    NO_SEMANTIC_WARNING,
    SEMANTIC_INFO,
    NO_SEMANTIC_INFO,
    NO_ISSUE
)

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class GrammarAndValidationTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Inject
    lateinit var validationHelper: ValidationTestHelper

    @TestFactory
    fun `should parse and validate`(): Stream<out DynamicNode> {
        val grammarTests = javaClass.classLoader
            .getResourcePath("languageTests/grammar")
            ?.createDynamicTestsFromResourceFolder(::validateTestFile, ::createTest)
            ?: Stream.empty()

        val validationTests = javaClass.classLoader
            .getResourcePath("languageTests/validation")
            ?.createDynamicTestsFromResourceFolder(::validateTestFile, ::createTest)
            ?: Stream.empty()

        return Stream.concat(grammarTests, validationTests)
    }

    /**
     * Checks if the given program is a valid test. If there are issues a description of the issue is returned, otherwise
     * this returns null.
     */
    private fun validateTestFile(program: String): String? {
        val severities = severities(program)

        // Must contain exactly one severity (which can be repeated)
        val numberOfUniqueSeverities = severities.toSet().size
        if (numberOfUniqueSeverities == 0) {
            return "No expected issue is specified."
        }

        // Severity must be valid
        val severity = severities.first()
        if (severity !in validSeverities) {
            return "Severity is invalid: \"$severity\"."
        }

        // Must not contain more locations markers than severities
        val locations = locations(program)
        if (severities.size < locations.size) {
            return "Test file contains more locations (»«) than severities."
        }

        // Must be able to parse the test file
        if (parseHelper.parseProgramWithStdlib(program) == null) {
            return "Could not parse test file."
        }

        return null
    }

    private fun createTest(resourcePath: Path, filePath: Path, program: String) = sequence {
        expectedIssues(program)
            .groupBy { it.severity to it.message }
            .keys
            .forEach { (severity, message) ->
                yield(
                    CategorizedTest(
                        severity,
                        dynamicTest(testDisplayName(resourcePath, filePath, message), filePath.toUri()) {
                            parsingTest(program, filePath, severity, message)
                        }
                    )
                )
            }
    }

    private fun parsingTest(program: String, filePath: Path, severity: String, message: String) {
        val actualIssues = actualIssues(program, filePath)
        expectedIssues(program)
            .filter { it.severity == severity && it.message == message }
            .forEach {
                when (it.severity) {
                    SYNTAX_ERROR -> actualIssues.shouldHaveSyntaxError(it)
                    NO_SYNTAX_ERROR -> actualIssues.shouldHaveNoSyntaxError(it)
                    SEMANTIC_ERROR -> actualIssues.shouldHaveSemanticError(it)
                    NO_SEMANTIC_ERROR -> actualIssues.shouldHaveNoSemanticError(it)
                    SEMANTIC_WARNING -> actualIssues.shouldHaveSemanticWarning(it)
                    NO_SEMANTIC_WARNING -> actualIssues.shouldHaveNoSemanticWarning(it)
                    SEMANTIC_INFO -> actualIssues.shouldHaveSemanticInfo(it)
                    NO_SEMANTIC_INFO -> actualIssues.shouldHaveNoSemanticInfo(it)
                    NO_ISSUE -> actualIssues.shouldHaveNoIssue(it)
                }
            }
    }

    private fun expectedIssues(program: String): List<ExpectedIssue> {
        return outerZipBy(severitiesAndMessages(program), locations(program)) { severityAndMessage, location ->
            ExpectedIssue(severityAndMessage!!.severity, severityAndMessage.message, location)
        }
    }

    private fun severities(program: String): List<String> {
        return severitiesAndMessages(program).map { it.severity }
    }

    private fun severitiesAndMessages(program: String): List<ExpectedIssue> {
        return """//\s*(?<severity>[^\s]*)\s*(?:"(?<message>[^"]*)")?"""
            .toRegex()
            .findAll(program)
            .map { ExpectedIssue(it.groupValues[1], it.groupValues[2], null) }
            .toList()
    }

    private fun locations(program: String): List<Location> {
        var currentLine = 1
        var currentColumn = 1

        var matchLine = 1
        var matchColumn = 1
        var matchLength = 0

        val result = mutableListOf<Location>()

        for (c in program.toCharArray()) {
            when (c) {
                '\u00BB' -> { // »
                    currentColumn++
                    matchLine = currentLine
                    matchColumn = currentColumn
                    matchLength = 0
                }
                '\u00AB' -> { // «
                    currentColumn++
                    result += Location(matchLine, matchColumn, matchLength)
                }
                '\n' -> {
                    currentLine++
                    currentColumn = 1
                    matchLength++
                }
                else -> {
                    currentColumn++
                    matchLength++
                }
            }
        }

        return result
    }

    private fun actualIssues(program: String, filePath: Path): List<Issue> {
        val parsingResult = parseHelper.parseProgramWithStdlib(program) ?: return emptyList()
        parsingResult.eResource().eAdapters().add(OriginalFilePath(filePath.toString()))
        return validationHelper.validate(parsingResult)
    }
}

class ExpectedIssue(
    val severity: String,
    val message: String,
    private val location: Location?
) {

    fun matches(issue: Issue): Boolean {
        return locationMatches(issue) && messageMatches(issue)
    }

    private fun locationMatches(issue: Issue): Boolean {
        return location == null || location == issue.location
    }

    private fun messageMatches(issue: Issue): Boolean {
        return when {
            message.isBlank() -> true
            "???" !in message -> message == issue.message
            else -> {
                val regex = Regex(message.replace("???", "\\w*"))
                regex.matches(issue.message)
            }
        }
    }

    private val Issue.location: Location
        get() = Location(lineNumber, column, length)

    override fun toString() = buildString {
        append(severity)
        if (message.isNotBlank()) {
            append(" \"$message\"")
        }
        location?.let { append(" at $location") }
    }
}

data class Location(val line: Int, val column: Int, val length: Int) {
    override fun toString(): String {
        val chars = if (length == 1) "char" else "chars"
        return "$line:$column ($length $chars)"
    }
}
