package de.unibonn.simpleml.tests.assertions

import de.unibonn.simpleml.tests.ExpectedIssue
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.validation.Issue

fun List<Issue>.shouldHaveSyntaxError(expected: ExpectedIssue) {
    val syntaxErrors = this.filter { it.isSyntaxError }
    if (syntaxErrors.none { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveNoSyntaxError(expected: ExpectedIssue) {
    val syntaxErrors = this.filter { it.isSyntaxError }
    if (syntaxErrors.any { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveSemanticError(expected: ExpectedIssue) {
    val errors = this.filter { it.severity == Severity.ERROR }
    if (errors.none { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveNoSemanticError(expected: ExpectedIssue) {
    val errors = this.filter { it.severity == Severity.ERROR }
    if (errors.any { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveSemanticWarning(expected: ExpectedIssue) {
    val warnings = this.filter { it.severity == Severity.WARNING }
    if (warnings.none { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveNoSemanticWarning(expected: ExpectedIssue) {
    val warnings = this.filter { it.severity == Severity.WARNING }
    if (warnings.any { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveSemanticInfo(expected: ExpectedIssue) {
    val warnings = this.filter { it.severity == Severity.INFO }
    if (warnings.none { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveNoSemanticInfo(expected: ExpectedIssue) {
    val warnings = this.filter { it.severity == Severity.INFO }
    if (warnings.any { expected.matches(it) }) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

fun List<Issue>.shouldHaveNoIssue(expected: ExpectedIssue) {
    if (this.isNotEmpty()) {
        throw AssertionError("Expected $expected but got${this.stringify()}")
    }
}

private fun List<Issue>.stringify(): String {
    if (this.isEmpty()) {
        return " nothing."
    }

    return this.joinToString(prefix = ":\n", separator = "\n") { "    * $it" }
}
