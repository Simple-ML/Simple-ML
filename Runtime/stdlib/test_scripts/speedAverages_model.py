from simpleml.dataset import loadDataset
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression._linear_regression import LinearRegression


def test_create():
    rf = LinearRegression()
    assert rf is not None  # nosec


def test_train_and_infer():
    rf = LinearRegression()
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

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("average_speed")
    X_test = test.dropAttributes("average_speed")
    y_train = train.keepAttributes("average_speed")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    meanAbsoluteError(test.keepAttributes("average_speed"), pred)

    assert len(pred) > 0  # nosec
