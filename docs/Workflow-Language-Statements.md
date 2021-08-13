Statements are used in the [workflow language](./Workflow-Language.md) to do a specific action. Simple-ML supports only a single type of statements, the [do-statement](#do-statements), which is used to evaluate an expression exactly once. Other types of statements such as
 * if-statements to conditionally execute code or
 * while-statements to repeatedly execute code

are not planned since we want to keep the language small and easy to learn. Moreover, we want to refrain from developing yet another general-purpose programming language. Instead, code that depends on such features can be implemented in Python, integrated into Simple-ML using the [stub language](./Stub-Language.md), and called in a workflow using the provided [do-statement](#do-statements).

## Do-Statements

Do-statements are used to evaluate an [expression](./Workflow-Language-Expressions.md) exactly once. The results of this expression can either be ignored or assigned to a placeholder.

The most basic form of a do-statement ignores all results of the expression, which is only useful if the expression has side-effects. The following snippet demonstrates this by [calling](./Workflow-Language-Expressions.md#calls) the `print` function that prints the given string to the console. Note that the statement ends with a semicolon.

    print("Hello, world!");

### Assigning Placeholders

The next snippet shows how the singular result of an expression (the number 1) can be assigned to a placeholder called "one". This placeholder stores the assigned value so it can be used later without having to recompute it:

    val one = 1;

If an expression produces more than one result, each one can be assigned to a placeholder individually. However, the list of placeholders must be enclosed in parentheses, whereas the parentheses can be omitted if there is only one result. For example, the `split` method in this snippet splits a large dataset into two datasets according to a given ratio, which can then be used for, say, training and testing:

    (val trainingDataset, val testDataset) = fullDataset.split(0.8);

In case we are only interested in some of the results, we can skip results by inserting an underscore in the appropriate location. In the next snippet, we only assign the dataset for testing to a placeholder and ignore the training dataset:

    (_, val testDataset) = fullDataset.split(0.8);

### Yielding Results

Finally, do-statements are used to yield results of [workflow steps](./Workflow-Language-Workflow-Steps.md) as shown in this snippet:

    step numbers() -> (one: Int, two: Int) {
        yield one = 1;
        yield two = 2;
    }

The identifier written after the yield keyword is used to reference a result, so the first yield assigns a value to the "one" result and the second yield assigns a value to the "two" result. Note that yield does not end the execution of the function but just assigns a value to the referenced result.

When the expression executed by the do-statement produces more than one value, it is possible to freely decide whether a value should be yielded, assigned to a placeholder or ignored. In the upcoming snippet we use this to yield the test dataset right away and use the training dataset only internally for training a model, which is then yielded as well. Note again that parentheses must be used around the left-hand side of a do-statement when there are multiple assignees:

    step createTestAndModel(fullDataset: Dataset) -> (testDataset: Dataset, trainedModel: Model) {
        (val trainingDataset, yield testDataset) = fullDataset.split(0.8);
        // ...
        yield trainedModel = model.fit(trainingDataset);
    }
