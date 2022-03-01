from simpleml.model.supervised.regression._tree import RandomForestRegressor, DecisionTreeRegressor
from simpleml.dataset import loadDataset


def test_create_forest():
    rf = RandomForestRegressor()
    assert rf is not None # nosec

def test_train_and_infer_forest():
    rf = RandomForestRegressor()
    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0 # nosec

def test_create_tree():
    rf = DecisionTreeRegressor()
    assert rf is not None # nosec

def test_train_and_infer_tree():
    rf = DecisionTreeRegressor()
    dataset = loadDataset("WhiteWineQualityBinary")

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0 # nosec
