# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.dateToTimestamp("start_time")
    print(dataset.data)


if __name__ == "__main__":
    exampleWorkflow()
