from typing import Protocol

from numpy.typing import ArrayLike

from simpleml.dataset import Dataset

DataType = Dataset


class Model(Protocol):

    def predict(self, data: DataType) -> ArrayLike:
        ...


class Estimator(Protocol):

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        ...
