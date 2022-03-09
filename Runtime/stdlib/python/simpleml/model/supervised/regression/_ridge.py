from pandas import DataFrame
from simpleml.model.supervised._domain import DataType, Estimator, Model
from sklearn.linear_model import Ridge as SkRidge


class RidgeRegressionModel(Model):
    def __init__(self, underlying: SkRidge, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> DataType:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class RidgeRegression(Estimator):
    def __init__(self, regularizationStrength: float = 1.0):
        self._underlying = SkRidge(
            alpha=regularizationStrength,
            fit_intercept=True,
            normalize=False,
            copy_X=True,
            max_iter=None,
            tol=1e-3,
            solver="auto",
            random_state=None,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return RidgeRegressionModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("float"), **kwargs
            ),
            labels,
        )
