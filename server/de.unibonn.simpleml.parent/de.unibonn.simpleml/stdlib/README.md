# Local Installation

Run the following command in the root directory of the project where the setup.py script is located to install this adapter package: 

    `pip install -e .`

# Docker Installation

1. Build the Docker image:

    `docker build -t simple-ml-adapters .`

2. Launch a Python script in the current working directory (replace the file name speedPrediction.py):

    `docker run -it --rm -v ${PWD}:/usr/src/workflows -w /usr/src/workflows simple-ml-adapters python speedPrediction.py`