# Package `simpleml.model.regression`

[Tutorial][tutorial] - [Idea and basic concepts][tutorial_concepts] | [Interface][tutorial_interface] | [**API**][api] | [DSL][dsl-tutorial]

[tutorial]: ../../Tutorial.md
[tutorial_concepts]: ../../Tutorial-Basic-Concepts.md
[tutorial_interface]: ../../Tutorial-The-Simple-ML-Interface.md
[api]: ./README.md
[dsl-tutorial]: ../../DSL/tutorial/README.md


## Table of Contents

* Classes
  * [`DecisionTreeRegressor`](#class-DecisionTreeRegressor)
  * [`DecisionTreeRegressorModel`](#class-DecisionTreeRegressorModel)
  * [`LinearRegression`](#class-LinearRegression)
  * [`LinearRegressionModel`](#class-LinearRegressionModel)
  * [`RandomForestRegressor`](#class-RandomForestRegressor)
  * [`RandomForestRegressorModel`](#class-RandomForestRegressorModel)
  * [`RidgeRegression`](#class-RidgeRegression)
  * [`RidgeRegressionModel`](#class-RidgeRegressionModel)

----------

<a name='class-DecisionTreeRegressor'/>

## Class `DecisionTreeRegressor`
_No description available._

**Constructor parameters:**
* `maxDepth: Int? = null` - _No description available._

**Attributes:**
* `attr maxDepth: Int?` - _No description available._

### `fit` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: DecisionTreeRegressorModel` - _No description available._


----------

<a name='class-DecisionTreeRegressorModel'/>

## Class `DecisionTreeRegressorModel`
_No description available._

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

<a name='class-LinearRegression'/>

## Class `LinearRegression`
_No description available._

**Constructor parameters:** _None expected._

### `fit` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: LinearRegressionModel` - _No description available._


----------

<a name='class-LinearRegressionModel'/>

## Class `LinearRegressionModel`
_No description available._

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

<a name='class-RandomForestRegressor'/>

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

### `fit` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RandomForestRegressorModel` - _No description available._


----------

<a name='class-RandomForestRegressorModel'/>

## Class `RandomForestRegressorModel`
_No description available._

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

<a name='class-RidgeRegression'/>

## Class `RidgeRegression`
_No description available._

**Constructor parameters:**
* `regularizationStrength: Float = 0.5` - _No description available._

**Attributes:**
* `attr regularizationStrength: Float` - _No description available._

### `fit` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RidgeRegressionModel` - _No description available._


----------

<a name='class-RidgeRegressionModel'/>

## Class `RidgeRegressionModel`
_No description available._

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

**This file was created automatically. Do not change it manually!**
