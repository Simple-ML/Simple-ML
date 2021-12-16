package de.unibonn.simpleml.conversion

import com.google.inject.Singleton
import org.eclipse.xtext.conversion.impl.STRINGValueConverter

/**
 * Handles the conversion between the textual representation of a string (including delimiters and escape sequences) to
 * its actual value.
 *
 * Example: The string `"myString \{"` in a DSL program has the value `myString {`.
 */
@Singleton
class SimpleMLSTRINGValueConverter : AbstractSimpleMLStringValueConverter("\"", "\"")
