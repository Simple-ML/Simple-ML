# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset, Instance
from simpleml.util import exportDictionaryAsJSON

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    def filterByQuality(instance: Instance):
        return instance.getValue("quality") == 8 or instance.getValue("quality") < 4

    dataset_filtered = dataset.filterInstances(filter_func=filterByQuality)

    print("Example quality value:", dataset_filtered.getRow(
        3).getAttribute("quality"))


if __name__ == '__main__':
    exampleWorkflow()
