from __future__ import annotations

import json
import os
from datetime import datetime
from typing import Any, Dict, List, Optional, Tuple

import category_encoders as ce  # For one hot encoding
import geopandas
import networkx as nx
import pandas as pd  # For data processing
import simpleml.util.global_configurations as global_config
import simpleml.util.jsonLabels_util as config
from libpysal.weights import Kernel
from node2vec import Node2Vec
from rdflib import Namespace
from sentence_transformers import SentenceTransformer
from shapely import geometry, wkb, wkt
from shapely.errors import WKBReadingError, WKTReadingError
from simpleml.dataset._instance import Instance
from simpleml.dataset._stats import getStatistics
from simpleml.util import (
    exportDictionaryAsJSON,
    get_pandas_type_from_python_type,
    get_python_type_from_pandas_type,
    get_simple_type_from_python_type,
    get_simple_type_from_sml_type,
    get_sml_type_from_python_type,
    simple_type_datetime,
    simple_type_geometry,
    simple_type_numeric_list,
    simple_type_string,
    type_datetime,
    type_float,
    type_geometry,
    type_numeric_list,
)
from sklearn.model_selection import train_test_split

from ._attribute import Attribute
from ._conversion import AttributeTransformer


class Dataset:
    def __init__(
        self,
        id,
        title: str,
        description: str = None,
        fileName: str = None,
        hasHeader: bool = True,
        null_value="",
        separator=",",
        number_of_instances: int = None,
        titles: dict = {},
        descriptions: dict = {},
        subjects: dict = {},
        coordinate_system: int = 4326,
        lat_before_lon: bool = False,
    ):
        self.id = id
        self.title = title
        self.description = description
        self.domain_model = None
        self.target_attribute: Optional[Attribute] = None
        self.data_sample = pd.DataFrame()
        self.titles = titles
        if not titles:
            titles["en"] = title
        self.descriptions = descriptions
        self.subjects = subjects
        self.dataset_json = None
        self.stats: Dict[
            str, Dict
        ] = {}  # attribute identifier to statistics dictionary
        self.data = pd.DataFrame()
        self.attributes: Dict[str, Attribute] = {}  # list of attributes

        self.fileName = fileName
        self.hasHeader = hasHeader
        self.null_value = null_value
        self.separator = separator
        self._sample_info = None
        self.lon_lat_pairs: List[
            Dict
        ] = []  # list of attribute pairs which are latitude-longitude pairs
        self.wkt_columns: List[
            str
        ] = []  # list of attribute identifiers for attributes with Well-Known-Text data
        self.wkb_columns: List[
            str
        ] = (
            []
        )  # list of attribute identifiers for attributes with Well-Known-Binary data
        # self._attributes_dict: dict[str, dict] = {}  # dictionary of attribute IDs to their attributes

        self._sample_for_profile = None
        self.number_of_instances = number_of_instances
        self.coordinate_system = coordinate_system
        self.lat_before_lon = lat_before_lon
        self._parse_dates: List[
            str
        ] = (
            []
        )  # list of attribute identifiers of attributes that should be parsed as date

    # USER FUNCTIONS

    def sample(self, nInstances: int, recompute_statistics: bool = True) -> Dataset:

        copy = self.copy_and_read(number_of_lines=nInstances)

        copy.data = copy.data.head(n=nInstances)

        # invalidate statistics
        copy.stats = {}

        if recompute_statistics:
            return copy.provide_statistics()
        return copy

    def setTargetAttribute(self, targetAttribute):
        self.target_attribute = self.attributes[targetAttribute]
        return self

    def keepAttributes(
        self, attributeIDs: list[str], recompute_statistics: bool = True
    ) -> Dataset:

        if not isinstance(attributeIDs, list):
            attributeIDs = [attributeIDs]

        copy = self.copy_and_read()

        copy.data = copy.data.filter(items=attributeIDs)

        attribute_ids_to_delete = set()
        # remove columns from statistics
        for attribute in copy.attributes.values():
            if attribute.id not in attributeIDs:
                if copy.stats:
                    del copy.stats[attribute]
                attribute_ids_to_delete.add(attribute.id)

        for k in attribute_ids_to_delete:
            copy.attributes.pop(k)

        return copy.provide_statistics(recompute_statistics)

    def keepAttribute(
        self, attributeID: str, recompute_statistics: bool = True
    ) -> Dataset:

        return self.keepAttributes([attributeID], recompute_statistics)

    def dropAttributes(
        self, attributeIDs: list[str], recompute_statistics: bool = True
    ) -> Dataset:
        copy = self.copy_and_read()

        copy.data = copy.data.drop(columns=attributeIDs)

        # remove column descriptions
        for attribute_id in attributeIDs:
            copy.drop_column_description(self.attributes[attribute_id])

        return copy.provide_statistics(recompute_statistics)

    def dropAttribute(
        self, attributeID: str, recompute_statistics: bool = True
    ) -> Dataset:
        return self.dropAttributes([attributeID], recompute_statistics)

    def filterByAttribute(self, attribute: str, value) -> Dataset:

        copy = self.copy_and_read()

        copy.data = copy.data.loc[copy.data[attribute] > value]

        return copy.provide_statistics()

    def filterInstances(self, filterFunc) -> Dataset:

        copy = self.copy_and_read()

        for index, row in copy.data.iterrows():
            if not filterFunc(Instance(row)):
                copy.data.drop(index, inplace=True)

        return copy.provide_statistics()

    def addAttribute(
        self,
        newAttributeId,
        transformer: AttributeTransformer,
        newAttributeLabel: str = None,
    ) -> Dataset:

        copy = self.add_attribute_data(newAttributeId, transformer.transform)
        # copy.data[newColumnName] = copy.data[newColumnName].convert_dtypes()

        data_type = get_python_type_from_pandas_type(
            copy.data[newAttributeId].convert_dtypes().dtype
        )

        # TODO
        # data_type = copy.data[newColumnName].dtype

        newAttributeLabelOrId = newAttributeId
        if newAttributeLabel:
            newAttributeLabelOrId = newAttributeLabel

        attribute = copy.add_column_description(
            newAttributeId, newAttributeLabelOrId, data_type
        )
        copy.create_simple_type(attribute, data_type)

        return copy.provide_statistics()

    def dropAllMissingValues(self):
        copy = self.copy_and_read()
        copy.data.dropna(inplace=True)

        return copy.provide_statistics()

    def dropMissingValues(self, attribute):
        copy = self.copy_and_read()
        copy.data.dropna(subset=[attribute], inplace=True)

        return copy.provide_statistics()

    def splitIntoTrainAndTestAndLabels(
        self, trainRatio: float, randomState=None
    ) -> Tuple[Dataset, Dataset, Dataset, Dataset]:

        if not self.target_attribute:
            raise ValueError(
                "No target attribute specified for this datatset (set it via setTargetAttribute)."
            )

        copy = self.copy_and_read()

        train, test = copy.splitIntoTrainAndTest(trainRatio, randomState)

        X_train = train.dropAttribute(self.target_attribute.id)
        X_test = test.dropAttribute(self.target_attribute.id)
        y_train = train.keepAttribute(self.target_attribute.id)
        y_test = test.keepAttribute(self.target_attribute.id)

        return (
            X_train.provide_statistics(),
            X_test.provide_statistics(),
            y_train.provide_statistics(),
            y_test.provide_statistics(),
        )

    def splitIntoTrainAndTest(
        self, trainRatio: float, randomState=None
    ) -> Tuple[Dataset, Dataset]:

        copy = self.copy_and_read()

        train_data, test_data = train_test_split(
            copy.data, train_size=trainRatio, random_state=randomState
        )

        train = self.copy(basic_data_only=True)
        train.title += " (Train)"

        if train.description:
            train.description += " (Train)"
        train.data = train_data

        test = self.copy(basic_data_only=True)
        test.title += " (Test)"
        if test.description:
            test.description += " (Test)"
        test.data = test_data

        return train.provide_statistics(), test.provide_statistics()

    def getColumn(self, column_identifier):
        if self.data.empty:
            self.readFile()
        return self.data[column_identifier]

    def getRow(self, row_number):
        if self.data.empty:
            self.readFile()
        return Instance(self.data.iloc[[row_number]].squeeze())

    # NON-USER FUNCTIONS

    def getColumnNames(self):
        if self.data.empty:
            self.readFile()
        return self.data.columns.values.tolist()

    def add_attribute_data(self, newAttributeId, transformFunc) -> Dataset:
        copy = self.copy_and_read()

        # for index, row in copy._data.iterrows():
        #    copy._data.at[index, newColumnName] = transformFunc(Instance(row))

        copy.data[newAttributeId] = copy.data.apply(
            lambda row: transformFunc(Instance(row)), axis=1
        )

        # TODO: Enforce type information from transformFunc via transformFunc.__annotations__?
        # copy.data[newColumnName] = copy.data[newColumnName].convert_dtypes()

        return copy

    def copy(self, basic_data_only: bool = False) -> Dataset:
        copy = Dataset(
            id=self.id,
            title=self.title,
            subjects=self.subjects,
            description=self.description,
            separator=self.separator,
            null_value=self.null_value,
            fileName=self.fileName,
            hasHeader=self.hasHeader,
            titles=self.titles,
            descriptions=self.descriptions,
        )

        for attribute in self.attributes.values():
            attribute_copy = attribute.copy()
            copy.attributes[attribute.id] = attribute_copy
            if attribute == self.target_attribute:
                copy.target_attribute = attribute_copy

        copy.wkb_columns = self.wkb_columns.copy()
        copy.wkt_columns = self.wkt_columns.copy()
        copy.lon_lat_pairs = self.lon_lat_pairs.copy()
        copy.coordinate_system = self.coordinate_system
        copy._parse_dates = self._parse_dates.copy()

        if self.domain_model:
            copy.domain_model = self.domain_model.copy()

        if not basic_data_only:
            if self.data is not None:
                copy.data = self.data.copy()
        return copy

    def drop_column_description(self, attribute):

        self.attributes.pop(attribute.id)

        if self.stats:
            del self.stats[attribute.id]

    def add_column_description(
        self,
        attribute_id: str,
        label: str,
        python_data_type: type,
        sml_data_type: str = None,
    ):

        if not sml_data_type:
            sml_data_type = get_simple_type_from_python_type(python_data_type)

        attribute = Attribute(
            id=attribute_id,
            label=label,
            python_data_type=python_data_type,
            data_type=sml_data_type,
            simple_data_type=get_simple_type_from_sml_type(sml_data_type),
        )

        self.attributes[attribute_id] = attribute

        return attribute

    def transform(self, attributeId: str, transformer: AttributeTransformer):

        attribute = self.attributes[attributeId]

        copy = self.copy_and_read()

        copy.data = self.add_attribute_data(attributeId, transformer.transform).data
        self.drop_column_description(attribute)

        data_type = get_python_type_from_pandas_type(
            copy.data[attributeId].convert_dtypes().dtype
        )

        attribute = copy.add_column_description(attributeId, attribute.label, data_type)
        copy.create_simple_type(attribute, data_type)

        return copy.provide_statistics()

    def transformGeometryToVector(self, attributeId):

        attribute = self.attributes[attributeId]

        copy = self.copy_and_read()

        # w = libpysal.weights.DistanceBand.from_dataframe(self.data, threshold=50000, binary=False)
        # print(w.islands)

        w = Kernel.from_dataframe(self.data, fixed=False, function="gaussian")
        # w = KNN.from_dataframe(self.data, k=5)
        nodes = w.weights.keys()
        edges = [(node, neighbour) for node in nodes for neighbour in w[node]]
        my_graph = nx.Graph(edges)

        dimensions = 64
        node2vec = Node2Vec(
            my_graph, dimensions=dimensions, walk_length=15, num_walks=100, workers=1
        )
        model = node2vec.fit(window=10, min_count=1, batch_words=4)

        self.drop_column_description(attribute)
        copy.data[attributeId + "_tmp"] = ""
        for index, _ in copy.data.iterrows():
            copy.data.at[index, attributeId + "_tmp"] = model.wv[str(index)]

        copy.data = copy.data.drop(columns=[attributeId])
        copy.data = copy.data.rename(columns={attributeId + "_tmp": attributeId})

        copy.add_column_description(
            attributeId,
            attribute.label,
            object,
            type_numeric_list,
        )

        return copy.provide_statistics()

    def transformCategoryToVector(self, attributeId):

        attribute = self.attributes[attributeId]

        copy = self.copy_and_read()

        column_data = copy.data[attributeId]

        column_encoder = ce.OneHotEncoder(
            cols=attributeId,
            handle_unknown="return_nan",
            return_df=True,
            use_cat_names=True,
        )

        column_data = column_encoder.fit_transform(column_data)
        copy.data[attributeId + "_tmp"] = column_data.values.tolist()

        self.drop_column_description(attribute)

        copy.data = copy.data.drop(columns=[attributeId])
        copy.data = copy.data.rename(columns={attributeId + "_tmp": attributeId})

        copy.add_column_description(
            attributeId,
            attribute.label,
            object,
            type_numeric_list,
        )

        return copy.provide_statistics()

    def transformTextToVector(self, attributeId):

        attribute = self.attributes[attributeId]

        copy = self.copy_and_read()

        column_to_list = copy.data[attributeId].tolist()

        model = SentenceTransformer("all-MiniLM-L6-v2")
        sentence_embeddings = model.encode(column_to_list)

        copy.data[attributeId + "_tmp"] = sentence_embeddings.tolist()

        self.drop_column_description(attribute)

        copy.data = copy.data.drop(columns=[attributeId])
        copy.data = copy.data.rename(columns={attributeId + "_tmp": attributeId})

        copy.add_column_description(
            attributeId,
            attribute.label,
            object,
            type_numeric_list,
        )

        return copy.provide_statistics()

    def transformDateToTimestamp(self, attributeId):

        attribute = self.attributes[attributeId]

        copy = self.copy_and_read()

        def transformIntoTimestamp(instance: Instance):
            return datetime.timestamp(instance.getValue(attributeId))

        copy.data = self.add_attribute_data(attributeId, transformIntoTimestamp).data
        self.drop_column_description(attribute)

        copy.add_column_description(attributeId, attribute.label, float, type_float)

        return copy.provide_statistics()

    def flattenData(self):

        # copy = self.copy_and_read()

        for attribute in self.attributes.values():
            if attribute.simple_data_type == simple_type_numeric_list:
                attribute_column_names = [
                    attribute.id + "_" + str(i)
                    for i in range(len(self.data[attribute.id].iloc[0]))
                ]
                self.data[attribute_column_names] = pd.DataFrame(
                    self.data[attribute.id].tolist(), index=self.data.index
                )

                # for i in range(len(self.data[attribute.id].iloc[0])):
                #    new_attribute = Attribute(id=attribute.id + "_" + str(i))
                #    self.attributes[attribute.id] = new_attribute

                # TODO: not re-read all the time
                self = self.dropAttribute(attribute.id, recompute_statistics=False)

        return self

    def getStatistics(self) -> dict:
        if not self.stats:
            if self.data.empty:
                self.readFile()
            self.stats = getStatistics(dataset=self)

        return self.stats

    def readFile(self, number_of_lines: int = None):

        if not self.fileName:
            raise ValueError("No filename given for file reading.")

        # TODO: Create global config file where we define the data folder path
        dirName = os.path.dirname(__file__)
        rootFolder = os.getenv(
            "SML_DATASET_PATH", os.path.join(dirName, global_config.data_folder_name)
        )
        dataFilePath = os.path.join(rootFolder, self.fileName)

        # TODO: Check infer_datetime_format

        parse_data_types: dict[str, object] = {}
        usecols = []

        for attribute in self.attributes.values():
            if not attribute.is_virtual:
                usecols.append(attribute.id)
                parse_data_types[attribute.id] = get_pandas_type_from_python_type(
                    attribute.python_data_type
                )
                if attribute.data_type == type_datetime:
                    parse_data_types[attribute.id] = str
                elif attribute.data_type == type_geometry:
                    parse_data_types[attribute.id] = object

        self.data = pd.read_csv(
            dataFilePath,
            sep=self.separator,
            dtype=parse_data_types,
            parse_dates=self._parse_dates,
            na_values=[self.null_value],
            usecols=usecols,
            nrows=number_of_lines,
        )

        self.parse_geo_columns()

    def getProfile(self, remove_lat_lon: bool = True):

        profile: Dict[str, Any] = {"type": config.type_dataset}

        if not self.stats:
            self.getStatistics()

        profile[config.topics] = self.subjects
        profile[config.title] = self.title
        if self.description:
            profile[config.description] = self.description
        profile[config.null_value] = self.null_value
        profile[config.separator] = self.separator
        if self.fileName:
            profile[config.file_location] = self.fileName
        profile[config.has_header] = self.hasHeader
        profile[config.sample] = self._sample_for_profile
        if self.id:
            profile[config.id] = self.id
        profile[config.number_of_instances] = self.number_of_instances

        # attributes
        profile_attributes: Dict[str, Dict] = {}
        profile[config.attributes] = profile_attributes

        for attribute in self.attributes.values():

            if remove_lat_lon:
                # Ignore longitude/latitude columns. They should be covered by geometry columns.
                valid = True
                for lon_lat_pair in self.lon_lat_pairs:
                    if attribute.id in lon_lat_pair.values():
                        valid = False
                        continue
                if not valid:
                    continue

            profile_attributes[attribute.id] = {}
            profile_attributes[attribute.id][config.attribute_label] = attribute.label

            profile_attributes[attribute.id][config.type] = attribute.data_type
            profile_attributes[attribute.id][
                config.simple_type
            ] = attribute.simple_data_type

            profile_attributes[attribute.id][config.id] = attribute.id

            if attribute.id in self.stats:
                profile_attributes[attribute.id][config.statistics] = self.stats[
                    attribute.id
                ]

        profile[config.sample] = self.get_sample_info_for_profile()

        return profile

    def parse_geo_columns(self):

        # parse geo data
        # WKT columns
        for wkt_column in self.wkt_columns:
            if not self.attributes[wkt_column].is_virtual:
                self.data[wkt_column] = self.data[wkt_column].apply(parseWKT, hex=True)
                self.attributes[wkt_column].simple_data_type = simple_type_geometry
        # WKB columns
        for wkb_column in self.wkb_columns:
            if not self.attributes[wkb_column].is_virtual:
                self.data[wkb_column] = self.data[wkb_column].apply(parseWKB, hex=True)
                self.attributes[wkb_column].simple_data_type = simple_type_geometry

        lon_lat_pair_number = 1
        for lon_lat_pair in self.lon_lat_pairs:

            # new column is either called "geometry" or "geometry1", "geometry2", ..., if there are multiple ones.
            if "geometry" in self.data:
                geo_column_number = 2
                while True:
                    column_name = "geometry" + geo_column_number
                    if column_name not in self.data:
                        break
            else:
                column_name = "geometry"

            if len(self.lon_lat_pairs) > 1:
                column_name += str(lon_lat_pair_number)
            self.data[column_name] = geopandas.points_from_xy(
                self.data[lon_lat_pair["longitude"]],
                self.data[lon_lat_pair["latitude"]],
            )

            attribute = self.add_column_description(
                column_name,
                column_name,
                python_data_type=geometry,
                sml_data_type=type_geometry,
            )
            SML = Namespace("https://simple-ml.de/resource/")
            attribute.graph = {}
            attribute.graph["value_type"] = SML.wellKnownText
            attribute.graph["property"] = SML.asWKT
            attribute.graph["resource"] = self.attributes[
                lon_lat_pair["longitude"]
            ].graph["resource"]
            attribute.graph["class"] = self.attributes[lon_lat_pair["longitude"]].graph[
                "class"
            ]
            attribute.is_virtual = True

            lon_lat_pair_number += 1

    def addColumnDescription(
        self,
        attribute_identifier,
        resource_node,
        domain_node,
        property_node,
        rdf_value_type,
        value_type,
        attribute_label,
        is_geometry: bool,
        resource_rank: int = None,
        is_virtual: bool = False,
    ):

        attribute = Attribute(
            id=attribute_identifier,
            label=attribute_label,
            python_data_type=value_type,
            data_type=get_sml_type_from_python_type(value_type),
            is_virtual=is_virtual,
        )
        self.attributes[attribute_identifier] = attribute

        if resource_node:
            attribute_graph: Dict[str, Any] = {
                "value_type": rdf_value_type,
                "resource": resource_node,
                "property": property_node,
                "class": domain_node,
            }
            if resource_rank:
                attribute_graph["resource_rank"] = resource_rank
            self.attributes[attribute_identifier].graph = attribute_graph

        self.create_simple_type(attribute, value_type, is_geometry=is_geometry)

    def create_simple_type(
        self, attribute, value_type: type, is_geometry: bool = False
    ):

        if is_geometry:
            attribute.simple_data_type = simple_type_geometry
            attribute.data_type = type_geometry
        else:
            attribute.simple_data_type = get_simple_type_from_python_type(value_type)
            if attribute.simple_data_type == simple_type_datetime:
                self._parse_dates.append(attribute.id)

    def getJson(self):
        json_input = {"id": self.id, "title": self.title, "topics": self.subjects}
        return json.dumps(json_input)

    def get_sample_info_for_profile(self):
        sample_as_list = []
        for i in range(0, len(self.data_sample)):
            row_list = []
            sample_as_list.append(row_list)
            for column in self.data_sample.columns:
                if not pd.isna(self.data_sample[column].values[i]):
                    row_list.append(self.data_sample[column].values[i])
                else:
                    row_list.append(self.null_value)

        sample_data_types = []
        sample_attribute_labels = []
        for attribute in self.attributes.values():
            # skip geo columns in sample
            if attribute.simple_data_type == type_geometry:
                pass
            else:
                sample_data_types.append(attribute.data_type)
                sample_attribute_labels.append(attribute.label)

        return {
            config.type: config.type_table,
            config.sample_lines: sample_as_list,
            config.sample_header_labels: sample_attribute_labels,
            config.type_table_data_types: sample_data_types,
        }

    def exportDataAsFile(self, file_path):
        self.data.to_csv(file_path, encoding="utf-8")

    def copy_and_read(self, number_of_lines: int = None):

        if self.data.empty:
            self.readFile(number_of_lines)

        copy = self.copy()

        return copy

    def transformDatatypes(self):

        copy = self.copy_and_read()

        # transform non-numeric columns
        for attribute in self.attributes.values():
            if attribute.simple_data_type == simple_type_string:
                copy = copy.transformCategoryToVector(attribute.id)
            elif attribute.simple_data_type == simple_type_datetime:
                copy = copy.transformDateToTimestamp(attribute.id)
            elif attribute.simple_data_type == simple_type_geometry:
                copy = copy.transformGeometryToVector(attribute.id)

        return copy

    def provide_statistics(self, recompute_statistics: bool = True):

        if recompute_statistics:
            if not self.stats:
                if self.data.empty:
                    self.readFile()
                self.stats = getStatistics(dataset=self)

            self.dataset_json = exportDictionaryAsJSON(self.getProfile())

        return self

    def toArray(self):

        copy = self.copy()
        copy = copy.flattenData()

        return copy.data.to_numpy()


def loadDataset(datasetID: str) -> Dataset:
    from simpleml.data_catalog import getDataset

    dataset = getDataset(datasetID)

    return dataset


def readDataSetFromCSV(
    fileName: str,
    datasetId: str,
    separator: str,
    hasHeader: bool,
    nullValue: str,
    datasetName: str = None,
    coordinateSystem=3857,
    lon_lat_pairs=[],
) -> Dataset:
    dir_name = os.path.dirname(__file__)
    data_file_path = os.path.join(dir_name, global_config.data_folder_name, fileName)
    data = pd.read_csv(data_file_path, sep=separator, na_values=nullValue)
    data = data.convert_dtypes()

    if not datasetName:
        datasetName = datasetId

    dataset = Dataset(
        id=datasetId,
        title=datasetName,
        fileName=fileName,
        hasHeader=hasHeader,
        separator=separator,
        null_value="",
        description="",
        subjects={},
        number_of_instances=None,
        titles={},
        descriptions={},
        coordinate_system=coordinateSystem,
    )
    dataset.data = data
    dataset.lon_lat_pairs = lon_lat_pairs

    for attribute in data.columns.values.tolist():
        is_geometry = False

        if data[attribute].dtype == "string":

            found_new_type = False
            try:
                data[attribute] = pd.to_datetime(data[attribute])
                found_new_type = True
            except ValueError:
                pass

            if not found_new_type:
                try:
                    data[attribute].apply(wkb.loads, hex=True)
                    dataset.wkb_columns.append(attribute)
                    is_geometry = True
                    found_new_type = True
                except (WKBReadingError, AttributeError, TypeError):
                    pass

            if not found_new_type:
                try:
                    data[attribute].apply(wkt.loads)
                    dataset.wkt_columns.append(attribute)
                    is_geometry = True
                except (WKTReadingError, AttributeError, TypeError):
                    pass

        dataset.addColumnDescription(
            attribute,
            None,
            None,
            None,
            None,
            get_python_type_from_pandas_type(data[attribute].dtype),
            attribute,
            is_geometry=is_geometry,
        )

    dataset.parse_geo_columns()

    return dataset


def joinTwoDatasets(
    dataset1: Dataset,
    dataset2: Dataset,
    attributeId1: str,
    attributeId2: str,
    suffix1: str,
    suffix2: str,
) -> Dataset:
    # TODO: check that types are join_column_name_1 and join_column_name_2 are the same
    if dataset1.data[attributeId1].dtypes == dataset2.data[attributeId2].dtypes:
        joint_data = dataset1.data.merge(
            dataset2.data,
            left_on=attributeId1,
            right_on=attributeId2,
            suffixes=(suffix1, suffix2),
        )

        # TODO: how to join based on two column names
        # joint_data = first_data._data.append(second_data._data, sort=False)
        # print(joint_data.shape[0])

        new_description = None
        if dataset1.description and dataset2.description:
            new_description = dataset1.description + " / " + dataset2.description

        dataset = Dataset(
            id=dataset1.id + "-" + dataset2.id,
            title=dataset1.title + "-" + dataset2.title,
            fileName=None,
            hasHeader=dataset1.hasHeader,
            separator=None,
            null_value="",
            description=new_description,
            number_of_instances=joint_data.shape[0],
        )

        for attribute in dataset1.attributes.values():
            attribute_copy = attribute.copy()
            if (
                attribute_copy.id != attributeId1
                and attribute_copy.id not in joint_data.columns
            ):
                attribute_copy.id = attribute.id + suffix1

            dataset.attributes[attribute_copy.id] = attribute_copy

        for attribute in dataset2.attributes.values():
            attribute_copy = attribute.copy()
            # only use joint attribute once
            if attribute_copy.id == attributeId1:
                continue
            if attribute_copy.id not in joint_data.columns:
                attribute_copy.id = attribute.id + suffix2

            dataset.attributes[attribute_copy.id] = attribute_copy

        dataset.data = joint_data
    else:
        raise TypeError("Datatypes of joined columns do not match.")

    return dataset


def parseWKB(value, hex):
    try:
        return wkb.loads(value, hex=hex)
    except (WKBReadingError, AttributeError, TypeError):
        return None


def parseWKT(value, hex):
    try:
        return wkt.loads(value, hex=hex)
    except (WKTReadingError, AttributeError, TypeError):
        return None
