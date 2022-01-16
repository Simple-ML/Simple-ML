package de.unibonn.simpleml.scoping

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.IContainer
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.resource.IResourceDescription
import org.eclipse.xtext.resource.IResourceDescriptions
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider

internal object IndexExtensionsInjectionTarget {

    @Inject
    lateinit var containerManager: IContainer.Manager

    @Inject
    lateinit var resourceDescriptionsProvider: ResourceDescriptionsProvider
}

fun EObject.visibleExternalGlobalDeclarationDescriptions(): Map<QualifiedName, List<IEObjectDescription>> {
    val allVisibleGlobalDeclarationDescriptions = visibleGlobalDeclarationDescriptions()
    val exportedGlobalDeclarationDescriptions = exportedGlobalDeclarationDescriptions()
    val difference = allVisibleGlobalDeclarationDescriptions - exportedGlobalDeclarationDescriptions.toSet()

    return difference.groupBy { it.qualifiedName }
}

private fun EObject.exportedGlobalDeclarationDescriptions(): List<IEObjectDescription> =
    this.exportedEObjectDescriptions().filter { it.isGlobalDeclaration() }

private fun EObject.exportedEObjectDescriptions(): List<IEObjectDescription> =
    this.eObjectDescription().exportedObjects.toList()

fun EObject.visibleGlobalDeclarationDescriptions(): List<IEObjectDescription> =
    visibleEObjectDescriptions().filter { it.isGlobalDeclaration() }

private fun EObject.visibleEObjectDescriptions(): List<IEObjectDescription> =
    this.visibleContainers().map { it.exportedObjects }.flatten()

private fun EObject.visibleContainers(): List<IContainer> =
    IndexExtensionsInjectionTarget.containerManager.getVisibleContainers(
        this.eObjectDescription(),
        this.resourceDescriptions()
    )

private fun EObject.eObjectDescription(): IResourceDescription =
    this.resourceDescriptions().getResourceDescription(this.eResource().uri)

private fun EObject.resourceDescriptions(): IResourceDescriptions =
    IndexExtensionsInjectionTarget.resourceDescriptionsProvider.getResourceDescriptions(this.eResource())

private fun IEObjectDescription.isGlobalDeclaration(): Boolean =
    this.eClass in setOf(
        Literals.SML_ANNOTATION,
        Literals.SML_CLASS,
        Literals.SML_ENUM,
        Literals.SML_FUNCTION,
        Literals.SML_STEP
    )
