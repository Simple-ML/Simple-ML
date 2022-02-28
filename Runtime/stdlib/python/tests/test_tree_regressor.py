from simpleml.model.supervised.regression._tree import RandomForestRegressor, DecisionTreeRegressor
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON
import pytest


def test_create_forest():
    rf = RandomForestRegressor()
    assert rf is not None

@pytest.mark.skip(reason="dataset resolution do not work")
def test_train_and_infer_forest():
    rf = RandomForestRegressor()
    dataset = loadDataset("WhiteWineQualityBinary")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0

def test_create_tree():
    rf = DecisionTreeRegressor()
    assert rf is not None

@pytest.mark.skip(reason="dataset resolution do not work")
def test_train_and_infer_tree():
    rf = DecisionTreeRegressor()
    dataset = loadDataset("WhiteWineQualityBinary")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0
