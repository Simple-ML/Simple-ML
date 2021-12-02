package de.unibonn.simpleml.resource

import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAttribute
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlEnum
import de.unibonn.simpleml.simpleML.SmlEnumVariant
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy
import org.eclipse.xtext.util.IAcceptor

class SimpleMLResourceDescriptionStrategy : DefaultResourceDescriptionStrategy() {
    override fun createEObjectDescriptions(eObject: EObject, acceptor: IAcceptor<IEObjectDescription>): Boolean {
        return when (eObject) {
            is SmlCompilationUnit -> createEObjectDescriptionsForSmlCompilationUnit(eObject, acceptor)
            is SmlAnnotation -> createEObjectDescriptionsForSmlAnnotation(eObject, acceptor)
            is SmlAttribute -> createEObjectDescriptionsForSmlAttribute(eObject, acceptor)
            is SmlClass -> createEObjectDescriptionsForSmlClass(eObject, acceptor)
            is SmlEnum -> createEObjectDescriptionsForSmlEnum(eObject, acceptor)
            is SmlEnumVariant -> createEObjectDescriptionsForSmlEnumVariant(eObject, acceptor)
            is SmlFunction -> createEObjectDescriptionsForSmlFunction(eObject, acceptor)
            is SmlParameter -> createEObjectDescriptionsForSmlParameter(eObject, acceptor)
            is SmlWorkflowStep -> createEObjectDescriptionsForSmlWorkflowStep(eObject, acceptor)
            else -> true
        }
    }

    private fun createEObjectDescriptionsForSmlCompilationUnit(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlAnnotation(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlAttribute(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlClass(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlEnum(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlEnumVariant(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlFunction(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlParameter(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }

    private fun createEObjectDescriptionsForSmlWorkflowStep(
        eObject: EObject,
        acceptor: IAcceptor<IEObjectDescription>
    ): Boolean {
        return super.createEObjectDescriptions(eObject, acceptor)
    }
}
