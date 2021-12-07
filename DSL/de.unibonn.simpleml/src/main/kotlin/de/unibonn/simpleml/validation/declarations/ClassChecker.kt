package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.constants.hasOpenModifier
import de.unibonn.simpleml.emf.membersOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.utils.ClassHierarchy
import de.unibonn.simpleml.utils.ClassResult
import de.unibonn.simpleml.utils.classOrNull
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.utils.maybeClass
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val CLASS_MUST_HAVE_ONLY_ONE_PARENT_CLASS = "CLASS_MUST_HAVE_ONLY_ONE_PARENT_CLASS"
const val CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES = "CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES"
const val CLASS_MUST_INHERIT_ONLY_CLASSES = "CLASS_MUST_INHERIT_ONLY_CLASSES"
const val CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF = "CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF"
const val PARENT_CLASS_MUST_BE_OPEN = "PARENT_CLASS_MUST_BE_OPEN"
const val UNNECESSARY_CLASS_BODY = "UNNECESSARY_CLASS_BODY"
const val UNNECESSARY_TYPE_PARAMETER_LIST = "UNNECESSARY_TYPE_PARAMETER_LIST"

class ClassChecker @Inject constructor(
    private val classHierarchy: ClassHierarchy
) : AbstractSimpleMLChecker() {

    @Check
    fun acyclicSuperTypes(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
            .filter {
                val resolvedClass = it.classOrNull()
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
                Literals.SML_CLASS__BODY,
                UNNECESSARY_CLASS_BODY
            )
        }
    }

    @Check
    fun mustInheritOnlyOpenClasses(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
            .filter {
                val resolvedClass = it.classOrNull()
                resolvedClass != null && !resolvedClass.hasOpenModifier()
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
            .filter { it.maybeClass() is ClassResult.NotAClass }
            .forEach {
                error(
                    "A class must only inherit classes.",
                    it,
                    null,
                    CLASS_MUST_INHERIT_ONLY_CLASSES
                )
            }
    }

    @Check
    fun onlyOneParentClass(smlClass: SmlClass) {
        val parentClasses = smlClass.parentTypesOrEmpty()
            .filter { it.classOrNull() != null }

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
        smlClass.parametersOrEmpty()
            .reportDuplicateNames { "A parameter with name '${it.name}' exists already in this class." }

        smlClass.membersOrEmpty()
            .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this class." }
    }

    @Check
    fun uniqueParentTypes(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
            .filter { it.classOrNull() != null }
            .duplicatesBy { it.classOrNull()?.name }
            .forEach {
                error(
                    "Parent types must be unique.",
                    it,
                    null,
                    CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES
                )
            }
    }

    @Check
    fun unnecessaryTypeParameterList(smlClass: SmlClass) {
        if (smlClass.typeParameterList != null && smlClass.typeParametersOrEmpty().isEmpty()) {
            warning(
                "Unnecessary type parameter list.",
                Literals.SML_CLASS__TYPE_PARAMETER_LIST,
                UNNECESSARY_TYPE_PARAMETER_LIST
            )
        }
    }
}
