package de.unibonn.simpleml.formatting2

import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.simpleML.SimpleMLPackage.Literals.*
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.FormatterPreferenceKeys.indentation
import org.eclipse.xtext.formatting2.IFormattableDocument
import org.eclipse.xtext.formatting2.ITextReplacerContext
import org.eclipse.xtext.formatting2.internal.AbstractTextReplacer
import org.eclipse.xtext.formatting2.regionaccess.ISemanticRegion
import org.eclipse.xtext.formatting2.regionaccess.ITextReplacement
import org.eclipse.xtext.preferences.MapBasedPreferenceValues
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.xbase.lib.Procedures
import kotlin.reflect.KFunction1
import org.eclipse.xtext.formatting2.IHiddenRegionFormatter as Format

// We follow the rule here that an object never formats its preceding or following region. This is left to the parent.

@Suppress("unused")
class SimpleMLFormatter : AbstractFormatter2() {

    private val indent = Format::indent
    private val noSpace = Format::noSpace
    private val oneSpace = Format::oneSpace
    private val newLine = Format::newLine
    private fun newLines(n: Int): Procedures.Procedure1<in Format> {
        return Procedures.Procedure1 { it.setNewLines(n) }
    }

    override fun format(obj: Any, doc: IFormattableDocument) {
        when (obj) {
            is XtextResource -> {
                useSpacesForIndentation()
                _format(obj, doc)
            }

            /**********************************************************************************************************
             * Declarations
             **********************************************************************************************************/

            is SmlCompilationUnit -> {

                // Keyword "package"
                doc.formatKeyword(obj, "package", noSpace, oneSpace)

                // Feature "name"
                val name = obj.regionForFeature(SML_COMPILATION_UNIT__NAME)
                if (name != null) {
                    doc.addReplacer(WhitespaceCollapser(doc, name))

                    if (obj.imports.isNotEmpty() || obj.members.isNotEmpty()) {
                        doc.append(name, newLines(2))
                    } else {
                        doc.append(name, noSpace)
                    }
                }

                // Feature "imports"
                obj.imports.forEach {
                    doc.format(it)

                    if (obj.imports.last() == it && obj.members.isNotEmpty()) {
                        doc.append(it, newLines(2))
                    } else if (obj.imports.last() != it) {
                        doc.append(it, newLine)
                    } else {
                        doc.append(it, noSpace)

                    }
                }

                // Feature "members"
                obj.members.forEach {
                    doc.format(it)

                    if (obj.members.last() != it) {
                        doc.append(it, newLines(2))
                    } else {
                        doc.append(it, noSpace)
                    }
                }

                doc.append(obj, newLine)
            }
            is SmlImport -> {

                // Keyword "import"
                doc.formatKeyword(obj, "import", noSpace, oneSpace)

                // Feature "importedNamespace"
                val importedNamespace = obj.regionForFeature(SML_IMPORT__IMPORTED_NAMESPACE)
                if (importedNamespace != null) {
                    doc.addReplacer(WhitespaceCollapser(doc, importedNamespace))

                    if (obj.regionForKeyword("as") == null) {
                        doc.append(importedNamespace, noSpace)
                    }
                }

                // Keyword "as"
                doc.formatKeyword(obj, "as", oneSpace, oneSpace)

                // Feature "alias"
                doc.formatFeature(obj, SML_IMPORT__ALIAS, null, noSpace)
            }
            is SmlAnnotation -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "annotation"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "annotation", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "annotation", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // EObject "parameterList"
                doc.formatObject(obj.parameterList, noSpace, null)
            }
            is SmlAnnotationUse -> {

                // Keyword "@"
                doc.formatKeyword(obj, "@", null, noSpace)

                // Feature "annotation"
                doc.formatFeature(obj, SML_ANNOTATION_USE__ANNOTATION, noSpace, null)

                // EObject "argumentList"
                doc.formatObject(obj.argumentList, noSpace, null)
            }
            is SmlAttribute -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "attr"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "attr", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "attr", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // Keyword ":"
                doc.formatKeyword(obj, ":", noSpace, oneSpace)

                // EObject "type"
                doc.formatType(obj.type, oneSpace, null)
            }
            is SmlClass -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "class"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "class", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "class", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // EObject "typeParameterList"
                doc.formatObject(obj.typeParameterList, noSpace, null)

                // EObject "constructor"
                doc.formatObject(obj.constructor, oneSpace, null)

                // EObject "parentTypeList"
                doc.formatObject(obj.parentTypeList, oneSpace, null)

                // EObject "typeParameterConstraintList"
                doc.formatObject(obj.typeParameterConstraintList, oneSpace, null)

                // EObject "body"
                doc.formatObject(obj.body, oneSpace, null)
            }
            is SmlConstructor -> {

                // Keyword "constructor"
                doc.formatKeyword(obj, "constructor", null, noSpace)

                // EObject "parameterList
                doc.formatObject(obj.parameterList, noSpace, null)
            }
            is SmlParentTypeList -> {

                // Keyword "sub"
                doc.formatKeyword(obj, "sub", null, oneSpace)

                // Feature "parentTypes"
                obj.parentTypes.forEach {
                    doc.formatType(it)
                }

                // Keywords ","
                doc.formatCommas(obj)
            }
            is SmlClassOrInterfaceBody -> {

                // Keyword "{"
                val openingBrace = obj.regionForKeyword("{")
                if (obj.members.isEmpty()) {
                    doc.append(openingBrace, noSpace)
                } else {
                    doc.append(openingBrace, newLine)
                }

                // Feature "members"
                obj.members.forEach {
                    doc.format(it)
                    if (obj.members.last() == it) {
                        doc.append(it, newLine)
                    } else {
                        doc.append(it, newLines(2))
                    }
                }

                // Keyword "}"
                val closingBrace = obj.regionForKeyword("}")
                doc.prepend(closingBrace, noSpace)

                doc.interior(openingBrace, closingBrace, indent)
            }
            is SmlEnum -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "enum"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "enum", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "enum", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // EObject "body"
                doc.formatObject(obj.body, oneSpace, null)
            }
            is SmlEnumBody -> {
                // Keyword "{"
                val openingBrace = obj.regionForKeyword("{")
                if (obj.instances.isEmpty()) {
                    doc.append(openingBrace, noSpace)
                } else {
                    doc.append(openingBrace, newLine)
                }

                // Feature "instances"
                obj.instances.forEach {
                    doc.format(it)
                    if (obj.instances.first() != it) {
                        doc.prepend(it, newLines(2))
                    }
                }

                // Keywords ","
                val commas = textRegionExtensions.allRegionsFor(obj).keywords(",")
                commas.forEach {
                    doc.prepend(it, noSpace)
                }

                // Keyword "}"
                val closingBrace = obj.regionForKeyword("}")
                if (obj.instances.isEmpty()) {
                    doc.prepend(closingBrace, noSpace)
                } else {
                    doc.prepend(closingBrace, newLine)
                }

                doc.interior(openingBrace, closingBrace, indent)
            }
            is SmlEnumInstance -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Feature "name"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)
                } else {
                    doc.formatFeature(obj, SML_DECLARATION__NAME)
                }
            }
            is SmlFunction -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "fun"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "fun", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "fun", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // EObject "typeParameterList"
                doc.formatObject(obj.typeParameterList, noSpace, null)

                // EObject "parameterList"
                doc.formatObject(obj.parameterList, noSpace, null)

                // EObject "resultList"
                doc.formatObject(obj.resultList, oneSpace, null)

                // EObject "typeParameterConstraintList"
                doc.formatObject(obj.typeParameterConstraintList, oneSpace, null)
            }
            is SmlInterface -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "interface"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatKeyword(obj, "interface", oneSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "interface", null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)

                // EObject "typeParameterList"
                doc.formatObject(obj.typeParameterList, noSpace, null)

                // EObject "constructor"
                doc.formatObject(obj.constructor, oneSpace, null)

                // EObject "parentTypeList"
                doc.formatObject(obj.parentTypeList, oneSpace, null)

                // EObject "typeParameterConstraintList"
                doc.formatObject(obj.typeParameterConstraintList, oneSpace, null)

                // EObject "body"
                doc.formatObject(obj.body, oneSpace, null)
            }
            is SmlWorkflow -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "workflow"
                if (obj.annotations.isEmpty() && obj.modifiers.isEmpty()) {
                    doc.formatKeyword(obj, "workflow", noSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "workflow", oneSpace, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, null, oneSpace)

                // EObject "body"
                doc.formatObject(obj.body)
            }
            is SmlWorkflowStep -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj)

                // Keyword "workflow"
                if (obj.annotations.isEmpty() && obj.modifiers.isEmpty()) {
                    doc.formatKeyword(obj, "step", noSpace, oneSpace)
                } else {
                    doc.formatKeyword(obj, "step", oneSpace, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, null, noSpace)

                // EObject "parameterList"
                doc.formatObject(obj.parameterList)

                // EObject "resultList"
                doc.formatObject(obj.resultList)

                // EObject "body"
                doc.formatObject(obj.body, oneSpace, null)
            }
            is SmlArgumentList -> {

                // Keyword "("
                doc.formatKeyword(obj, "(", null, noSpace)

                // Feature "parameters"
                obj.arguments.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ")"
                doc.formatKeyword(obj, ")", noSpace, null)
            }
            is SmlArgument -> {

                // Feature "parameter"
                doc.formatFeature(obj, SML_ARGUMENT__PARAMETER)

                // Keyword "="
                doc.formatKeyword(obj, "=", oneSpace, oneSpace)

                // EObject "value"
                doc.formatExpression(obj.value)
            }
            is SmlParameterList -> {

                // Keyword "("
                doc.formatKeyword(obj, "(", null, noSpace)

                // Feature "parameters"
                obj.parameters.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ")"
                doc.formatKeyword(obj, ")", noSpace, null)
            }
            is SmlParameter -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj, inlineAnnotations = true)

                // Keyword "vararg"
                if (!obj.annotations.isEmpty() || !obj.modifiers.isEmpty()) {
                    doc.formatKeyword(obj, "vararg", oneSpace, null)
                }

                // Feature "name"
                val name = obj.regionForFeature(SML_DECLARATION__NAME)
                if (!obj.annotations.isEmpty() || !obj.modifiers.isEmpty() || obj.isVararg) {
                    doc.prepend(name, oneSpace)
                }

                // Keyword ":"
                doc.formatKeyword(obj, ":", noSpace, oneSpace)

                // EObject "type"
                doc.formatType(obj.type)

                // Keyword "or"
                doc.formatKeyword(obj, "or", oneSpace, oneSpace)

                // EObject "defaultValue"
                doc.formatObject(obj.defaultValue)
            }
            is SmlResultList -> {

                // Keyword "->"
                doc.formatKeyword(obj, "->", oneSpace, oneSpace)

                // Keyword "("
                doc.formatKeyword(obj, "(", null, noSpace)

                // Feature "results"
                obj.results.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ")"
                doc.formatKeyword(obj, ")", noSpace, null)
            }
            is SmlResult -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj, inlineAnnotations = true)

                // Feature "name"
                val name = obj.regionForFeature(SML_DECLARATION__NAME)
                if (!obj.annotations.isEmpty() || !obj.modifiers.isEmpty()) {
                    doc.prepend(name, oneSpace)
                }

                // Keyword ":"
                doc.formatKeyword(obj, ":", noSpace, oneSpace)

                // EObject "type"
                doc.formatType(obj.type)
            }


            /**********************************************************************************************************
             * Statements
             **********************************************************************************************************/

            is SmlBlock -> {

                // Keyword "{"
                val openingBrace = obj.regionForKeyword("{")
                if (obj.statements.isEmpty()) {
                    doc.append(openingBrace, noSpace)
                } else {
                    doc.append(openingBrace, newLine)
                }

                // Feature "statements"
                obj.statements.forEach {
                    doc.formatObject(it, null, newLine)
                }

                // Keyword "}"
                val closingBrace = obj.regionForKeyword("}")
                doc.prepend(closingBrace, noSpace)

                doc.interior(openingBrace, closingBrace, indent)
            }
            is SmlAssignment -> {

                // EObject "assigneeList"
                doc.formatObject(obj.assigneeList, null, oneSpace)

                // Keyword "="
                doc.formatKeyword(obj, "=", oneSpace, oneSpace)

                // EObject "expression"
                doc.formatExpression(obj.expression)

                // Keyword ";"
                doc.formatKeyword(obj, ";", noSpace, null)
            }
            is SmlAssigneeList -> {

                // Keyword "("
                doc.formatKeyword(obj, "(", null, noSpace)

                // Feature "parameters"
                obj.assignees.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ")"
                doc.formatKeyword(obj, ")", noSpace, null)
            }
            is SmlLambdaYield -> {

                // Keyword "yield"
                doc.formatKeyword(obj, "yield", null, oneSpace)

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)
            }
            is SmlPlaceholder -> {

                // Keyword "val"
                doc.formatKeyword(obj, "val", null, oneSpace)

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME, oneSpace, null)
            }
            is SmlYield -> {

                // Keyword "yield"
                doc.formatKeyword(obj, "yield", null, oneSpace)

                // Feature "result"
                doc.formatFeature(obj, SML_YIELD__RESULT)
            }
            is SmlExpressionStatement -> {

                // EObject "expression"
                doc.formatExpression(obj.expression)

                // Keyword ";"
                doc.formatKeyword(obj, ";", noSpace, null)
            }


            /**********************************************************************************************************
             * Expressions
             **********************************************************************************************************/

            is SmlCall -> {

                // EObject "receiver"
                doc.formatExpression(obj.receiver, null, noSpace)

                // EObject "typeArgumentList"
                doc.formatObject(obj.typeArgumentList, null, noSpace)

                // EObject "argumentList"
                doc.formatObject(obj.argumentList)
            }
            is SmlInfixOperation -> {

                // EObject "leftOperand"
                doc.formatExpression(obj.leftOperand, null, oneSpace)

                // Feature "operator"
                doc.formatFeature(obj, SML_INFIX_OPERATION__OPERATOR, oneSpace, oneSpace)

                // EObject "rightOperand"
                doc.formatExpression(obj.rightOperand, oneSpace, null)
            }
            is SmlMemberAccess -> {

                // EObject "receiver"
                doc.formatExpression(obj.receiver, null, noSpace)

                // Feature "nullable"
                doc.formatFeature(obj, SML_MEMBER_ACCESS__NULLABLE, noSpace, noSpace)

                // Keyword "."
                doc.formatKeyword(obj, ".", noSpace, noSpace)

                // EObject "member"
                doc.formatObject(obj.member, noSpace, null)
            }
            is SmlPrefixOperation -> {

                // Feature "operator"
                doc.formatFeature(
                        obj,
                        SML_PREFIX_OPERATION__OPERATOR,
                        prepend = null,
                        append = if (obj.operator == "not") oneSpace else noSpace
                )

                // EObject "operand"
                doc.formatExpression(obj.operand)
            }
            is SmlLambda -> {

                // Keyword "lambda"
                doc.formatKeyword(obj, "lambda", null, null)

                // EObject "parameterList"
                doc.formatObject(obj.parameterList, oneSpace, null)

                // EObject "body"
                doc.formatObject(obj.body, oneSpace, null)
            }


            /**********************************************************************************************************
             * Types
             **********************************************************************************************************/

            is SmlCallableType -> {

                // Keyword "callable"
                doc.formatKeyword(obj, "callable", null, oneSpace)

                // EObject "parameterList"
                doc.formatObject(obj.parameterList, oneSpace, oneSpace)

                // EObject "resultList"
                doc.formatObject(obj.resultList, oneSpace, null)
            }
            is SmlMemberType -> {

                // EObject "receiver"
                doc.formatObject(obj.receiver, null, noSpace)

                // Keyword "."
                doc.formatKeyword(obj, ".", noSpace, noSpace)

                // EObject "member"
                doc.formatObject(obj.member, noSpace, null)
            }
            is SmlNamedType -> {

                // Feature "declaration"
                doc.formatFeature(obj, SML_NAMED_TYPE__DECLARATION)

                // EObject "typeArgumentList"
                doc.formatObject(obj.typeArgumentList, noSpace, null)

                // Feature "nullable"
                doc.formatFeature(obj, SML_NAMED_TYPE__NULLABLE, noSpace, null)
            }
            is SmlUnionType -> {

                // Keyword "union"
                doc.formatKeyword(obj, "union", null, noSpace)

                // EObject "typeArgumentList"
                doc.formatObject(obj.typeArgumentList, noSpace, null)
            }
            is SmlTypeArgumentList -> {

                // Keyword "<"
                doc.formatKeyword(obj, "<", null, noSpace)

                // Feature "typeArguments"
                obj.typeArguments.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ">"
                doc.formatKeyword(obj, ">", noSpace, null)
            }
            is SmlTypeArgument -> {

                // Feature "typeParameter"
                doc.formatFeature(obj, SML_TYPE_ARGUMENT__TYPE_PARAMETER)

                // Keyword "="
                doc.formatKeyword(obj, "=", oneSpace, oneSpace)

                // EObject "value"
                doc.formatObject(obj.value)
            }
            is SmlTypeProjection -> {

                // Feature "variance"
                doc.formatFeature(obj, SML_TYPE_PROJECTION__VARIANCE, null, oneSpace)

                // EObject "type"
                doc.formatType(obj.type)
            }
            is SmlTypeParameterList -> {

                // Keyword "<"
                doc.formatKeyword(obj, "<", null, noSpace)

                // Feature "typeParameters"
                obj.typeParameters.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)

                // Keyword ">"
                doc.formatKeyword(obj, ">", noSpace, null)
            }
            is SmlTypeParameter -> {

                // Features "annotations" and "modifiers"
                doc.formatAnnotationsAndModifiers(obj, inlineAnnotations = true)

                // Feature "variance"
                if (obj.annotations.isNotEmpty() || obj.modifiers.isNotEmpty()) {
                    doc.formatFeature(obj, SML_TYPE_PARAMETER__VARIANCE, oneSpace, oneSpace)
                } else {
                    doc.formatFeature(obj, SML_TYPE_PARAMETER__VARIANCE, null, oneSpace)
                }

                // Feature "name"
                doc.formatFeature(obj, SML_DECLARATION__NAME)
            }
            is SmlTypeParameterConstraintList -> {

                // Keyword "where"
                doc.formatKeyword(obj, "where", null, oneSpace)

                // Feature "constraints"
                obj.constraints.forEach {
                    doc.formatObject(it)
                }

                // Keywords ","
                doc.formatCommas(obj)
            }
            is SmlTypeParameterConstraint -> {

                // Feature "leftOperand"
                doc.formatFeature(obj, SML_TYPE_PARAMETER_CONSTRAINT__LEFT_OPERAND, null, oneSpace)

                // Feature "operator"
                doc.formatFeature(obj, SML_TYPE_PARAMETER_CONSTRAINT__OPERATOR, oneSpace, oneSpace)

                // EObject "rightOperand"
                doc.formatType(obj.rightOperand, oneSpace, null)
            }
        }
    }

    /******************************************************************************************************************
     * Helpers
     ******************************************************************************************************************/

    private fun useSpacesForIndentation() {
        val newPreferences = mutableMapOf<String, String>()
        newPreferences[indentation.id] = "    "
        request.preferences = MapBasedPreferenceValues(preferences, newPreferences)
    }

    private fun EObject.regionForFeature(feature: EStructuralFeature): ISemanticRegion? {
        return textRegionExtensions.regionFor(this).feature(feature)
    }

    private fun EObject.regionForKeyword(keyword: String): ISemanticRegion? {
        return textRegionExtensions.regionFor(this).keyword(keyword)
    }

    private fun IFormattableDocument.formatObject(
            obj: EObject?,
            prepend: KFunction1<Format, Unit>? = null,
            append: KFunction1<Format, Unit>? = null
    ) {
        if (obj != null) {
            if (prepend != null) {
                this.prepend(obj, prepend)
            }
            this.format(obj)
            if (append != null) {
                this.append(obj, append)
            }
        }
    }

    private fun IFormattableDocument.formatFeature(
            obj: EObject?,
            feature: EStructuralFeature,
            prepend: KFunction1<Format, Unit>? = null,
            append: KFunction1<Format, Unit>? = null
    ) {
        if (obj == null) {
            return
        }

        val featureRegion = obj.regionForFeature(feature)
        if (featureRegion != null) {
            if (prepend != null) {
                this.prepend(featureRegion, prepend)
            }
            if (append != null) {
                this.append(featureRegion, append)
            }
        }
    }

    private fun IFormattableDocument.formatKeyword(
            obj: EObject?,
            keyword: String,
            prepend: KFunction1<Format, Unit>? = null,
            append: KFunction1<Format, Unit>? = null
    ) {
        if (obj == null) {
            return
        }

        val keywordRegion = obj.regionForKeyword(keyword)
        if (keywordRegion != null) {
            if (prepend != null) {
                this.prepend(keywordRegion, prepend)
            }
            if (append != null) {
                this.append(keywordRegion, append)
            }
        }
    }

    private fun IFormattableDocument.formatAnnotationsAndModifiers(obj: SmlDeclaration, inlineAnnotations: Boolean = false) {

        // Feature "annotations"
        obj.annotations.forEach {
            format(it)

            if (inlineAnnotations) {
                append(it, oneSpace)
            } else {
                append(it, newLine)
            }
        }

        // Feature "modifiers"
        val modifiers = textRegionExtensions.allRegionsFor(obj).features(SML_DECLARATION__MODIFIERS)
        modifiers.forEach {
            if (modifiers.last() != it) {
                append(it, oneSpace)
            }
        }
    }

    private fun IFormattableDocument.formatExpression(
            obj: SmlExpression?,
            prepend: KFunction1<Format, Unit>? = null,
            append: KFunction1<Format, Unit>? = null
    ) {

        // Keyword "("
        formatKeyword(obj, "(", null, noSpace)

        // Type itself
        formatObject(obj, prepend, append)

        // Keyword ")"
        formatKeyword(obj, ")", noSpace, null)
    }

    private fun IFormattableDocument.formatType(
            obj: SmlType?,
            prepend: KFunction1<Format, Unit>? = null,
            append: KFunction1<Format, Unit>? = null
    ) {

        // Keyword "("
        formatKeyword(obj, "(", null, noSpace)

        // Type itself
        formatObject(obj, prepend, append)

        // Keyword ")"
        formatKeyword(obj, ")", noSpace, null)
    }

    private fun IFormattableDocument.formatCommas(obj: EObject) {
        val commas = textRegionExtensions.allRegionsFor(obj).keywords(",")
        commas.forEach {
            prepend(it, noSpace)
            append(it, oneSpace)
        }
    }
}

class WhitespaceCollapser(doc: IFormattableDocument, name: ISemanticRegion?) : AbstractTextReplacer(doc, name) {
    override fun createReplacements(context: ITextReplacerContext): ITextReplacerContext {
        context.addReplacement(collapseWhitespace())
        return context
    }

    private fun collapseWhitespace(): ITextReplacement {
        return region.replaceWith(region.text.replace(Regex("\\s+"), ""))
    }
}