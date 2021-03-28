# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    print("\n### ML Algorithm classes and algorithms ###")
    print("# ML Algorithm classes:")
    for algorithmClass in getMLAlgorithmClasses():
        print(algorithmClass["label"])
    print("# Decision tree algorithms:")
    for algorithm in getMLAlgorithms("DecisionTrees"):
        if "description" in algorithm:
            print(algorithm["label"] + ": " + algorithm["description"])
    print("# Regression metrics:")
    for metric in getMetrics("RegressionMeasure"):
        if "label" in metric:
            print(metric["label"] + ", data type: " + metric["range"])
    print("# Benchmarks for SpeedAverages")
    for benchmark in getBenchmarks("SpeedAverages"):
        print(benchmark["algorithm"] + ", " + benchmark["metricLabel"] + ", score: " + benchmark["score"])

    print("\n### Data Catalog ###")
    # query the data catalog for its datasets
    datasets = getDatasets(topic="Verkehr")
    for dataset in datasets:
        print("Dataset: " + dataset.id + " -> " + dataset.title + ", Topics: " + ", ".join(dataset.topics))

    print("\n### Data Set Workflow ###")

    dataset = loadDataset("SpeedAverages")
    #dataset = loadDataset("WhiteWineQualityBinary")

    print(exportDictionaryAsJSON(dataset.getStatistics()))

    for attribute_label in dataset.attribute_labels.values():
        print(attribute_label)

    dataset = dataset.sample(5)

    #dataset = dataset.keepAttributes(["alcohol", "fixed acidity", "quality"])
    dataset = dataset.keepAttributes(["street_type", "max_speed", "start_time", "end_time", "number_of_records", "number_of_drivers", "average_speed", "season", "daylight", "is_weekend", "day_type", "geometry"])
    #dataset = dataset.keepAttributes(["geometry"])
    #dataset = dataset.keepAttributes(["name_en", "name_de", "polygon_simple", "polygon"])
    train, test = dataset.splitIntoTrainAndTest(0.8)

    # compute statistics from the dataset
    trainStatistics = train.getStatistics()

    print(exportDictionaryAsJSON(trainStatistics))

# Workflows --------------------------------------------------------------------

# def createStats():
#    features = loadSample(nInstances=3)
#    featuresName = ["id","street_type","number_of_drivers","is_weekend","geometry"]
#    stats = Statistics()
#
#    streatType = stats.numericAndStringStats(features, featuresName[1])
#    print(streatType)
#    numberOfDrivers = stats.numericAndStringStats(features, featuresName[2])
#    print(numberOfDrivers)
#    isWeekend = stats.numericAndStringStats(features, featuresName[3])
#    print(isWeekend)
#    print('Geometry = ')
#    geometry = stats.geometryStats(features, featuresName[4])
#    print(geometry)

if __name__ == '__main__':
    exampleWorkflow()
