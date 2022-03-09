# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.transformCategoryToVector("season")
    dataset = dataset.flattenData()

    print(dataset.data.columns.values.tolist())
    print(dataset.data)


if __name__ == "__main__":
    exampleWorkflow()
