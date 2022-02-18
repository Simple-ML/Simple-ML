# Stub Language

The stub language is the part of the Simple-ML DSL that is used to integrate functions written in Python into Simple-ML. It describes which functionality is available and how it must be used. The stub language has the following concepts:

* [Classes][classes] define custom datatypes that bundle data and operations on this data.
* [Global Functions][global-functions] define operations that do not belong to a specific [class][classes].
* [Enumerations][enumerations] define custom datatypes with a fixed set of possible variants.
* [Annotations][annotations] attach additional metainformation to declarations.
* [Packages and imports][packages-imports] help avoid conflicts that could arise if two declarations have the same name.
* [Types][types] describe the values that a declaration can accept.

Files that use the stub language must have the extension `.smlstub`.

[classes]: ./classes.md
[global-functions]: ./global-functions.md
[enumerations]: ./enumerations.md
[annotations]: ./annotations.md
[packages-imports]: ../common/packages-and-imports.md
[types]: ../common/types.md
