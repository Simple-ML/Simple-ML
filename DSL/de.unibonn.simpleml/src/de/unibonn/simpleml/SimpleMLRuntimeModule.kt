package de.unibonn.simpleml

import com.google.inject.Binder
import com.google.inject.name.Names
import de.unibonn.simpleml.resource.SimpleMLResourceDescriptionStrategy
import de.unibonn.simpleml.scoping.SimpleMLImportedNamespaceAwareLocalScopeProvider
import de.unibonn.simpleml.serializer.SimpleMLCrossReferenceSerializer
import de.unibonn.simpleml.serializer.SimpleMLHiddenTokenSequencer
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy
import org.eclipse.xtext.scoping.IScopeProvider
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider
import org.eclipse.xtext.serializer.sequencer.IHiddenTokenSequencer
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
@Suppress("unused")
open class SimpleMLRuntimeModule : AbstractSimpleMLRuntimeModule() {
    fun bindICrossReferenceSerializer(): Class<out ICrossReferenceSerializer> {
        return SimpleMLCrossReferenceSerializer::class.java
    }

    fun bindIDefaultResourceDescriptionStrategy(): Class<out IDefaultResourceDescriptionStrategy> {
        return SimpleMLResourceDescriptionStrategy::class.java
    }

    fun bindIHiddenTokenSequencer(): Class<out IHiddenTokenSequencer> {
        return SimpleMLHiddenTokenSequencer::class.java
    }

    override fun configureIScopeProviderDelegate(binder: Binder) {
        binder.bind(IScopeProvider::class.java)
            .annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
            .to(SimpleMLImportedNamespaceAwareLocalScopeProvider::class.java)
    }
}
