from __future__ import annotations
from typing import Any, Tuple
import os

# import Statistics

from simpleml.dataset import getStatistics

import pandas as pd  # For data processing
import numpy as np  # For huge arrays and matrices
import json
import matplotlib as plt  # for graphs
from plpygis import Geometry
from shapely import wkt, wkb
import geopandas

data_folder_name = '../../../data/'  # TODO: Configure globally


class Dataset:
    def __init__(self, id: str = None, title: str = None, topics=[], fileName: str = None, hasHeader: bool = True,
                 null_value="", separator=","):
        self.id = id
        self.title = title
        self.topics = topics
        self.fileName = fileName
        self.data = None
        self.hasHeader = hasHeader
        self.null_value = null_value
        self.separator = separator
        self.domain_model = None
        self.attribute_graph = {}
        self.attribute_statistics = {}
        self.data_types = {}
        self.attribute_labels = {}
        self.stats = None
        self.data_sample = None
        self.lon_lat_pairs = []
        self.wkt_columns = []
        self.wkb_columns = []

    def sample(self, nInstances: int) -> Dataset:

        if self.data is None:
            self.readFile(self.separator)

        self.data = self.data.head(n=nInstances)

        # invalidate statistics
        self.stats = None

        return self

    def keepAttributes(self, attributeIDs: Any) -> Dataset:

        if self.data is None:
            self.readFile(self.separator)

        self.data = self.data.filter(attributeIDs)

        # remove columns from statistics
        if self.stats:
            for attribute in self.stats:
                if attribute not in attributeIDs:
                    del self.stats[attribute]

        return self

    def dropAttributes(self, attributeIDs: Any) -> Dataset:

        if self.data is None:
            self.readFile(self.separator)

        self.data = self.data.drop(columns=attributeIDs)

        # remove columns from statistics
        if self.stats:
            for attribute in self.stats:
                if attribute not in attributeIDs:
                    del self.stats[attribute]

        return self

    def getStatistics(self) -> dict:

        if self.stats is None:
            if self.data is None:
                self.readFile(self.separator)
            self.stats = getStatistics(dataset=self)

        return self.stats

    def splitIntoTrainAndTest(self, trainRatio: float) -> Tuple[Dataset, Dataset]:

        if self.data is None:
            self.readFile(self.separator)

        from sklearn.model_selection import train_test_split
        train_data, test_data = train_test_split(self.data, train_size=trainRatio, random_state=50)

        train = Dataset(id=self.id + "Train", title=self.title + " (Training)", topics=self.topics)
        train.data = train_data

        test = Dataset(self.id + "Test", title=self.title + " (Test)", topics=self.topics)
        test.data = test_data

        return train, test

    def readFile(self, sep):

        # TODO: Create global config file where we define the data folder path
        dirName = os.path.dirname(__file__)
        dataFilePath = os.path.join(dirName, data_folder_name, self.fileName)
        print(dataFilePath)

        # TODO: Check infer_datetime_format
        parse_dates = []
        for attribute, data_type in self.data_types.items():
            if data_type == np.datetime64:
                parse_dates.append(attribute)
                self.data_types[attribute] = np.str

        self.data = pd.read_csv(dataFilePath, sep=sep, dtype=self.data_types, parse_dates=parse_dates)

        # parse geo data
        # WKT columns
        for wkt_column in self.wkt_columns:
            self.data[wkt_column] = self.data[wkt_column].apply(wkt.loads)
        # WKB columns
        for wkb_column in self.wkb_columns:
            self.data[wkb_column] = self.data[wkb_column].apply(wkb.loads, hex=True)
        # latitude/longitude pairs

        lon_lat_pair_number = 1
        for lon_lat_pair in self.lon_lat_pairs:

            # new column is either called "geometry" or "geometry1", "geometry2", ..., if there are multiple ones.
            column_name = "geometry"
            if len(self.lon_lat_pairs) > 1:
                column_name += str(lon_lat_pair_number)
            self.data[column_name] = geopandas.points_from_xy(self.data[lon_lat_pair["longitude"]],
                                                              self.data[lon_lat_pair["latitude"]])
            lon_lat_pair_number += 1

        print(self.data)
        print(self.data.describe())

    def addColumnDescription(self, attribute_identifier, resource_node, domain_node, property_node, value_type,
                             attribute_label):
        self.attribute_graph[attribute_identifier] = {"resource": resource_node, "property": property_node,
                                                      "class": domain_node}
        self.data_types[attribute_identifier] = value_type
        self.attribute_labels[attribute_identifier] = attribute_label

    def getJson(self):
        json_input = {"id": self.id, "title": self.title, "topics": self.topics}
        return json.dumps(json_input)

    def getColumn(self, column_identifier):
        if self.data is None:
            self.readFile(self.separator)
        return self.data[column_identifier]

    def getRow(self, row_number):
        if self.data is None:
            self.readFile(self.separator)
        return self.data.loc[row_number:row_number]

    def getRows(self, row_number_start, row_number_end):
        if self.data is None:
            self.readFile(self.separator)
        return self.data.loc[row_number_start: row_number_end]


def loadDataset(datasetID: str) -> Dataset:
    from simpleml.data_catalog import getDataset
    dataset = getDataset(datasetID)

    # TODO: From the data catalog, get the list of temporal and spatial columns. Currently, we have them here fixed in the code.
    # dataset.spatial_columns = ["geometry"]

    return dataset
