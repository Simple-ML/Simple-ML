package de.unibonn.simpleml.generator

import com.google.inject.Inject
import de.unibonn.simpleml.emf.resourceSetOrNull
import de.unibonn.simpleml.testing.CategorizedTest
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.createDynamicTestsFromResourceFolder
import de.unibonn.simpleml.testing.getResourcePath
import de.unibonn.simpleml.testing.testDisplayName
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.xbase.testing.CompilationTestHelper
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.extension
import kotlin.io.path.readText
import kotlin.streams.toList

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLGeneratorTest {

    @Inject
    private lateinit var compilationTestHelper: CompilationTestHelper

    @Inject
    private lateinit var parseHelper: ParseHelper

    @TestFactory
    fun `should compile test files correctly`(): Stream<out DynamicNode> {
        return javaClass.classLoader
            .getResourcePath("generator")
            ?.createDynamicTestsFromResourceFolder(::validateTestFile, ::createTest)
            ?: Stream.empty()
    }

    /**
     * Checks if the given program is a valid test. If there are issues a description of the issue is returned,
     * otherwise this returns `null`.
     */
    private fun validateTestFile(program: String, filePath: Path): String? {

        // Must be able to parse the test file
        if (parseHelper.parseProgramText(program) == null) {
            return "Could not parse test file."
        }

        return null
    }

    private fun createTest(resourcePath: Path, filePath: Path, program: String) = sequence {
        println(resourcePath)
        println(filePath)
        yield(
            CategorizedTest(
                "valid test file",
                DynamicTest.dynamicTest(testDisplayName(resourcePath, filePath), filePath.toUri()) {
                    generatorTest(resourcePath, filePath)
                }
            )
        )
    }

    private fun generatorTest(resourcePath: Path, filePath: Path) {
        val expectedOutputs = expectedOutputs(filePath)
        val actualOutputs = actualOutputs(resourcePath, filePath)

        // File paths should match exactly
        actualOutputs.map { it.filePath }.shouldContainExactlyInAnyOrder(expectedOutputs.map { it.filePath })

        // Contents should match
        actualOutputs.forEachAsClue { actualOutput ->
            val expectedOutput = expectedOutputs.first { it.filePath == actualOutput.filePath }
            actualOutput.content shouldBe expectedOutput.content
        }
    }

    private data class OutputFile(val filePath: String, val content: String)

    private fun expectedOutputs(filePath: Path): List<OutputFile> {
        val root = filePath.parent

        return Files.walk(root)
            .filter { it.extension == "py" }
            .map {
                OutputFile(
                    root.relativize(it).toUnixString(),
                    it.readText()
                )
            }
            .toList()
    }

    private fun actualOutputs(resourcePath: Path, filePath: Path): List<OutputFile> {
        var actualOutput: List<OutputFile> = emptyList()

        compilationTestHelper.compile(resourceSet(resourcePath, filePath)) { result ->
            actualOutput = result.allGeneratedResources.map {
                OutputFile(it.key.removePathPrefix(), it.value.toString())
            }
        }

        return actualOutput
    }

    private fun resourceSet(resourcePath: Path, filePath: Path): ResourceSet {
        return parseHelper
            .parseResource(resourceName(resourcePath, filePath))
            ?.resourceSetOrNull()
            .shouldNotBeNull()
    }

    private fun resourceName(resourcePath: Path, filePath: Path): String {
        return resourcePath
            .parent
            .relativize(filePath)
            .toUnixString()
    }

    private fun Path.toUnixString(): String {
        return this.toString().replace("\\", "/")
    }

    private fun String.removePathPrefix(): String {
        return this.removePrefix("/myProject/./")
    }
}
