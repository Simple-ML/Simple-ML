package de.unibonn.simpleml.tests.util

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.utils.SimpleMLStdlib
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.testing.util.ParseHelper

class ParseWithStdlib @Inject constructor(
        private val parseHelper: ParseHelper<SmlCompilationUnit>,
        private val resourceSetProvider: Provider<ResourceSet>,
        private val stdlib: SimpleMLStdlib
) {
    fun parse(input: String) : SmlCompilationUnit? {
        val resourceSet = resourceSetProvider.get()
        stdlib.load(resourceSet)
        return parseHelper.parse(input, resourceSet)
    }
}