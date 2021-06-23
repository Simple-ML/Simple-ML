# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():

    dataset = loadDataset("RossmannSales")
    print(exportDictionaryAsJSON(dataset.getProfile()))
    print(dataset.data_types)
    sample=dataset.sample(100)
    print(sample.data_types)


    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("Sales")
    X_test = test.dropAttributes("Sales")
    y_train = train.keepAttributes("Sales")

    # compute statistics from the dataset
    print(exportDictionaryAsJSON(X_train.getProfile()))
#    print(exportDictionaryAsJSON(y_train.getProfile()))
#    print(exportDictionaryAsJSON(X_test.getProfile()))

if __name__ == '__main__':
    exampleWorkflow()
