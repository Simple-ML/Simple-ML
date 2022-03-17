package de.unibonn.simpleml.stdlibDocumentation

import de.unibonn.simpleml.scoping.allGlobalDeclarations
import org.eclipse.emf.ecore.EObject
import java.nio.file.Path

/**
 * Generates documentation for all declarations that are visible from the given context.
 *
 * @receiver The context.
 */
fun EObject.generateDocumentation(outputDirectory: Path) {
    allGlobalDeclarations()
        .forEach {
            println(it.qualifiedName)
        }
}
