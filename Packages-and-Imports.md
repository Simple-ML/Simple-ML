In order to prevent name conflicts when multiple parties add functionality to Simple-ML using the [stub language](./Stub-Language) we support the concept of [packages](#packages). These are used to group [type definitions](./Stub-Language-Types) together under a common namespace. Other files can then use declarations from a package by [importing](#imports) it.

## Packages

Packages are used to provide namespaces, thereby preventing name conflicts. For example, it is perfectly fine to have two [classes](./Stub-Language-Classes) named "DecisionTree" as long as they are in different packages.

Each file must declare its package right at the top. The syntax is the keyword `package` followed by the name of the package, which can be split hierarchically using dots. Here is an example:

    package simpleml.model.regression

Multiple files can be part of the same package. For custom files that are not part of the standard library we recommend to use the [reverse domain name notation](https://en.wikipedia.org/wiki/Reverse_domain_name_notation).

## Imports

The counterpart to packages are imports, which are necessary for using declarations from other packages. The following snippet shows the basic syntax of imports, which consists of the keyword `import` followed by the fully qualified name of the declaration to imports. To refer to the imported declaration in the file we simply need to write its simple name rather than the qualified one. Here the declaration has the name "DecisionTree" and is located in the package called "simpleml.models":

    import simpleml.model.regression.DecisionTree

If the name of the declaration conflicts with other names in the same file it is possible to provide an alias. The syntax starts the same as the basic import but then we add the keyword `as` followed by the alias. Afterwards we can only refer to the declaration using the alias ("DT" in the following example) and not the original name (which was "DecisionTree" in the following example):

    import simpleml.model.regression.DecisionTree as DT

Finally, we can import all declarations in a package using the wildcard `*` instead of the name of a specific declaration. Note that declarations in subpackages, i. e. packages that have a different name but the same prefix as the imported one, are **not** imported. The following import, for instance, imports all declarations that are directly in the package simpleml.model.regression:

    import simpleml.model.regression.*