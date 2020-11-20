# Imports ----------------------------------------------------------------------

from  import listOf
from adapters.simpleml.l3s.dataset import loadDataset
from simpleml.lang import println
from simpleml.model.regression import DecisionTree

# Workflow steps ---------------------------------------------------------------

def loadAdacSample(nInstances):
    adacAugustSample = loadDataset('ADACAugust').sample(nInstances=nInstances)
    features = adacAugustSample.keepAttributes('season', 'daylight', 'day_type', 'street_type')
    target = adacAugustSample.keepAttributes('average_speed')
    return features, target

# Workflows --------------------------------------------------------------------

def predictSpeed():
    features, target = loadAdacSample(nInstances=1000)
    model = DecisionTree(maxDepth=3)
    trained_model = model.fit(features, target)
    predictionFeatures = listOf(listOf('summer', 'Daylight', 'Sun', 'motorway'))
    predictedTargets = trained_model.predict(features=predictionFeatures)
    println(predictedTargets)
