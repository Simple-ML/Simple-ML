# Description

[Code](https://github.com/Simple-ML/RuntimeData/tree/main/api/simpleml/data_catalog)

Data Catalog API mainly deals with selecting right dataset to process. It queries all datasets exists within our sparql endpoint.

# Access/Usage

(url...) 

# API calls

### Get Datasets

getDatasets():

Loading all datasets with sparql query from an endpoint with sparql connector.

### Get Dataset

getDataset(dataset_id: str) -> Dataset:

Getting specific dataset with dataset_id in parameter.

### Get Statistics

getStatistics(dataset: Dataset):

Collecting statistics of given dataset for data catalog.

