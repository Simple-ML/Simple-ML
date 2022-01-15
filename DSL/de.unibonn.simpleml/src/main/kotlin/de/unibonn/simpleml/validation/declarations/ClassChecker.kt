package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.emf.classMembersOrEmpty
import de.unibonn.simpleml.emf.objectsInBodyOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.parentTypesOrEmpty
import de.unibonn.simpleml.emf.protocolsOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.staticAnalysis.ClassResult
import de.unibonn.simpleml.staticAnalysis.classOrNull
import de.unibonn.simpleml.staticAnalysis.maybeClass
import de.unibonn.simpleml.typing.ClassHierarchy
import de.unibonn.simpleml.typing.inheritedNonStaticMembersOrEmpty
import de.unibonn.simpleml.utils.duplicatesBy
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

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
                    ErrorCode.CLASS_MUST_NOT_BE_SUBTYPE_OF_ITSELF
                )
            }
    }

    @Check
    fun body(smlClass: SmlClass) {
        if (smlClass.body != null && smlClass.objectsInBodyOrEmpty().isEmpty()) {
            info(
                "Unnecessary class body.",
                Literals.SML_CLASS__BODY,
                InfoCode.UnnecessaryBody
            )
        }
    }

    @Check
    fun mustInheritOnlyClasses(smlClass: SmlClass) {
        smlClass.parentTypesOrEmpty()
            .filter { it.maybeClass() is ClassResult.NotAClass }
            .forEach {
                error(
                    "A class must only inherit classes.",
                    it,
                    null,
                    ErrorCode.CLASS_MUST_INHERIT_ONLY_CLASSES
                )
            }
    }

    @Check
    fun mustHaveUniqueInheritedMembers(smlClass: SmlClass) {
        smlClass.inheritedNonStaticMembersOrEmpty()
            .groupBy { it.name }
            .forEach { (name, declarationsWithName) ->
                if (declarationsWithName.size > 1) {
                    error(
                        "Inherits multiple members called '$name'.",
                        Literals.SML_ABSTRACT_DECLARATION__NAME,
                        ErrorCode.CLASS_MUST_HAVE_UNIQUE_INHERITED_MEMBERS
                    )
                }
            }
    }

    @Check
    fun uniqueNames(smlClass: SmlClass) {
        smlClass.parametersOrEmpty()
            .reportDuplicateNames { "A parameter with name '${it.name}' exists already in this class." }

        smlClass.classMembersOrEmpty()
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
                    ErrorCode.CLASS_MUST_HAVE_UNIQUE_PARENT_TYPES
                )
            }
    }

    @Check
    fun unnecessaryTypeParameterList(smlClass: SmlClass) {
        if (smlClass.typeParameterList != null && smlClass.typeParametersOrEmpty().isEmpty()) {
            info(
                "Unnecessary type parameter list.",
                Literals.SML_CLASS__TYPE_PARAMETER_LIST,
                InfoCode.UnnecessaryTypeParameterList
            )
        }
    }

    @Check
    fun multipleProtocols(smlClass: SmlClass) {
        val protocols = smlClass.protocolsOrEmpty()
        if (protocols.size > 1) {
            protocols.forEach {
                error(
                    "A class must have only one protocol.",
                    it,
                    null,
                    ErrorCode.OneProtocolPerClass
                )
            }
        }
    }
}
