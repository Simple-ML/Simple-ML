# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import readDataSetFromCSV
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():

    dataset = readDataSetFromCSV('WhiteWineQualityBinary2.csv', 'New dataset')
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    # compute statistics from the dataset
    print(exportDictionaryAsJSON(X_train.getProfile()))
    print(exportDictionaryAsJSON(y_train.getProfile()))
    print(exportDictionaryAsJSON(X_test.getProfile()))


if __name__ == '__main__':
    exampleWorkflow()
