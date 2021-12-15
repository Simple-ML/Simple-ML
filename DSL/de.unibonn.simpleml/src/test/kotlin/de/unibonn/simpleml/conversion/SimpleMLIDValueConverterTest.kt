package de.unibonn.simpleml.conversion

import com.google.inject.Inject
import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.emf.createSmlCompilationUnit
import de.unibonn.simpleml.emf.createSmlDummyResource
import de.unibonn.simpleml.emf.createSmlPackage
import de.unibonn.simpleml.emf.smlPackage
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToFormattedString
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
import de.unibonn.simpleml.utils.descendants
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.eclipse.xtext.conversion.impl.IDValueConverter
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InjectionExtension::class)
@InjectWith(SimpleMLInjectorProvider::class)
class SimpleMLIDValueConverterTest {

    @Inject
    private lateinit var parseHelper: ParseHelper

    @Inject
    private lateinit var idValueConverter: SimpleMLIDValueConverter

    @Test
    fun `should remove backticks (simple)`() {
        idValueConverter.toValue("`package`", null) shouldBe "package"
    }

    @Test
    fun `should remove backticks`() {
        val compilationUnit = parseHelper.parseResource("conversion/idValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val `package` = compilationUnit.uniquePackageOrNull()
        `package`.shouldNotBeNull()
        `package`.name shouldBe "package"
    }

    @Test
    fun `should escape keywords (simple)`() {
        idValueConverter.toString("package") shouldBe "`package`"
    }

    @Test
    fun `should escape keywords`() {
        val compilationUnit = createSmlCompilationUnit {
            smlPackage("package")
        }
        createSmlDummyResource("test", FileExtension.TEST, compilationUnit)

        val result = compilationUnit.serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "package `package`"
    }

    @Test
    fun `should not escape non-keywords (simple)`() {
        idValueConverter.toString("notAKeyword") shouldBe "notAKeyword"
    }

    @Test
    fun `should not escape non-keywords`() {
        val compilationUnit = createSmlCompilationUnit {
            smlPackage("notAKeyword")
        }
        createSmlDummyResource("test", FileExtension.TEST, compilationUnit)

        val result = compilationUnit.serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "package notAKeyword"
    }
}
