# Package `simpleml.model.supervised.regression`

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
Functionalities to train a decision tree regression model.

**Constructor parameters:**
* `maxDepth: Int? = null` - _No description available._

**Attributes:**
* `attr maxDepth: Int?` - _No description available._

### `fit` (Instance Method )
Train the model given a dataset of features and a dataset of labels

**Parameters:**
* `features: Dataset` - A dataset consisting of features for training the model.
* `target: Dataset` - A dataset consisting of one column with the target values.

**Results:**
* `trainedModel: DecisionTreeRegressorModel` - A trained decision tree regression model.


----------

<a name='class-DecisionTreeRegressorModel'/>

## Class `DecisionTreeRegressorModel`
A trained decision tree regression model.

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
Predict values given a dataset of features

**Parameters:**
* `features: Dataset` - A dataset consisting of features for prediction.

**Results:**
* `results: Dataset` - A dataset consisting of the predicted values.


----------

<a name='class-LinearRegression'/>

## Class `LinearRegression`
Functionalities to train a linear regression model.

**Constructor parameters:** _None expected._

### `fit` (Instance Method )
Train the model given a dataset of features and a dataset of labels

**Parameters:**
* `features: Dataset` - A dataset consisting of features for training the model.
* `target: Dataset` - A dataset consisting of one column with the target values.

**Results:**
* `trainedModel: LinearRegressionModel` - A trained linear regression model.


----------

<a name='class-LinearRegressionModel'/>

## Class `LinearRegressionModel`
A trained linear regression model.

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
Predict values given a dataset of features

**Parameters:**
* `features: Dataset` - A dataset consisting of features for prediction.

**Results:**
* `results: Dataset` - A dataset consisting of the predicted values.


----------

<a name='class-RandomForestRegressor'/>

## Class `RandomForestRegressor`
Functionalities to train a random forest regression model.

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
Train the model given a dataset of features and a dataset of labels

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RandomForestRegressorModel` - _No description available._


----------

<a name='class-RandomForestRegressorModel'/>

## Class `RandomForestRegressorModel`
A trained random forest regression model.

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
Predict values given a dataset of features

**Parameters:**
* `features: Dataset` - A dataset consisting of features for prediction.

**Results:**
* `results: Dataset` - A dataset consisting of the predicted values.


----------

<a name='class-RidgeRegression'/>

## Class `RidgeRegression`
Functionalities to train a ridge regression model.

**Constructor parameters:**
* `regularizationStrength: Float = 0.5` - _No description available._

**Attributes:**
* `attr regularizationStrength: Float` - _No description available._

### `fit` (Instance Method )
Train the model given a dataset of features and a dataset of labels

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RidgeRegressionModel` - _No description available._


----------

<a name='class-RidgeRegressionModel'/>

## Class `RidgeRegressionModel`
A trained ridge regression model.

**Constructor parameters:** _None expected._

### `predict` (Instance Method )
Predict values given a dataset of features

**Parameters:**
* `features: Dataset` - A dataset consisting of features for prediction.

**Results:**
* `results: Dataset` - A dataset consisting of the predicted values.


----------

**This file was created automatically. Do not change it manually!**
