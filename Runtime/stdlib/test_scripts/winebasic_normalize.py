# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset
from simpleml.dataset._normalize import StandardNormalizer

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    dataset = StandardNormalizer().normalize(dataset)
    print(dataset.data)


if __name__ == "__main__":
    exampleWorkflow()
