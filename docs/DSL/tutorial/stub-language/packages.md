# Packages

In order to prevent name conflicts when multiple parties add functionality to Simple-ML using the [stub language](./Stub-Language.md) we support the concept of [packages](#packages). These are used to group [type definitions](./Stub-Language-Types.md) together under a common namespace.

## Packages

Packages are used to provide namespaces, thereby preventing name conflicts. For example, it is perfectly fine to have two [classes](./Stub-Language-Classes.md) named "DecisionTree" as long as they are in different packages.

Each file must declare its package right at the top. The syntax is the keyword `package` followed by the name of the package, which can be split hierarchically using dots. Here is an example:

    package simpleml.model.regression

Multiple files can be part of the same package. For custom files that are not part of the standard library we recommend to use the [reverse domain name notation](https://en.wikipedia.org/wiki/Reverse_domain_name_notation).

**TODO: PythonModule, corresponding Python code**
