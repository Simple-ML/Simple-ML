# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset, Instance
from simpleml.util import exportDictionaryAsJSON

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    def transformIntoBinaryQuality(instance: Instance):
        return 1 if instance.getValue("quality") >= 7 else 0

    dataset = dataset.addAttribute("goodquality", transformFunc = transformIntoBinaryQuality)

    print("Example quality value:", dataset.getRow(3).getValue("goodquality"))


if __name__ == '__main__':
    exampleWorkflow()
