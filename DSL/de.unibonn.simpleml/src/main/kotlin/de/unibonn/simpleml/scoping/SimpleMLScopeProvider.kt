package de.unibonn.simpleml.scoping

import com.google.inject.Inject
import de.unibonn.simpleml.emf.compilationUnitOrNull
import de.unibonn.simpleml.emf.containingClassOrNull
import de.unibonn.simpleml.emf.membersOrEmpty
import de.unibonn.simpleml.emf.parametersOrEmpty
import de.unibonn.simpleml.emf.placeholdersOrEmpty
import de.unibonn.simpleml.emf.typeParametersOrNull
import de.unibonn.simpleml.emf.uniquePackageOrNull
import de.unibonn.simpleml.emf.variantsOrEmpty
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlAnnotationUse
import de.unibonn.simpleml.simpleML.SmlArgument
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlAssignment
import de.unibonn.simpleml.simpleML.SmlBlock
import de.unibonn.simpleml.simpleML.SmlCall
import de.unibonn.simpleml.simpleML.SmlClass
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlLambda
import de.unibonn.simpleml.simpleML.SmlMemberAccess
import de.unibonn.simpleml.simpleML.SmlMemberType
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNamedTypeDeclaration
import de.unibonn.simpleml.simpleML.SmlPlaceholder
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlStatement
import de.unibonn.simpleml.simpleML.SmlTypeArgument
import de.unibonn.simpleml.simpleML.SmlTypeArgumentList
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraint
import de.unibonn.simpleml.simpleML.SmlTypeParameterConstraintList
import de.unibonn.simpleml.simpleML.SmlWorkflow
import de.unibonn.simpleml.simpleML.SmlWorkflowStep
import de.unibonn.simpleml.simpleML.SmlYield
import de.unibonn.simpleml.typing.ClassType
import de.unibonn.simpleml.typing.EnumType
import de.unibonn.simpleml.typing.EnumVariantType
import de.unibonn.simpleml.typing.NamedType
import de.unibonn.simpleml.typing.TypeComputer
import de.unibonn.simpleml.utils.ClassHierarchy
import de.unibonn.simpleml.utils.closestAncestorOrNull
import de.unibonn.simpleml.utils.isStatic
import de.unibonn.simpleml.utils.parametersOrNull
import de.unibonn.simpleml.utils.resultsOrNull
import de.unibonn.simpleml.utils.typeParametersOrNull
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.FilteringScope

/**
 * This class contains custom scoping description.
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class SimpleMLScopeProvider @Inject constructor(
    private val classHierarchy: ClassHierarchy,
    private val typeComputer: TypeComputer
) : AbstractSimpleMLScopeProvider() {

    override fun getScope(context: EObject, reference: EReference): IScope {
        return when (context) {
            is SmlArgument -> scopeForArgumentParameter(context)
            is SmlNamedType -> scopeForNamedTypeDeclaration(context)
            is SmlReference -> scopeForReferenceDeclaration(context)
            is SmlTypeArgument -> scopeForTypeArgumentTypeParameter(context)
            is SmlTypeParameterConstraint -> scopeForTypeParameterConstraintLeftOperand(context)
            is SmlAnnotationUse, is SmlYield -> {
                super.getScope(context, reference)
            }
            else -> IScope.NULLSCOPE
        }
    }

    private fun scopeForArgumentParameter(smlArgument: SmlArgument): IScope {
        val parameters = smlArgument
            .closestAncestorOrNull<SmlArgumentList>()
            ?.parametersOrNull()
            ?.filterNot { it.isVararg }
            ?: emptyList()
        return Scopes.scopeFor(parameters)
    }

    private fun scopeForReferenceDeclaration(context: SmlReference): IScope {
        val container = context.eContainer()
        return when {
            container is SmlMemberAccess && container.member == context -> scopeForMemberAccessDeclaration(container)
            else -> {
                val resource = context.eResource()

                // Declarations in other files
                var result: IScope = FilteringScope(
                    super.delegateGetScope(context, SimpleMLPackage.Literals.SML_REFERENCE__DECLARATION)
                ) {
                    // Keep only external declarations, since this also includes all local declarations
                    it != null && it.eObjectOrProxy.eResource() != resource && it.eObjectOrProxy !is SmlAnnotation
                }

                // Declarations in this file
                result = declarationsInSameFile(resource, result)

                // Declarations in this package
                result = declarationsInSamePackageDeclaration(resource, result)

                // Declarations in containing classes
                context.containingClassOrNull()?.let {
                    result = classMembers(it, result)
                }

                // Declarations in containing blocks
                localDeclarations(context, result)
            }
        }
    }

    private fun scopeForMemberAccessDeclaration(context: SmlMemberAccess): IScope {
        val receiver = context.receiver

        // Call results
        var resultScope = IScope.NULLSCOPE
        if (receiver is SmlCall) {
            val results = receiver.resultsOrNull()
            when {
                results == null -> return IScope.NULLSCOPE
                results.size > 1 -> return Scopes.scopeFor(results)
                results.size == 1 -> resultScope = Scopes.scopeFor(results)
            }
        }

        // Members
        val type = (typeComputer.typeOf(receiver) as? NamedType) ?: return resultScope

        return when {
            type.isNullable && !context.isNullable -> resultScope
            type is ClassType -> {
                val members = type.smlClass.membersOrEmpty().filter { it.isStatic() == type.isStatic }
                val superTypeMembers = classHierarchy.superClassMembers(type.smlClass)
                    .filter { it.isStatic() == type.isStatic }
                    .toList()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers, resultScope))
            }
            type is EnumType -> {
                val members = when {
                    type.isStatic -> type.smlEnum.variantsOrEmpty()
                    else -> emptyList()
                }
                val superTypeMembers = emptyList<SmlDeclaration>()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers, resultScope))
            }
            type is EnumVariantType -> Scopes.scopeFor(type.smlEnumVariant.parametersOrEmpty())
            else -> resultScope
        }
    }

    private fun declarationsInSameFile(resource: Resource, parentScope: IScope): IScope {
        if (resource.compilationUnitOrNull()?.uniquePackageOrNull() != null) {
            return Scopes.scopeFor(
                emptyList(),
                parentScope
            )
        }

        val members = resource.compilationUnitOrNull()
            ?.members
            ?.filter { it !is SmlAnnotation && it !is SmlWorkflow }
            ?: emptyList()

        return Scopes.scopeFor(
            members,
            parentScope
        )
    }

    private fun declarationsInSamePackageDeclaration(resource: Resource, parentScope: IScope): IScope {
        val members = resource.compilationUnitOrNull()
            ?.uniquePackageOrNull()
            ?.members
            ?.filter { it !is SmlAnnotation && it !is SmlWorkflow }
            ?: emptyList()

        return Scopes.scopeFor(
            members,
            parentScope
        )
    }

    private fun classMembers(context: SmlClass, parentScope: IScope): IScope {
        return when (val containingClassOrNull = context.containingClassOrNull()) {
            is SmlClass -> Scopes.scopeFor(context.membersOrEmpty(), classMembers(containingClassOrNull, parentScope))
            else -> Scopes.scopeFor(context.membersOrEmpty(), parentScope)
        }
    }

    private fun localDeclarations(context: EObject, parentScope: IScope): IScope {
        val containingStatement = context.closestAncestorOrNull<SmlStatement>()
        val containingBlock = containingStatement?.closestAncestorOrNull<SmlBlock>() ?: return parentScope

        val placeholders = containingBlock.placeholdersUpTo(containingStatement)

        return when (val callable = containingBlock.eContainer()) {
            is SmlLambda -> Scopes.scopeFor(
                placeholders + callable.parametersOrEmpty(),
                localDeclarations(callable, parentScope)
            )
            is SmlWorkflowStep -> Scopes.scopeFor(placeholders + callable.parametersOrEmpty(), parentScope)
            else -> Scopes.scopeFor(placeholders, parentScope)
        }
    }

    private fun SmlBlock.placeholdersUpTo(containingStatement: SmlStatement): List<SmlPlaceholder> {
        return this.statements
            .takeWhile { it !== containingStatement }
            .filterIsInstance<SmlAssignment>()
            .flatMap { it.placeholdersOrEmpty() }
    }

    private fun scopeForNamedTypeDeclaration(context: SmlNamedType): IScope {
        val container = context.eContainer()
        return when {
            container is SmlMemberType && container.member == context -> scopeForMemberTypeDeclaration(container)
            else -> {
                super.getScope(context, SimpleMLPackage.Literals.SML_NAMED_TYPE__DECLARATION)
            }
        }
    }

    private fun scopeForMemberTypeDeclaration(context: SmlMemberType): IScope {
        val type = (typeComputer.typeOf(context.receiver) as? NamedType) ?: return IScope.NULLSCOPE

        return when {
            type.isNullable -> IScope.NULLSCOPE
            type is ClassType -> {
                val members = type.smlClass.membersOrEmpty().filterIsInstance<SmlNamedTypeDeclaration>()
                val superTypeMembers = classHierarchy.superClassMembers(type.smlClass)
                    .filterIsInstance<SmlNamedTypeDeclaration>()
                    .toList()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers))
            }
            type is EnumType -> Scopes.scopeFor(type.smlEnum.variantsOrEmpty())
            else -> IScope.NULLSCOPE
        }
    }

    private fun scopeForTypeArgumentTypeParameter(smlTypeArgument: SmlTypeArgument): IScope {
        val typeParameters = smlTypeArgument
            .closestAncestorOrNull<SmlTypeArgumentList>()
            ?.typeParametersOrNull()
            ?: emptyList()

        return Scopes.scopeFor(typeParameters)
    }

    private fun scopeForTypeParameterConstraintLeftOperand(smlTypeParameterConstraint: SmlTypeParameterConstraint): IScope {
        val typeParameters = smlTypeParameterConstraint
            .closestAncestorOrNull<SmlTypeParameterConstraintList>()
            ?.typeParametersOrNull()
            ?: emptyList()

        return Scopes.scopeFor(typeParameters)
    }
}
