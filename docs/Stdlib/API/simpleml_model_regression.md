# Package `simpleml.model.regression`

## Table of Contents

* [Class `DecisionTreeRegressor`](#class-DecisionTreeRegressor)
* [Class `DecisionTreeRegressorModel`](#class-DecisionTreeRegressorModel)
* [Class `LinearRegression`](#class-LinearRegression)
* [Class `LinearRegressionModel`](#class-LinearRegressionModel)
* [Class `RandomForestRegressor`](#class-RandomForestRegressor)
* [Class `RandomForestRegressorModel`](#class-RandomForestRegressorModel)
* [Class `RidgeRegression`](#class-RidgeRegression)
* [Class `RidgeRegressionModel`](#class-RidgeRegressionModel)

----------

## Class `DecisionTreeRegressor`
_No description available._

**Constructor parameters:**
* `maxDepth: Int? = null` - _No description available._

**Attributes:**
* `attr maxDepth: Int?` - _No description available._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: DecisionTreeRegressorModel` - _No description available._


----------

## Class `DecisionTreeRegressorModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

## Class `LinearRegression`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: LinearRegressionModel` - _No description available._


----------

## Class `LinearRegressionModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

## Class `RandomForestRegressor`
_No description available._

**Constructor parameters:**
* `nEstimator: Int = 100` - _No description available._
* `criterion: String = "mse"` - _No description available._
* `maxDepth: Int? = null` - _No description available._
* `randomState: Int? = null` - _No description available._

**Attributes:**
* `attr criterion: String?` - _No description available._
* `attr maxDepth: Int?` - _No description available._
* `attr nEstimator: Int?` - _No description available._
* `attr randomState: Int?` - _No description available._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RandomForestRegressorModel` - _No description available._


----------

## Class `RandomForestRegressorModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

## Class `RidgeRegression`
_No description available._

**Constructor parameters:**
* `regularizationStrength: Float = 0.5` - _No description available._

**Attributes:**
* `attr regularizationStrength: Float` - _No description available._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RidgeRegressionModel` - _No description available._


----------

## Class `RidgeRegressionModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

**This file was created automatically. Do not change it manually!**
