# Runtime
## Installation for Developers

### Custom Installation

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
1. Add the runtime as a local library:
    ```shell
    conda develop runtime
    ```
1. Add the stdlib files as a local library:
    ```shell
    conda develop stdlib/python
    ```

## Adding new Dependencies

1. Add new dependencies to the [environment.yml](./environment.yml) file.
2. Update your environment:
    ```shell
    conda env update -n runtime --file environment.yml --prune
    ```

## Execution

### Locally

```shell
python -m runtime
```

### With Docker

1. Build the Docker image:
    ```shell
    docker build -t simpleml-runtime .
    ```
2. Launch a Docker container from this image:
    ```shell
    docker run --name simpelml-runtime -d --rm -p 6789:6789 simpleml-runtime
    ```
