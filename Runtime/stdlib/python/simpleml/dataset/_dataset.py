from __future__ import annotations

import json
import os
from datetime import datetime
from typing import Tuple

import category_encoders as ce  # For one hot encoding
import geopandas
import networkx as nx
import numpy as np  # For huge arrays and matrices
import pandas as pd  # For data processing
import simpleml.util.global_configurations as global_config
import simpleml.util.jsonLabels_util as config
from libpysal.weights import Kernel
from node2vec import Node2Vec
from shapely import geometry, wkb, wkt
from shapely.errors import WKBReadingError, WKTReadingError
from simpleml.dataset._instance import Instance
from simpleml.dataset._stats import getStatistics
from simpleml.util import exportDictionaryAsJSON
from sklearn.model_selection import train_test_split


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
        self.fileName = fileName
        self.data = pd.DataFrame()
        self.hasHeader = hasHeader
        self.null_value = null_value
        self.separator = separator
        self.domain_model = None
        self.target_attribute = None
        self.attribute_graph: dict[
            str, dict
        ] = {}  # attribute identifier to dictionary of RDF relations
        self.data_types: dict[str, str] = {}  # attribute identifier to data type
        self.attribute_labels: dict[str, str] = {}  # attribute identifier to label
        self.stats: dict[
            str, dict
        ] = {}  # attribute identifier to statistics dictionary
        self.data_sample = pd.DataFrame()
        self.sample_info = None
        self.lon_lat_pairs: list[
            str
        ] = []  # list of attribute pairs which are latitude-longitude pairs
        self.wkt_columns: list[
            str
        ] = []  # list of attribute identifiers for attributes with Well-Known-Text data
        self.wkb_columns: list[
            str
        ] = (
            []
        )  # list of attribute identifiers for attributes with Well-Known-Binary data
        self.attributes: list[str] = []  # list of attribute identifiers
        self.sample_for_profile = None
        self.number_of_instances = number_of_instances
        self.titles = titles
        if not titles:
            titles["en"] = title
        self.descriptions = descriptions
        self.subjects = subjects
        self.simple_data_types: dict[
            str, str
        ] = {}  # attribute identifier to simple data type (numeric, ...)
        self.coordinate_system = coordinate_system
        self.lat_before_lon = lat_before_lon
        self.parse_dates: list[
            str
        ] = (
            []
        )  # list of attribute identifiers of attributes that should be parsed as date

        self.dataset_json = None

    # USER FUNCTIONS

    def sample(self, nInstances: int, recompute_statistics: bool = True) -> Dataset:

        copy = self.copy_and_read(number_of_lines=nInstances)

        copy.data = copy.data.head(n=nInstances)

        # invalidate statistics
        copy.stats = {}

        if recompute_statistics:
            return copy.provide_statistics()
        else:
            return copy

    def setTargetAttribute(self, targetAttribute):
        self.target_attribute = targetAttribute
        return self

    def keepAttributes(
        self, attributeIDs: list[str], recompute_statistics: bool = True
    ) -> Dataset:

        if not isinstance(attributeIDs, list):
            attributeIDs = [attributeIDs]

        copy = self.copy_and_read()

        copy.data = copy.data.filter(items=attributeIDs)

        # remove columns from statistics
        for attribute in copy.attributes:
            if attribute not in attributeIDs:
                if copy.stats:
                    del copy.stats[attribute]
                copy.attribute_labels.pop(attribute)
                copy.simple_data_types.pop(attribute)
                copy.data_types.pop(attribute)
        copy.attributes = [value for value in copy.attributes if value in attributeIDs]

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
        for attribute in attributeIDs:
            copy.drop_column_description(attribute)

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
        self, newColumnName, transformFunc, newColumnLabel: str = None
    ) -> Dataset:

        copy = self.add_attribute_data(newColumnName, transformFunc)
        # copy.data[newColumnName] = copy.data[newColumnName].convert_dtypes()

        data_type = dataTypes(copy.data[newColumnName].dtype)

        if not newColumnLabel:
            newColumnLabel = newColumnName

        copy.add_column_description(newColumnName, newColumnLabel, data_type)
        copy.create_simple_type(newColumnName, data_type)

        return copy.provide_statistics()

    def addIsWeekendAttribute(self, columnName):

        copy = self.copy_and_read()

        def transformIntoWeekend(instance: Instance) -> bool:
            week_num = instance.getValue(columnName).weekday()
            if week_num < 5:
                return False
            else:
                return True
            # return instance.getValue(columnName) is weekend

        copy = self.addAttribute(
            columnName + "_isWeekend",
            transformIntoWeekend,
            self.attribute_labels[columnName] + " (is weekend)",
        )

        return copy.provide_statistics()

    def addDayOfTheYearAttribute(self, columnName):

        copy = self.copy_and_read()

        def transformIntoDayOfTheYear(instance: Instance):
            return instance.getValue(columnName).timetuple().tm_yday

        copy = self.addAttribute(
            columnName + "_DayOfTheYear",
            transformIntoDayOfTheYear,
            self.attribute_labels[columnName] + " (day of the year)",
        )

        return copy.provide_statistics()

    def addWeekDayAttribute(self, columnName):

        copy = self.copy_and_read()

        def transformIntoWeekDay(instance: Instance) -> str:
            return instance.getValue(columnName).strftime("%A")

        copy = self.addAttribute(
            columnName + "_weekDay",
            transformIntoWeekDay,
            self.attribute_labels[columnName] + " (week day)",
        )

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

        X_train = train.dropAttribute(self.target_attribute)
        X_test = test.dropAttribute(self.target_attribute)
        y_train = train.keepAttribute(self.target_attribute)
        y_test = test.keepAttribute(self.target_attribute)

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

    def add_attribute_data(self, newColumnName, transformFunc) -> Dataset:
        copy = self.copy_and_read()

        # for index, row in copy.data.iterrows():
        #    copy.data.at[index, newColumnName] = transformFunc(Instance(row))

        copy.data[newColumnName] = copy.data.apply(
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
        copy.attribute_labels = self.attribute_labels.copy()
        copy.attributes = self.attributes.copy()
        copy.data_types = self.data_types.copy()
        copy.simple_data_types = self.simple_data_types.copy()
        copy.attribute_graph = self.attribute_graph.copy()
        copy.wkb_columns = self.wkb_columns.copy()
        copy.wkt_columns = self.wkt_columns.copy()
        copy.lon_lat_pairs = self.lon_lat_pairs.copy()
        copy.coordinate_system = self.coordinate_system
        copy.parse_dates = self.parse_dates.copy()
        copy.target_attribute = self.target_attribute

        if self.domain_model:
            copy.domain_model = self.domain_model.copy()

        if not basic_data_only:
            if self.data is not None:
                copy.data = self.data.copy()
        return copy

    def drop_column_description(self, attribute):

        self.attributes.remove(attribute)
        self.attribute_labels.pop(attribute)
        self.simple_data_types.pop(attribute)
        self.data_types.pop(attribute)
        if attribute in self.attribute_graph:
            self.attribute_graph.pop(attribute)
        if self.stats:
            del self.stats[attribute]

    def add_column_description(
        self, attribute, label, data_type, simple_data_type=None
    ):
        self.attributes.append(attribute)
        self.attribute_labels[attribute] = label
        self.data_types[attribute] = data_type
        if simple_data_type:
            self.simple_data_types[attribute] = simple_data_type

    def transformGeometryToVector(self, columnName):

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

        copy.data[columnName + "_tmp"] = ""
        for index, row in copy.data.iterrows():
            copy.data.at[index, columnName + "_tmp"] = model.wv[str(index)]

        copy = copy.dropAttribute(columnName, recompute_statistics=False)
        copy.data = copy.data.rename(columns={columnName + "_tmp": columnName})
        copy.add_column_description(
            columnName,
            self.attribute_labels[columnName],
            config.type_numeric_list,
            config.type_numeric_list,
        )

        return copy.provide_statistics()

    def transformCategoryToVector(self, columnName):

        copy = self.copy_and_read()

        column_data = copy.data[columnName]

        column_encoder = ce.OneHotEncoder(
            cols=columnName,
            handle_unknown="return_nan",
            return_df=True,
            use_cat_names=True,
        )

        column_data = column_encoder.fit_transform(column_data)

        copy.data[columnName + "_tmp"] = column_data.values.tolist()

        copy = copy.dropAttribute(columnName, recompute_statistics=False)
        copy.data = copy.data.rename(columns={columnName + "_tmp": columnName})
        copy.add_column_description(
            columnName,
            self.attribute_labels[columnName],
            config.type_numeric_list,
            config.type_numeric_list,
        )

        return copy.provide_statistics()

    def transformDateToTimestamp(self, columnName):

        copy = self.copy_and_read()

        def transformIntoTimestamp(instance: Instance):
            return datetime.timestamp(instance.getValue(columnName))

        copy.data = self.add_attribute_data(
            columnName + "_tmp", transformIntoTimestamp
        ).data

        copy = copy.dropAttribute(columnName, recompute_statistics=False)
        copy.data = copy.data.rename(columns={columnName + "_tmp": columnName})
        copy.add_column_description(
            columnName, self.attribute_labels[columnName], np.float, config.type_numeric
        )

        return copy

    def flattenData(self):

        copy = self.copy_and_read()

        for attribute in copy.attributes:
            if copy.simple_data_types[attribute] == config.type_numeric_list:
                attribute_column_names = [
                    attribute + str(i) for i in range(len(copy.data[attribute].iloc[0]))
                ]
                copy.data[attribute_column_names] = pd.DataFrame(
                    copy.data[attribute].tolist(), index=copy.data.index
                )

                # TODO: not re-read all the time
                copy = copy.dropAttribute(attribute, recompute_statistics=False)

        return copy

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
        for attribute, value_type in self.data_types.items():
            parse_data_types[attribute] = value_type
            if value_type == np.datetime64:
                parse_data_types[attribute] = str
            elif value_type == geometry:
                parse_data_types[attribute] = object

        self.data = pd.read_csv(
            dataFilePath,
            sep=self.separator,
            dtype=parse_data_types,
            parse_dates=self.parse_dates,
            na_values=[self.null_value],
            usecols=self.attributes,
            nrows=number_of_lines,
        )

        self.parse_geo_columns()

    def getProfile(self):

        profile = {"type": config.type_dataset}

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
        profile[config.sample] = self.sample_for_profile
        if self.id:
            profile[config.id] = self.id
        profile[config.number_of_instances] = self.number_of_instances

        # attributes
        profile_attributes = {}
        profile[config.attributes] = profile_attributes

        for attribute in self.attributes:

            profile_attributes[attribute] = {}
            profile_attributes[attribute][
                config.attribute_label
            ] = self.attribute_labels[attribute]

            profile_attributes[attribute][config.type] = config.data_type_labels[
                self.data_types[attribute]
            ]
            profile_attributes[attribute][config.simple_type] = self.simple_data_types[
                attribute
            ]

            profile_attributes[attribute][config.id] = attribute

            if attribute in self.stats:
                profile_attributes[attribute][config.statistics] = self.stats[attribute]

        profile[config.sample] = self.get_sample_info_for_profile()

        return profile

    def parse_geo_columns(self):
        # parse geo data
        # WKT columns
        for wkt_column in self.wkt_columns:
            self.data[wkt_column] = self.data[wkt_column].apply(parseWKT, hex=True)
            self.simple_data_types[wkt_column] = config.type_geometry
        # WKB columns
        for wkb_column in self.wkb_columns:
            self.data[wkb_column] = self.data[wkb_column].apply(parseWKB, hex=True)
            # latitude/longitude pairs
            self.simple_data_types[wkb_column] = config.type_geometry

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
            self.data_types[column_name] = geometry
            self.simple_data_types[column_name] = config.type_geometry
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
        resource_rank=None,
    ):
        self.attributes.append(attribute_identifier)

        if resource_node:
            self.attribute_graph[attribute_identifier] = {
                "value_type": rdf_value_type,
                "resource": resource_node,
                "property": property_node,
                "class": domain_node,
            }
            if resource_rank:
                self.attribute_graph[attribute_identifier][
                    "resource_rank"
                ] = resource_rank
        self.data_types[attribute_identifier] = value_type

        self.attribute_labels[attribute_identifier] = attribute_label

        self.create_simple_type(
            attribute_identifier, value_type, is_geometry=is_geometry
        )

    def create_simple_type(
        self, attribute_identifier, value_type, is_geometry: bool = False
    ):
        # create simple types
        if value_type == np.datetime64:
            self.parse_dates.append(attribute_identifier)
            # self.data_types[attribute_identifier] = np.str
            self.simple_data_types[attribute_identifier] = config.type_datetime
        elif (
            value_type == np.integer
            or value_type == pd.Int32Dtype()
            or value_type == pd.Int64Dtype()
        ):
            self.simple_data_types[attribute_identifier] = config.type_numeric
        elif value_type == np.float64:
            self.simple_data_types[attribute_identifier] = config.type_numeric
        elif value_type == np.bool:
            self.simple_data_types[attribute_identifier] = config.type_bool
        else:
            self.simple_data_types[attribute_identifier] = config.type_string

        if is_geometry:
            self.simple_data_types[attribute_identifier] = config.type_geometry
            self.data_types[attribute_identifier] = geometry

    def getJson(self):
        json_input = {"id": self.id, "title": self.title, "topics": self.subjects}
        return json.dumps(json_input)

    def get_sample_info_for_profile(self):
        sample_as_list = []
        for i in range(0, len(self.data_sample)):
            row_list = []
            sample_as_list.append(row_list)
            for column in self.data_sample.columns:
                row_list.append(self.data_sample[column].values[i])

        sample_data_types = []
        sample_attribute_labels = []
        for attribute in self.attributes:
            # skip geo columns in sample
            if self.simple_data_types[attribute] == config.type_geometry:
                pass
            else:
                sample_data_types.append(
                    config.data_type_labels[self.data_types[attribute]]
                )
                sample_attribute_labels.append(self.attribute_labels[attribute])

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
        for attribute in self.attributes:
            if self.simple_data_types[attribute] == config.type_string:
                copy = copy.transformCategoryToVector(attribute)
            elif self.simple_data_types[attribute] == config.type_datetime:
                copy = copy.transformDateToTimestamp(attribute)
            elif self.simple_data_types[attribute] == config.type_geometry:
                copy = copy.transformGeometryToVector(attribute)

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
        copy = self.copy_and_read()

        copy = copy.flattenData()

        return copy.data.to_numpy()


def loadDataset(datasetID: str) -> Dataset:
    from simpleml.data_catalog import getDataset

    dataset = getDataset(datasetID)

    return dataset


def readDataSetFromCSV(
    file_name: str,
    dataset_id: str,
    separator: str,
    has_header: bool,
    null_value: str,
    dataset_name: str = None,
    coordinate_system=3857,
    lon_lat_pairs=[],
) -> Dataset:
    dir_name = os.path.dirname(__file__)
    data_file_path = os.path.join(dir_name, global_config.data_folder_name, file_name)
    data = pd.read_csv(data_file_path, sep=separator, na_values=null_value)
    data = data.convert_dtypes()

    if not dataset_name:
        dataset_name = dataset_id

    dataset = Dataset(
        id=dataset_id,
        title=dataset_name,
        fileName=file_name,
        hasHeader=has_header,
        separator=separator,
        null_value="",
        description="",
        subjects={},
        number_of_instances=None,
        titles={},
        descriptions={},
        coordinate_system=coordinate_system,
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
            dataTypes(data[attribute].dtype),
            attribute,
            is_geometry=is_geometry,
        )

    dataset.parse_geo_columns()

    return dataset


def dataTypes(type):

    # TODO: Cleanup the whole attributes' type system

    if type == "Float64" or type == np.float64 or type == np.float32:
        return np.float64
    elif type == "Int64" or type == np.int64 or type == np.int32:
        return pd.Int32Dtype()
    elif type == "string" or type == np.str:
        return np.str
    elif type == "datetime64[ns]" or type == np.datetime64:
        return np.datetime64
    elif type == "boolean" or type == np.bool:
        return np.bool
    elif type == "object":
        return np.str


def joinTwoDatasets(
    first_data: Dataset,
    second_data: Dataset,
    join_column_name_1: str,
    join_column_name_2: str,
    first_suffix: str,
    second_suffix: str,
) -> Dataset:
    # TODO: check that types are join_column_name_1 and join_column_name_2 are the same
    if (
        first_data.data[join_column_name_1].dtypes
        == second_data.data[join_column_name_2].dtypes
    ):
        # print(second_data.data)
        joint_data = first_data.data.merge(
            second_data.data,
            left_on=join_column_name_1,
            right_on=join_column_name_2,
            suffixes=(first_suffix, second_suffix),
        )
        # TODO: how tpo join based on two column names
        # joint_data = first_data.data.append(second_data.data, sort=False)
        # print(joint_data.shape[0])

        new_description = None
        if first_data.description and second_data.description:
            new_description = first_data.description + "-" + second_data.description

        dataset = Dataset(
            id=first_data.id + "-" + second_data.id,
            title=first_data.title + "-" + second_data.title,
            fileName=None,
            hasHeader=first_data.hasHeader,
            separator=None,
            null_value="",
            description=new_description,
            subjects={"qw": "qw"},
            number_of_instances=joint_data.shape[0],
        )

        new_attribute_types = {}
        for attribute in first_data.attributes:
            if attribute == join_column_name_1:
                continue
            new_attribute_name = attribute + first_suffix
            new_attribute_types[new_attribute_name] = first_data.data_types[attribute]
        for attribute in second_data.attributes:
            if attribute == join_column_name_2:
                continue
            new_attribute_name = attribute + second_suffix
            new_attribute_types[new_attribute_name] = second_data.data_types[attribute]

        # same for labels
        # print(new_attribute_types)

        new_attribute_types[join_column_name_1] = first_data.data_types[
            join_column_name_1
        ]

        # joint_dataset = first_data.copy()
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
        return wkt.loads(value)
    except (WKTReadingError, AttributeError, TypeError):
        return None
