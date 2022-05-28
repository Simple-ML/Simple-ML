# Imports ----------------------------------------------------------------------
from simpleml.dataset import StandardScaler, joinTwoDatasets, loadDataset
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import RidgeRegression

# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset_sales = loadDataset("RossmannSales").sample(1000)
    dataset_stores = loadDataset("RossmannStores").sample(1000)

    datastore_joined = joinTwoDatasets(
        dataset_sales,
        dataset_stores,
        attributeId1="Store",
        attributeId2="Store",
        suffix1="_Sales",
        suffix2="_Stores",
    )  # .sample(1000)
    datastore_joined.setTargetAttribute("Sales")
    datastore_joined = datastore_joined.dropAllMissingValues()
    datastore_joined = datastore_joined.transformDatatypes()
    datastore_joined = StandardScaler().scale(datastore_joined)

    print(datastore_joined.data)
    for column in datastore_joined.data:
        print(column)
        print(datastore_joined.data[column])

    X_train, X_test, y_train, y_test = datastore_joined.splitIntoTrainAndTestAndLabels(
        0.8
    )

    rr = RidgeRegression().fit(X_train, y_train)
    y_pred = rr.predict(X_test)

    print("MAE:", meanAbsoluteError(y_test, y_pred))


if __name__ == "__main__":
    exampleWorkflow()
