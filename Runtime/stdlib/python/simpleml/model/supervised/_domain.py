from typing import Protocol

from simpleml.dataset import Dataset

DataType = Dataset


class Model(Protocol):
    def predict(self, data: DataType) -> DataType:
        ...


class Estimator(Protocol):
    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        ...
