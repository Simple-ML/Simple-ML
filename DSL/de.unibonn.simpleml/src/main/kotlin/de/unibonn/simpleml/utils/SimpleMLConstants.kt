package de.unibonn.simpleml.utils

import org.eclipse.xtext.naming.QualifiedName

// File extensions -----------------------------------------------------------------------------------------------------

const val SML_STUB_EXTENSION = ".stub.simpleml"
const val SML_TEST_EXTENSION = ".test.simpleml"
const val SML_WORKFLOW_EXTENSION = ".simpleml"

// Annotations ---------------------------------------------------------------------------------------------------------

val smlDeprecated: QualifiedName = QualifiedName.create("simpleml", "lang", "Deprecated")
val smlPure: QualifiedName = QualifiedName.create("simpleml", "lang", "Pure")

// Modifiers -----------------------------------------------------------------------------------------------------------

const val SML_OPEN = "open"
const val SML_OVERRIDE = "override"
const val SML_STATIC = "static"

val smlModifiers = listOf(SML_OPEN, SML_OVERRIDE, SML_STATIC)
