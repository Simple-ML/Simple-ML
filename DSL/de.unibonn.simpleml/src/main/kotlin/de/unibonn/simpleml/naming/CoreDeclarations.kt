package de.unibonn.simpleml.naming

import org.eclipse.xtext.naming.QualifiedName

object CorePackages {
    val LANG: QualifiedName = QualifiedName.create("simpleml", "lang")
}

object CoreAnnotations {
    val DEPRECATED: QualifiedName = CorePackages.LANG.append("Deprecated")
    val PURE: QualifiedName = CorePackages.LANG.append("Pure")
}
