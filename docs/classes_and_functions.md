[Tutorial][tutorial] - [Idea and basic concepts][tutorial_concepts] | [Interface][tutorial_interface] | Functions | [DSL][dsl-tutorial]


[tutorial_concepts]: ./Tutorial-Basic-Concepts.md
[tutorial_interface]: ./Tutorial-The-Simple-ML-Interface.md
[functions]: ./classes_and_functions.md
[dsl-tutorial]: ./DSL/tutorial/README.md
[tutorial]: ./Tutorial.md

This page gives an overview of the classes and functions required to create a data processing and machine learning workflow.

# Overview

* [Class: Dataset](#class-dataset)  
* [Class: Instance](#class-instance)  
* [Class: StandardNormalizer](#class-standardnormalizer)  
* [Class: StandardScaler](#class-standardscaler)  
* [Class: Regression Models](#classes-regression)  
* [Class: Classification Models](#classes-classification)  
* [Global Functions](#global-functions)  

## Classes and their Methods

<a name="class-dataset"/>

### Class: Dataset

A dataset with its data instances (e.g., rows and columns).

---

#### Sampling

Create a sample of a dataset.

```
sample(nInstances: Int) -> dataset: Dataset
```

* Input
  * `nInstances`: Number of instances in the sample.
* Output
  * `dataset`: The sampled dataset.

---

#### Keep Attributes 

Retain attributes of a dataset.

```
keepAttributes(vararg attributes: String) -> dataset: Dataset
```

* Input
  * `attributes`: The list of attributes to retain in the dataset.
* Output
  * `dataset`: The updated dataset.

---

#### Keep Attribute

Retain a single attribute of a dataset.

```
keepAttribute(attribute: String) -> dataset: Dataset
```

* Input
  * `attribute`: The attribute to retain in the dataset.
* Output
  * `dataset`: The updated dataset.

---


#### Drop Attributes 

Drop attributes of a dataset.

```
dropAttributes(vararg attributes: String) -> dataset: Dataset
```

* Input
  * `attributes`: The list of attributes to drop from the dataset.
* Output
  * `dataset`: The updated dataset.
 
---

#### Drop Attribute

Drop a single attribute of a dataset.

```
dropAttribute(attribute: String) -> dataset: Dataset
```

* Input
  * `attribute`: The attribute to drop from the dataset.
* Output
  * `dataset`: The updated dataset.

---

#### Set Target Attribute

Set the specified attribute as prediction target.

```
setTargetAttribute(targetAttribute: String) -> dataset: Dataset
```

* Input
  * `targetAttribute`: The name of the attribute to be predicted later on.
* Output
  * `dataset`: The updated dataset.

---

#### Split into Train and Test 

Split a dataset in a train and a test dataset.

```
splitIntoTrainAndTest(trainRatio: Float, randomState: Int? = null) -> (train: Dataset, test: Dataset)
```

* Input
  * `trainRatio`: The percentage of instances to keep in the training dataset.
  * `randomState` (optional): A random seed to use for splitting.
* Output
  * `train`: The training dataset.
  * `test`: The test dataset.

---

#### Split into Train, Test, Features and Labels

Split a dataset into four datasets: train/test and labels/features. Requires that a target attribute has been set before via `setTargetAttribute()`.

```
 splitIntoTrainAndTestAndLabels(trainRatio: Float, randomState: Int? = null) -> (xTrain: Dataset, xTest: Dataset, yTrain: Dataset, yTest: Dataset)```
```

* Input
  * `trainRatio`: The percentage of instances to keep in the training dataset.
  * `randomState` (optional): A random seed to use for splitting.
* Output
  * `xTrain`: Features of the training dataset.
  * `xTest`: Features of the test dataset.
  * `yTrain`: Labels of the training dataset.
  * `yTest`: Labels of the test dataset.

---

#### Filter Instances

Remove instances in a dataset according to a filter function.

```
filterInstances(filterFunc: (instance: Instance) -> shouldKeep: Boolean) -> dataset: Dataset

```

* Input
  * `filterFunc`: The filter function that returns either `True` (keep) or `False` (remove) for each instance.
* Output
  * `dataset`: The updated dataset.

---

#### Get Row

Get a specific row of a dataset.

```
getRow(rowNumber: Int) -> instance: Instance
```

* Input
  * `rowNumber`: The number of the row to be retreived.
* Output
  * `instance`: The specified row.

---

#### Add Attribute

Add a new attribute to the dataset with values according to a transformation function.

```
addAttribute(columnName: String, transformFunc: (instance: Instance) -> value: Any, newColumnLabel: String) -> dataset: Dataset

```

* Input
  * `columnName`: The name of the attribute where the transformation function is applied on.
  * `transformFunc`: The transformation function.
  * `newColumnLabel` (optional): The name of the new attribute.

* Output
  * `dataset`: The updated dataset.

---

#### Date Transformation

Convert date column values into timestamps.

```
transformDateToTimestamp(columnName: String) -> dataset: Dataset
```

* Input
  * `columnName`: The name of the attribute to be transformed.
* Output
  * `dataset`: The updated dataset.

---

#### Datatype Transformations

Convert all column values into numbers.

```
transformDatatypes() -> dataset: Dataset
```

* Output
  * `dataset`: The updated dataset.

---

#### Add "is weekend" Attribute

Add a new attribute to the dataset specifying if the dates of the specified column are on the weekend or not.

```
addIsWeekendAttribute(columnName: String) -> dataset: Dataset
```

* Input
  * `columnName`: The name of the date attribute.
* Output
  * `dataset`: The updated dataset.

---

#### Add "day of the year" Attribute

Add a new attribute to the dataset specifying the day of the year of the specified column.

```
addDayOfTheYearAttribute(columnName: String) -> dataset: Dataset
```

* Input
  * `columnName`: The name of the date attribute.
* Output
  * `dataset`: The updated dataset.

---

#### Add "week day" Attribute

Add a new attribute to the dataset specifying the day of the week (string) of the specified column.

```
addWeekDayAttribute(columnName: String) -> dataset: Dataset
```

* Input
  * `columnName`: The name of the date attribute.
* Output
  * `dataset`: The updated dataset.


---

#### Date to File Export

Stores the dataset into a CSV file.

```
exportDataAsFile(filePath: String)
```

* Input
  * `filePath`: The path and name of the file to be created.

---

<a name="class-instance"/>

### Instance

A single instance (e.g., row) of a dataset.

---

#### Get Value

Return a specific value of the instance.

```
getValue(attribute: String) -> value: Any
```

* Input
  * `nInstances`: Number of instances in the sample.
* Output
  * `value`: The specified value.

---

<a name="class-standardnormalizer"/>

### Class: StandardNormalizer

A normalizer to normalize dataset values.

---

#### Normalize

Normalize all numeric values in the dataset.

```
normalize(dataset: Dataset) -> normalizedDataset: Dataset
```

* Input
  * `dataset`: Dataset to be normalized.
* Output
  * `normalizedDataset`: The normalized dataset.

---

<a name="class-standardscaler"/>

### Class: StandardScaler

A normalizer to normalize dataset values.

---

#### Scaling

Scale all numeric values in the dataset.

```
scale(dataset: Dataset) -> scaledDataset: Dataset
```

* Input
  * `dataset`: Dataset to be scaled.
* Output
  * `scaledDataset`: The scaled dataset.

---

<a name="classes-regression"/>

### Classes: Regression Models

---

#### Class: LinearRegression

A linear regression.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: LinearRegressionModel

A trained linear regression model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.

---

#### Class: DecisionTreeRegressor

A decision tree regressor.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: DecisionTreeRegressorModel

A decision tree regression model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.

---

#### Class: RandomForestRegressor

A random forest regressor.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: RandomForestRegressorModel

A trained random forest regression model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.

---

#### Class: RidgeRegression

A ridge regression.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: RidgeRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: RidgeRegressionModel

A trained ridge regression model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.


---

<a name="classes-classification"/>

### Classes: Classification Models

---

#### Class: DecisionTreeClassifier

A decision tree classifier.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: DecisionTreeClassifierModel

A trained decision tree classification model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.

---

#### Class: RandomForestClassifier

A random forest classifier.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: RandomForestClassifierModel

A random forest classification model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.
---

#### Class: SupportVectorMachineClassifier

An SVM classifier.

---

##### Training

Train the model.

```
fit(features: Dataset, target: Dataset) -> trainedModel: LinearRegressionModel
```

* Input
  * `features`: The features of the training dataset.
  * `target`: The labels of the training dataset.
* Output
  * `trainedModel`: The trained model.

---

#### Class: SupportVectorMachineClassifierModel

An SVM classification model.

---

##### Prediction

Use the trained model for a prediction.

```
predict(features: Dataset) -> results: Dataset
```

* Input
  * `features`: The features of the dataset.
* Output
  * `trainedModel`: The dataset with predictions.

---

<a name="global-functions"/>

## Global Functions

---

### Global Functions (Datasets)

---

#### Load Dataset

Loads a dataset via its identifier.

```
loadDataset(datasetID: String) -> dataset: Dataset
```

* Input
  * `datasetID`: Identifier of the dataset.
* Output
  * `scaledDataset`: The loaded dataset.

---

#### Load Dataset from CSV

Loads a dataset from a CSV file.

```
readDataSetFromCSV(fileName: String, datasetId: String, separator: String, hasHeader: String, nullValue: String, datasetName: String, coordinateSystem: Int = 3857) -> dataset: Dataset
 ```

* Input
  * `fileName`: Path and name of the CSV file.
  * `datasetId`: Identifier of the dataset.
  * `separator`: Separator used in the file.
  * `hasHeader`: `True`, if the file has a header row.
  * `nullValue`: String that should be parsed as missing value.
  * `datasetName`: Name of the dataset.
  * `coordinateSystem` (optional): Coordinate system used in the geometry columns of the dataset.

* Output
  * `scaledDataset`: The loaded dataset.

---

#### Join Datasets

Join two datasets into one dataset.

```
fun joinTwoDatasets(firstData: Dataset, secondData: Dataset, joinColumnName1: String, joinColumnName2: String, firstSuffix: String, secondSuffix: String) -> dataset: Dataset
```
 

* Input
  * `firstData`: The first dataset.
  * `secondData`: The second dataset.
  * `joinColumnName1`: The attribute of the first dataset to use for the join.
  * `joinColumnName2`: The attribute of the second dataset to use for the join.
  * `firstSuffix`: The suffix to be attached to the attribute names of the first dataset.
  * `secondSuffix`: The suffix to be attached to the attribute names of the second dataset.

* Output
  * `scaledDataset`: The joined dataset.


---

### Global Functions (Classification Metrics)

---

#### Accuracy

Compute the accuracy.

```
accuracy(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The accuracy value.

---

#### Balanced Accuracy

Compute the balanced accuracy.

```
balancedAccuracy(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The balanced accuracy value.

---

#### Balanced Accuracy

Compute the balanced accuracy.

```
averagePrecision(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The balanced accuracy value.

---

#### Precision

Compute the precision.

```
precision(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The precision value.

---

#### Recall

Compute the recall.

```
recall(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The recall value.

---

### Global Functions (Regression Metrics)

---

#### Mean Absolute Error

Compute the mean absolute error (MAE).

```
meanAbsoluteError(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The mean absolute error value.

---

#### Mean Absolute Error

Compute the mean squared error (MSE).

```
meanSquaredError(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The mean squared error value.

---

#### Mean Absolute Error

Compute the mean squared log error.

```
meanSquaredLogError(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The mean squared log error value.

---

#### Median Absolute Error

Compute the median absolute error.

```
medianAbsoluteError(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The median absolute error value.

---

#### R^2

Compute the R^2 score.

```
r2(yTrue: Dataset, yPred: Dataset) -> score: Float
```

* Input
  * `yTrue`: The test dataset labels.
  * `yPred`: The predicted dataset labels.
* Output
  * `score`: The R^2 value.
