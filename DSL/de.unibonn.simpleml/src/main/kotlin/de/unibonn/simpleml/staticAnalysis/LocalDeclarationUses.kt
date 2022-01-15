package de.unibonn.simpleml.staticAnalysis

import de.unibonn.simpleml.emf.descendants
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.simpleML.SmlAbstractStatement
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import org.eclipse.emf.ecore.EObject

fun SmlParameter.usesIn(obj: EObject) = obj.descendants<SmlReference>().filter { it.declaration == this }

fun SmlPlaceholder.usesIn(obj: EObject): Sequence<SmlReference> {
    return obj.descendants<SmlAbstractStatement>()
        .dropWhile { it !is SmlAssignment || this !in it.placeholdersOrEmpty() }
        .drop(1)
        .flatMap { statement ->
            statement.descendants<SmlReference>()
                .filter { it.declaration == this }
        }
}
