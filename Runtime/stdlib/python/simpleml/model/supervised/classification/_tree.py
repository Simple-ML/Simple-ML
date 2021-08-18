from numpy.typing import ArrayLike

from simpleml.model.supervised._domain import Estimator, Model, DataType
from sklearn.tree import DecisionTreeClassifier as SkDecisionTreeClassifier
from typing import Union, Optional, Dict, List


class DecisionTreeClassifierModel(Model):
    def __init__(self, underlying: SkDecisionTreeClassifier):
        self._underlying = underlying

    def predict(self, data: DataType) -> ArrayLike:
        return self._underlying.predict(data.toArray())


class DecisionTreeClassifier(Estimator):
    
    def __init__(self,
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
                 ccpAlpha: float = 0.0
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
            ccp_alpha=ccpAlpha
        )

    def fit(self, train_data: DataType, labels: DataType, **kwargs) -> Model:
        return DecisionTreeClassifierModel(self._underlying.fit(train_data.toArray(), labels.toArray(), **kwargs))




