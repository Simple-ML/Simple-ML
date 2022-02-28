from simpleml.model.supervised.classification._svm import SupportVectorMachineClassifier, SupportVectorMachineClassifierModel
from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON
import pytest


def test_create():
    rf = SupportVectorMachineClassifier()
    assert rf is not None

@pytest.mark.skip(reason="dataset resolution do not work")
def test_train_and_infer():
    rf = SupportVectorMachineClassifier()
    dataset = loadDataset("WhiteWineQualityBinary")
    print(exportDictionaryAsJSON(dataset.getProfile()))

    train, test = dataset.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    X_train = train.dropAttributes("quality")
    X_test = test.dropAttributes("quality")
    y_train = train.keepAttributes("quality")

    model = rf.fit(X_train, y_train)

    pred = model.predict(X_test)

    assert len(pred) > 0
