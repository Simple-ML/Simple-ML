from numpy.typing import ArrayLike
from sklearn.linear_model import LinearRegression as SkLinearRegression
from simpleml.model.supervised._domain import Estimator, Model, DataType

class LinearRegressionModel(Model):
    def __init__(self, underlying: SkLinearRegression):
        self._underlying = underlying

    def predict(self, data: DataType) -> ArrayLike:
        return self._underlying.predict(data.toArray())


class LinearRegression(Estimator):
    
    def __init__(self):
        self._underlying =  SkLinearRegression(
            fit_intercept=True,
            normalize=False,
            copy_X=True,
            n_jobs=None
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return LinearRegressionModel(self._underlying.fit(train_data.toArray(), labels.toArray().astype('float'), **kwargs))

