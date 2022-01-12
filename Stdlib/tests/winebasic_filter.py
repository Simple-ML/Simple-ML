# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset, Instance
from simpleml.util import exportDictionaryAsJSON

# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")
    #print(exportDictionaryAsJSON(dataset.getProfile()))

#    dataset_filtered = dataset.filterByAttribute(attribute='quality', value=6)
#    print(dataset_filtered.data)

    def filterGoodQuality(instance: Instance):
        return instance.getAttribute("quality") == 8 or instance.getAttribute("quality") < 4

    dataset_filtered = dataset.filterInstances(filter_func = filterGoodQuality)
    print(dataset_filtered.data)

    #for index, row in dataset_filtered.data.iterrows():
    #    print(row['quality'])

    #train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    #X_train = train.dropAttributes("quality")
    #X_test = test.dropAttributes("quality")
    #y_train = train.keepAttributes("quality")

    # compute statistics from the dataset
    #print(exportDictionaryAsJSON(X_train.getProfile()))
    #print(exportDictionaryAsJSON(y_train.getProfile()))
    #print(exportDictionaryAsJSON(X_test.getProfile()))


if __name__ == '__main__':
    exampleWorkflow()
