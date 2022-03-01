from numpy.typing import ArrayLike
from sklearn.linear_model import Ridge as SkRidge
from simpleml.model.supervised._domain import Estimator, Model, DataType


class RidgeRegressionModel(Model):
    def __init__(self, underlying: SkRidge):
        self._underlying = underlying

    def predict(self, data: DataType) -> ArrayLike:
        return self._underlying.predict(data.toArray())


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
            random_state=None
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return RidgeRegressionModel(self._underlying.fit(train_data.toArray(), labels.toArray().astype('float'), **kwargs))
