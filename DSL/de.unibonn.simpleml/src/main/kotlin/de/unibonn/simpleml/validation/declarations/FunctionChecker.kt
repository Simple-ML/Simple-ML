package de.unibonn.simpleml.validation.declarations

import com.google.inject.Inject
import de.unibonn.simpleml.constants.hasOpenModifier
import de.unibonn.simpleml.constants.hasOverrideModifier
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.resultsOrEmpty
import de.unibonn.simpleml.stdlib.isPure
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlFunction
import de.unibonn.simpleml.utils.ClassHierarchy
import de.unibonn.simpleml.utils.isClassMember
import de.unibonn.simpleml.utils.isStatic
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import org.eclipse.xtext.validation.Check

const val FUNCTION_MUST_NOT_BE_OPEN_AND_STATIC = "FUNCTION_MUST_NOT_BE_OPEN_AND_STATIC"
const val FUNCTION_MUST_NOT_BE_OVERRIDE_AND_STATIC = "FUNCTION_MUST_NOT_BE_OVERRIDE_AND_STATIC"
const val NON_STATIC_PROPAGATES = "NON_STATIC_PROPAGATES"
const val OVERRIDDEN_FUNCTION_MUST_BE_OPEN = "OVERRIDDEN_FUNCTION_MUST_BE_OPEN"
const val OVERRIDING_FUNCTION_MUST_HAVE_OVERRIDE_MODIFIER = "OVERRIDING_FUNCTION_MUST_HAVE_OVERRIDE_MODIFIER"
const val OVERRIDING_FUNCTION_MUST_NOT_BE_STATIC = "OVERRIDING_FUNCTION_MUST_NOT_BE_STATIC"
const val OVERRIDING_FUNCTION_MUST_OVERRIDE_SOMETHING = "OVERRIDING_FUNCTION_MUST_OVERRIDE_SOMETHING"
const val PURE_PROPAGATES = "PURE_PROPAGATES"
const val STATIC_PROPAGATES = "STATIC_PROPAGATES"

class FunctionChecker @Inject constructor(
    private val classHierarchy: ClassHierarchy
) : AbstractSimpleMLChecker() {

    @Check
    fun mustNotBeOpenAndStatic(smlFunction: SmlFunction) {
        if (smlFunction.isClassMember() && smlFunction.hasOpenModifier() && smlFunction.isStatic()) {
            error(
                "A function must not be static and open.",
                Literals.SML_DECLARATION__NAME,
                FUNCTION_MUST_NOT_BE_OPEN_AND_STATIC
            )
        }
    }

    @Check
    fun mustNotBeOverrideAndStatic(smlFunction: SmlFunction) {
        if (smlFunction.isClassMember() && smlFunction.hasOverrideModifier() && smlFunction.isStatic()) {
            error(
                "A function must not be static and override.",
                Literals.SML_DECLARATION__NAME,
                FUNCTION_MUST_NOT_BE_OVERRIDE_AND_STATIC
            )
        }
    }

    @Check
    fun nonStaticPropagates(smlFunction: SmlFunction) {
        if (smlFunction.isStatic()) {
            val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
            if (hiddenFunction != null && !hiddenFunction.isStatic()) {
                error(
                    "One of the supertypes of this class declares a non-static function with this name, so this must be non-static as well.",
                    Literals.SML_DECLARATION__NAME,
                    NON_STATIC_PROPAGATES
                )
            }
        }
    }

    @Check
    fun overriddenFunctionMustBeOpen(smlFunction: SmlFunction) {
        val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
        if (hiddenFunction != null && !hiddenFunction.hasOpenModifier() && !hiddenFunction.isStatic()) {
            error(
                "The overridden function must be open.",
                Literals.SML_DECLARATION__NAME,
                OVERRIDDEN_FUNCTION_MUST_BE_OPEN
            )
        }
    }

    @Check
    fun overridingFunctionMustHaveOverrideModifier(smlFunction: SmlFunction) {
        if (!smlFunction.hasOverrideModifier()) {
            val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
            if (hiddenFunction != null && !hiddenFunction.isStatic()) {
                error(
                    "An overriding function must have the override modifier.",
                    Literals.SML_DECLARATION__NAME,
                    OVERRIDING_FUNCTION_MUST_HAVE_OVERRIDE_MODIFIER
                )
            }
        }
    }

    @Check
    fun overriddenFunctionMustNotBeStatic(smlFunction: SmlFunction) {
        if (smlFunction.hasOverrideModifier()) {
            val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
            if (hiddenFunction != null && hiddenFunction.isStatic()) {
                error(
                    "The overridden function must not be static.",
                    Literals.SML_DECLARATION__NAME,
                    OVERRIDING_FUNCTION_MUST_NOT_BE_STATIC
                )
            }
        }
    }

    @Check
    fun overridingFunctionMustOverrideSomething(smlFunction: SmlFunction) {
        if (smlFunction.hasOverrideModifier() && classHierarchy.hiddenFunction(smlFunction) == null) {
            error(
                "This function does not override anything.",
                Literals.SML_DECLARATION__NAME,
                OVERRIDING_FUNCTION_MUST_OVERRIDE_SOMETHING
            )
        }
    }

    @Check
    fun purePropagates(smlFunction: SmlFunction) {
        if (!smlFunction.isPure()) {
            val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
            if (hiddenFunction != null && hiddenFunction.isPure()) {
                error(
                    "One of the supertypes of this class declares a pure function with this name, so this must be pure as well.",
                    Literals.SML_DECLARATION__NAME,
                    PURE_PROPAGATES
                )
            }
        }
    }

    @Check
    fun staticPropagates(smlFunction: SmlFunction) {
        if (!smlFunction.isStatic()) {
            val hiddenFunction = classHierarchy.hiddenFunction(smlFunction)
            if (hiddenFunction != null && hiddenFunction.isStatic()) {
                error(
                    "One of the supertypes of this class declares a static function with this name, so this must be static as well.",
                    Literals.SML_DECLARATION__NAME,
                    STATIC_PROPAGATES
                )
            }
        }
    }

    @Check
    fun uniqueNames(smlFunction: SmlFunction) {
        val declarations = smlFunction.parametersOrEmpty() + smlFunction.resultsOrEmpty()
        declarations.reportDuplicateNames {
            "A parameter or result with name '${it.name}' exists already in this function."
        }
    }
}
