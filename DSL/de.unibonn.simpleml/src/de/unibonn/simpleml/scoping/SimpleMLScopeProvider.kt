package de.unibonn.simpleml.scoping

import com.google.inject.Inject
import de.unibonn.simpleml.simpleML.*
import de.unibonn.simpleml.typing.*
import de.unibonn.simpleml.utils.*
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes

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
            is SmlReference -> scopeForReferenceDeclaration(context)
            is SmlTypeArgument -> scopeForTypeArgumentTypeParameter(context)
            is SmlTypeParameterConstraint -> scopeForTypeParameterConstraintLeftOperand(context)
            is SmlAnnotationUse, is SmlNamedType, is SmlYield -> {
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
//                super.getScope(context, SimpleMLPackage.Literals.SML_REFERENCE__DECLARATION)
                // TODO
                val locals = locals(context)
                val globals = super.delegateGetScope(context, SimpleMLPackage.Literals.SML_REFERENCE__DECLARATION)

                Scopes.scopeFor(locals, globals)
//                 Must also include functions defined in the same file (if we allow mixing workflows and functions)
            }
        }
    }

    private fun scopeForMemberAccessDeclaration(context: SmlMemberAccess): IScope {
        val type = (typeComputer.typeOf(context.receiver) as? NamedType) ?: return IScope.NULLSCOPE

        // TODO we also want to access results of a function by name call().result (maybe it does make sense to put the
        //  names into the type???

        return when {
            type.isNullable && !context.isNullable -> IScope.NULLSCOPE // TODO: should be able to access extension methods that work on nullable types
            type is ClassType -> {
                val members = type.smlClass.membersOrEmpty().filter { it.isStatic() == type.isStatic }
                val superTypeMembers = classHierarchy.superClassMembers(type.smlClass)
                        .filter { it.isStatic() == type.isStatic }
                        .toList()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers))
            }
            type is EnumType -> {
                val members = when {
                    type.isStatic -> type.smlEnum.instancesOrEmpty()
                    else -> emptyList()
                }
                val superTypeMembers = emptyList<SmlDeclaration>()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers))
            }
            type is InterfaceType -> {
                val members = type.smlInterface.membersOrEmpty()
                        .filterIsInstance<SmlFunction>()
                        .filter { !it.isStatic() }
                val superTypeMembers = classHierarchy.superInterfaceMembers(type.smlInterface)
                        .filterIsInstance<SmlFunction>()
                        .filter { !it.isStatic() }
                        .toList()

                Scopes.scopeFor(members, Scopes.scopeFor(superTypeMembers))
            }
            else -> IScope.NULLSCOPE
        }
    }

    private fun locals(context: EObject): List<SmlDeclaration> {
        return when (val container = context.eContainer()) {
            is SmlBlock -> container.statements.placeholdersUpTo(context)
            null -> emptyList()
            else -> locals(container)
        }
    }

    private fun List<SmlStatement>.placeholdersUpTo(context: EObject): List<SmlPlaceholder> {
        return this.takeWhile { it !== context }
                .filterIsInstance<SmlAssignment>()
                .flatMap { it.placeholdersOrEmpty() }
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
