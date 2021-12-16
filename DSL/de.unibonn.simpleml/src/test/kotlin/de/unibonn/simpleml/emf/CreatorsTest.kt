package de.unibonn.simpleml.emf

import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToFormattedString
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Includes tests for the (extension) functions in Creators.kt. Since most of the functions are straightforward, not
 * everything is being tested. These are the guidelines for what should be tested:
 *
 * - Handling of annotations (features annotationUseHolder vs. annotations)
 * - Extension functions should add created object to receiver
 * - Creators for objects with cross-references that take a name instead of the referenced object
 *
 * There are also some special tests:
 * - Dummy resource should be serializable
 * - Assignments requires at least one assignee
 * - Template string creator should check structure of template string
 * - Union type requires at least one type argument
 */
@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class CreatorsTest {

    @Test
    fun `createSmlDummyResource should create serializable dummy resource`() {
        val result = createSmlDummyResource("test", FileExtension.TEST)

        result.contents.shouldHaveSize(1)
        result.contents[0].serializeToFormattedString().shouldBeInstanceOf<SerializationResult.Success>()
    }

    @Test
    fun `createSmlAnnotation should store annotation uses in annotationUseHolder`() {
        val annotation = createSmlAnnotation(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        annotation.annotations.shouldHaveSize(0)

        val annotationUseHolder = annotation.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlAnnotation should add the created annotation to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlAnnotation("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlAnnotation should add the created annotation to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlAnnotation("Test")
        }

        `package`.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlAnnotationUse should create an SmlAnnotation when only a name is passed`() {
        val annotationUse = createSmlAnnotationUse("Test")
        val annotation = annotationUse.annotation
        annotation.shouldNotBeNull()
        annotation.name shouldBe "Test"
    }

    @Test
    fun `createSmlArgument should create an SmlParameter when only a name is passed`() {
        val argument = createSmlArgument(createSmlInt(1), "Test")
        val parameter = argument.parameter
        parameter.shouldNotBeNull()
        parameter.name shouldBe "Test"
    }

    @Test
    fun `smlAssignment should throw if no type arguments are passed`() {
        shouldThrowExactly<IllegalArgumentException> {
            createSmlAssignment(listOf(), createSmlInt(1))
        }
    }

    @Test
    fun `smlAssignment should add the created assignment to the receiving lambda`() {
        val lambda = createSmlLambda {
            smlAssignment(
                listOf(createSmlWildcard()),
                createSmlInt(1)
            )
        }

        val body = lambda.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `smlAssignment should add the created assignment to the receiving workflow`() {
        val workflow = createSmlWorkflow("Test") {
            smlAssignment(
                listOf(createSmlWildcard()),
                createSmlInt(1)
            )
        }

        val body = workflow.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `smlAssignment should add the created assignment to the receiving workflow step`() {
        val step = createSmlWorkflowStep("Test") {
            smlAssignment(
                listOf(createSmlWildcard()),
                createSmlInt(1)
            )
        }

        val body = step.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `createSmlAttribute should store annotation uses in annotationUseHolder`() {
        val attribute = createSmlAttribute(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        attribute.annotations.shouldHaveSize(0)

        val annotationUseHolder = attribute.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlAttribute should add the created attribute to the receiver`() {
        val `class` = createSmlClass("Test") {
            smlAttribute("Test")
        }

        val body = `class`.body
        body.shouldNotBeNull()
        body.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlClass should store annotation uses in annotationUseHolder`() {
        val `class` = createSmlClass(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        `class`.annotations.shouldHaveSize(0)

        val annotationUseHolder = `class`.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlClass should add the created class to the receiving class`() {
        val `class` = createSmlClass("Test") {
            smlClass("Test")
        }

        val body = `class`.body
        body.shouldNotBeNull()
        body.members.shouldHaveSize(1)
    }

    @Test
    fun `smlClass should add the created class to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlClass("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlClass should add the created class to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlClass("Test")
        }

        `package`.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlEnum should store annotation uses in annotationUseHolder`() {
        val `enum` = createSmlEnum(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        `enum`.annotations.shouldHaveSize(0)

        val annotationUseHolder = `enum`.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlEnum should add the created enum to the receiving class`() {
        val `class` = createSmlClass("Test") {
            smlEnum("Test")
        }

        val body = `class`.body
        body.shouldNotBeNull()
        body.members.shouldHaveSize(1)
    }

    @Test
    fun `smlEnum should add the created enum to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlEnum("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlEnum should add the created enum to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlEnum("Test")
        }

        `package`.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlEnumVariant should store annotation uses in annotations`() {
        val variant = createSmlEnumVariant(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        variant.annotations.shouldHaveSize(1)
        variant.annotationUseHolder.shouldBeNull()
    }

    @Test
    fun `smlEnumVariant should add the created variant to the receiver`() {
        val enum = createSmlEnum("Test") {
            smlEnumVariant("Test")
        }

        val body = enum.body
        body.shouldNotBeNull()
        body.variants.shouldHaveSize(1)
    }

    @Test
    fun `smlExpressionStatement should add the created expression statement to the receiving lambda`() {
        val lambda = createSmlLambda {
            smlExpressionStatement(createSmlInt(1))
        }

        val body = lambda.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `smlExpressionStatement should add the created expression statement to the receiving workflow`() {
        val workflow = createSmlWorkflow("Test") {
            smlExpressionStatement(createSmlInt(1))
        }

        val body = workflow.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `smlExpressionStatement should add the created expression statement to the receiving workflow step`() {
        val step = createSmlWorkflowStep("Test") {
            smlExpressionStatement(createSmlInt(1))
        }

        val body = step.body
        body.shouldNotBeNull()
        body.statements.shouldHaveSize(1)
    }

    @Test
    fun `createSmlFunction should store annotation uses in annotationUseHolder`() {
        val function = createSmlFunction(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        function.annotations.shouldHaveSize(0)

        val annotationUseHolder = function.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlFunction should add the created function to the receiving class`() {
        val `class` = createSmlClass("Test") {
            smlFunction("Test")
        }

        val body = `class`.body
        body.shouldNotBeNull()
        body.members.shouldHaveSize(1)
    }

    @Test
    fun `smlFunction should add the created function to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlFunction("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlFunction should add the created function to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlFunction("Test")
        }

        `package`.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlLambdaResult should store annotation uses in annotationUseHolder`() {
        val lambdaResult = createSmlLambdaResult(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        lambdaResult.annotations.shouldHaveSize(0)

        val annotationUseHolder = lambdaResult.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `createSmlPackage should store annotation uses in annotationUseHolder`() {
        val `package` = createSmlPackage(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        `package`.annotations.shouldHaveSize(0)

        val annotationUseHolder = `package`.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `createSmlParameter should store annotation uses in annotations`() {
        val parameter = createSmlParameter(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        parameter.annotations.shouldHaveSize(1)
        parameter.annotationUseHolder.shouldBeNull()
    }

    @Test
    fun `createSmlPlaceholder should store annotation uses in annotationUseHolder`() {
        val placeholder = createSmlPlaceholder(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        placeholder.annotations.shouldHaveSize(0)

        val annotationUseHolder = placeholder.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `createSmlResult should store annotation uses in annotations`() {
        val result = createSmlResult(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        result.annotations.shouldHaveSize(1)
        result.annotationUseHolder.shouldBeNull()
    }

    @Test
    fun `createSmlTemplate should throw if there are fewer than 2 string parts`() {
        shouldThrowExactly<IllegalArgumentException> {
            createSmlTemplateString(
                listOf("Test"),
                listOf(createSmlInt(1))
            )
        }
    }

    @Test
    fun `createSmlTemplate should throw if there is no template expression`() {
        shouldThrowExactly<IllegalArgumentException> {
            createSmlTemplateString(
                listOf("Test", "Test"),
                listOf()
            )
        }
    }

    @Test
    fun `createSmlTemplate should throw if numbers of string parts and template expressions don't match`() {
        shouldThrowExactly<IllegalArgumentException> {
            createSmlTemplateString(
                listOf("Test", "Test", "Test"),
                listOf(createSmlInt(1))
            )
        }
    }

    @Test
    fun `createSmlTemplate should interleave string parts and template expressions`() {
        val templateString = createSmlTemplateString(
            listOf("Start", "Inner", "Inner", "End"),
            listOf(createSmlInt(1), createSmlInt(1), createSmlInt(1))
        )

        templateString.expressions.asClue {
            it.shouldHaveSize(7)
            it[0].shouldBeInstanceOf<SmlTemplateStringStart>()
            it[1].shouldBeInstanceOf<SmlInt>()
            it[2].shouldBeInstanceOf<SmlTemplateStringInner>()
            it[3].shouldBeInstanceOf<SmlInt>()
            it[4].shouldBeInstanceOf<SmlTemplateStringInner>()
            it[5].shouldBeInstanceOf<SmlInt>()
            it[6].shouldBeInstanceOf<SmlTemplateStringEnd>()
        }
    }

    @Test
    fun `createSmlTypeArgument should create an SmlTypeParameter when only a name is passed`() {
        val typeArgument = createSmlTypeArgument(
            createSmlStarProjection(),
            "Test"
        )
        val typeParameter = typeArgument.typeParameter
        typeParameter.shouldNotBeNull()
        typeParameter.name shouldBe "Test"
    }

    @Test
    fun `createSmlTypeParameter should store annotation uses in annotations`() {
        val result = createSmlTypeParameter(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        result.annotations.shouldHaveSize(1)
        result.annotationUseHolder.shouldBeNull()
    }

    @Test
    fun `createTypeParameterConstraint should create an SmlTypeParameter when only a name is passed`() {
        val constraint = createSmlTypeParameterConstraint(
            "Test",
            "sub",
            createSmlNamedType(createSmlClass("Test"))
        )
        val leftOperand = constraint.leftOperand
        leftOperand.shouldNotBeNull()
        leftOperand.name shouldBe "Test"
    }

    @Test
    fun `createSmlUnionType should throw if no type arguments are passed`() {
        shouldThrowExactly<IllegalArgumentException> {
            createSmlUnionType(listOf())
        }
    }

    @Test
    fun `createSmlYield should create an SmlResult when only a name is passed`() {
        val constraint = createSmlYield("Test")
        val result = constraint.result
        result.shouldNotBeNull()
        result.name shouldBe "Test"
    }

    @Test
    fun `createSmlWorkflow should store annotation uses in annotationUseHolder`() {
        val workflow = createSmlWorkflow(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        workflow.annotations.shouldHaveSize(0)

        val annotationUseHolder = workflow.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlWorkflow should add the created workflow to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlWorkflow("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlWorkflow should add the created workflow to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlWorkflow("Test")
        }

        `package`.members.shouldHaveSize(1)
    }

    @Test
    fun `createSmlWorkflowStep should store annotation uses in annotationUseHolder`() {
        val workflowStep = createSmlWorkflowStep(
            "Test",
            listOf(createSmlAnnotationUse("Test"))
        )

        workflowStep.annotations.shouldHaveSize(0)

        val annotationUseHolder = workflowStep.annotationUseHolder
        annotationUseHolder.shouldNotBeNull()
        annotationUseHolder.annotations.shouldHaveSize(1)
    }

    @Test
    fun `smlWorkflowStep should add the created workflow step to the receiving compilation unit`() {
        val compilationUnit = createSmlCompilationUnit {
            smlWorkflowStep("Test")
        }

        compilationUnit.members.shouldHaveSize(1)
    }

    @Test
    fun `smlWorkflowStep should add the created workflow step to the receiving package`() {
        val `package` = createSmlPackage("Test") {
            smlWorkflowStep("Test")
        }

        `package`.members.shouldHaveSize(1)
    }
}
