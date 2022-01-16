@file:Suppress("TestFunctionName")

package de.unibonn.simpleml.staticAnalysis.typing

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlFileExtension
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.staticAnalysis.assignedOrNull
import de.unibonn.simpleml.stdlibAccess.StdlibClass
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.getResourcePath
import io.kotest.matchers.shouldBe
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Path

@Suppress("PrivatePropertyName")
@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class TypeComputerTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    private val testRoot = javaClass.classLoader.getResourcePath("typeComputer").toString()

    // *****************************************************************************************************************
    // Expressions
    // ****************************************************************************************************************/

    // Literals --------------------------------------------------------------------------------------------------------

    @Nested
    inner class Literals {

        @Test
        fun `boolean literals should have type Boolean`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("booleanLiteral").assignedValueOrFail() shouldHaveType Boolean(this)
            }
        }

        @Test
        fun `float literals should have type Float`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("floatLiteral").assignedValueOrFail() shouldHaveType Float(this)
            }
        }

        @Test
        fun `int literals should have type Int`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("intLiteral").assignedValueOrFail() shouldHaveType Int(this)
            }
        }

        @Test
        fun `string literals should have type String`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("stringLiteral").assignedValueOrFail() shouldHaveType String(this)
            }
        }
    }

    // Operations ------------------------------------------------------------------------------------------------------

    @Nested
    inner class Operations {

        @Test
        fun `arithmetic operations with only Int operands should have type Int`() {
            withCompilationUnitFromFile("expressions/operations/arithmetic") {
                listOf(
                    "additionIntInt",
                    "subtractionIntInt",
                    "multiplicationIntInt",
                    "divisionIntInt",
                    "negationInt"
                ).forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Int(this)
                }
            }
        }

        @Test
        fun `arithmetic operations without only Int operands should have type Float`() {
            withCompilationUnitFromFile("expressions/operations/arithmetic") {
                listOf(
                    "additionIntFloat",
                    "subtractionIntFloat",
                    "multiplicationIntFloat",
                    "divisionIntFloat",
                    "negationFloat",
                    "additionInvalid",
                    "subtractionInvalid",
                    "multiplicationInvalid",
                    "divisionInvalid",
                    "negationInvalid"
                ).forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Float(this)
                }
            }
        }

        @Test
        fun `comparison operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/comparison") {
                listOf(
                    "lessThan",
                    "lessThanOrEquals",
                    "greaterThanOrEquals",
                    "greaterThan",
                    "lessThanInvalid",
                    "lessThanOrEqualsInvalid",
                    "greaterThanOrEqualsInvalid",
                    "greaterThanInvalid"
                ).forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Boolean(this)
                }
            }
        }

        @Test
        fun `equality operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/equality") {
                listOf("equals", "notEquals").forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Boolean(this)
                }
            }
        }

        @Test
        fun `strict equality operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/strictEquality") {
                listOf("strictlyEquals", "notStrictlyEquals").forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Boolean(this)
                }
            }
        }

        @Test
        fun `logical operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/logical") {
                listOf(
                    "conjunction",
                    "disjunction",
                    "negation",
                    "conjunctionInvalid",
                    "disjunctionInvalid",
                    "negationInvalid"
                ).forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType Boolean(this)
                }
            }
        }
    }

    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

    infix fun SmlAbstractObject.shouldHaveType(expectedType: Type) {
        this.type().shouldBe(expectedType)
    }

    private fun SmlPlaceholder.assignedValueOrFail(): SmlAbstractObject {
        return this.assignedOrNull()
            ?: throw IllegalArgumentException("No value is assigned to placeholder with name '$name'.")
    }

    private fun SmlCompilationUnit.placeholderWithName(name: String): SmlPlaceholder {
        val candidates = this.eAllContents().asSequence()
            .filterIsInstance<SmlPlaceholder>()
            .filter { it.name == name }
            .toList()

        when (candidates.size) {
            1 -> return candidates.first()
            else -> throw IllegalArgumentException("File contains ${candidates.size} placeholders with name '$name'.")
        }
    }

    private fun withCompilationUnitFromFile(file: String, lambda: SmlCompilationUnit.() -> Unit) {
        val program = Files.readString(Path.of(testRoot, "$file.${SmlFileExtension.Test}"))
        val compilationUnit = parseHelper.parseProgramTextWithStdlib(program)
            ?: throw IllegalArgumentException("File is not a compilation unit.")
        compilationUnit.apply(lambda)
    }

    private fun Any(context: EObject) = ClassType(StdlibClass.Any(context))
    private fun Boolean(context: EObject) = ClassType(StdlibClass.Boolean(context))
    private fun Float(context: EObject) = ClassType(StdlibClass.Float(context))
    private fun Int(context: EObject) = ClassType(StdlibClass.Int(context))
    private fun Nothing(context: EObject) = ClassType(StdlibClass.Nothing(context))
    private fun String(context: EObject) = ClassType(StdlibClass.String(context))
}
