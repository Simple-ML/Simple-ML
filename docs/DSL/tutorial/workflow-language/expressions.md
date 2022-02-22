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

## Working With Numbers

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

## Working With Booleans

To work with logic, Simple-ML has the two boolean literals `false` and `true` as well as operations to work with them:
* (Logical) negation (example `not a`): Output is `true` if and only if the operand is false:

`not a` | false | true
--------|-------|------
&nbsp;  | true  | false

* Conjunction (example `a and b`): Output is `true` if and only if both operands are `true`. Note that the second operand is always evaluated, even if the first operand is `false` and, thus, already determines the result of the expression. The operator is not _short-circuited:

`a and b` | false | true
----------|-------|------
**false** | false | false
**true**  | false | true

* Disjunction (example `a or b`): Output is `true` if and only if at least one operand is `true`. Note that the second operand is always evaluated, even if the first operand is `true` and, thus, already determines the result of the expression. The operator is not _short-circuited:

`a or b`  | false | true
----------|-------|-----
**false** | false | true
**true**  | true  | true

## References

References are used to refer to a declaration, such as a [class][classes] or a [placeholder][placeholders]. The syntax is to simply write the name of the declaration, as shown in the next snippet where we first declare a placeholder called "one" and then refer to it when computing the value for the placeholder called "two":

    val one = 1;
    val two = one + one;

## Calls

Calls are used to trigger the execution of a specific action, which can be the creation of an instance of a [class][classes] or executing the code in a (global) [function][global-functions] or [workflow step][steps]. In any case a call consists of a _receiver_, which is a [reference](#references) to the declaration to call, and a list of _arguments_ (inputs) enclosed by parentheses and separated by commas.

Arguments can either be named or positional. For named arguments the name of the parameter the argument assigned to is given explicitly. Textually we write the name of the parameter, an assignment operator and the value.

Positional arguments are implicitly assigned to the parameter with the same index. So the first argument is assigned to the first parameter etc. Note that to the right of a named argument all arguments must be named. Syntactically we only need to write the value.

The following example first shows the declaration of a [class][classes] with the name "DecisionTree" that has a single parameter called "maxDepth" and then two calls that create an instance of this class.

**Definition of the example class:**

    class DecisionTree(maxDepth: Int) {}

**Positional argument:**

    DecisionTree(10)

**Named argument:**

    DecisionTree(maxDepth = 10)

## Member Accesses

A member access is used to refer to members of a [class][classes] or class instance, i. e. attributes and methods, and instances of an [enum][enums].

Syntactically a member access starts with the _receiver_, which is a [reference](#references) to the program element containing the member followed by a dot and a reference to the member itself. Note that static class member are only accessible from the class itself while non-static class member are only accessible from instance of the class, as the following snippet shows:

**Definition of the example class and enum:**

    class DecisionTree() {
        static val verboseTraining: Boolean
        val score: Float
    }

    enum SvmKernel {
        Linear,
        RBF
    }

**Accessing static class member:**

    DecisionTree.verboseTraining

**Accessing non-static class member (note that we create an instance of the "DecisionTree" class by [calling](#calls) it):**

    DecisionTree().score

**Accessing enum instances:**

    SvmKernel.Linear

## Working With _null_

In order to represent missing or unknown values Simple-ML has the literal `null` as well as special operators to deal with it.

### Safe member access

If an expression could be null it cannot be used as the receiver of a regular [member access](#member-access), since null does not have members. Instead a safe member access must be used. The syntax is identical to a normal member access except that we replace the dot with the operator `?.`. A safe member access evaluates to null if the receiver is null. Otherwise it evaluates to the accessed member, just like a normal member access. Here is an example:

    nullableExpression?.member1?.member2

### Elvis operator

The elvis operator `?:` (given its name because it resembles Elvis's haircut) is used to specify a default value that should be used instead in case an expression is null. In the following example the whole expression evaluated to `nullableExpression` if this value is not null and to `42` if it is:

    nullableExpression ?: 42

## Indexed Access

**TODO**

## Checking for Equality

There are two different types of equality in Simple-ML, _identity_ and _structural equality_. Identity checks if two objects are one and the same, whereas structural equality checks if two objects have the same structure and content. Using a real world example, two phones of the same type would be structurally equal but not identical. Both types of equality checks return a boolean literal `true` if the check was positive and `false` if the check was negative. The syntax for these operations is as follows:

* Identity: `1 === 2`
* Structural equality: `1 == 2`

Simple-ML also has shorthand versions for negated equality checks which should be used instead of an explicit logical negation with the `not` operator:

* Negated identity: `1 !== 2`
* Negated structural equality: `1 != 2`
## Template Strings

**TODO**

## Lambdas

**TODO**
### Block Lambdas

**TODO**
### Expression Lambdas

**TODO**

## Operator precedence

We all know that `2 + 3 * 7` is 23 and not 35. The reason is that the `*` operator has a higher precedence than the `+` operator and is therefore evaluated first. These precedence rules are necessary for all types of expressions listed above and shown in the following list. The higher up an expression is in the list, the higher its precedence. Expressions listed beside each other have the same precedence and are evaluated from left to right:

* **HIGHER PRECEDENCE**
* `()` (parentheses around an expression)
* `1` ([integer literals](#working-with-numbers)), `1.0` ([float literals](#working-with-numbers)), `"a"` ([string literals](#working-with-text)), `true`/`false` ([boolean literals](#working-with-logic)), `someName` ([references](#references)), `null` ([null literal](#working-with-null)), `"age: {{ age }}"` (template strings)
* `()` ([calls](#calls)), `.` ([member accesses](#member-accesses)), `?.` ([safe member accesses](#working-with-null)), `[]` (indexed access)
* `-` (unary, [arithmetic negations](#working-with-numbers))
* `?:` ([Elvis operators](#working-with-null))
* `*`, `/` ([multiplicative operators](#working-with-numbers))
* `+`, `-` (binary, [additive operators](#working-with-numbers))
* `<`, `<=`, `>=`, `>` ([ordering operators](#checking-for-order))
* `===`, `==`, `!==`, `!=` ([equality operators](#checking-for-equality))
* `not` ([logical negations](#working-with-logic))
* `and` ([conjunctions](#working-with-logic))
* `or` ([disjunctions](#working-with-logic))
* `() -> 1` (expression lambda), `() {}` (block lambda)
* **LOWER PRECEDENCE**

If the default precedence of operators is not sufficient, parentheses can be used to force a part of an expression to be evaluated first.

[classes]: ../stub-language/classes.md
[enums]: ../stub-language/enumerations.md
[global-functions]: ../stub-language/global-functions.md
[workflow-language]: ./README.md
[placeholders]: ./statements.md#declaring-placeholders
[steps]: ./steps.md
