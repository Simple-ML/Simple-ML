# Simple-ML

[![Main](https://github.com/Simple-ML/Simple-ML/actions/workflows/main.yml/badge.svg)](https://github.com/Simple-ML/Simple-ML/actions/workflows/main.yml)
## Installation for users

1. Install [Docker](https://www.docker.com/).
1. **On Linux:** Install [Docker Compose v2](https://docs.docker.com/compose/cli-command/#install-on-linux).
1. Clone this repository.
1. Install and start Simple-ML by running the following command in the root directory of the cloned repository. This will take a few minutes **the first time** you do it:
    ```shell
    docker compose up
    ```
1. Open [localhost:4200](http://localhost:4200) in a Chromium-based browser like Chrome or the new Edge. Other browser like Firefox or Internet Explorer are not yet supported.
1. **Later:** After pulling again from the repository you need to update the Docker images:
    ```shell
    docker compose build
    ```
