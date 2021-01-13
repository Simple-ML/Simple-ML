# Description

[Code](https://github.com/Simple-ML/RuntimeData/tree/main/api/simpleml/dataset)

Main implementation of this API is regarding loading selected dataset file, sampling, feature selection, train/test split and finally generating required statistics from data. This dataset can be accessible anywhere in application for further processing of machine learning algorithms.

# Access/Usage

(url...)

# API calls

### Load Dataset

loadDataset(datasetID: str) -> Dataset:

This function is loading relevant data.

### Reading File  

readFile(self):

Currently we are reading from a CSV file. So this function read csv file in to pandas dataframe.

### Dataset Sampling

`sample(nInstances: int) -> Dataset`

Takes a random sample with `nInstances` from the dataset.

### Feature Selection 

keepAttributes(attributeIDs: Any) -> Dataset:

Selects features from dataset as per list provided as parameter.

### Train Test Split 

splitIntoTrainAndTest(trainRatio: float) -> Dataset:

This function is splitting data into train and test with given ratio as parameter.

### Generating Statistics

getStatistics(self) -> dict:

Generation metadata with this function. Its creating stats for numeric, string and geometry columns. 
Stats are number of null values, number of valid values, median, histogram (10% bucket/interval size), value distributions, mean, number of values, numberOfValidNonNullValues, minimum, maximum, standardDeviation, numberOfInvalidValues, deciles and quantiles and outliers