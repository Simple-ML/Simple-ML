package de.unibonn.simpleml.conversion

import com.google.inject.Inject
import de.unibonn.simpleml.constant.FileExtension
import de.unibonn.simpleml.emf.createSmlCompilationUnit
import de.unibonn.simpleml.emf.createSmlDummyResource
import de.unibonn.simpleml.emf.smlClass
import de.unibonn.simpleml.emf.smlPackage
import de.unibonn.simpleml.serializer.SerializationResult
import de.unibonn.simpleml.serializer.serializeToFormattedString
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.testing.ParseHelper
import de.unibonn.simpleml.testing.SimpleMLInjectorProvider
import de.unibonn.simpleml.testing.assertions.findUniqueDeclarationOrFail
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
    private lateinit var idValueConverter: IDValueConverter

    @Test
    fun `should remove backticks (direct converter call)`() {
        idValueConverter.toValue("`package`", null) shouldBe "package"
    }

    @Test
    fun `should remove backticks (file)`() {
        val compilationUnit = parseHelper.parseResource("conversion/idValueConverter.smltest")
        compilationUnit.shouldNotBeNull()

        val `class` = compilationUnit.findUniqueDeclarationOrFail<SmlClass>("class")
        `class`.shouldNotBeNull()
    }

    @Test
    fun `should escape keywords (direct converter call)`() {
        idValueConverter.toString("package") shouldBe "`package`"
    }

    @Test
    fun `should escape keywords (creator)`() {
        val compilationUnit = createSmlCompilationUnit {
            smlClass("class")
        }
        createSmlDummyResource("test", FileExtension.TEST, compilationUnit)

        val result = compilationUnit.serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "class `class`"
    }

    @Test
    fun `should not escape non-keywords (direct converter call)`() {
        idValueConverter.toString("notAKeyword") shouldBe "notAKeyword"
    }

    @Test
    fun `should not escape non-keywords (creator)`() {
        val compilationUnit = createSmlCompilationUnit {
            smlPackage("notAKeyword")
        }
        createSmlDummyResource("test", FileExtension.TEST, compilationUnit)

        val result = compilationUnit.serializeToFormattedString()
        result.shouldBeInstanceOf<SerializationResult.Success>()
        result.code shouldBe "package notAKeyword"
    }
}
