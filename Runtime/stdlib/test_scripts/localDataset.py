# Imports ----------------------------------------------------------------------
from simpleml.dataset import readDataSetFromCSV
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = readDataSetFromCSV('SpeedAveragesMiniSampleWKT.csv', 'Local dataset', ',', 'True')
    print(exportDictionaryAsJSON(dataset.getProfile()))

    sample_dataset = dataset.sample(50)
    train, test = sample_dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)

    # X_train = train.dropAttribute("season")
    # X_test = test.dropAttribute("season")
    # y_train = train.keepAttribute("season")

    # compute statistics from the dataset
    print(exportDictionaryAsJSON(X_train.getProfile()))
    print(exportDictionaryAsJSON(y_train.getProfile()))
    print(exportDictionaryAsJSON(X_test.getProfile()))


if __name__ == '__main__':
    exampleWorkflow()
