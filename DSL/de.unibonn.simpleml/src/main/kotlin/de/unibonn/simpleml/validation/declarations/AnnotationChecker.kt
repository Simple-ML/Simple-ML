package de.unibonn.simpleml.validation.declarations

import de.unibonn.simpleml.emf.isConstant
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.staticAnalysis.typing.ClassType
import de.unibonn.simpleml.staticAnalysis.typing.EnumType
import de.unibonn.simpleml.staticAnalysis.typing.type
import de.unibonn.simpleml.stdlibAccess.StdlibClass
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import de.unibonn.simpleml.validation.codes.InfoCode
import org.eclipse.xtext.validation.Check

class AnnotationChecker : AbstractSimpleMLChecker() {

    @Check
    fun uniqueNames(smlAnnotation: SmlAnnotation) {
        smlAnnotation.parametersOrEmpty().reportDuplicateNames {
            "A parameter with name '${it.name}' exists already in this annotation."
        }
    }

    @Check
    fun unnecessaryParameterList(smlAnnotation: SmlAnnotation) {
        if (smlAnnotation.parameterList != null && smlAnnotation.parametersOrEmpty().isEmpty()) {
            info(
                "Unnecessary parameter list.",
                Literals.SML_ABSTRACT_CALLABLE__PARAMETER_LIST,
                InfoCode.UnnecessaryParameterList
            )
        }
    }

    @Check
    fun parameterTypes(smlAnnotation: SmlAnnotation) {
        val validParameterTypes = setOf(
            StdlibClass.Boolean,
            StdlibClass.Float,
            StdlibClass.Int,
            StdlibClass.String,
        )

        smlAnnotation.parametersOrEmpty().forEach {
            val isValid = when (val parameterType = it.type()) {
                is ClassType -> parameterType.smlClass in validParameterTypes
                is EnumType -> parameterType.smlEnum.isConstant()
                else -> false
            }

            if (!isValid) {
                error(
                    "Parameters of annotations must have type Boolean, Float, Int, String, or a constant enum.",
                    it,
                    Literals.SML_PARAMETER__TYPE,
                    ErrorCode.UnsupportedAnnotationParameterType
                )
            }
        }
    }
}
