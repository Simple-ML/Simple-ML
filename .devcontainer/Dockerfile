FROM mcr.microsoft.com/vscode/devcontainers/python:3.9

COPY python/requirements.txt /tmp/pip-tmp/
RUN pip3 --disable-pip-version-check --no-cache-dir install -r /tmp/pip-tmp/requirements.txt \
    && rm -rf /tmp/pip-tmp

# Remove once the extension is on the marketplace
COPY simple-ml-0.0.7.vsix /tmp/
