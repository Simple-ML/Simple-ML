# Description

The data API enables access to and pre-processing of all data sets that are contained in the Simple-ML data catalog. There are two parts of it, the [data catalog API](./Data-Catalog-API.md) that accesses meta data only, and the [data set API](./Data-Set-API.md) that proceeds the files themselves.
In addition, the data API provides access to the Simple-ML machine learning (ML) catalog via its [ML catalog API](./Machine-Learning-Catalog-API.md).

# Installation

Run the following command in the root directory of the project where the setup.py script is located to install the Simple-ML API: 

```
pip install -e API
```

The (currently still incomplete) DSL stubs for the data API are defined [here](../Stdlib/stubs/simpleml).

# Testing

Run the example workflow (using the Python environment that contains the installed Simple-ML API):

```python
python tests/load.py
```

# Python Example

The following example shows how the data catalog API and the data set API are used in practice.

```python

# call the data catalog API for retrieving the meta data of data sets contained in the data catalog 
datasets = getDatasets()

# use the data set API to create an instance of the data set class from a data set's identifier from the data catalog
dataset = loadDataset("SpeedAverages")

# more calls to the data set API that do actually pre-process the data
dataset = dataset.sample(50) # -> statistics get invalid
dataset = dataset.keepAttributes(
        ["osm_id", "street_type", "number_of_drivers", "start_time", "is_weekend", "geometry"])
train, test = dataset.splitIntoTrainAndTest(0.8)
```

# DSL Example

The code from above can be written as follows, using the DSL:

```
val dataset = loadDataset("SpeedAverages");
val datasetSample = dataset.sample(50);
val datasetFiltered = dataset.keepAttributes("osm_id", "street_type", "number_of_drivers", "start_time", "is_weekend", "geometry");
val train, val test = datasetFiltered.splitIntoTrainAndTest(0.8);
```

# Data Catalog

For efficient access to semantic meta data, Simple-ML uses a data catalog.

## Usage
There are three ways to use the Simple-ML data catalog.

1. Use an externally hosted SPARQL endpoint. To do so, configure the endpoint URL in ``Runtime/stdlib/python/simpleml/rdf/_sparql_connector.py``.
2. Use a common .ttl data catalog file and load that one at runtime. This is the default but slightly less efficient option. 
3. Experimentally, the use of a HDTStore is supported. Please check ``Runtime/stdlib/python/simpleml/rdf/_sparql_connector_local.py`` for configuration.

You can change between the first option and the other two options in ``Runtime/stdlib/python/simpleml/rdf/__init__.py``.

# Creation
To create a new version of the data catalog .ttl file, do the following steps:

1. Run the script ``Runtime/stdlib/python/simpleml/rdf/data_catalog_creator.py``  to create .ttl profiles for each dataset.
2. Merge these .ttl profiles into one data_catalog.ttl file using the script ``Runtime/stdlib/python/simpleml/rdf/data_catalog_merger.py``.

Profiles of single datasets are created based on (i) the dataset file (e.g., a CSV file), (ii) a schema file (e.g., mobility-related concepts) and (iii) a meta file which has basic information about the file and its connection to the schema. Examples are available in the folder ``C:\Users\user\Documents\Simple-ML\Simple-ML\Runtime\stdlib\data_catalog``.
