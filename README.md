# Installation for Developers

1. Install [miniconda](https://docs.conda.io/en/latest/miniconda.html) with Python 3.9. Ensure that your PATH is configured properly, so the `conda` command is available in your shell.
2. If you want to use miniconda with Powershell on Windows, run the following command:
   ```shell
   conda init powershell
   ```
3. Create a new conda environment with the provided recipe:
    ```shell
    conda env create -f environment.yml
    ```
4. Activate this environment (**you need to do this whenever you open a new shell**):
    ```shell
    conda activate runtime
    ```
5. Add the stdlib files as a local library:
   ```shell
   conda develop stdlib/python
   ```
6. Start the server:
   ```shell
   python __main__.py
   ```
   
# Adding new Dependencies

1. Add new dependencies to the [environment.yml](./environment.yml) file. Ensure the environment also contains everything the stdlib needs (refer to the [environment.yml from the stdlib](./stdlib/python/environment.yml).
2. Update your environment:
   ```shell
   conda env update -n runtime --file environment.yml --prune
   ```
