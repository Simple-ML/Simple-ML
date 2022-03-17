# Package `simpleml.model.classification`

## Table of Contents

* [Class `DecisionTreeClassifier`](#class-DecisionTreeClassifier)
* [Class `DecisionTreeClassifierModel`](#class-DecisionTreeClassifierModel)
* [Class `RandomForestClassifier`](#class-RandomForestClassifier)
* [Class `RandomForestClassifierModel`](#class-RandomForestClassifierModel)
* [Class `SupportVectorMachineClassifier`](#class-SupportVectorMachineClassifier)
* [Class `SupportVectorMachineClassifierModel`](#class-SupportVectorMachineClassifierModel)

----------

## Class `DecisionTreeClassifier`
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
* `trainedModel: DecisionTreeClassifierModel` - _No description available._


----------

## Class `DecisionTreeClassifierModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

## Class `RandomForestClassifier`
_No description available._

**Constructor parameters:**
* `nEstimator: Int = 100` - _No description available._
* `criterion: String = "gini"` - _No description available._
* `maxDepth: Int? = null` - _No description available._
* `randomState: Int? = null` - _No description available._

**Attributes:**
* `attr criterion: String` - _No description available._
* `attr maxDepth: Int?` - _No description available._
* `attr nEstimator: Int` - _No description available._
* `attr randomState: Int?` - _No description available._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: RandomForestClassifierModel` - _No description available._


----------

## Class `RandomForestClassifierModel`
_No description available._

**Constructor parameters:** _None expected._

### Instance Method `predict`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._

**Results:**
* `results: Dataset` - _No description available._


----------

## Class `SupportVectorMachineClassifier`
_No description available._

**Constructor parameters:**
* `penalty: String = "l2"` - _No description available._
* `loss: String = "squared_hinge"` - _No description available._
* `dual: Boolean = true` - _No description available._
* `tol: Float = 1e-4` - _No description available._
* `c: Float = 1.0` - _No description available._
* `multiClass: String = "ovr"` - _No description available._

**Attributes:**
* `attr c: Float` - _No description available._
* `attr dual: Boolean` - _No description available._
* `attr loss: String` - _No description available._
* `attr multiClass: String` - _No description available._
* `attr penalty: String` - _No description available._
* `attr tol: Float` - _No description available._

### Instance Method `fit`
_No description available._

**Parameters:**
* `features: Dataset` - _No description available._
* `target: Dataset` - _No description available._

**Results:**
* `trainedModel: SupportVectorMachineClassifierModel` - _No description available._


----------

## Class `SupportVectorMachineClassifierModel`
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
