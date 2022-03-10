# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset

# Workflow steps ---------------------------------------------------------------
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression


def exampleWorkflow():
    dataset = loadDataset("WhiteWineQuality")

    dataset = dataset.setTargetAttribute("quality")
    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)

    lr = LinearRegression().fit(X_train, y_train)
    y_pred = lr.predict(X_test)
    print(y_pred.data)
    print("MAE:", meanAbsoluteError(y_test, y_pred))


if __name__ == "__main__":
    exampleWorkflow()
