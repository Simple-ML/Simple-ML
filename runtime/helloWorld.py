# Imports ----------------------------------------------------------------------
from runtimeBridge import save_placeHolder
from simpleml.dataset import loadDataset
from simpleml.model.supervised.classification import DecisionTreeClassifier

# Workflows -----------------------------------------------------------------
def winebasic():

    df = loadDataset('WhiteWineQualityBinary')
    save_placeHolder('df', df)
    df_train, df_test = df.splitIntoTrainAndTest(trainRatio=0.75, randomState=1)
    save_placeHolder('df_train', df_train)
    save_placeHolder('df_test', df_test)
    X_train = df_train.dropAttributes('quality')
    save_placeHolder('X_train', X_train)
    X_test = df_test.dropAttributes('quality')
    save_placeHolder('X_test', X_test)
    y_train = df_train.keepAttributes('quality')
    save_placeHolder('y_train', y_train)
    estimator = DecisionTreeClassifier()
    save_placeHolder('estimator', estimator)
    model = estimator.fit(X_train, y_train)
    save_placeHolder('model', model)
    y_pred = model.predict(X_test)
    save_placeHolder('y_pred', y_pred)