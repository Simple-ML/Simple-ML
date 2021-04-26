# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets, getDatasetsJson
from simpleml.dataset import loadDataset
from simpleml.util import exportAsJSON, exportDictionaryAsJSON
import sys

# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():

    # query the data catalog for its datasets
    datasets = getDatasets(topic="Traffic")
    for dataset in datasets:
        print("Dataset: " + dataset.id + " -> " + dataset.title + ", Topics: " + ", ".join(dataset.topics))
    print(getDatasetsJson(datasets))

    dataset = loadDataset("SpeedAveragesMiniSampleWKT")

    print(exportDictionaryAsJSON(dataset))

    dataset = dataset.sample(5)

    dataset = dataset.keepAttributes(["max_speed", "end_time", "number_of_records", "average_speed", "season", "daylight", "day_type"])
    dataset_train = dataset.splitIntoTrainAndTest(0.8)[0]

    # compute statistics from the dataset
    trainStatistics = dataset_train.getStatistics()

    print(exportDictionaryAsJSON(trainStatistics))