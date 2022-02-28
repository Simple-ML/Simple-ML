from simpleml.model.supervised._domain import DataType

import sklearn.metrics as skMetrics

def meanAbsoluteError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_absolute_error(yTrue.toArray(), yPred.toArray())

def meanSquaredError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_squared_error(yTrue.toArray(), yPred.toArray())

def meanSquaredLogError(yTrue: DataType, yPred: DataType):
    return skMetrics.mean_squared_log_error(yTrue.toArray(), yPred.toArray())

def medianAbsoluteError(yTrue: DataType, yPred: DataType):
    return skMetrics.median_absolute_error(yTrue.toArray(), yPred.toArray())

def r2(yTrue: DataType, yPred: DataType):
    return skMetrics.r2_score(yTrue.toArray(), yPred.toArray())