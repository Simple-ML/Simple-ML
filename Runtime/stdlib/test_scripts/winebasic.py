# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset


# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")
    print(dataset.dataset_json)

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    print(train.dataset_json)

    X_train = train.dropAttribute("quality")
    X_test = test.dropAttribute("quality")
    y_train = train.keepAttribute("quality")
    y_test = test.keepAttribute("quality")

    print(X_train.dataset_json)
    print(X_test.dataset_json)
    print(y_train.dataset_json)
    print(y_test.dataset_json)

    # DecisionTreeClassifier().fit(X_train, y_train)

    # compute statistics from the dataset
    # print(exportDictionaryAsJSON(X_train.getProfile()))
    # print(exportDictionaryAsJSON(y_train.getProfile()))
    # print(exportDictionaryAsJSON(X_test.getProfile()))

    # lr = LinearRegression().fit(X_train, y_train)
    # print(lr)
    # y_pred = lr.predict(X_test)
    # print("MAE:", meanAbsoluteError(y_test, y_pred))


if __name__ == "__main__":
    exampleWorkflow()
