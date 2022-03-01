from simpleml.model.supervised._domain import DataType
from typing import Union
from numpy.typing import ArrayLike
from ._util import convert_to_array

import sklearn.metrics as skMetrics

def accuracy(yTrue: Union[DataType, ArrayLike], yPred: Union[DataType, ArrayLike]):
    return skMetrics.accuracy_score(convert_to_array(yTrue, 'int'), convert_to_array(yPred, 'int'))

def balancedAccuracy(yTrue: Union[DataType, ArrayLike], yPred: Union[DataType, ArrayLike]):
    return skMetrics.balanced_accuracy_score(convert_to_array(yTrue, 'int'), convert_to_array(yPred, 'int'))

def averagePrecision(yTrue: Union[DataType, ArrayLike], yPred: Union[DataType, ArrayLike]):
    return skMetrics.average_precision_score(convert_to_array(yTrue, 'int'), convert_to_array(yPred, 'int'))

def precision(yTrue: Union[DataType, ArrayLike], yPred: Union[DataType, ArrayLike]):
    return skMetrics.precision_score(convert_to_array(yTrue, 'int'), convert_to_array(yPred, 'int'))

def recall(yTrue: Union[DataType, ArrayLike], yPred: Union[DataType, ArrayLike]):
    return skMetrics.recall_score(convert_to_array(yTrue, 'int'), convert_to_array(yPred, 'int'))