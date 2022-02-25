# Types

Types describe the values that a declaration can accept. Simple-ML has various categories of types, which are explained in this document.

## Categories of Types
### Named Types

**TODO**

#### Nullable Named Types

**TODO**

#### Type Arguments

**TODO**

##### Star Projection

**TODO**

##### Use-Site Variance

**TODO**

### Member Types

**TODO**

### Union Types

If a declaration can have one of multiple types you can denote that with a _union type_:

```
union<String, Int>
```

Here is a breakdown of the syntax:
* The keyword `union`.
* An opening angle bracket.
* A list of types, which are separated by commas. A trailing comma is allowed.
* A closing angle bracket

Note that it is preferable to write the common superclass if this is equivalent to the union type. For example, `Number` has the two subclasses `Int` and `Float`. Therefore, it is usually better to write `Number` as the type rather than `union<Int, Float>`. Use the union type only when you are not able to handle the later addition of further subclasses of `Number` other than `Int` or `Float`.

### Callable Types

**TODO**

### Parenthesized Types

To improve clarity, parts of a type or the entire type can be enclosed in parentheses. The parentheses have no special meaning and are just meant as a visual guide. Here is an example:

```
(Int)
```

## Corresponding Python Code

Optionally, [type hints][type-hints] can be used in Python to denote the type of a declaration. This is generally advisable, since IDEs can use this information to offer additional feature, like improved refactorings. Moreover, static type checker like [mypy][mypy] can detect misuse of an API without running the code. We will now briefly describe how to best use Python's [type hints][type-hints] and explain how they relate to Simple-ML types.

First, to get [type hints][type-hints] in Python closer to the expected behavior, add the following import to your Python file:

```py
from __future__ import annotations
```

Also add the following import, which brings the declarations that are used by the [type hints][type-hints] into scope. You can remove any declaration you do not need:

```py
from typing import Callable, Optional, Tuple, TypeVar, Union
```

The following table shows how Simple-ML types can be written as Python [type hints][type-hints]:

|Simple-ML Type|Python Type Hint|
|-|-|
|`Boolean`|`bool`|
|`Float`|`float`|
|`Int`|`int`|
|`String`|`str`|
|`SomeClass`|`SomeClass`|
|`SomeEnum`|`SomeEnum`|
|`SomeClass?`|`Optional[SomeClass]`|
|`SomeEnum?`|`Optional[SomeEnum]`|
|`SomeSpecialList<Int>`|`SomeSpecialList[int]`|
|`SomeOuterClass.SomeInnerClass`|`SomeOuterClass.SomeInnerClass`|
|`union<String, Int>`|`Union[str, int]`|
|`(a: Int, b: Int) -> r: Int`|`Callable[[int, int], int]`|
|`(a: Int, b: Int) -> (r: Int, s: Int)`|`Callable[[int, int], Tuple[int, int]]`|
|`(SomeClass)`|No exact equivalent. Convert the type without parentheses instead.|

Most of these are rather self-explanatory. We will, however, cover the translation of [callable types](#callable-types) in a little more detail: In Python, the type hint for a callable type has the following general syntax: 

```
Callable[<list of parameter types>, <result type>]
```

To get the `<list of parameter types>`, simply 
1. convert the types of the parameters to their Python syntax,
2. separate them all by commas,
3. surround them by square brackets.

Getting the `<result type`> depends on the number of results. If there is only a single result, simply write down its type. If there are multiple types, do this instead:
1. convert the types of the results to their Python syntax,
2. separate them all by commas,
3. add the prefix `Tuple[`,
4. add the suffix `]`.

[mypy]: http://mypy-lang.org/
[type-hints]: https://docs.python.org/3/library/typing.html
