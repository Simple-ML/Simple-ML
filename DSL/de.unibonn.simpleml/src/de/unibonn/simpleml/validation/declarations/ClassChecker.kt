package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val CLASS_MUST_HAVE_ONLY_ONE_PARENT_CLASS = "CLASS_MUST_HAVE_ONLY_ONE_PARENT_CLASS"
const val CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES = "CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES"
const val CLASS_MUST_INHERIT_ONLY_INTERFACES_AND_ONE_OPEN_CLASS = "CLASS_MUST_INHERIT_ONLY_INTERFACES_AND_ONE_OPEN_CLASS"
const val CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF = "CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF"
const val PARENT_CLASS_MUST_BE_OPEN = "PARENT_CLASS_MUST_BE_OPEN"
const val UNNECESSARY_CLASS_BODY = "UNNECESSARY_CLASS_BODY"

class ClassChecker @Inject constructor(
        private val classHierarchy: ClassHierarchy
) : AbstractSimpleMLChecker() {

    @Check
    fun acyclicSuperTypes(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
                .filter {
                    val resolvedClass = it.resolveToClassOrNull()
                    resolvedClass != null && classHierarchy.isSubtypeOf(resolvedClass, smlClass)
                }
                .forEach {
                    error(
                            "A class must not directly or indirectly be a subtype of itself.",
                            it,
                            null,
                            CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF
                    )
                }
    }

    @Check
    fun body(smlClass: SmlClass) {
        if (smlClass.body != null && smlClass.membersOrEmpty().isEmpty()) {
            warning(
                    "Unnecessary class body.",
                    Literals.SML_CLASS_OR_INTERFACE__BODY,
                    UNNECESSARY_CLASS_BODY
            )
        }
    }

    @Check
    fun mustInheritOnlyOpenClassesAndInterfaces(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
                .filter {
                    val resolvedClass = it.resolveToClassOrNull()
                    resolvedClass != null && !resolvedClass.isOpen()
                }
                .forEach {
                    error(
                            "The parent class must be open.",
                            it,
                            null,
                            PARENT_CLASS_MUST_BE_OPEN
                    )
                }

        smlClass.parentTypesOrEmpty()
                .filter { it.resolveToClassOrInterfaceOrNull() == null }
                .forEach {
                    error(
                            "A class must only inherit interfaces and one open class.",
                            it,
                            null,
                            CLASS_MUST_INHERIT_ONLY_INTERFACES_AND_ONE_OPEN_CLASS
                    )
                }
    }

    @Check
    fun onlyOneParentClass(smlClass: SmlClass) {
        val parentClasses = smlClass.parentTypesOrEmpty()
                .filter { it.resolveToClassOrNull() != null }

        if (parentClasses.size > 1) {
            parentClasses.forEach {
                error(
                        "A class must have only one parent class.",
                        it,
                        null,
                        CLASS_MUST_HAVE_ONLY_ONE_PARENT_CLASS
                )
            }
        }
    }

    @Check
    fun uniqueNames(smlClass: SmlClass) {
        smlClass.membersOrEmpty()
                .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this class." }
    }

    @Check
    fun uniqueParentTypes(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
                .filter { it.resolveToClassOrInterfaceOrNull() != null }
                .duplicatesBy { it.resolveToClassOrInterfaceOrNull()?.name }
                .forEach {
                    error(
                            "Parent types must be unique.",
                            it,
                            null,
                            CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES
                    )
                }
    }
}