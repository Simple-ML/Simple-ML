# DSL

## Installation for Developers

1. Install [VS Code](https://code.visualstudio.com/).
2. Clone this repository.
3. Build everything:
    ```shell
    ./gradlew build
    ```

## Execution

### Running the VS Code Extension

1. Ensure VS Code is closed.
2. Install the extension and launch VS Code:
    ```shell
    ./gradlew launchVSCode
    ```

### Running the Xtext Web Backend

```shell
./gradlew jettyRun
```

### Running the Xtext Web Backend with Docker

1. Build the image:
    ```shell
    docker build -t simpleml-dsl .
    ```
1. Run a container:
    ```shell
    docker run --name simpleml-dsl -d --rm -p 8080:8080 simpleml-dsl
    ```
1. Visit [localhost:8080/simpleml](http://localhost:8080/simpleml) in your Browser to check that everything worked.

### Generating the Stdlib documentation

```shell
./gradlew generateStdlibDocumentation
```
