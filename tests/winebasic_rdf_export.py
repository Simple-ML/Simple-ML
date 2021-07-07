# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import exportStatisticsAsRDF
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    dataset = loadDataset("WhiteWineQualityBinary")

    print("JSON:")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    # train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    # X_train = train.dropAttributes("quality")
    # X_test = test.dropAttributes("quality")
    # y_train = train.keepAttributes("quality")

    # compute statistics from the dataset
    # print(exportDictionaryAsJSON(X_train.getProfile()))
    # print(exportDictionaryAsJSON(y_train.getProfile()))
    # print(exportDictionaryAsJSON(X_test.getProfile()))
    print("RDF:")
    print(exportStatisticsAsRDF(dataset))


if __name__ == '__main__':
    exampleWorkflow()
