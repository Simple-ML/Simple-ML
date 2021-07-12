# Installation for Developers

## Option 1: VS Code Devcontainer

1. Install [Docker](https://docs.docker.com/get-docker/).
1. Install [VS Code](https://code.visualstudio.com/).
1. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VS Code Extension.
1. Clone this repository.
1. Open the repository in VS Code.
1. Press F1 and type "Remote-Containers: Reopen in Container".

## Option 1a: VS Code Devcontainer (advanced)

Note: This solution requires that Git itself can clone this private repository from GitHub. **If you only used GUIs for Git before it's likely this solution will not work right away.** You need to either [configure SSH](https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh), use a credentials manager like the [Git Credentials Manager](https://github.com/microsoft/Git-Credential-Manager-Core) or revert to _Option 1_.

1. Install [Docker](https://docs.docker.com/get-docker/).
1. Install [VS Code](https://code.visualstudio.com/).
1. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) VS Code Extension.
1. Open VS Code.
1. Press F1 and type "Remote-Containers: Remote-Containers: Clone Repository in Container Volume...".
1. Select "GitHub".
1. Type "Simple-ML/Runtime".

## Option 2: Custom Installation

1. Install [miniconda](https://docs.conda.io/en/latest/miniconda.html) with Python 3.9. Ensure that your PATH is configured properly, so the `conda` command is available in your shell.
1. If you want to use miniconda with Powershell on Windows, run the following command:
    ```shell
    conda init powershell
    ```
1. Create a new conda environment with the provided recipe:
    ```shell
    conda env create -f environment.yml
    ```
1. Activate this environment (**you need to do this whenever you open a new shell**):
    ```shell
    conda activate runtime
    ```
1. Add the stdlib files as a local library:
    ```shell
    conda develop stdlib/python
    ```

# Adding new Dependencies

1. Add new dependencies to the [environment.yml](./environment.yml) **and** the [requirements.txt](./requirements.txt) file. Make sure the version numbers are identical in both files and that [requirements.txt](./requirements.txt) **does not** contain `python` and `conda-build` as dependencies.
1. Update your environment:
    - If you used _Option 1 or 1a_ for installation press F1 and type "Remote-Containers: Rebuild Container".
    - If you used _Option 2_ for installation (starting from the root of the repository):
        ```shell
        cd python
        conda env update -n runtime --file environment.yml --prune
        ```

# Execution

## Locally

```shell
python -m runtime
```

## With Docker

1. Build the Docker image:
    ```shell
    docker build -t simpleml-runtime .
    ```
2. Launch a Docker container from this image:
    ```shell
    docker run -d -p 6789:6789 simpleml-runtime
    ```
