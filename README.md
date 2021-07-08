# Installation for Developers

## Option 1: VS Code Devcontainer

1. Install [Docker](https://docs.docker.com/get-docker/).
2. Install [VS Code](https://code.visualstudio.com/).
3. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VS Code Extension.
4. Open the repository in VS Code.
5. Press F1 and type "Remote-Containers: Reopen in Container"

## Option 2: Custom Installation

1. Install [miniconda](https://docs.conda.io/en/latest/miniconda.html) with Python 3.9. Ensure that your PATH is configured properly, so the `conda` command is available in your shell.
2. If you want to use miniconda with Powershell on Windows, run the following command:
   ```shell
   conda init powershell
   ```
3. Go into the python folder (starting from the root of the repository):
    ```shell
    cd python
    ```
4. Create a new conda environment with the provided recipe:
    ```shell
    conda env create -f environment.yml
    ```
5. Activate this environment (**you need to do this whenever you open a new shell**):
    ```shell
    conda activate stdlib
    ```
6. Add the stdlib files as a local library:
   ```shell
   conda develop .
   ```
   
# Adding new Dependencies

1. Add new dependencies to the [environment.yml](./python/environment.yml) file.
2. Update your environment (starting from the root of the repository):
   ```shell
   cd python
   conda env update -n stdlib --file environment.yml --prune
   ```
