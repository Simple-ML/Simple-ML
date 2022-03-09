from simpleml.dataset import loadDataset
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression._linear_regression import LinearRegression


def test_create():
    rf = LinearRegression()
    assert rf is not None  # nosec


def test_train_and_infer():
    rf = LinearRegression()
    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttribute("quality")
    X_test = test.dropAttribute("quality")
    y_train = train.keepAttribute("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    meanAbsoluteError(test.keepAttribute("quality"), pred)

    print("YES")
    assert len(pred.data["quality"]) > 0  # nosec
