# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():

    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(train_ratio=0.75)
    X_train = train.dropAttributes("quality")
    X_test = train.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    # compute statistics from the dataset
    trainStatistics = X_train.getStatistics()

    print(exportDictionaryAsJSON(trainStatistics))

if __name__ == '__main__':
    exampleWorkflow()
