# Classes and their Methods

### Dataset

A dataset with its data instances (e.g., rows and columns).

#### Sampling

Creates a sample of a dataset.

```
sample(nInstances: Int) -> dataset: Dataset
```

* Input
  * `nInstances`: Number of instances in the sample.
* Output
  * `dataset`: The sampled dataset.


#### Keep Attributes 

Retains attributes of a dataset.

```
keepAttributes(vararg attributes: String) -> dataset: Dataset
```

* Input
  * `attributes`: The list of attributes to retain in the dataset.
* Output
  * `dataset`: The updated dataset.

#### Keep Attribute

Retains a single attribute of a dataset.

```
keepAttribute(attribute: String) -> dataset: Dataset
```

* Input
  * `attribute`: The attribute to retain in the dataset.
* Output
  * `dataset`: The updated dataset.


#### Drop Attributes 

Drops attributes of a dataset.

```
dropAttributes(vararg attributes: String) -> dataset: Dataset
```

* Input
  * `attributes`: The list of attributes to drop from the dataset.
* Output
  * `dataset`: The updated dataset.

#### Drop Attribute

Drops a single attribute of a dataset.

```
dropAttribute(attribute: String) -> dataset: Dataset
```

* Input
  * `attribute`: The attribute to drop from the dataset.
* Output
  * `dataset`: The updated dataset.

#### Drop Attributes 

Splits a dataset in a train and a test dataset.

```
splitIntoTrainAndTest(trainRatio: Float, randomState: Int? = null) -> (dataset1: Dataset, dataset2: Dataset)
```

* Input
  * `trainRatio`: The percentage of instances to keep in the training dataset.
  * `randomState` (optional): A random seed to use for splitting.
* Output
  * `dataset1`: The training dataset.
  * `dataset2`: The test dataset.


#### Drop Attributes 

Splits dataset into four datasets: train/test and labels/features. Requires that a target attribute has been set before via `setTargetAttribute()`.

```
 splitIntoTrainAndTestAndLabels(trainRatio: Float, randomState: Int? = null) -> (xTrain: Dataset, xTest: Dataset, yTrain: Dataset, yTest: Dataset)```

* Input
  * `trainRatio`: The percentage of instances to keep in the training dataset.
  * `randomState` (optional): A random seed to use for splitting.
* Output
  * `xTrain`: Features of the training dataset.
  * `xTest`: Features of the test dataset.
  * `yTrain`: Labels of the training dataset.
  * `yTest`: Labels of the test dataset.
