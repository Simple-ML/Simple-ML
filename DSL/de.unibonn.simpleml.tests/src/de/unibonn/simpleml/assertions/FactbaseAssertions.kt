package de.unibonn.simpleml.assertions

import de.unibonn.simpleml.prolog_bridge.model.facts.AnnotationUseT
import de.unibonn.simpleml.prolog_bridge.model.facts.DeclarationT
import de.unibonn.simpleml.prolog_bridge.model.facts.ExpressionT
import de.unibonn.simpleml.prolog_bridge.model.facts.ModifierT
import de.unibonn.simpleml.prolog_bridge.model.facts.Node
import de.unibonn.simpleml.prolog_bridge.model.facts.NodeWithParent
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFact
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFactbase
import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.simpleML.SmlExpression
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.eclipse.emf.ecore.EObject

inline fun <reified T : PlFact> PlFactbase.findUniqueFactOrFail(filter: (T) -> Boolean = { true }): T {
    shouldHaveUniqueFact(filter)
    return findUniqueFact(filter)!!
}

inline fun <reified T : PlFact> PlFactbase.shouldHaveUniqueFact(filter: (T) -> Boolean) {
    val candidates = findFacts(filter)
    if (candidates.isEmpty()) {
        throw AssertionError("Expected a unique matching fact of type ${T::class.simpleName} but found none.")
    } else if (candidates.size > 1) {
        throw AssertionError("Expected a unique matching fact but found ${candidates.size}: $candidates")
    }
}

fun PlFactbase.shouldBeChildOf(childId: Id<EObject>, parent: Node) {
    val child = findUniqueFactOrFail<NodeWithParent> { it.id == childId }
    child.asClue {
        it.id.value shouldBeGreaterThan parent.id.value
        it.parent shouldBe parent.id
    }
}

fun PlFactbase.shouldBeNChildrenOf(
    childIds: List<Id<EObject>>?,
    parent: Node,
    n: Int
) {
    childIds.shouldNotBeNull()
    childIds shouldHaveSize n
    childIds.forEach {
        shouldBeChildOf(it, parent)
    }
}

fun PlFactbase.shouldBeChildExpressionOf(childId: Id<SmlExpression>, parent: Node) {
    val child = findUniqueFactOrFail<ExpressionT> { it.id == childId }
    child.asClue {
        it.id.value shouldBeGreaterThan parent.id.value
        it.parent shouldBe parent.id
        it.enclosing shouldBe expectedEnclosing(parent)
    }
}

fun PlFactbase.shouldBeNChildExpressionsOf(
    childIds: List<Id<SmlExpression>>?,
    parent: Node,
    n: Int
) {
    childIds.shouldNotBeNull()
    childIds shouldHaveSize n
    childIds.forEach {
        shouldBeChildExpressionOf(it, parent)
    }
}

private fun expectedEnclosing(parent: Node): Id<EObject> {
    return when (parent) {
        is ExpressionT -> parent.enclosing
        else -> parent.id
    }
}

fun PlFactbase.shouldHaveNAnnotationUses(
    declaration: DeclarationT,
    n: Int,
) {
    val annotationUses = findFacts<AnnotationUseT> { it.parent == declaration.id }
    annotationUses shouldHaveSize n
    annotationUses.forEach { shouldBeChildOf(it.id, declaration) }
}

fun PlFactbase.shouldHaveNModifiers(
    declaration: DeclarationT,
    n: Int
) {
    val modifiers = findFacts<ModifierT> { it.target == declaration.id }
    modifiers shouldHaveSize n
}
