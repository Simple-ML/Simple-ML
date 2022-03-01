from simpleml.model.supervised._domain import DataType
from typing import Union
from numpy.typing import ArrayLike
from ._util import convert_to_array

import sklearn.metrics as skMetrics

def meanAbsoluteError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_absolute_error(convert_to_array(yTrue, 'float'), convert_to_array(yPred, 'float'))

def meanSquaredError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_squared_error(convert_to_array(yTrue, 'float'), convert_to_array(yPred, 'float'))

def meanSquaredLogError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_squared_log_error(convert_to_array(yTrue, 'float'), convert_to_array(yPred, 'float'))

def medianAbsoluteError(yTrue: DataType, yPred: DataType):
    return skMetrics.median_absolute_error(convert_to_array(yTrue, 'float'), convert_to_array(yPred, 'float'))

def r2(yTrue: DataType, yPred: DataType):
    return skMetrics.r2_score(convert_to_array(yTrue, 'float'), convert_to_array(yPred, 'float'))