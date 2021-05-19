## Setting up your development environment

1. Install OpenJDK 11 from https://adoptopenjdk.net/.
1. Install Node.js from https://nodejs.org/en/.
1. Install VS Code from https://code.visualstudio.com/.
1. Clone the repository from https://github.com/Simple-ML/LSP.
1. Open the repository in VS Code.
1. Build the server by running the following commands in the terminal within VS Code:
    ```
    cd server/de.unibonn.simpleml.parent
    ./gradlew clean generateXtextLanguage build
    ```
1. Build the client by running the following commands in the terminal within VS Code:
    ```
    cd ../../client
    npm i
    npm run compile
    ```
1. Open `client/out/extension.js` in VS Code.
1. Press F5 and select "VS Code Extension Development".
