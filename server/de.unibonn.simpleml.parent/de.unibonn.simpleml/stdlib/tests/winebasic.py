# Imports ----------------------------------------------------------------------
from simpleml.data_catalog import getDatasets
from simpleml.ml_catalog import getMLAlgorithmClasses, getMLAlgorithms, getMetrics, getBenchmarks
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON
from simpleml.model.supervised.classification import DecisionTreeClassifier
import numpy as np


# Workflow steps ---------------------------------------------------------------

def exampleWorkflow():

    dataset = loadDataset("WhiteWineQualityBinary")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    # compute statistics from the dataset
    print(exportDictionaryAsJSON(X_train.getProfile()))
    print(exportDictionaryAsJSON(y_train.getProfile()))
    print(exportDictionaryAsJSON(X_test.getProfile()))

    classifier = DecisionTreeClassifier()

    model = classifier.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    mae = np.mean(np.abs(y_pred - test.keepAttributes("quality").toArray()))
    acc = np.mean(y_pred == test.keepAttributes("quality").toArray())

    print('Test MAE: ' + str(mae))
    print('Test ACC: ' + str(acc))



if __name__ == '__main__':
    exampleWorkflow()
