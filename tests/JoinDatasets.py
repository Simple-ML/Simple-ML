# Imports ----------------------------------------------------------------------
from simpleml.dataset import joinTwoDatasets
from simpleml.util import exportDictionaryAsJSON


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    dataset = joinTwoDatasets('SpeedAveragesMiniSampleWKT2.csv', 'SpeedAveragesMiniSampleWKT3.csv', ',', '_first', '_second')
    '''print(exportDictionaryAsJSON(dataset.getProfile()))

    sample_dataset = dataset.sample(50)
    train, test = sample_dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)

    X_train = train.dropAttributes("chlorides")
    X_test = test.dropAttributes("chlorides")
    y_train = train.keepAttributes("chlorides")

    # compute statistics from the dataset
    print(exportDictionaryAsJSON(X_train.getProfile()))
    print(exportDictionaryAsJSON(y_train.getProfile()))
    print(exportDictionaryAsJSON(X_test.getProfile()))

'''
if __name__ == '__main__':
    exampleWorkflow()
