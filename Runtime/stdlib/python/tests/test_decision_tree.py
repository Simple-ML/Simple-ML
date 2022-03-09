from simpleml.dataset import loadDataset
from simpleml.metrics import precision
from simpleml.model.supervised.classification._tree import DecisionTreeClassifier


def test_create():
    dt = DecisionTreeClassifier()
    assert dt is not None  # nosec


def test_train_and_infer():
    dt = DecisionTreeClassifier()
    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttribute("quality")
    X_test = test.dropAttribute("quality")
    y_train = train.keepAttribute("quality")

    model = dt.fit(X_train, y_train)

    pred = model.predict(X_test)

    m = precision(test.keepAttribute("quality"), pred)
    assert m > 0  # nosec
    assert len(pred.data["quality"]) > 0  # nosec
