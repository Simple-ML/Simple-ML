package de.unibonn.simpleml.names

import org.eclipse.xtext.naming.QualifiedName

object StdlibPackages {
    val lang: QualifiedName = QualifiedName.create("simpleml", "lang")
}

object StdlibAnnotations {
    val Deprecated: QualifiedName = StdlibPackages.lang.append("Deprecated")
    val Pure: QualifiedName = StdlibPackages.lang.append("Pure")
}

object StdlibClasses {
    val Any: QualifiedName = StdlibPackages.lang.append("Any")
    val Boolean: QualifiedName = StdlibPackages.lang.append("Boolean")
    val Float: QualifiedName = StdlibPackages.lang.append("Float")
    val Int: QualifiedName = StdlibPackages.lang.append("Int")
    val String: QualifiedName = StdlibPackages.lang.append("String")
}
