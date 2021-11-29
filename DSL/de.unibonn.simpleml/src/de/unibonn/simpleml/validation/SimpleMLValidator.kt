package de.unibonn.simpleml.validation


import de.unibonn.simpleml.validation.declarations.AnnotationChecker
import de.unibonn.simpleml.validation.declarations.AttributeChecker
import de.unibonn.simpleml.validation.declarations.ClassChecker
import de.unibonn.simpleml.validation.declarations.CompilationUnitChecker
import de.unibonn.simpleml.validation.declarations.DeclarationChecker
import de.unibonn.simpleml.validation.declarations.EnumChecker
import de.unibonn.simpleml.validation.declarations.FunctionChecker
import de.unibonn.simpleml.validation.declarations.ImportChecker
import de.unibonn.simpleml.validation.declarations.ParameterChecker
import de.unibonn.simpleml.validation.declarations.ParameterListChecker
import de.unibonn.simpleml.validation.declarations.PlaceholderChecker
import de.unibonn.simpleml.validation.declarations.ResultChecker
import de.unibonn.simpleml.validation.declarations.WorkflowChecker
import de.unibonn.simpleml.validation.declarations.WorkflowStepChecker
import de.unibonn.simpleml.validation.expressions.CallChecker
import de.unibonn.simpleml.validation.expressions.LambdaChecker
import de.unibonn.simpleml.validation.expressions.MemberAccessChecker
import de.unibonn.simpleml.validation.other.ArgumentListChecker
import de.unibonn.simpleml.validation.other.TypeArgumentListChecker
import de.unibonn.simpleml.validation.statements.AssignmentChecker
import de.unibonn.simpleml.validation.statements.ExpressionsStatementChecker
import de.unibonn.simpleml.validation.types.CallableTypeChecker
import de.unibonn.simpleml.validation.types.NamedTypeChecker
import de.unibonn.simpleml.validation.types.UnionTypeChecker
import org.eclipse.xtext.validation.ComposedChecks


/**
 * This class contains custom validation rules.
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@ComposedChecks(
    validators = [
        PrologChecker::class,

        // Declarations
        AnnotationChecker::class,
        AttributeChecker::class,
        ClassChecker::class,
        CompilationUnitChecker::class,
        DeclarationChecker::class,
        EnumChecker::class,
        FunctionChecker::class,
        ImportChecker::class,
        ParameterChecker::class,
        ParameterListChecker::class,
        PlaceholderChecker::class,
        ResultChecker::class,
        TypeArgumentListChecker::class,
        WorkflowChecker::class,
        WorkflowStepChecker::class,

        // Expressions
        CallChecker::class,
        LambdaChecker::class,
        MemberAccessChecker::class,

        // Statements
        AssignmentChecker::class,
        ExpressionsStatementChecker::class,

        // Types
        CallableTypeChecker::class,
        NamedTypeChecker::class,
        UnionTypeChecker::class,

        // Other
        ArgumentListChecker::class
    ]
)
class SimpleMLValidator : AbstractSimpleMLValidator()
