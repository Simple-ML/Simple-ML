# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    # query the data catalog for its datasets
    #datasets = getDatasets(topic="Traffic")
    dataset_names = ['WhiteWineQualityBinary', 'SpeedAveragesMiniSampleWKT', 'PostOffices', 'PublicHolidaysGermany']

    for dataset_name in dataset_names:
        dataset = loadDataset(dataset_name)
        print(exportDictionaryAsJSON(dataset.getProfile()))

        column_names = dataset.getColumnNames()
        train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
        X_train = train.dropAttributes(column_names[2])
        X_test = test.dropAttributes(column_names[2])
        y_train = train.keepAttributes(column_names[2])

        # compute statistics from the dataset
        print(X_train.getProfile())
        print(exportDictionaryAsJSON(X_train.getProfile()))
        print(exportDictionaryAsJSON(y_train.getProfile()))
        print(exportDictionaryAsJSON(X_test.getProfile()))
        print('----------------------------------------------------------')

if __name__ == '__main__':
    exampleWorkflow()
