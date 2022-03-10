# Idea and Concepts

The vision of Simple-ML is to facilitate the use of Machine Learning (ML), and to make the results of ML procedures available to a wider range of applications. Simple-ML is intended support users with different backgrounds (e.g., ML experts, data scientists, business managers, etc.).

Simple-ML needs to ensure two aspects:
1. The syntactic correctness and robustness of the ML workflows created by the users.
2. An intuitive access to ML workflows for non-expert users.

To this end, the following three components build the foundation of Simple-ML:
1. The domain-specific language (DSL) for defining ML workflows.
2. An interface including a workflow graph for intuitive creation of an ML workflow.
3. A data catalog and a set of data processing functions to provide an easy starting point and simplifying the data preprocessing task.

These three components are now introduced in detail.

## Domain-specific Language (DSL)

The domain-specific language (DSL) provides an integrated description of the ML workflows and their components, and can be specified by textual and graphical editors.

For example, consider the following DSL code snippet that loads a dataset:

<p align="center">
<kbd>
<img src="https://github.com/Simple-ML/Simple-ML/raw/main/docs/img/textual_example.PNG" width="600"/>
</kbd>
</p>

Through the definition of classes, functions and other elements, an ML workflow can be created that is both robust, syntactically clear and focusses on the actual data processing and ML tasks. The user does not need to care about the underlying implementation.

## Workflow Graph

To simplify the creation of an ML workflow, users are not required to learn the whole syntax of the DSL. Instead, one can create a visual workflow. For example, the previous DSL code is visualised as follows:

<p align="center">
<kbd>
<img src="https://github.com/Simple-ML/Simple-ML/raw/main/docs/img/visual_example.PNG" width="200"/>
</kbd>
</p>

The Simple-ML interface supports the user in the creation and extension of the workflow graph. Changes on the textual workflow are adopted in the visual workflow and vice versa.

## Data Catalog ##

To provide a simple starting point towards the creation of ML workflows, Simple-ML provides a data catalog of pre-defined datasets. Users can easily load these datasets and base their ML experiments on them.
