# TODO we only want to define the default config in one place. When we call these methods the config should already be
#  the way it should be. Then the adapters use those values to call the methods of the framework.
#  ***Default values should be specified in the meta-model and setters for the defaults should be generated.***
#  The compile should insert the override_defaults calls as needed (should not be part of the function)
#  The functions expect the config to be complete.

lassoRegressionDefaults = {
    "regularizationStrength": 1.0
}

ridgeRegressionDefaults = {
    "regularizationStrength": 1.0
}

elasticNetRegressionDefaults = {
    "regularizationStrength": 1.0,
    "lassoRatio": 0.5
}
