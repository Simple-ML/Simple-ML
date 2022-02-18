# Steps

Steps can be used to extract a sequence of [statements][statements] from a Machine Learning program to give the sequence a name and make it reusable. In the following discussion we explain how to [declare a step](#declaring-a-step) and how to use it by [calling it](#calling-a-step).

## Declaring a Step

### Minimal Example

Let's look at a minimal example of a step:

```
step loadMovieRatingsSample() {}
```

This declaration of a step has the following syntactic elements:
* The keyword `step`.
* The name of the step, here `loadMovieRatingsSample`. This can be any combination of upper- and lowercase letters, underscores, and numbers, as long as it does not start with a number. However, we suggest to use `lowerCamelCase` for the names of steps.
* The list of parameters (i.e. inputs) of the step. This is delimited by parentheses. In the example above, the step has no parameters.
* The body of the step, which contains the [statements][statements] that should be run when the step is executed. The body is delimited by curly braces. In this example, the body is empty, so running this step does nothing.

### Parameters

To make a step configurable, you can add [parameters][parameters].



### Results

[Results][results] are used to return values that are produced inside the step back to the caller.

### Statements

In order to describe what should be done when the step is executed, we need to add [statements][statements] to its body, as shown in this example:

```
step loadMovieRatingsSample(n_instances: Int) -> (features: Table, target: Table) {
    val movieRatingsSample = loadDataset("movieRatings").sample(n_instances = 1000);
    
    // ...
}
```

More information about statements can be found in the [linked document][statements]. Note, however, that all statements must end with a semicolon.

* The list of _parameters_ (inputs) enclosed in parentheses and separated by commas (`(n_instances: Int)` in the following snippet). For each parameter we list the name of the parameter followed by a colon and its type.
* Optionally we can list the _results_ (outputs) after the symbol `->`. If this section is missing it means the workflow step does not produce results. The list of results is again enclosed in parentheses and we use commas to separate the entries (`-> (features: Table, target: Table)` in the following snippet). If there is exactly one result we can omit the parentheses. For each result we specify its name followed by a colon and its type.
* Finally we have the _body_ of the workflow step, which is the list of [statements](./Workflow-Language-Statements.md) that should be executed if the workflow step is [called](#calling-a-workflow-step). This list is enclosed by curly braces and each statement ends with a semicolon.
 
```
step loadMovieRatingsSample(n_instances: Int) -> (features: Table, target: Table) {
    val movieRatingsSample = loadDataset("movieRatings").sample(n_instances = 1000);
    yield features = movieRatingsSample.keepAttributes(
        "leadingActor",
        "genre",
        "length"
    );
    yield target = movieRatingsSample.keepAttributes(
        "rating"
    );
}
```

## Calling a Step

Inside a workflow or another workflow step we can then call a workflow step just like we can [call global functions or class constructors](./Workflow-Language-Expressions.md#calls): We write the name of the workflow step we want to call, followed by the arguments we want to pass enclosed in parentheses and separated by commas. We can use either named or positional arguments. The result of a workflow step can then be [assigned to placeholders](./Workflow-Language-Statements.md#assigning-placeholders) as needed. Here is how to call the workflow step defined above:

    val features, val target = loadMovieRatingsSample(n_instances = 1000);

[parameters]: ../common/parameters.md
[results]: ../common/results.md
[statements]: ./statements.md
