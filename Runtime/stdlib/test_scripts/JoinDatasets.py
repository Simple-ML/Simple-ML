# Imports ----------------------------------------------------------------------
from simpleml.dataset import joinTwoDatasets, readDataSetFromCSV


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():
    dataset1 = readDataSetFromCSV('SpeedAveragesMiniSampleWKT.csv', 'Local dataset', ',', 'True')
    sample_dataset1 = dataset1.sample(5)

    dataset2 = readDataSetFromCSV('SpeedAveragesMiniSampleWKT.csv', 'Local dataset', ',', 'True')
    sample_dataset2 = dataset2.sample(5)
    #print(sample_dataset1.data)

    dataset = joinTwoDatasets(sample_dataset1, sample_dataset2, 'id', 'id', '_l', '_r')
    #dataset = joinTwoDatasets('SpeedAveragesMiniSampleWKT.csv', 'SpeedAveragesMiniSampleWKT.csv', ',', '_first', '_second')
    #print(dataset)
    dataset.exportDataAsFile("/home/fakhar/Downloads/test123.csv")

    sample_dataset = dataset.sample(3)

    #print(type(sample_dataset))
    train, test = sample_dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    #print(sample_dataset.data)
    print(train.data)
    '''
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
