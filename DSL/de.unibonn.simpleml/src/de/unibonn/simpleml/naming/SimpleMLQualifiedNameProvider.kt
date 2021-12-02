package de.unibonn.simpleml.naming

import de.unibonn.simpleml.simpleML.SmlAnnotationUseHolder
import de.unibonn.simpleml.simpleML.SmlDeclaration
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider
import org.eclipse.xtext.naming.QualifiedName

class SimpleMLQualifiedNameProvider : DefaultDeclarativeQualifiedNameProvider() {
    override fun computeFullyQualifiedNameFromNameAttribute(obj: EObject): QualifiedName? {
        return if (obj is SmlAnnotationUseHolder && obj.eContainer() is SmlDeclaration) {
            super.computeFullyQualifiedNameFromNameAttribute(obj.eContainer())
        } else {
            super.computeFullyQualifiedNameFromNameAttribute(obj)
        }
    }
}
