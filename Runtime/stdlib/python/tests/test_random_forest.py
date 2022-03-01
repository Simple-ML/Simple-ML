from simpleml.model.supervised.classification._tree import RandomForestClassifier
from simpleml.dataset import loadDataset


def test_create():
    rf = RandomForestClassifier()
    assert rf is not None

def test_train_and_infer():
    rf = RandomForestClassifier()
    dataset = loadDataset("WhiteWineQualityBinary")
    
    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0
