# Description

The data API enables access to and pre-processing of all data sets that are contained in the Simple-ML data catalog. There are two parts of it, the [data catalog API](https://github.com/Anzumana/Simple-ML/wiki/Data-Catalog-API) that accesses meta data only, and the [data set API](https://github.com/Anzumana/Simple-ML/wiki/Dataset-API) that proceeds the files themselves.
In addition, the data API provides access to the Simple-ML machine learning (ML) catalog via its [ML catalog API](https://github.com/Anzumana/Simple-ML/wiki/Machine-Learning-Catalog-API):

# Installation

Run the following command in the root directory of the project where the setup.py script is located to install the Simple-ML API: 

    pip install -e API

DSL stubs for the data API are defined [here](https://github.com/Simple-ML/Stdlib/blob/master/stubs/simpleml/dataset/dataset.stub.simpleml).

# Testing

Run the example workflow (using the Python environment that contains the installed Simple-ML API):

    python tests/speedPrediction_predictSpeed.py

# Python Example

The following example shows how the data catalog API and the data set API are used in practice.

```python

# call the data catalog API for retrieving the meta data of data sets contained in the data catalog 
datasets = getDatasets()

# use the data set API to create an instance of the data set calss from a data set's identifier from the data catalog
dataset = loadDataset("SpeedAverages")

# more calls to the data set API that do actually pre-process the data
dataset = dataset.sample(50) # -> statistics get invalid
dataset = dataset.keepAttributes(
        ["osm_id", "street_type", "number_of_drivers", "start_time", "is_weekend", "geometry"])
train, test = dataset.splitIntoTrainAndTest(0.8)
```

# DSL Example

To do.