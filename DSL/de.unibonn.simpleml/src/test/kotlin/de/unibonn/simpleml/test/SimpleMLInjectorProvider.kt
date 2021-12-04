package de.unibonn.simpleml.test

import org.eclipse.xtext.testing.IInjectorProvider
import org.eclipse.xtext.testing.IRegistryConfigurator
import org.eclipse.xtext.testing.GlobalRegistries.GlobalStateMemento
import com.google.inject.Injector
import org.eclipse.xtext.testing.GlobalRegistries
import com.google.inject.Guice
import de.unibonn.simpleml.SimpleMLRuntimeModule
import de.unibonn.simpleml.SimpleMLStandaloneSetup

class SimpleMLInjectorProvider : IInjectorProvider, IRegistryConfigurator {
    private var stateBeforeInjectorCreation: GlobalStateMemento? = null
    private var stateAfterInjectorCreation: GlobalStateMemento? = null
    private var injector: Injector? = null
    override fun getInjector(): Injector {
        if (injector == null) {
            injector = internalCreateInjector()
            stateAfterInjectorCreation = GlobalRegistries.makeCopyOfGlobalState()
        }
        return injector!!
    }

    private fun internalCreateInjector(): Injector {
        return object : SimpleMLStandaloneSetup() {
            override fun createInjector(): Injector {
                return Guice.createInjector(createRuntimeModule())
            }
        }.createInjectorAndDoEMFRegistration()
    }

    private fun createRuntimeModule(): SimpleMLRuntimeModule {
        // make it work also with Maven/Tycho and OSGI
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=493672
        return object : SimpleMLRuntimeModule() {
            override fun bindClassLoaderToInstance(): ClassLoader {
                return SimpleMLInjectorProvider::class.java
                    .classLoader
            }
        }
    }

    override fun restoreRegistry() {
        stateBeforeInjectorCreation!!.restoreGlobalState()
        stateBeforeInjectorCreation = null
    }

    override fun setupRegistry() {
        stateBeforeInjectorCreation = GlobalRegistries.makeCopyOfGlobalState()
        if (injector == null) {
            getInjector()
        }
        stateAfterInjectorCreation!!.restoreGlobalState()
    }

    companion object {
        init {
            GlobalRegistries.initializeDefaults()
        }
    }
}
