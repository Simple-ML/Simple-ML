package de.unibonn.simpleml.utils

import com.google.inject.Inject
import com.google.inject.Singleton
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.IContainer
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.resource.IResourceDescription
import org.eclipse.xtext.resource.IResourceDescriptions
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider

class SimpleMLIndexExtensions @Inject constructor(
        private val containerManager: IContainer.Manager,
        private val resourceDescriptionsProvider: ResourceDescriptionsProvider
) {

    fun visibleExternalGlobalDeclarationDescriptions(eObject: EObject): Map<QualifiedName, List<IEObjectDescription>> {
        val allVisibleGlobalDeclarationDescriptions = visibleGlobalDeclarationDescriptions(eObject)
        val exportedGlobalDeclarationDescriptions = eObject.exportedGlobalDeclarationDescriptions()
        val difference = allVisibleGlobalDeclarationDescriptions - exportedGlobalDeclarationDescriptions

        return difference.groupBy { it.qualifiedName }
    }

    private fun EObject.exportedGlobalDeclarationDescriptions(): List<IEObjectDescription> =
            this.exportedEObjectDescriptions().filter { it.isGlobalDeclaration() }

    private fun EObject.exportedEObjectDescriptions(): List<IEObjectDescription> =
            this.eObjectDescription().exportedObjects.toList()

    fun visibleGlobalDeclarationDescriptions(eObject: EObject): List<IEObjectDescription> =
            eObject.visibleEObjectDescriptions().filter { it.isGlobalDeclaration() }

    private fun EObject.visibleEObjectDescriptions(): List<IEObjectDescription> =
            this.visibleContainers().map { it.exportedObjects }.flatten()

    private fun EObject.visibleContainers(): List<IContainer> =
            containerManager.getVisibleContainers(this.eObjectDescription(), this.resourceDescriptions())

    private fun EObject.eObjectDescription(): IResourceDescription =
            this.resourceDescriptions().getResourceDescription(this.eResource().uri)

    private fun EObject.resourceDescriptions(): IResourceDescriptions =
            resourceDescriptionsProvider.getResourceDescriptions(this.eResource())

    private fun IEObjectDescription.isGlobalDeclaration(): Boolean =
            this.eClass in setOf(
                    Literals.SML_ANNOTATION,
                    Literals.SML_CLASS,
                    Literals.SML_ENUM,
                    Literals.SML_FUNCTION,
                    Literals.SML_INTERFACE,
                    Literals.SML_WORKFLOW_STEP
            )
}