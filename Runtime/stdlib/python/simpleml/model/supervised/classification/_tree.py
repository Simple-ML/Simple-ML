from typing import Dict, List, Optional, Union

from numpy.typing import ArrayLike
from pandas import DataFrame
from simpleml.model.supervised._domain import DataType, Estimator, Model
from sklearn.ensemble import RandomForestClassifier as SkRandomForestClassifier
from sklearn.tree import DecisionTreeClassifier as SkDecisionTreeClassifier


class DecisionTreeClassifierModel(Model):
    def __init__(self, underlying: SkDecisionTreeClassifier, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> DataType:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class DecisionTreeClassifier(Estimator):
    def __init__(
        self,
        criterion: str = "gini",
        splitter: str = "best",
        maxDepth: Optional[int] = None,
        minSamplesSplit: Union[int, float] = 2,
        minSamplesLeaf: Union[int, float] = 1,
        minWeightFractionLeaf: float = 0.0,
        maxFeatures: Optional[Union[int, float, str]] = None,
        randomState: Optional[int] = None,
        maxLeafNodes: int = None,
        minImpurityDecrease: float = 0.0,
        classWeight: Union[str, Dict[int, int], List[Dict[int, int]]] = None,
        ccpAlpha: float = 0.0,
    ):
        self._underlying = SkDecisionTreeClassifier(
            criterion=criterion,
            splitter=splitter,
            max_depth=maxDepth,
            min_samples_split=minSamplesSplit,
            min_samples_leaf=minSamplesLeaf,
            min_weight_fraction_leaf=minWeightFractionLeaf,
            max_features=maxFeatures,
            random_state=randomState,
            max_leaf_nodes=maxLeafNodes,
            min_impurity_decrease=minImpurityDecrease,
            class_weight=classWeight,
            ccp_alpha=ccpAlpha,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return DecisionTreeClassifierModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("int"), **kwargs
            ),
            labels,
        )


class RandomForestClassifierModel(Model):
    def __init__(self, underlying: SkRandomForestClassifier, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> ArrayLike:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class RandomForestClassifier(Estimator):
    def __init__(
        self,
        nEstimator: int = 100,
        criterion: str = "gini",
        maxDepth: Optional[int] = None,
        minSamplesSplit: Union[int, float] = 2,
        minSamplesLeaf: Union[int, float] = 1,
        minWeightFractionLeaf: Optional[float] = 0.0,
        maxFeatures: Union[int, str, float] = "auto",
        maxLeafNodes: Optional[int] = None,
        minImpurityDecrease: float = 0.0,
        bootstrap: bool = True,
        oobScore: bool = False,
        warmStart: bool = False,
        classWeight: Optional[Union[str, Dict, List[Dict]]] = None,
        ccpAlpha: float = 0.0,
        maxSamples: Optional[Union[int, float]] = None,
        randomState: Optional[int] = None,
    ):
        self._underlying = SkRandomForestClassifier(
            n_estimators=nEstimator,
            criterion=criterion,
            max_depth=maxDepth,
            min_samples_split=minSamplesSplit,
            min_samples_leaf=minSamplesLeaf,
            min_weight_fraction_leaf=minWeightFractionLeaf,
            max_features=maxFeatures,
            random_state=randomState,
            max_leaf_nodes=maxLeafNodes,
            min_impurity_decrease=minImpurityDecrease,
            class_weight=classWeight,
            ccp_alpha=ccpAlpha,
            bootstrap=bootstrap,
            oob_score=oobScore,
            warm_start=warmStart,
            max_samples=maxSamples,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return RandomForestClassifierModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("int"), **kwargs
            ),
            labels,
        )
