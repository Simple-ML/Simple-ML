package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.simpleML.SmlInterface
import de.unibonn.simpleml.utils.*
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val INTERFACE_MEMBERS_MUST_BE_FUNCTIONS = "INTERFACE_MEMBERS_MUST_BE_FUNCTIONS"
const val INTERFACE_MUST_HAVE_NO_CONSTRUCTOR = "INTERFACE_MUST_HAVE_NO_CONSTRUCTOR"
const val INTERFACE_MUST_HAVE_UNIQUE_PARENT_TYPES = "INTERFACE_MUST_HAVE_UNIQUE_PARENT_TYPES"
const val INTERFACE_MUST_INHERIT_ONLY_INTERFACES = "INTERFACE_MUST_INHERIT_ONLY_INTERFACES"
const val INTERFACE_MUST_NOT_BE_SUBTYPE_OF_ITSELF = "INTERFACE_MUST_NOT_BE_SUBTYPE_OF_ITSELF"
const val UNNECESSARY_INTERFACE_BODY = "UNNECESSARY_INTERFACE_BODY"

class InterfaceChecker @Inject constructor(
        private val classHierarchy: ClassHierarchy
) : AbstractSimpleMLChecker() {

    @Check
    fun acyclicSuperTypes(smlInterface: SmlInterface) {
        smlInterface.parentTypesOrEmpty()
                .filter {
                    val resolvedInterface = it.resolveToInterfaceOrNull()
                    resolvedInterface != null && classHierarchy.isSubtypeOf(resolvedInterface, smlInterface)
                }.forEach {
                    error(
                            "An interface must not directly or indirectly be a subtype of itself.",
                            it,
                            null,
                            INTERFACE_MUST_NOT_BE_SUBTYPE_OF_ITSELF
                    )
                }
    }

    @Check
    fun body(smlInterface: SmlInterface) {
        if (smlInterface.body != null && smlInterface.membersOrEmpty().isEmpty()) {
            warning(
                    "Unnecessary interface body.",
                    Literals.SML_CLASS_OR_INTERFACE__BODY,
                    UNNECESSARY_INTERFACE_BODY
            )
        }
    }

    @Check
    fun membersMustBeFunctions(smlInterface: SmlInterface) {
        smlInterface.membersOrEmpty()
                .filterNot { it is SmlFunction }
                .forEach {
                    error(
                            "Interface members must be functions.",
                            it,
                            Literals.SML_DECLARATION__NAME,
                            INTERFACE_MEMBERS_MUST_BE_FUNCTIONS
                    )
                }
    }

    @Check
    fun mustInheritOnlyInterfaces(smlInterface: SmlInterface) {
        smlInterface.parentTypesOrEmpty()
                .filter { it.resolveToInterfaceOrNull() == null }
                .forEach {
                    error(
                            "An interface must only inherit interfaces.",
                            it,
                            null,
                            INTERFACE_MUST_INHERIT_ONLY_INTERFACES
                    )
                }
    }

    @Check
    fun noConstructor(smlInterface: SmlInterface) {
        if (smlInterface.constructor != null) {
            error(
                    "An interface must have no constructor.",
                    Literals.SML_CLASS_OR_INTERFACE__CONSTRUCTOR,
                    INTERFACE_MUST_HAVE_NO_CONSTRUCTOR
            )
        }
    }

    @Check
    fun uniqueNames(smlInterface: SmlInterface) {
        smlInterface.membersOrEmpty().filterIsInstance<SmlFunction>()
                .reportDuplicateNames { "A declaration with name '${it.name}' exists already in this interface." }
    }

    @Check
    fun uniqueParentTypes(smlInterface: SmlInterface) {
        smlInterface.parentTypesOrEmpty()
                .filter { it.resolveToInterfaceOrNull() != null }
                .duplicatesBy { it.resolveToInterfaceOrNull()?.name }
                .forEach {
                    error(
                            "Parent types must be unique.",
                            it,
                            null,
                            INTERFACE_MUST_HAVE_UNIQUE_PARENT_TYPES
                    )
                }
    }
}