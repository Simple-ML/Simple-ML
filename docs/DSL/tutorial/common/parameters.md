# Parameters

_Parameters_ define the expected inputs of some declaration that can be [called][calls]. We refer to such declarations as _callables_.

## Required Parameters

_Required parameters_ must always be passed when the declaration is [called][calls]. Let us look at an example:

```
requiredParameter: Int
```

Here are the pieces of syntax:
* The name of the parameter (here `requiredParameter`). This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of parameters.
* A colon.
* The [type][types] of the parameter (here `Int`).

## Optional Parameters

_Optional parameters_ have a default value and, thus, need not be passed as an [argument][calls] unless the default value does not fit. Here is an example:

```
optionalParameter: Int = 1
```

These are the syntactic elements:
* The name of the parameter (here `optionalParameter`). This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of parameters.
* A colon.
* The [type][types] of the parameter (here `Int`).
* An equals sign.
* The default value of the parameter (here `1`). This must be a constant expression, i.e. something that can be evaluated by the compiler. Particularly [calls][calls] usually do not fulfill this requirement.

## Variadic Parameters

_Variadic parameters_ can consume arbitrarily many [arguments][calls]. Here is an example:

```
vararg variadicParameter: Int
```

Let us break down the syntax:
* The keyword `vararg`
* The name of the parameter (here `variadicParameter`). This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of parameters.
* A colon.
* The [type][types] of the parameter (here `Int`).

## Complete Example

Let us now look at a full example of a [step][steps] called `doSomething` with one [required parameter](#required-parameters) and one [optional parameter](#optional-parameters):

```
step doSomething(requiredParameter: Int, optionalParameter: Boolean = false) {
    // ...
}
```

The interesting part is the list of parameters, which uses the following syntactic elements:
* An opening parenthesis.
* A list of parameters, the syntax is as described above. They are separated by commas. A trailing commas is permitted.
* A closing parenthesis.

## Restrictions

Several restrictions apply to the order of parameters and to combinations of the various categories of parameters:
* After an [optional parameter](#optional-parameters) all parameters must be optional.
* A single [variadic parameter](#variadic-parameters) can be added at the end of the parameter list.
* Implied by this: A callable cannot have both [optional parameters](#optional-parameters) and [variadic parameters](#variadic-parameters).


[types]: ./types.md
[steps]: ../workflow-language/steps.md
[calls]: ../workflow-language/expressions.md#calls
