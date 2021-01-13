Workflow steps can be used to extract a sequence of [statements](./Workflow-Language-Statements) from a workflow to give the sequence a name and make it reusable.

## Defining a Workflow Step

The definition of a workflow step has the following syntactic elements:
* The keyword `step`.
* The name of the step ("loadMovieRatingsSample" in the following snippet).
* The list of _parameters_ (inputs) enclosed in parentheses and separated by commas (`(n_instances: Int)` in the following snippet). For each parameter we list the name of the parameter followed by a colon and its type.
* Optionally we can list the _results_ (outputs) after the symbol `->`. If this section is missing it means the workflow step does not produce results. The list of results is again enclosed in parentheses and we use commas to separate the entries (`-> (features: Table, target: Table)` in the following snippet). If there is exactly one result we can omit the parentheses. For each result we specify its name followed by a colon and its type.
* Finally we have the _body_ of the workflow step, which is the list of [statements](./Workflow-Language-Statements) that should be executed if the workflow step is [called](#calling-a-workflow-step). This list is enclosed by curly braces and each statement ends with a semicolon.
 
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

## Calling a Workflow Step

Inside a workflow or another workflow step we can then call a workflow step just like we can [call global functions or class constructors](./Workflow-Language-Expressions#calls): We write the name of the workflow step we want to call, followed by the arguments we want to pass enclosed in parentheses and separated by commas. We can use either named or positional arguments. The result of a workflow step can then be [assigned to placeholders](./Workflow-Language-Statements#assigning-placeholders) as needed. Here is how to call the workflow step defined above:

    (val features, val target) = loadMovieRatingsSample(n_instances = 1000);