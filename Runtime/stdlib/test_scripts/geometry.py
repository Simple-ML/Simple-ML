# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset


# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.addGeometryEmbeddings('geometry')
    print(dataset.data)


if __name__ == '__main__':
    exampleWorkflow()
