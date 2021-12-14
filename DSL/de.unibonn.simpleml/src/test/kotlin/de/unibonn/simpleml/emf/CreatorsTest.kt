package de.unibonn.simpleml.emf

import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToString
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
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
 * everything is being tested. These are the guidelines of what should be tested:
 *
 * - Dummy resource should be serializable
 * - Handling of annotations (features annotationUseHolder vs. annotations)
 * - Extension functions should add created element to receiver
 * - Creators for objects with cross-references that take a name instead of the referenced object
 */
@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class CreatorsTest {

    @Test
    fun `createSmlDummyResource should create serializable dummy resource`() {
        val result = createSmlDummyResource(
            "test",
            FileExtension.TEST,
            createSmlCompilationUnit()
        )

        result.contents.shouldHaveSize(1)
        result.contents[0].serializeToString().shouldBeInstanceOf<SerializationResult.Success>()
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
}
