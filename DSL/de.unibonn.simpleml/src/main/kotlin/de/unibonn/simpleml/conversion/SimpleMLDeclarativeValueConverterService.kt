@file:Suppress("FunctionName")

package de.unibonn.simpleml.conversion

import com.google.inject.Inject
import com.google.inject.Singleton
import org.eclipse.xtext.conversion.IValueConverter
import org.eclipse.xtext.conversion.ValueConverter
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService
import org.eclipse.xtext.conversion.impl.INTValueConverter

/**
 * Converters for ID, INT, and STRING.
 */
@Singleton
open class SimpleMLDeclarativeValueConverterService : AbstractDeclarativeValueConverterService() {

    @ValueConverter(rule = "ID")
    fun ID() = SimpleMLIDValueConverter()

    @Inject
    private lateinit var intValueConverter: INTValueConverter

    @ValueConverter(rule = "INT")
    fun INT(): IValueConverter<Int?> {
        return intValueConverter
    }

    @ValueConverter(rule = "STRING")
    fun STRING() = SimpleMLSTRINGValueConverter()
}
