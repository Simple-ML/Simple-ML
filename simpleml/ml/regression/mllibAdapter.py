import pyspark.ml.regression as regression


# Linear Models


def LinearRegression():
    return regression.LinearRegression(
        regParam=0.0
    )


def LassoRegression(config):
    return regression.LinearRegression(
        regParam=config["regularizationStrength"],
        elasticNetParam=1.0
    )


def RidgeRegression(config):
    return regression.LinearRegression(
        regParam=config["regularizationStrength"],
        elasticNetParam=0.0
    )


def ElasticNetRegression(config):
    return regression.LinearRegression(
        regParam=config["regularizationStrength"],
        elasticNetParam=config["lassoRatio"]
    )
