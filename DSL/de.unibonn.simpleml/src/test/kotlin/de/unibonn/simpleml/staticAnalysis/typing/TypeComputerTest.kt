package de.unibonn.simpleml.staticAnalysis.typing

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlFileExtension
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.operator
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.staticAnalysis.assignedOrNull
import de.unibonn.simpleml.stdlibAccess.StdlibClasses
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.testing.getResourcePath
import io.kotest.matchers.shouldBe
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
                placeholderWithName("booleanLiteral").assignedValueOrFail() shouldHaveType Boolean
            }
        }

        @Test
        fun `float literals should have type Float`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("floatLiteral").assignedValueOrFail() shouldHaveType Float
            }
        }

        @Test
        fun `int literals should have type Int`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("intLiteral").assignedValueOrFail() shouldHaveType Int
            }
        }

        @Test
        fun `null literals should have type nullable Nothing`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("nullLiteral").assignedValueOrFail() shouldHaveType NothingOrNull
            }
        }

        @Test
        fun `string literals should have type String`() {
            withCompilationUnitFromFile("expressions/literals") {
                placeholderWithName("stringLiteral").assignedValueOrFail() shouldHaveType String
            }
        }
    }

    // Template Strings ------------------------------------------------------------------------------------------------

    @Nested
    inner class TemplateStrings {

        @Test
        fun `template strings should have type String`() {
            withCompilationUnitFromFile("expressions/templateStrings") {
                placeholderWithName("templateString").assignedValueOrFail() shouldHaveType String
            }
        }
    }

    // Arguments -------------------------------------------------------------------------------------------------------

    @Nested
    inner class Arguments {

        @Test
        fun `arguments should have type of value`() {
            withCompilationUnitFromFile("expressions/arguments") {
                descendants<SmlArgument>().forEach {
                    it shouldHaveType it.value
                }
            }
        }
    }

    // Member Accesses -------------------------------------------------------------------------------------------------

    @Nested
    inner class MemberAccesses {

        @Test
        fun `non-null-safe member accesses should have type of referenced member`() {
            withCompilationUnitFromFile("expressions/memberAccesses") {
                descendants<SmlMemberAccess>()
                    .filter { !it.isNullSafe }
                    .forEach {
                        it shouldHaveType it.member
                    }
            }
        }

        @Test
        fun `null-safe member accesses should have type of referenced member but nullable`() {
            withCompilationUnitFromFile("expressions/memberAccesses") {
                descendants<SmlMemberAccess>()
                    .filter { it.isNullSafe }
                    .forEach {
                        it shouldHaveType it.member.type().setIsNullableOnCopy(isNullable = true)
                    }
            }
        }
    }

    // Parenthesized Expressions ---------------------------------------------------------------------------------------

    @Nested
    inner class ParenthesizedExpressions {

        @Test
        fun `parenthesized expressions should have type of expressions`() {
            withCompilationUnitFromFile("expressions/parenthesizedExpressions") {
                descendants<SmlParenthesizedExpression>().forEach {
                    it shouldHaveType it.expression
                }
            }
        }
    }

    // References ------------------------------------------------------------------------------------------------------

    @Nested
    inner class References {

        @Test
        fun `references should have type of referenced declaration`() {
            withCompilationUnitFromFile("expressions/references") {
                descendants<SmlReference>().forEach {
                    it shouldHaveType it.declaration
                }
            }
        }
    }

    // Operations ------------------------------------------------------------------------------------------------------

    @Nested
    inner class Operations {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "additionIntInt",
                "subtractionIntInt",
                "multiplicationIntInt",
                "divisionIntInt",
                "negationInt"
            ],
        )
        fun `arithmetic operations with only Int operands should have type Int`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/arithmetic") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Int
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
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
            ],
        )
        fun `arithmetic operations with non-Int operands should have type Float`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/arithmetic") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Float
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "lessThan",
                "lessThanOrEquals",
                "greaterThanOrEquals",
                "greaterThan",
                "lessThanInvalid",
                "lessThanOrEqualsInvalid",
                "greaterThanOrEqualsInvalid",
                "greaterThanInvalid"
            ],
        )
        fun `comparison operations should have type Boolean`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/comparison") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Boolean
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "equals",
                "notEquals"
            ],
        )
        fun `equality operations should have type Boolean`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/equality") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Boolean
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "strictlyEquals",
                "notStrictlyEquals"
            ],
        )
        fun `strict equality operations should have type Boolean`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/strictEquality") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Boolean
            }
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "conjunction",
                "disjunction",
                "negation",
                "conjunctionInvalid",
                "disjunctionInvalid",
                "negationInvalid"
            ],
        )
        fun `logical operations should have type Boolean`(placeholderName: String) {
            withCompilationUnitFromFile("expressions/operations/logical") {
                placeholderWithName(placeholderName).assignedValueOrFail() shouldHaveType Boolean
            }
        }

        @Test
        fun `elvis operator with non-nullable left operand should have type of left operand`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                findUniqueDeclarationOrFail<SmlWorkflow>("elvisWithNonNullableLeftOperand")
                    .descendants<SmlInfixOperation>()
                    .filter { it.operator() == SmlInfixOperationOperator.Elvis }
                    .forEach { it shouldHaveType it.leftOperand }
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseIntOrNull)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseIntOrNull") shouldHaveType IntOrNull
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseNull)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseNull") shouldHaveType IntOrNull
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseInt)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseInt") shouldHaveType Int
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseFloat)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseFloat") shouldHaveType Number
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseString)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseString") shouldHaveType Any
            }
        }

        @Test
        fun `elvis operator with nullable left operand should have lowest common supertype of non-nullable left operand and right operand (intOrNullElseStringOrNull)`() {
            withCompilationUnitFromFile("expressions/operations/elvis") {
                placeholderWithName("intOrNullElseStringOrNull") shouldHaveType AnyOrNull
            }
        }
    }

    // *****************************************************************************************************************
    // Helpers
    // ****************************************************************************************************************/

    infix fun SmlAbstractObject.shouldHaveType(expectedType: Type) {
        this.type().shouldBe(expectedType)
    }

    infix fun SmlAbstractObject.shouldHaveType(expected: SmlAbstractObject) {
        this.type().shouldBe(expected.type())
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
        val compilationUnit = parseHelper.parseProgramText(program)
            ?: throw IllegalArgumentException("File is not a compilation unit.")
        compilationUnit.apply(lambda)
    }

    private val SmlCompilationUnit.Any get() = stdlibType(this, StdlibClasses.Any)
    private val SmlCompilationUnit.AnyOrNull get() = stdlibType(this, StdlibClasses.Any, isNullable = true)
    private val SmlCompilationUnit.Boolean get() = stdlibType(this, StdlibClasses.Boolean)
    private val SmlCompilationUnit.Number get() = stdlibType(this, StdlibClasses.Number)
    private val SmlCompilationUnit.Float get() = stdlibType(this, StdlibClasses.Float)
    private val SmlCompilationUnit.Int get() = stdlibType(this, StdlibClasses.Int)
    private val SmlCompilationUnit.IntOrNull get() = stdlibType(this, StdlibClasses.Int, isNullable = true)
    private val SmlCompilationUnit.NothingOrNull get() = stdlibType(this, StdlibClasses.Nothing, isNullable = true)
    private val SmlCompilationUnit.String get() = stdlibType(this, StdlibClasses.String)
}
