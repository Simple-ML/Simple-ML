package de.unibonn.simpleml.assertions

import de.unibonn.simpleml.prolog_bridge.utils.Id
import de.unibonn.simpleml.prolog_bridge.model.facts.*
import de.unibonn.simpleml.simpleML.SmlExpression
import io.kotest.assertions.asClue
import io.kotest.matchers.ints.shouldBeGreaterThan
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

fun PlFactbase.shouldBeChildExpressionOf(childId: Id<SmlExpression>, parent: Node) {
    val child = findUniqueFactOrFail<ExpressionT> { it.id == childId }
    child.asClue {
        it.id.value shouldBeGreaterThan parent.id.value
        it.parent shouldBe parent.id
        it.enclosing shouldBe expectedEnclosing(parent)
    }
}

private fun expectedEnclosing(parent: Node): Id<EObject> {
    return when (parent) {
        is ExpressionT -> parent.enclosing
        else -> parent.id
    }
}
