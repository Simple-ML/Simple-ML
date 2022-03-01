# Imports ----------------------------------------------------------------------
from dataset._scale import StandardScaler
from simpleml.dataset import loadDataset

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    # print(dataset.data)
    dataset = StandardScaler().scale(dataset)
    print(dataset.data)


if __name__ == "__main__":
    exampleWorkflow()
