from pandas import DataFrame
from simpleml.model.supervised._domain import DataType, Estimator, Model
from sklearn.linear_model import LinearRegression as SkLinearRegression


class LinearRegressionModel(Model):
    def __init__(self, underlying: SkLinearRegression, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> DataType:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class LinearRegression(Estimator):
    def __init__(self):
        self._underlying = SkLinearRegression(
            fit_intercept=True, normalize=False, copy_X=True, n_jobs=None
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return LinearRegressionModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("float"), **kwargs
            ),
            labels,
        )
