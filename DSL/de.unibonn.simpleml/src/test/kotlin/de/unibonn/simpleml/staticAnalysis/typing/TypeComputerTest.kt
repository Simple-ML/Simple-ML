package de.unibonn.simpleml.staticAnalysis.typing

import com.google.inject.Inject
import de.unibonn.simpleml.constant.SmlFileExtension
import de.unibonn.simpleml.constant.SmlInfixOperationOperator
import de.unibonn.simpleml.constant.operator
import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.emf.typeArgumentsOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractObject
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlCallableType
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlIndexedAccess
import de.unibonn.simpleml.simpleML.SmlInfixOperation
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParenthesizedExpression
import de.unibonn.simpleml.simpleML.SmlParenthesizedType
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlStep
import de.unibonn.simpleml.simpleML.SmlUnionType
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
    // Declarations
    // ****************************************************************************************************************/

    @Nested
    inner class Attribute {

        @Test
        fun `attributes should have declared type`() {
            withCompilationUnitFromFile("declarations/attributes") {
                descendants<SmlAttribute>().forEach {
                    it shouldHaveType it.type
                }
            }
        }
    }

    @Nested
    inner class Class {

        @Test
        fun `classes should have non-nullable class type`() {
            withCompilationUnitFromFile("declarations/classes") {
                descendants<SmlClass>().forEach {
                    it shouldHaveType ClassType(it, isNullable = false)
                }
            }
        }
    }

    @Nested
    inner class Parameter {

        @Test
        fun `parameters should have declared type`() {
            withCompilationUnitFromFile("declarations/parameters") {
                descendants<SmlParameter>().forEach {
                    it shouldHaveType it.type
                }
            }
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `results should have declared type`() {
            withCompilationUnitFromFile("declarations/results") {
                descendants<SmlResult>().forEach {
                    it shouldHaveType it.type
                }
            }
        }
    }

    // *****************************************************************************************************************
    // Expressions
    // ****************************************************************************************************************/

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

    @Nested
    inner class TemplateStrings {

        @Test
        fun `template strings should have type String`() {
            withCompilationUnitFromFile("expressions/templateStrings") {
                placeholderWithName("templateString").assignedValueOrFail() shouldHaveType String
            }
        }
    }

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

    @Nested
    inner class IndexedAccesses {

        @Test
        fun `indexed accesses should return element type if receiver is variadic (myStep1)`() {
            withCompilationUnitFromFile("expressions/indexedAccesses") {
                findUniqueDeclarationOrFail<SmlStep>("myStep1")
                    .descendants<SmlIndexedAccess>()
                    .forEach {
                        it shouldHaveType Int
                    }
            }
        }

        @Test
        fun `indexed accesses should return element type if receiver is variadic (myStep2)`() {
            withCompilationUnitFromFile("expressions/indexedAccesses") {
                findUniqueDeclarationOrFail<SmlStep>("myStep2")
                    .descendants<SmlIndexedAccess>()
                    .forEach {
                        it shouldHaveType String
                    }
            }
        }

        @Test
        fun `indexed accesses should return Nothing type if receiver is not variadic`() {
            withCompilationUnitFromFile("expressions/indexedAccesses") {
                findUniqueDeclarationOrFail<SmlStep>("myStep3")
                    .descendants<SmlIndexedAccess>()
                    .forEach {
                        it shouldHaveType Nothing
                    }
            }
        }

        @Test
        fun `indexed accesses should return Unresolved type if receiver is unresolved`() {
            withCompilationUnitFromFile("expressions/indexedAccesses") {
                findUniqueDeclarationOrFail<SmlStep>("myStep4")
                    .descendants<SmlIndexedAccess>()
                    .forEach {
                        it shouldHaveType UnresolvedType
                    }
            }
        }
    }

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
    // Types
    // ****************************************************************************************************************/

    @Nested
    inner class CallableType {

        @Test
        fun `callable type should have callable type with respective parameters and results`() {
            withCompilationUnitFromFile("types/callableTypes") {
                descendants<SmlCallableType>().forEach { callableType ->
                    callableType shouldHaveType CallableType(
                        callableType.parametersOrEmpty().map { it.type() },
                        callableType.resultsOrEmpty().map { it.type() }
                    )
                }
            }
        }
    }

    @Nested
    inner class MemberType {

        @Test
        fun `non-nullable member type should have type of referenced member`() {
            withCompilationUnitFromFile("types/memberTypes") {
                findUniqueDeclarationOrFail<SmlFunction>("nonNullableMemberTypes")
                    .descendants<SmlMemberType>().forEach {
                        it shouldHaveType it.member
                    }
            }
        }

        @Test
        fun `nullable member type should have nullable type of referenced member`() {
            withCompilationUnitFromFile("types/memberTypes") {
                findUniqueDeclarationOrFail<SmlFunction>("nullableMemberTypes")
                    .descendants<SmlMemberType>().forEach {
                        it shouldHaveType it.member.type().setIsNullableOnCopy(isNullable = true)
                    }
            }
        }
    }

    @Nested
    inner class NamedType {

        @Test
        fun `non-nullable named type should have type of referenced declaration`() {
            withCompilationUnitFromFile("types/namedTypes") {
                findUniqueDeclarationOrFail<SmlFunction>("nonNullableNamedTypes")
                    .descendants<SmlNamedType>().forEach {
                        it shouldHaveType it.declaration
                    }
            }
        }

        @Test
        fun `nullable named type should have nullable type of referenced declaration`() {
            withCompilationUnitFromFile("types/namedTypes") {
                findUniqueDeclarationOrFail<SmlFunction>("nullableNamedTypes")
                    .descendants<SmlNamedType>().forEach {
                        it shouldHaveType it.declaration.type().setIsNullableOnCopy(isNullable = true)
                    }
            }
        }
    }

    @Nested
    inner class ParenthesizedType {

        @Test
        fun `parenthesized type should have type of type`() {
            withCompilationUnitFromFile("types/parenthesizedTypes") {
                descendants<SmlParenthesizedType>().forEach {
                    it shouldHaveType it.type
                }
            }
        }
    }

    @Nested
    inner class UnionTypes {

        @Test
        fun `union type should have union type over its type arguments`() {
            withCompilationUnitFromFile("types/unionTypes") {
                descendants<SmlUnionType>().forEach { unionType ->
                    unionType shouldHaveType UnionType(
                        unionType.typeArgumentsOrEmpty().map { it.type() }.toSet()
                    )
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
    private val SmlCompilationUnit.Nothing get() = stdlibType(this, StdlibClasses.Nothing)
    private val SmlCompilationUnit.NothingOrNull get() = stdlibType(this, StdlibClasses.Nothing, isNullable = true)
    private val SmlCompilationUnit.String get() = stdlibType(this, StdlibClasses.String)
}
