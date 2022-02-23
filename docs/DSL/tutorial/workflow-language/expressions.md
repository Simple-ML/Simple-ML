# Expressions

Expressions are the parts of the [workflow language][workflow-language] that evaluate to some value. A multitude of different expression types known from other programming languages are supported by Simple-ML, from basic [literals](#literals) to [lambdas](#lambdas).

## Literals

Literals are the basic building blocks of expressions. They describe a fixed, constant value.

### Int Literals

Int literals denote integers. They use the expected syntax. For example, the integer three is written as `3`.

### Float Literals

Float literals denote floating point numbers. There are two ways to specify them:
* **Decimal form**: One half can be written as `0.5`. Note that neither the integer part nor the decimal part can be omitted, so `.5` and `0.` are syntax errors.
* **Scientific notation**: Writing very large or very small numbers in decimal notation can be cumbersome. In those cases, [scientific notation](https://en.wikipedia.org/wiki/Scientific_notation) is helpful. For example, one thousandth can be written in Simple-ML as `1.0e-3` or `1.0E-3`. You can read this as `1.0 × 10⁻³`. When scientific notation is used, it is allowed to omit the decimal part, so this can be shortened to `1e-3` or `1E-3`.

### String Literals

String literals describe text. Their syntax is simply text enclosed by double quotes: `"Hello, world!"`. Various special characters can be denoted with _escape sequences_:

|Escape sequence|Meaning|
|-|-|
|`\b`|Backspace|
|`\t`|Tab|
|`\n`|New line|
|`\f`|Form feed|
|`\r`|Carriage return|
|`\"`|Double quote|
|`\'`|Single quote|
|`\\`|Backslash|
|`\{`|Opening curly brace (used for [template strings](#template-strings))|
|`\uXXXX`|Unicode character, where `XXXX` is its hexadecimal index|

String literals can contain also contain raw line breaks:

```
"Hello,

world!"
```

In order to interpolate text with other computed values, use [template strings](#template-strings).

### Boolean Literals

To work with truthiness, Simple-ML has the two boolean literals `false` and `true`.

### `null` Literal

To denote that a value is unknown or absent, use the literal `null`.

## Operations

Operations are special functions that can be applied to one or two expressions. Simple-ML has a fixed set of operations that cannot be extended. We distinguish between
* prefix operations (general form `<operator> <operand>`), and
* infix operations (general form `<left operand> <operator> <right operand>`).

### Operations on Numbers

Numbers can be negated using the unary `-` operator:
* The integer negative three is `-3`.
* The float negative three is `-3.0`.

The usual arithmetic operations are also supported for integers, floats and combinations of the two. Note that when either operand is a float, the whole expression is evaluated to a float.
* Addition: `0 + 5` (result is an integer)
* Subtraction: `6 - 2.9` (result is a float)
* Multiplication: `1.1 * 3` (result is a float)
* Division: `1.0 / 4.2` (result is a float)

Finally, two numbers can be compared, which results in a boolean. The integer `3` for example is less than the integer `5`. Simple-ML offers operators to do such checks for order:

* Less than: `5 < 6`
* Less than or equal: `1 <= 3`
* Greater than or equal: `7 >= 7`
* Greater than: `9 > 2`

### Logical Operations

To work with logic, Simple-ML has the two boolean literals `false` and `true` as well as operations to work with them:
* (Logical) negation (example `not a`): Output is `true` if and only if the operand is false:

`not a` | false | true
--------|-------|------
&nbsp;  | true  | false

* Conjunction (example `a and b`): Output is `true` if and only if both operands are `true`. Note that the second operand is always evaluated, even if the first operand is `false` and, thus, already determines the result of the expression. The operator is not short-circuited:

`a and b` | false | true
----------|-------|------
**false** | false | false
**true**  | false | true

* Disjunction (example `a or b`): Output is `true` if and only if at least one operand is `true`. Note that the second operand is always evaluated, even if the first operand is `true` and, thus, already determines the result of the expression. The operator is not short-circuited:

`a or b`  | false | true
----------|-------|-----
**false** | false | true
**true**  | true  | true

### Equality Checks

There are two different types of equality in Simple-ML, _identity_ and _structural equality_. Identity checks if two objects are one and the same, whereas structural equality checks if two objects have the same structure and content. Using a real world example, two phones of the same type would be structurally equal but not identical. Both types of equality checks return a boolean literal `true` if the check was positive and `false` if the check was negative. The syntax for these operations is as follows:

* Identity: `1 === 2`
* Structural equality: `1 == 2`

Simple-ML also has shorthand versions for negated equality checks which should be used instead of an explicit logical negation with the `not` operator:

* Negated identity: `1 !== 2`
* Negated structural equality: `1 != 2`

### Elvis Operator

The elvis operator `?:` (given its name because it resembles Elvis's haircut) is used to specify a default value that should be used instead if the left operand is `null`. This operator is not short-circuited, so both operand are always evaluated. In the following example the whole expression evaluates to `nullableExpression` if this value is not `null` and to `42` if it is:

```
nullableExpression ?: 42
```

## Template Strings

[String literals](#string-literals) can only be used to denote a fixed string. Sometimes, however, parts of the string have to be computed and then interpolated into the remaining text. This is done with template strings. Here is an example:

```
"1 + 2 = {{ 1 + 2 }}"
```

The syntax for template strings is similar to [string literals](#string-literals): They are also delimited by double quotes, the text can contain escape sequences, and raw newlines can be inserted. The additional syntax are _template expressions_, which are any expression enclosed by `{{` and `}}`. There must be no space between the curly braces.

These template expressions are evaluated, converted to a string and inserted into the template string at their position. The template string in the example above is, hence, equivalent to the [string literal](#string-literals) "1 + 2 = 3".

## References

References are used to refer to a declaration, such as a [class][classes] or a [placeholder][placeholders]. The syntax is simply the name of the declaration, as shown in the next snippet where we first declare a [placeholder][placeholders] called `one` and then refer to it when computing the value for the [placeholder][placeholders] called `two`:

```
val one = 1;
val two = one + one;
```

In order to refer to global declarations in other [packages][packages], we first need to [import][imports] them.

## Calls

Calls are used to trigger the execution of a specific action, which can, for example, be the creation of an instance of a [class][classes] or executing the code in a [step][steps]. Let's look at an example:

First, we show the code of the [step][steps] that we want to call.

```
step createDecisionTree(maxDepth: Int = 10) {
    // ... do something ...
}
```

This [step][steps] has a single [parameter][parameters] `maxDepth`, which must have [type][types] `Int`, and has the default value `10`. Since it has a default value, we are not required to specify a value when we call this [step][steps]. The most basic legal call of the [step][steps] is, thus, this:

```
createDecisionTree()
```

This calls the [step][steps] `createDecisionTree`, using the default `maxDepth` of `10`.

The syntax consists of these elements:
* The _callee_ of the call, which is the expression to call (here a [reference](#references) to the [step][steps] `createDecisionTree`)
* The list of arguments, which is delimited by parentheses. In this case the list is empty, so no arguments are passed.

If we want to override the default value of an optional [parameter][parameters] or if the callee has required [parameters][parameters], we need to pass arguments. We can either use _positional arguments_ or _named arguments_. 

In the case of positional arguments, they are mapped to parameters by position, i.e. the first argument is assigned to the first parameter, the second argument is assigned to the second parameter and so forth. We do this in the following example to set `maxDepth` to 5:

```
createDecisionTree(5)
```

The syntax for positional argument is simply the expression we want to pass as value.

Named arguments, however, are mapped to parameters by name. On the one hand, this can improve readability of the code, since the meaning of a value becomes obvious. On the other hand, it allows to override only specific optional parameters and keep the rest unchanged. Here is how to set `maxDepth` to 5 using a named argument:

```
createDecisionTree(maxDepth = 5)
```

These are the syntactic elements:
* The name of the parameter for which we want to specify a value.
* An equals sign.
* The value to assign to the parameter.

### Passing Multiple Arguments

We now add another parameter to the `createDecisionTree` [step][steps]:

```
step createDecisionTree(isBinary: Boolean, maxDepth: Int = 10) {
    // ... do something ...
}
```

This allows us to show how multiple arguments can be passed:

```
createDecisionTree(isBinary = true, maxDepth = 5)
```

We have already seen the syntax for a single argument. If we want to pass multiple arguments, we just separate them by commas. A trailing comma is allowed.

### Restrictions For Arguments

There are some restriction regarding the choice of positional vs. named arguments and passing arguments in general:
* For all [parameters][parameters] of the callee there must be at most one argument.
* For all [required parameters][required-parameters] there must be exactly one argument.
* After a named argument all arguments must be named.
* [Variadic parameters][variadic-parameters] can only be assigned by position.
### Legal Callees

Depending on the callee, a call can do different things. The following table lists all legal callees and what happens if they are called:

|Callee|Meaning|
|-|-|
|[Class][classes]|Create a new instance of the class. The class must have a constructor to be callable. The call evaluates to this new instance.|
|[Enum Variant][enum-variants]|Creates a new instance of the enum variant. Enum variants are always callable. The call evaluates to this new instance.|
|[Global Function][global-functions]|Invokes the function and runs the associated Python code. The call evaluates to the result record of the function.|
|[Method][methods]|Invokes the method and runs the associated Python code. The call evaluates to the result record of the method.|
|[Step][steps]|Invokes the step and runs the Simple-ML code in its body. The call evaluates to the result record of the step.||
|[Block Lambda](#block-lambdas)|Invokes the lambda and runs the Simple-ML code in its body. The call evaluates to the result record of the lambda.||
|[Expression Lambda](#expression-lambdas)|Invokes the lambda and runs the Simple-ML code in its body. The call evaluates to the result record of the lambda.||
|Declaration with [Callable Type][callable-types]|Call whatever the value of the declaration is.

#### Result Record

The term _result record_ warrants further explanation: A result record maps [results][results] of a
* [global function][global-functions],
* [method][methods],
* [step][steps], or
* [lambda](#lambdas)

to their computed values.

If the result record only has a single entry, its value can be accessed directly. Otherwise, the result record must be _deconstructed_ either by an [assignment][assignment-multiple-assignees] (can access multiple results) or by a [member access](#member-access-of-results) (can access a single result).

## Member Accesses

A member access is used to refer to members of a complex data structure such as
* a [class][classes],
* an [enum][enums], or
* the [result record](#result-record) of a [call](#calls).

### Member Access of Class Members

**TODO**

#### Null-Safe Member Access

**TODO**

### Member Access of Enum Variants

**TODO**
### Member Access of Results

**TODO**



**Definition of the example class and enum:**

```
class DecisionTree() {
    static val verboseTraining: Boolean
    val score: Float
}

enum SvmKernel {
    Linear,
    RBF
}
```

**Accessing static class member:**

```
DecisionTree.verboseTraining
```

**Accessing non-static class member (note that we create an instance of the "DecisionTree" class by [calling](#calls) it):**

```
DecisionTree().score
```

**Accessing enum instances:**

```
SvmKernel.Linear
```

### Safe member access

If an expression could be null it cannot be used as the receiver of a regular [member access](#member-access), since null does not have members. Instead a safe member access must be used. The syntax is identical to a normal member access except that we replace the dot with the operator `?.`. A safe member access evaluates to null if the receiver is null. Otherwise it evaluates to the accessed member, just like a normal member access. Here is an example:

```
nullableExpression?.member1?.member2
```

## Indexed Accesses

**TODO**

## Lambdas

**TODO**
### Block Lambdas

**TODO**
### Expression Lambdas

**TODO**

## Precedence

We all know that `2 + 3 * 7` is `23` and not `35`. The reason is that the `*` operator has a higher precedence than the `+` operator and is, therefore, evaluated first. These precedence rules are necessary for all types of expressions listed above and shown in the following list. The higher up an expression is in the list, the higher its precedence and the earlier it is evaluated. Expressions listed beside each other have the same precedence and are evaluated from left to right:

* **HIGHER PRECEDENCE**
* `()` (parentheses around an expression)
* `1` ([integer literals](#int-literals)), `1.0` ([float literals](#float-literals)), `"a"` ([string literals](#string-literals)), `true`/`false` ([boolean literals](#boolean-literals)), `null` ([null literal](#null-literal)), `someName` ([references](#references)), `"age: {{ age }}"` ([template strings](#template-strings))
* `()` ([calls](#calls)), `.` ([member accesses](#member-accesses)), `?.` ([safe member accesses](#working-with-null)), `[]` ([indexed accesses](#indexed-accesses))
* `-` (unary, [arithmetic negations](#operations-on-numbers))
* `?:` ([Elvis operators](#elvis-operator))
* `*`, `/` ([multiplicative operators](#operations-on-numberss))
* `+`, `-` (binary, [additive operators](#operations-on-numbers))
* `<`, `<=`, `>=`, `>` ([comparison operators](#operations-on-numbersa))
* `===`, `==`, `!==`, `!=` ([equality operators](#equality-checks))
* `not` ([logical negations](#logical-operations))
* `and` ([conjunctions](#logical-operations))
* `or` ([disjunctions](#logical-operations))
* `() -> 1` ([expression lambdas](#expression-lambdas)), `() {}` ([block lambdas](#block-lambdas))
* **LOWER PRECEDENCE**

If the default precedence of operators is not sufficient, parentheses can be used to force a part of an expression to be evaluated first.

[imports]: ../common/imports.md
[parameters]: ../common/parameters.md
[required-parameters]: ../common/parameters.md#required-parameters
[optional-parameters]: ../common/parameters.md#optional-parameters
[variadic-parameters]: ../common/parameters.md#variadic-parameters
[results]: ../common/results.md
[types]: ../common/types.md
[callable-types]: ../common/types.md#callable-type
[classes]: ../stub-language/classes.md
[enums]: ../stub-language/enumerations.md
[enum-variants]: ../stub-language/enumerations.md#enum-variants
[global-functions]: ../stub-language/global-functions.md
[methods]: ../stub-language/classes.md#defining-methods
[workflow-language]: ./README.md
[packages]: ./packages.md
[assignment-multiple-assignees]: ./statements.md#multiple-assignees
[placeholders]: ./statements.md#declaring-placeholders
[steps]: ./steps.md
