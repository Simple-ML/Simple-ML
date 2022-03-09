from typing import Dict, List, Optional, Union

from numpy.typing import ArrayLike
from pandas import DataFrame
from simpleml.model.supervised._domain import DataType, Estimator, Model
from sklearn.svm import LinearSVC as SkLinearSVC


class SupportVectorMachineClassifierModel(Model):
    def __init__(self, underlying: SkLinearSVC, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> ArrayLike:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class SupportVectorMachineClassifier(Estimator):
    def __init__(
        self,
        penalty: str = "l2",
        loss: str = "squared_hinge",
        dual: bool = True,
        tol: float = 1e-4,
        c: float = 1.0,
        multiClass: str = "ovr",
        fitIntercept: bool = True,
        interceptScaling: float = 1,
        classWeight: Union[str, Dict[int, int], List[Dict[int, int]]] = None,
        verbose: int = 0,
        randomState: Optional[int] = None,
        maxIter: int = 1000,
    ):
        self._underlying = SkLinearSVC(
            penalty=penalty,
            loss=loss,
            dual=dual,
            tol=tol,
            C=c,
            multi_class=multiClass,
            fit_intercept=fitIntercept,
            intercept_scaling=interceptScaling,
            class_weight=classWeight,
            verbose=verbose,
            random_state=randomState,
            max_iter=maxIter,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return SupportVectorMachineClassifierModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("int"), **kwargs
            ),
            labels,
        )
