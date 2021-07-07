# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    dataset = loadDataset("SpeedAveragesMiniSample")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    # print(train.data)
    X_train = train.dropAttributes("is_weekend")
    X_test = test.dropAttributes("is_weekend")
    y_train = train.keepAttributes("is_weekend")
    # print(X_train.data)

    # compute statistics from the dataset
    # print('x train', X_train.getProfile())
    print(exportDictionaryAsJSON(X_train.getProfile()))
    print(exportDictionaryAsJSON(X_test.getProfile()))
    print(exportDictionaryAsJSON(y_train.getProfile()))


if __name__ == '__main__':
    exampleWorkflow()
