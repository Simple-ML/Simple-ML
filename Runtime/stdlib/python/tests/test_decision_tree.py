from simpleml.model.supervised.classification._tree import DecisionTreeClassifier, DecisionTreeClassifierModel
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON
import pytest


def test_create():
    dt = DecisionTreeClassifier()
    assert dt is not None


@pytest.mark.skip(reason="dataset resolution do not work")
def test_train_and_infer():
    dt = DecisionTreeClassifier()
    dataset = loadDataset("WhiteWineQualityBinary")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = dt.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0

