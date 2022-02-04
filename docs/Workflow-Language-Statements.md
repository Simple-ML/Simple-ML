Statements are used in the [workflow language](./Workflow-Language.md) to run a specific action. Simple-ML supports only two type of statements, namely
* [expression statements](#expression-statements), which is used to evaluate an expression exactly once and discard any results, and
* [assignments](#assignments), which also evaluate an expression exactly once, but then assign selected results to _placeholders_ or _results_.

Other types of statements such as
 * if-statements to conditionally execute code or
 * while-statements to repeatedly execute code

are not planned since we want to keep the language small and easy to learn. Moreover, we want to refrain from developing yet another general-purpose programming language. Instead, code that depends on such features can be implemented in Python, integrated into Simple-ML using the [stub language](./Stub-Language.md), and called in a workflow using the provided statements.

## Expression Statements

Expression statements are used to evaluate an [expression](./Workflow-Language-Expressions.md) exactly once. The results of this expression are ignored. Therefore, expression statements are only useful, if the expression has side effects. The following snippet demonstrates this by [calling](./Workflow-Language-Expressions.md#calls) the `print` function that prints the given string to the console. Note that the statement ends with a semicolon.

    print("Hello, world!");

## Assignments

As assignment evaluates an [expression](./Workflow-Language-Expressions.md) exactly once, just like [expression statements](#expression-statements). However, the results of this expression can then either be assigned to placeholders, assigned to results, or ignored.

### Assigning Placeholders

The next snippet shows how the singular result of an expression (the number 1) can be assigned to a placeholder called "one". This placeholder stores the assigned value so it can be used later without having to recompute it:

    val one = 1;

If an expression produces more than one result, each one can be assigned to a placeholder individually. For example, the `split` method in this snippet splits a large dataset into two datasets according to a given ratio, which can then be used for, say, training and testing:

    val trainingDataset, val testDataset = fullDataset.split(0.8);

### Yielding Results

Assignments are also used to yield results of [steps](./Workflow-Language-Workflow-Steps.md) as shown in this snippet:

    step numbers() -> (one: Int, two: Int) {
        yield one = 1;
        yield two = 2;
    }

The identifier written after the yield keyword is used to reference a result, so the first yield assigns a value to the result called "one" and the second yield assigns a value to the result called "two". Note that yield does not end the execution of the function but just assigns a value to the referenced result.

Similar syntax is used to yield results of block lambdas (**TODO: insert link**):

    () -> {
        yield one = 1;
        yield two = 2;
    }

This creates a block lambda that has two results, called "one" and "two". The difference to a step is that yielding a result in a block lambda declares a new result, while yielding a result in a step refers to an already declared result in the result list of the step `-> (one: Int, two: Int)`.

### Skipping Results

In case we are only interested in some of the results, we can skip results by inserting an underscore in the appropriate location. In the next snippet, we only assign the dataset for testing to a placeholder and ignore the training dataset:

    _, val testDataset = fullDataset.split(0.8);

### Everything Taken Together

When the right-hand side of the assignment produces more than one value, it is possible to freely decide whether a value should be assigned to a placeholder, yielded or ignored. In the upcoming snippet we use this to yield the test dataset right away and use the training dataset only internally for training a model, which is then yielded as well:

    step createTestAndModel(fullDataset: Dataset) -> (testDataset: Dataset, trainedModel: Model) {
        val trainingDataset, yield testDataset = fullDataset.split(0.8);
        // ...
        yield trainedModel = model.fit(trainingDataset);
    }
