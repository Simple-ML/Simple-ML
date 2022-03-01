from simpleml.model.supervised.regression._linear_regression import LinearRegression
from simpleml.dataset import loadDataset
from simpleml.metrics import meanAbsoluteError


def test_create():
    rf = LinearRegression()
    assert rf is not None # nosec

def test_train_and_infer():
    rf = LinearRegression()
    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    meanAbsoluteError(test.keepAttributes("quality"), pred)

    assert len(pred) > 0 # nosec
