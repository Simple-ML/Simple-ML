FROM mcr.microsoft.com/vscode/devcontainers/miniconda:0-3

COPY python/environment.yml /tmp/conda-tmp/
RUN conda env update -n base -f /tmp/conda-tmp/environment.yml \
    && rm -rf /tmp/conda-tmp
