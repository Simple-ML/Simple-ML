from simpleml.model.supervised._domain import DataType

import sklearn.metrics as skMetrics

def accuracy(yTrue: DataType, yPred: DataType):
    return skMetrics.accuracy_score(yTrue.toArray(), yPred.toArray())

def balancedAccuracy(yTrue: DataType, yPred: DataType):
    return skMetrics.balanced_accuracy_score(yTrue.toArray(), yPred.toArray())

def averagePrecision(yTrue: DataType, yPred: DataType):
    return skMetrics.average_precision_score(yTrue.toArray(), yPred.toArray())

def precision(yTrue: DataType, yPred: DataType):
    return skMetrics.precision_score(yTrue.toArray(), yPred.toArray())

def recall(yTrue: DataType, yPred: DataType):
    return skMetrics.recall_score(yTrue.toArray(), yPred.toArray())