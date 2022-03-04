# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset


# Workflow steps ---------------------------------------------------------------
from simpleml.model.supervised.regression import LinearRegression


def speedAveragesExampleWorkflow():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.categoryToVector('street_type')
    dataset = dataset.categoryToVector('max_speed')
    dataset = dataset.categoryToVector('season')
    dataset = dataset.categoryToVector('daylight')

    dataset = dataset.addDayOfTheYearAttribute('start_time')
    dataset = dataset.dateToTimestamp("start_time")
    #dataset = dataset.dateToTimestamp("end_time")

    dataset = dataset.flattenData()

    dataset = dataset.dropAttributes(
        ["osm_id", "street_type", "max_speed", "start_time", "end_time", "season", "daylight", "street_type_encoded",
         "max_speed_encoded",
         "season_encoded", "daylight_encoded", "geometry"])

    print(dataset.data.columns.values.tolist())
    # print(dataset.data.dtypes)

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    # X_train = train.keepAttributes(["street_type", "max_speed", "start_time", "number_of_records", "number_of_drivers",
    #                                "season", "daylight"])
    # X_test = test.keepAttributes(["street_type", "max_speed", "start_time", "number_of_records", "number_of_drivers",
    #                              "season", "daylight"])
    X_train = train.dropAttributes("average_speed")
    X_test = train.dropAttributes("average_speed")
    y_train = train.keepAttributes("average_speed")
    y_test = test.keepAttributes("average_speed")
    print(y_test.data)

    lr = LinearRegression()
    lr = lr.fit(X_train, y_train)
    y_pred = lr.predict(X_test)

    # linearRegressionModel(dataset, "average_speed")

if __name__ == "__main__":
    speedAveragesExampleWorkflow()
