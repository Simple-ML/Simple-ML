[![Master](https://github.com/Simple-ML/DSL/actions/workflows/master.yml/badge.svg)](https://github.com/Simple-ML/DSL/actions/workflows/master.yml)

## Installation for Developers

### Option 1: VS Code Devcontainer

1. Install [Docker](https://docs.docker.com/get-docker/).
1. Install [VS Code](https://code.visualstudio.com/).
1. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VS Code Extension.
1. Clone this repository.
1. Open the repository in VS Code.
1. Press F1 and type "Remote-Containers: Reopen in Container".

### Option 1a: VS Code Devcontainer (advanced)

Note: This solution requires that Git itself can clone this private repository from GitHub. **If you only used GUIs for Git before it's likely this solution will not work right away.** You need to either [configure SSH](https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh), use a credentials manager like the [Git Credentials Manager](https://github.com/microsoft/Git-Credential-Manager-Core) or revert to _Option 1_.

1. Install [Docker](https://docs.docker.com/get-docker/).
1. Install [VS Code](https://code.visualstudio.com/).
1. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VS Code Extension.
1. Open VS Code.
1. Press F1 and type "Remote-Containers: Remote-Containers: Clone Repository in Container Volume...".
1. Select "GitHub".
1. Type "Simple-ML/DSL".

### Option 2: Custom Installation

1. Install OpenJDK 11 from https://adoptopenjdk.net/.
1. Install Node.js from https://nodejs.org/en/.
1. Install VS Code from https://code.visualstudio.com/.
1. Clone the repository from https://github.com/Simple-ML/LSP.
1. Open the repository in VS Code.
1. Build the server by running the following commands in the terminal within VS Code:
    ```shell
    cd server/de.unibonn.simpleml.parent
    ./gradlew clean generateXtextLanguage build
    ```
1. Build the client by running the following commands in the terminal within VS Code:
    ```shell
    cd ../../client
    npm i
    npm run compile
    ```

## Execution

### Running the VS Code Extension

1. Open `client/dist/extension.js` in VS Code.
1. Press F5 and select "VS Code Extension Development".

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
