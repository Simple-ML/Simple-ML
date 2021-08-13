# Description

The data catalog API serves as the entry point to the data catalog. Its methods query the data catalog via pre-defined SPARQL queries. In contrast to the data set API, the data catalog API does never load or proceed the actual data set files.

# Access/Usage

[Code](../Stdlib/python/simpleml/data_catalog)

The data catalog API provides a set of methods as follows:

# Methods

### get data sets

`getDatasets()`

This method returns all data sets contained in the data catalog, all described using basic meta data such as their identifiers.

### get data set

`getDataset(dataset_id: str) -> Dataset`

Given a data set identifier, this method returns the specific data sets contained in the data catalog, together with more meta data.

### get data set statistics

`getStatistics(dataset: Dataset)`

Given a data set, this method returns its statistics contained in the data catalog.
