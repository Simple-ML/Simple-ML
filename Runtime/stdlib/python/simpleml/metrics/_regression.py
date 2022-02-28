from simpleml.model.supervised._domain import DataType

import sklearn.metrics as skMetrics

def meanAbsoluteError(y_true: DataType, y_pred: DataType):
    return skMetrics.mean_absolute_error(y_true.toArray(), y_pred.toArray())

def meanSquaredError(y_true: DataType, y_pred: DataType):
    return skMetrics.mean_squared_error(y_true.toArray(), y_pred.toArray())

def meanSquaredLogError(y_true: DataType, y_pred: DataType):
    return skMetrics.mean_squared_log_error(y_true.toArray(), y_pred.toArray())

def medianAbsoluteError(y_true: DataType, y_pred: DataType):
    return skMetrics.median_absolute_error(y_true.toArray(), y_pred.toArray())

def r2(y_true: DataType, y_pred: DataType):
    return skMetrics.r2_score(y_true.toArray(), y_pred.toArray())