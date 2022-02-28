# Imports ----------------------------------------------------------------------
from dataset._normalize import StandardNormalizer

from simpleml.dataset import loadDataset


# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    dataset = StandardNormalizer().normalize(dataset)
    print(dataset.data)


if __name__ == '__main__':
    exampleWorkflow()
