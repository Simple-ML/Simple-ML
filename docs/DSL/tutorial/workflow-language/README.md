# Workflow Language

The workflow language is the part of the Simple-ML DSL that is designed to solve specific machine learning problems. It has the following concepts:

* [Packages and imports][packages-imports] help avoid conflicts that could arise if two declarations have the same name.
* [Workflows][workflows] define the entry point of a Machine Learning program.
* [Steps][steps] encapsulate parts of a workflow and make them reusable.
* [Statements][statements] are the instructions that are executed as part of a [workflow][workflows] or [step][steps].
* [Expressions][expressions] are computations that produce some value.
* [Types][types] describe the values that a declaration can accept. They are most prominently used when defining the parameters and results of a [step][steps].

Files that use the workflow language must have the extension `.smlflow`.

[packages-imports]: ../common/packages-and-imports.md
[workflows]: ./workflows.md
[steps]: ./steps.md
[statements]: ./statements.md
[expressions]: ./expressions.md
[types]: ../common/types.md
