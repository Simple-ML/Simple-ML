# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    # query the data catalog for its datasets
    datasets = getDatasets()
    #dataset_names = ['WhiteWineQualityBinary', 'SpeedAveragesMiniSampleWKT', 'PostOffices', 'PublicHolidaysGermany']

    for dataset in datasets:

        print("===",dataset.id,"===")

        # Pandas can't deal with missing boolean values, so skip this dataset.
        if dataset.id == 'SpeedAveragesHeavilyCorrupted':
            continue
        # TODO: check
        if dataset.id == 'TrafficWarnings' or dataset.id == 'TrafficTweets':
            continue
        if dataset.id == 'SpeedAveragesCorrupted':
            continue

        dataset = loadDataset(dataset.id)
        print(exportDictionaryAsJSON(dataset.getProfile()))

        dataset_sample = dataset.sample(500)

        column_names = dataset_sample.getColumnNames()
        train, test = dataset_sample.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)

        print("Drop",column_names[0])

        X_train = train.dropAttributes(column_names[0])
        X_test = test.dropAttributes(column_names[0])
        y_train = train.keepAttributes(column_names[0])

        # compute statistics from the dataset
        print(X_train.getProfile())
        print(exportDictionaryAsJSON(X_train.getProfile()))
        print(exportDictionaryAsJSON(y_train.getProfile()))
        print(exportDictionaryAsJSON(X_test.getProfile()))
        print("")

if __name__ == '__main__':
    exampleWorkflow()
