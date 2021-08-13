# Description

This API provides all methods regarding the loading and pre-processing of data sets. This includes loading a selected dataset file, sampling, feature selection, train/test split and finally generating required statistics for data set profiling. The pre-processed dataset can be accessed anywhere in the application for further processing of the machine learning algorithms.

# Access/Usage

* [Code](../Stdlib/python/simpleml/dataset)

The data set methods are applied on instance of the `Dataset` class that can be initiated using the `loadDataset` method which internally calls the [data catalog API](./Data-Catalog-API.md).
Using the DSL, these methods will be accessible after configuration of the [respective stubs](../Stdlib/stubs/simpleml/dataset/dataset.stub.simpleml).

# Methods

### data set loading

`loadDataset(datasetID: str) -> Dataset`

This function is loading the data sets meta data given the data set's unique identifier in the data catalog. The data set's data itself is only read when it is first accessed (e.g., as part of the `sample` method).

### data set rReading

`readFile() -> Dataset`

This function is reading the actual data of the data set. Currently, one CSV file per data set is supported. The file name is given by the data catalog.

### data set sampling

`sample(nInstances: int) -> Dataset`

Takes a random sample with `nInstances` from the dataset.

### feature selection 

`keepAttributes(attributeIDs: Any) -> Dataset`

Selects features from the data set as per the list of attribute identifiers provided as parameter.

### train/test split 

`splitIntoTrainAndTest(trainRatio: float) -> Dataset`

This function splits data into train and test with the given training ratio as a parameter.

### generating statistics

`getStatistics() -> dict`

Generation of metadata, i.e., statistics for numeric, string and geometry columns. 
The Statistics are number of null values, number of valid values, median, histogram (10% bucket/interval size), value distributions, mean, number of values, number of valid non-null values, minimum, maximum, standard deviation, number of invalid values, deciles, quantiles and outliers
