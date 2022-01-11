package de.unibonn.simpleml.interpreter

import de.unibonn.simpleml.emf.createSmlBoolean
import de.unibonn.simpleml.emf.createSmlFloat
import de.unibonn.simpleml.emf.createSmlInt
import de.unibonn.simpleml.emf.createSmlNull
import de.unibonn.simpleml.emf.createSmlString
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAbstractExpression
import de.unibonn.simpleml.simpleML.SmlBoolean
import de.unibonn.simpleml.simpleML.SmlFloat
import de.unibonn.simpleml.simpleML.SmlInt
import de.unibonn.simpleml.simpleML.SmlNull
import de.unibonn.simpleml.simpleML.SmlString
import de.unibonn.simpleml.simpleML.SmlTemplateStringEnd
import de.unibonn.simpleml.simpleML.SmlTemplateStringInner
import de.unibonn.simpleml.simpleML.SmlTemplateStringStart

fun SmlAbstractExpression.toConstantExpression(): SmlAbstractExpression? {
    return when (this) {

        // Base cases
        is SmlBoolean -> createSmlBoolean(isTrue)
        is SmlFloat -> createSmlFloat(value)
        is SmlInt -> createSmlInt(value)
        is SmlNull -> createSmlNull()
        is SmlString -> createSmlString(value)
        is SmlTemplateStringStart -> createSmlString(value)
        is SmlTemplateStringInner -> createSmlString(value)
        is SmlTemplateStringEnd -> createSmlString(value)


        // Catch-all case
        else -> null // TODO: throw instead
    }
}
