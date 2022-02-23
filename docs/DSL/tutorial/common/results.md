# Results

_Results_ define the outputs of some declaration when it is [called][calls]. Here is an example:

```
result: Int
```

Here is a breakdown of the syntax:
* The name of the result (here `result`). This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of parameters.
* A colon.
* The [type][types] of the parameter (here `Int`).

## Complete Example

Let us now look at a full example of a [step][steps] called `doSomething` with two results:

```
step doSomething() -> (result1: Int, result2: Boolean) {
    // ...
}
```

The interesting part is the list of results, which uses the following syntactic elements:
* An arrow `->`.
* An opening parenthesis.
* A list of results, the syntax is as described above. They are separated by commas. A trailing commas is permitted.
* A closing parenthesis.

## Shorthand Version: Single Result

In case that the callable produces only a single result, we can omit the parentheses. The following two declarations are, hence, equivalent:

```
step doSomething1() -> (result: Int) {}
```

```
step doSomething2() -> result: Int {}
```

## Shorthand Version: No Results

In case that the callable produces no results, we can usually omit the entire results list. The following two declarations are, hence equivalent:

```
step doSomething1() -> () {}
```

```
step doSomething2() {}
```

The notable exception are [callable types][callable-types], where the result list must always be specified even when it is empty.

[types]: ./types.md
[callable-types]: ./types.md#callable-type
[steps]: ../workflow-language/steps.md
[calls]: ../workflow-language/expressions.md#calls
