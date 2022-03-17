# ![Simple-ML](https://simple-ml.de/wp-content/uploads/2019/05/Simple-ML-Logo-03-e1557838304632.png)

[![Main](https://github.com/Simple-ML/Simple-ML/actions/workflows/main.yml/badge.svg)](https://github.com/Simple-ML/Simple-ML/actions/workflows/main.yml)

**Simple-ML is a project funded by the German Federal Ministry of Education and Research (BMBF). Our goal is to increase the usability of creating machine learning workflows.**

* [Website](https://simple-ml.de/)
* [Twitter](https://twitter.com/MlSimple)

The efficient application of current Machine Learning (**ML**) procedures requires a very high level of expert knowledge, which stands in the way of a widespread use of ML approaches, especially by small and medium-sized enterprises. The goal of the Simple-ML project is to significantly improve the usability of ML processes in order to make them more accessible to a broad user group.

The definition of a domain-specific language (**DSL**) is a central contribution of the project: The DSL provides an integrated description of the ML workflows and their components, and can be specified by textual and graphical editors. Furthermore, the project contributes towards the robustness of created ML workflows, explainability and transparency of the learned models, efficiency and scalability of the created applications, as well as towards the reusability of the created solutions. This is done by applying semantic technologies, extending symbolic ML procedures and by setting up scalable ML frameworks.

## Installation for Users

1. Install [Docker](https://www.docker.com/).
1. **On Linux:** Install [Docker Compose v2](https://docs.docker.com/compose/cli-command/#install-on-linux).
1. Clone this repository.
1. Install and start Simple-ML by running the following command in the root directory of the cloned repository. This will take a few minutes **the first time** you do it:
    ```shell
    docker compose up
    ```
1. Open [localhost:4200](http://localhost:4200) in a Chromium-based browser like Chrome or the new Edge. Other browser like Firefox or Internet Explorer are not yet supported.
1. **Later:** After pulling again from the repository you need to update the Docker images:
    ```shell
    docker compose build
    ```

## Tutorial

The following page provides a tutorial for users of Simple-ML:

* [Tutorial][tutorial]

## Additional Documentation for Developers

Developers get more information about the implementation of the specific components here:

* Components
   * [Frontend][frontend]
   * [Runtime][runtime]
   * [Data API][data_api]
   * [DSL][adding-new-language-feature]

[tutorial]: ./docs/Tutorial.md
[data_api]: ./docs/Data-API.md
[data_set_api]: ./docs/Data-Set-API.md
[data_catalog_api]: ./docs/Data-Catalog-API.md
[frontend]: ./docs/Front-End.md
[ml_catalog_api]: ./docs/Machine-Learning-Catalog-API.md
[runtime]: ./docs/Runtime-Server.md
[adding-new-language-feature]: ./docs/DSL/development/how-to-add-a-new-language-concept.md
