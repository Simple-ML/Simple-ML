# Package `simpleml.dataset`

[Tutorial][tutorial] - [Idea and basic concepts][tutorial_concepts] | [Interface][tutorial_interface] | [**API**][api] | [DSL][dsl-tutorial]

[tutorial]: ../../Tutorial.md
[tutorial_concepts]: ../../Tutorial-Basic-Concepts.md
[tutorial_interface]: ../../Tutorial-The-Simple-ML-Interface.md
[api]: ./README.md
[dsl-tutorial]: ../../DSL/tutorial/README.md


## Table of Contents

* Classes
  * [`Dataset`](#class-Dataset)
  * [`Instance`](#class-Instance)
  * [`StandardNormalizer`](#class-StandardNormalizer)
  * [`StandardScaler`](#class-StandardScaler)
* Global functions
  * [`joinTwoDatasets`](#global-function-joinTwoDatasets)
  * [`loadDataset`](#global-function-loadDataset)
  * [`readDataSetFromCSV`](#global-function-readDataSetFromCSV)

----------

<a name='class-Dataset'/>

## Class `Dataset`
A dataset with its data instances (e.g., rows and columns)

**Constructor:** _Class has no constructor._

### `addAttribute` (Instance Method )
Add a new attribute to the dataset with values according to a transformation function

**Parameters:**
* `columnName: String` - The name of the new attribute
* `transformFunc: (instance: Instance) -> value: Any` - The transformation function.
* `newColumnLabel: String` - _No description available._

**Results:**
* `dataset: Dataset` - The updated dataset

### `addDayOfTheYearAttribute` (Instance Method )
Add a new attribute to the dataset specifying the day of the year of the specified column

**Parameters:**
* `columnName: String` - The attribute to be transformed

**Results:**
* `dataset: Dataset` - The updated dataset

### `addIsWeekendAttribute` (Instance Method )
Add a new attribute to the dataset specifying if the dates of the specified column are on the weekend or not

**Parameters:**
* `columnName: String` - The attribute to be transformed

**Results:**
* `dataset: Dataset` - The updated dataset

### `addWeekDayAttribute` (Instance Method )
Extract week day from given date attribute and add new attribute in dataset with weekday name

**Parameters:**
* `columnName: String` - The attribute to be transformed

**Results:**
* `dataset: Dataset` - The updated dataset

### `dropAttribute` (Instance Method )
Drop a single attribute from a dataset

**Parameters:**
* `attribute: String` - The attribute to drop from the dataset

**Results:**
* `dataset: Dataset` - The updated dataset

### `dropAttributes` (Instance Method )
Drop attributes from a dataset

**Parameters:**
* `vararg attributes: String` - The list of attributes to drop from the dataset

**Results:**
* `dataset: Dataset` - The updated dataset

### `dropMissingValues` (Instance Method )
Drops instances with missing values in the specified attribute

**Parameters:**
* `attribute: String` - Attribute whose empty values are dropped

**Results:**
* `dataset: Dataset` - The updated dataset

### `exportDataAsFile` (Instance Method )
Export any dataset to CSV file

**Parameters:**
* `filePath: String` - _No description available._

**Results:** _None returned._

### `filterInstances` (Instance Method )
Remove instances in a dataset according to a filter function

**Parameters:**
* `filterFunc: (instance: Instance) -> shouldKeep: Boolean` - The filter function that returns either True (keep) or False (remove) for each instance

**Results:**
* `dataset: Dataset` - The updated dataset

### `getRow` (Instance Method )
Get a specific row of a dataset

**Parameters:**
* `rowNumber: Int` - The number of the row to be retreived

**Results:**
* `instance: Instance` - The specified row

### `keepAttribute` (Instance Method )
Retain a single attribute of a dataset

**Parameters:**
* `attribute: String` - The attribute to retain in the dataset

**Results:**
* `dataset: Dataset` - The updated dataset

### `keepAttributes` (Instance Method )
Retain attributes of a dataset

**Parameters:**
* `vararg attributes: String` - The list of attributes to retain in the dataset

**Results:**
* `dataset: Dataset` - The updated dataset

### `sample` (Instance Method )
Create a sample of a dataset

**Parameters:**
* `nInstances: Int` - Number of instances in the sample

**Results:**
* `sample: Dataset` - The sampled dataset

### `setTargetAttribute` (Instance Method )
Set the specified attribute as prediction target

**Parameters:**
* `targetAttribute: String` - The attribute to be predicted later on

**Results:**
* `dataset: Dataset` - The updated dataset

### `splitIntoTrainAndTest` (Instance Method )
Split a dataset in a train and a test dataset

**Parameters:**
* `trainRatio: Float` - The percentage of instances to keep in the training dataset
* `randomState: Int? = null` - A random seed to use for splitting

**Results:**
* `train: Dataset` - The training dataset
* `test: Dataset` - The test dataset

### `splitIntoTrainAndTestAndLabels` (Instance Method )
Split a dataset into four datasets: train/test and labels/features. Requires that a target attribute has been set before via setTargetAttribute()

**Parameters:**
* `trainRatio: Float` - The percentage of instances to keep in the training dataset
* `randomState: Int? = null` - A random seed to use for splitting

**Results:**
* `xTrain: Dataset` - Features of the training dataset
* `xTest: Dataset` - Features of the test dataset
* `yTrain: Dataset` - Labels of the training dataset
* `yTest: Dataset` - Labels of the test dataset

### `transformDatatypes` (Instance Method )
Convert all column values into numbers

**Parameters:** _None expected._

**Results:**
* `dataset: Dataset` - The updated dataset

### `transformDateToTimestamp` (Instance Method )
Convert date column values into timestamps

**Parameters:**
* `columnName: String` - The attribute to be transformed

**Results:**
* `dataset: Dataset` - The updated dataset


----------

<a name='class-Instance'/>

## Class `Instance`
A single instance (e.g., row) of a dataset

**Constructor:** _Class has no constructor._

### `getValue` (Instance Method )
Return a specific value of the instance

**Parameters:**
* `attribute: String` - The attribute whose value is returned

**Results:**
* `value: Any` - The specified value


----------

<a name='class-StandardNormalizer'/>

## Class `StandardNormalizer`
A normalizer to normalize dataset values

**Constructor parameters:** _None expected._

### `normalize` (Instance Method )
Normalize all numeric values in the dataset

**Parameters:**
* `dataset: Dataset` - Dataset to be normalized

**Results:**
* `normalizedDataset: Dataset` - The normalized dataset


----------

<a name='class-StandardScaler'/>

## Class `StandardScaler`
A scaler to scale dataset values

**Constructor parameters:** _None expected._

### `scale` (Instance Method )
Scale all numeric values in the dataset

**Parameters:**
* `dataset: Dataset` - Dataset to be scaled

**Results:**
* `scaledDataset: Dataset` - The scaled dataset


## Global Functions

<a name='global-function-joinTwoDatasets'/>

## Global Function `joinTwoDatasets`
Join two datasets into one dataset

**Parameters:**
* `firstData: Dataset` - The first dataset
* `secondData: Dataset` - The second dataset
* `joinColumnName1: String` - The attribute of the first dataset to use for the join
* `joinColumnName2: String` - The attribute of the second dataset to use for the join
* `firstSuffix: String` - The suffix to be attached to the attribute names of the first dataset
* `secondSuffix: String` - The suffix to be attached to the attribute names of the second dataset

**Results:**
* `dataset: Dataset` - The joined dataset

<a name='global-function-loadDataset'/>

## Global Function `loadDataset`
Load a dataset via its identifier

**Parameters:**
* `datasetID: String` - Identifier of the dataset

**Results:**
* `dataset: Dataset` - The loaded dataset

<a name='global-function-readDataSetFromCSV'/>

## Global Function `readDataSetFromCSV`
Load a dataset from a CSV file

**Parameters:**
* `fileName: String` - Path and name of the CSV file
* `datasetId: String` - Identifier of the dataset
* `separator: String` - Separator used in the file
* `hasHeader: String` - True, if the file has a header row
* `nullValue: String` - String that should be parsed as missing value
* `datasetName: String` - Name of the dataset
* `coordinateSystem: Int = 3857` - Coordinate system used in the geometry columns of the dataset

**Results:**
* `dataset: Dataset` - The loaded dataset

----------

**This file was created automatically. Do not change it manually!**
