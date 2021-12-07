package de.unibonn.simpleml.test

import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.constants.FileExtensions
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.stdlib.StdlibClasses
import de.unibonn.simpleml.test.util.ParseHelper
import de.unibonn.simpleml.test.util.getResourcePath
import de.unibonn.simpleml.typing.Type
import de.unibonn.simpleml.typing.TypeComputer
import de.unibonn.simpleml.utils.assignedOrNull
import io.kotest.matchers.shouldBe
import org.eclipse.emf.ecore.EObject
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

@Suppress("PrivatePropertyName")
class TypeComputerTest {
    private val parseHelper: ParseHelper
    private val typeComputer: TypeComputer

    init {
        SimpleMLStandaloneSetup().createInjectorAndDoEMFRegistration().apply {
            parseHelper = getInstance(ParseHelper::class.java)
            typeComputer = getInstance(TypeComputer::class.java)
        }
    }

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
                placeholderWithName("booleanLiteral").assignedValueOrFail() shouldHaveType BOOLEAN
            }
        }

        @Test
        fun `float literals should have type Float`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("floatLiteral").assignedValueOrFail() shouldHaveType FLOAT
            }
        }

        @Test
        fun `int literals should have type Int`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("intLiteral").assignedValueOrFail() shouldHaveType INT
            }
        }

        @Test
        fun `string literals should have type String`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("stringLiteral").assignedValueOrFail() shouldHaveType STRING
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
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType INT
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
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType FLOAT
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
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType BOOLEAN
                }
            }
        }

        @Test
        fun `equality operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/equality") {
                listOf("equals", "notEquals").forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType BOOLEAN
                }
            }
        }

        @Test
        fun `strict equality operations should have type Boolean`() {
            withCompilationUnitFromFile("expressions/operations/strictEquality") {
                listOf("strictlyEquals", "notStrictlyEquals").forEach {
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType BOOLEAN
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
                    placeholderWithName(it).assignedValueOrFail() shouldHaveType BOOLEAN
                }
            }
        }
    }

    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

    infix fun EObject.shouldHaveType(expectedType: Type) {
        typeComputer.typeOf(this).shouldBe(expectedType)
    }

    private fun SmlPlaceholder.assignedValueOrFail(): EObject {
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
        val program = Files.readString(Path.of(testRoot, "$file${FileExtensions.TEST}"))
        val compilationUnit = parseHelper.parseProgramTextWithStdlib(program)
            ?: throw IllegalArgumentException("File is not a compilation unit.")
        compilationUnit.apply(lambda)
    }

    private val SmlCompilationUnit.ANY get() = typeComputer.stdlibType(this, StdlibClasses.Any.toString())
    private val SmlCompilationUnit.BOOLEAN get() = typeComputer.stdlibType(this, StdlibClasses.Boolean.toString())
    private val SmlCompilationUnit.FLOAT get() = typeComputer.stdlibType(this, StdlibClasses.Float.toString())
    private val SmlCompilationUnit.INT get() = typeComputer.stdlibType(this, StdlibClasses.Int.toString())
    private val SmlCompilationUnit.STRING get() = typeComputer.stdlibType(this, StdlibClasses.String.toString())
}
