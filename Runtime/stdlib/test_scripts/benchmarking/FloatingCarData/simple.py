# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression


# Workflow steps ---------------------------------------------------------------
def exampleWorkflow():
    dataset = loadDataset("FloatingCarData")
    dataset = dataset.sample(1000)

    dataset = dataset.dropAllMissingValues()

    dataset = dataset.setTargetAttribute("speed")
    dataset = dataset.transformDatatypes()

    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)
    lr = LinearRegression().fit(X_train, y_train)
    y_pred = lr.predict(X_test)

    print("MAE:", meanAbsoluteError(y_test, y_pred))

if __name__ == "__main__":
    exampleWorkflow()
