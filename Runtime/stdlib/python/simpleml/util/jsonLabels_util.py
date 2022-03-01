import numpy as np
from pandas import Int32Dtype, Int64Dtype, StringDtype, Timestamp
from shapely import geometry

lang = "de"
type = "type"
simple_type = "simple_type"
i18n_id = "id"

type_numeric = "numeric"
type_numeric_list = 'numeric_list'
type_integer = "integer"
type_long = "long"
type_float = "float"
type_string = "string"
type_datetime = "datetime"
type_bool = "boolean"
type_geometry = "geometry"

type_dataset = "dataset"

type_table = "table"
type_table_values = "lines"
type_table_data_types = "data_types"
type_table_header_labels = "header_labels"

type_histogram = "histogram"
type_histogram_buckets = "buckets"
data_type_labels = {
    np.int32: type_integer,
    Int32Dtype(): type_integer,
    Int64Dtype(): type_long,
    np.bool_: type_bool,
    np.datetime64: type_datetime,
    str: type_string,
    int: type_integer,
    float: type_float,
    np.int64: type_integer,
    np.float64: type_float,
    np.floating: type_float,
    np.integer: type_integer,
    np.dtype("float64"): type_float,
    np.dtype("int64"): type_integer,
    np.dtype("bool"): type_bool,
    "str": type_string,
    bool: type_bool,
    object: "ERROR",
    geometry: type_geometry,
    Timestamp: type_datetime,
    StringDtype: type_string,
}

type_bar_chart = "bar_chart"
type_bar_chart_bars = "bars"

type_list = "list"
type_list_values = "values"
type_list_data_type = "data_type"

type_box_plot = "box_plot"
type_box_plot_values = "values"
type_box_plot_data_type = "data_type"

statistics = "statistics"
attributes = "attributes"
topics = "subjects"
title = "title"
null_value = "null_string"
separator = "separator"
file_location = "fileName"
has_header = "hasHeader"
description = "description"
id = "id"
list_data_type = "list_data_type"
bucket_data_type = "bucket_data_type"
number_of_instances = "number_of_instances"

attribute_label = "label"

spatialValueDistribution = "spatialValueDistribution"
type_spatial_distribution = "spatial_value_distribution"
type_spatial_distribution_areas = "areas"

numberOfNullValues = "numberOfNullValues"
deciles = "decile"
# decile = 'decile'
quartiles = "quartile"
averageNumberOfSpecialCharacters = "averageNumberOfSpecialCharacters"
averageNumberOfTokens = "averageNumberOfTokens"
numberOfValidValues = "numberOfValidValues"
numberOfOutliersBelow = "numberOfOutliersBelow"
averageNumberOfCapitalisedValues = "averageNumberOfCapitalisedValues"
averageNumberOfDigits = "averageNumberOfDigits"
numberOfOutliersAbove = "numberOfOutliersAbove"
numberOfDistinctValues = "numberOfDistinctValues"
histogram = "histogram"
averageNumberOfCharacters = "averageNumberOfCharacters"
median = "median"
numberOfValues = "numberOfValues"
mean = "mean"
numberOfValidNonNullValues = "numberOfValidNonNullValues"
maximum = "maximum"
minimum = "minimum"
standardDeviation = "standardDeviation"
numberOfInvalidValues = "numberOfInvalidValues"

value = "value"
values = "values"

valueDistribution = "valueDistribution"
numberOfInstances = "numberOfInstances"
bucketMinimum = "bucketMinimum"
bucketMaximum = "bucketMaximum"
bucketValue = "value"

sample = "sample_instances"
sample_header_labels = "header_labels"
sample_lines = "lines"

value_distribution = "value_distribution"
value_distribution_number_of_instances = "number_of_instances"
value_distribution_value = "value"

type_numeric_names = {
    "de": {
        "averageNumberOfSpecialCharacters": "durchschnittliche Anzahl an Sonderzeichen",
        "averageNumberOfTokens": "durchschnittliche Anzahl an Wörtern",
        "averageNumberOfCapitalisedValues": "durchschnittliche Anzahl an Großbuchstaben",
        "averageNumberOfCharacters": "durchschnittliche Anzahl an Zeichen",
        "averageNumberOfDigits": "durchschnittliche Anzahl an Ziffern",
        numberOfDistinctValues: "Anzahl verschiedener Werte",
    },
    "en": {
        "averageNumberOfSpecialCharacters": "average number of special characters",
        "averageNumberOfTokens": "average number of words",
        "averageNumberOfCapitalisedValues": "average number of capitalised characters",
        "averageNumberOfCharacters": "average number of characters",
        "averageNumberOfDigits": "average number of digits",
        numberOfDistinctValues: "number of distinct values",
    },
}

type_numeric_data_type = "data_type"
type_numeric_value = "value"
type_numeric_name = "name"

datetime_format = {"en": "%Y-%m-%d %H:%M:%S", "de": "%d.%m.%Y, %H:%M:%S"}


def type_numeric_list():
    return None
