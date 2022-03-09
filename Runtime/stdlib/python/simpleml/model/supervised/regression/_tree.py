from typing import Optional, Union

from pandas import DataFrame
from simpleml.model.supervised._domain import DataType, Estimator, Model
from sklearn.ensemble import RandomForestRegressor as SkRandomForestRegressor
from sklearn.tree import DecisionTreeRegressor as SkDecisionTreeRegressor


class DecisionTreeRegressorModel(Model):
    def __init__(self, underlying: SkDecisionTreeRegressor, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> DataType:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()), columns=self._yTrain.data.columns
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class DecisionTreeRegressor(Estimator):
    def __init__(
        self,
        criterion: str = "mse",
        splitter: str = "best",
        maxDepth: Optional[int] = None,
        minSamplesSplit: Union[int, float] = 2,
        minSamplesLeaf: Union[int, float] = 1,
        minWeightFractionLeaf: float = 0.0,
        maxFeatures: Optional[Union[int, float, str]] = None,
        randomState: Optional[int] = None,
        maxLeafNodes: int = None,
        minImpurityDecrease: float = 0.0,
        ccpAlpha: float = 0.0,
    ):
        self._underlying = SkDecisionTreeRegressor(
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
            ccp_alpha=ccpAlpha,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return DecisionTreeRegressorModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("float"), **kwargs
            ),
            labels,
        )


class RandomForestRegressorModel(Model):
    def __init__(self, underlying: SkRandomForestRegressor, yTrain: DataType):
        self._underlying = underlying
        self._yTrain = yTrain

    def predict(self, data: DataType) -> DataType:
        yPred = self._yTrain.copy(basic_data_only=True)
        yPred.data = DataFrame(
            self._underlying.predict(data.toArray()),
            columns=self._yTrain.data.columns,
        )
        yPred.title = self._yTrain.title.replace("(Train)", "(Predicton)")
        return yPred.provide_statistics()


class RandomForestRegressor(Estimator):
    def __init__(
        self,
        nEstimator: int = 100,
        criterion: str = "mse",
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
        ccpAlpha: float = 0.0,
        maxSamples: Optional[Union[int, float]] = None,
        randomState: Optional[int] = None,
    ):
        self._underlying = SkRandomForestRegressor(
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
            ccp_alpha=ccpAlpha,
            bootstrap=bootstrap,
            oob_score=oobScore,
            warm_start=warmStart,
            max_samples=maxSamples,
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return RandomForestRegressorModel(
            self._underlying.fit(
                train_data.toArray(), labels.toArray().astype("float"), **kwargs
            ),
            labels,
        )
