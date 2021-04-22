## Setting up your development environment

1. Install OpenJDK 11 from https://adoptopenjdk.net/.
1. Install Node.js from https://nodejs.org/en/.
1. Install VS Code from https://code.visualstudio.com/.
1. Clone the repository from https://github.com/lars-reimann/simple-ml-lsp.
1. Open the repository in VS Code.
1. Build the server:
    ```
    cd server/de.unibonn.simpleml.parent
    ./gradlew clean generateXtext build
    ```
1. Build the client:
    ```
    cd ../../client
    npm i
    npm run compile
    ```
1. Open `client/out/extension.js` in VS Code.
1. Press F5 and select "VS Code Extension Development".