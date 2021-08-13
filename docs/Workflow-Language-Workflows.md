Workflows are machine learning programs designed to solve a specific task. They act as the entry point to start the evaluation. Workflows are not meant to be reusable, reusable code should instead be extracted into [workflow steps](./Workflow-Language-Workflow-Steps.md).

The definition of a workflow has the following syntactic elements:
* The keyword `workflow`.
* The name of the workflow ("predictSpeed" in the following example).
* The body of the workflow, which is the list of [statements](./Workflow-Language-Statements.md) that should be executed if the workflow is executed. This list is enclosed by curly braces and each statement ends with a semicolon.

```
workflow predictSpeed {
	val adac = loadDataset("ADAC");
	val adacSample = adac.sample(1000);

	// …
}
```
