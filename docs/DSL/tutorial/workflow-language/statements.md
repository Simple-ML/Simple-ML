# Statements

Statements are used in the [workflow language][workflow-language] to run a specific action. Simple-ML supports only two type of statements, namely
* [expression statements](#expression-statements), which are used to evaluate an expression exactly once and discard any results, and
* [assignments](#assignments), which also evaluate an expression exactly once, but can then [assign selected results to placeholders](#declaring-placeholders) or [assign them to own results](#yielding-results).

Other types of statements such as
* if-statements to conditionally execute code or
* while-statements to repeatedly execute code

are not planned since we want to keep the language small and easy to learn. Moreover, we want to refrain from developing yet another general-purpose programming language. Instead, code that depends on such features can be implemented in Python, integrated into Simple-ML using the [stub language][stub-language], and called in a Simple-ML program using the provided statements.

## Expression Statements

Expression statements are used to evaluate an [expression][expressions] exactly once. The results of this expression are ignored. Therefore, expression statements are only useful, if the expression has side effects. The following snippet demonstrates this by [calling][calls] the `print` function that prints the given string to the console:

```
print("Hello, world!");
```

As we can see here, an expression statement has the following syntactic elements:
* The [expression][expressions] to evaluate.
* A semicolon at the end.

## Assignments

An assignment evaluates an [expression][expressions], its _right-hand side_, exactly once. This is identical to [expression statements](#expression-statements). However, the results of this expression can then either be [assigned to placeholders](#declaring-placeholders), [assigned to results](#yielding-results), or [ignored](#skipping-results).

### Declaring Placeholders

Placeholders are used to provide a name for a fixed value. This later allows us to use this value without recomputing it. In line with those semantics, placeholders must be given a value exactly once: They must be given a value directly when they are declared and that value cannot be changed afterwards (immutability).

The next snippet shows how the singular result of an expression (the integer `1`) can be assigned to a placeholder called `one`:

```
val one = 1;
```

This assignment to a placeholder has the following syntactic elements:

* The keyword `val`, which indicates that we want to declare a placeholder.
* The name of the placeholder, here `one`. This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of placeholders.
* An `=` sign.
* The expression to evaluate (right-hand side).
* A semicolon at the end.

If an expression produces more than one result, each one can be assigned to a placeholder individually. For example, the `split` method in the next example splits a large dataset into two datasets according to a given ratio, which can then be used for, say, training and testing:

```
val trainingDataset, val testDataset = fullDataset.split(0.8);
```

Here we see some additional syntax:

* The left-hand side of the assignment can be a comma-separated list of placeholder declarations. Trailing commas are permitted.

It is important to note, that **we may declare at most as many placeholders as the expression on the right-hand side has results**. For everything but calls this means only a single placeholder can be declared. For calls it depends on the number of declared [results][results] of the callee.

#### References to Placeholder

We can access the value of a placeholder in any statement that follows the assignment of that placeholder in the closest containing [workflow][workflows], [step][steps], or [block lambda][block-lambdas] using a [reference][references]. Here is a basic example where we print the value of the `one` placeholder (here `1`) to the console:

```
step loadMovieRatingsSample(nInstances: Int) {
    val one = 1;
    print(one);
}
```

More information about references can be found in the [linked document][references].

### Yielding Results

In addition to the [declaration of placeholders](#declaring-placeholders), assignments are used to assign a value to a [result of a step](#yielding-results-of-steps) or declare [results of a block lambda](#declare-results-of-block-lambdas).

#### Yielding Results of Steps

The following snippet shows how we can assign a value to a declared [result][results] of a [step][steps]:

```
step trulyRandomInt() -> result: Int {
    yield result = 1;
}
```

The assignment here has the following syntactic elements:
* The keyword `yield`, which indicates that we want to assign to a result.
* The name of the result, here `result`. This must be identical to one of the names of a declared result in the header of the step.
* An `=` sign.
* The expression to evaluate (right-hand side).
* A semicolon at the end.

#### Declare Results of Block Lambdas

Similar syntax is used to yield results of [block lambdas][block-lambdas]. The difference to steps is that block lambdas do not declare their results in their header. Instead the results are declared within the assignments, just like [placeholders](#declaring-placeholders). The block lambda in the following snippet has a single result called `greeting`, which gets the value `"Hello, world!"`:

```
() -> {
    yield greeting = "Hello, world!";
}
```

The assignment here has the following syntactic elements:
* The keyword `yield`, which indicates that we want to declare a result.
* The name of the result, here `result`. This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of results.
* An `=` sign.
* The expression to evaluate (right-hand side).
* A semicolon at the end.

### Skipping Results

In case we are only interested in some of the results of the expression on the right-hand side of the assignment, we can skip results by inserting an underscore (called _wildcard_) in the appropriate position. In the next snippet, we only assign the second dataset produced by the `split` functions and ignore the first one:

```
_, val testDataset = fullDataset.split(0.8);
```

### Everything Taken Together

When the right-hand side of the assignment produces more than one value, it is possible to freely decide whether a value should be [assigned to a placeholder](#declaring-placeholders), [yielded](#yielding-results) or [ignored](#skipping-results). In the upcoming snippet we use this to yield the second dataset produced by `split` right away and use the first dataset only internally for training a model, which is then yielded as well:

```
step createTestAndModel(fullDataset: Dataset) -> (testDataset: Dataset, trainedModel: Model) {
    val trainingDataset, yield testDataset = fullDataset.split(0.8);
    // ...
    yield trainedModel = model.fit(trainingDataset);
}
```

Let us sum up the syntax:
* A comma-separated list of assignees, possibly with a trailing comma (left-hand side). Each entry is one of
  * [Placeholder](#declaring-placeholders)
  * [Yield](#yielding-results)
  * [Wildcard](#skipping-results)
* An `=` sign.
* The expression to evaluate (right-hand side).
* A semicolon at the end.


**There must be at most as many assignees on the left-hand side as the right-hand side has results.** For everything but calls this means only a single assignee can be specified. For calls it depends on the number of declared [results][results] of the callee.

Assignment happens by index, so the first result is assigned to the first assignee, the second result is assigned to the second assignee, and so forth. If there are more results than assignee any trailing results are implicitly ignored.


[results]: ../common/results.md
[stub-language]: ../stub-language/README.md
[workflow-language]: ./README.md
[expressions]: ./expressions.md
[block-lambdas]: ./expressions.md#block-lambdas
[calls]: ./expressions.md#calls
[references]: ./expressions.md#references
[steps]: ./steps.md
[workflows]: ./workflows.md
