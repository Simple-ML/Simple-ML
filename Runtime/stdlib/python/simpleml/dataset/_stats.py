from __future__ import annotations

import os
import re
import time
from datetime import datetime as datet

import numpy as np
import pandas as pd
import pyproj
import simpleml.util.global_configurations as global_config
import simpleml.util.jsonLabels_util as config
from shapely import geometry, ops, wkt
from shapely.geometry import LineString, Point, Polygon
from simpleml.util import (
    get_sml_type_from_python_type,
    simple_type_boolean,
    simple_type_datetime,
    simple_type_geometry,
    simple_type_numeric,
    simple_type_numeric_list,
    simple_type_string,
    type_datetime,
    type_float,
    type_integer,
    type_numeric_list,
)


def addSpatialValueDistribution(geometry_object, polygon_count, areas, proj=None):
    if not geometry_object:
        if None not in polygon_count:
            polygon_count[None] = 1
        else:
            polygon_count[None] = polygon_count[None] + 1
        return None

    transformedGeometryObject = geometry_object

    if proj:
        transformedGeometryObject = ops.transform(proj, geometry_object)

    # print("CHECK", transformedGeometryObject, "---", geometry_object)
    for polygon_id, polygonObject in areas.items():
        if transformedGeometryObject.within(polygonObject):
            if polygon_id not in polygon_count:
                polygon_count[polygon_id] = 1
            else:
                polygon_count[polygon_id] = polygon_count[polygon_id] + 1


def addValueDistribution(column):
    value_distribution = []

    for value, count in column.value_counts().head(10).iteritems():
        value_distribution.append(
            {
                config.value_distribution_value: value,
                config.value_distribution_number_of_instances: int(count),
            }
        )

    return value_distribution


def addHistograms(
    stats, column, name, number_of_unique_values, transform_timestamp=False
):
    count = []
    division = []
    column = column.dropna()

    count, division = np.histogram(column, bins=min(10, number_of_unique_values))

    histograms = [
        {config.bucketMinimum: i, config.bucketValue: int(j)}
        for i, j in zip(division, count)
    ]

    # add bucket maximum from the column maximum and the bucket minimum of the next bucket
    bucket_maximum = stats[name][config.maximum][config.type_numeric_value]
    for i in range(len(histograms) - 1, -1, -1):
        bucket = histograms[i]
        bucket[config.bucketMaximum] = bucket_maximum
        bucket_maximum = bucket[config.bucketMinimum]

    if transform_timestamp:
        i = 0
        for bucket in histograms:
            i += 1
            bucket[config.bucketMinimum] = get_pd_timestamp(
                bucket[config.bucketMinimum]
            )
            if i < len(histograms):  # the last bucket maximum is date already
                bucket[config.bucketMaximum] = get_pd_timestamp(
                    bucket[config.bucketMaximum]
                )

    bucket_data_type = type(histograms[0][config.bucketMaximum])

    return histograms, bucket_data_type


def addQuantiles(column, bins, transform_timestamp=False):
    quantiles = pd.qcut(column, bins, retbins=True, labels=False, duplicates="drop")[
        1
    ].tolist()
    if transform_timestamp:
        quantiles = [get_pd_timestamp(item) for item in quantiles]

    return quantiles, type(quantiles[0])


def addNumericValue(
    column_stats, name, value, data_type=None, transform_timestamp=False
):
    simple_type = simple_type_numeric

    if transform_timestamp:
        value = get_pd_timestamp(value)
        data_type = type_datetime
        simple_type = simple_type_datetime

    column_stats[name] = createNumericValue(name, value, simple_type, data_type)


def createNumericValue(name, value, simple_type, data_type=None):
    if not data_type:
        data_type = get_sml_type_from_python_type(type(value))

    # avoid conversion to numpy.float if float (e.g., happens when using Series.mean)
    if type(value).__module__ == np.__name__:
        value = value.item()

    return {
        config.type: simple_type,
        config.type_numeric_data_type: data_type,
        config.type_numeric_value: value,
        config.i18n_id: name,
    }


def addGenericStatistics(column, column_stats, column_type):
    # TODO: for efficiency and to avoid number overflow: don't compute total sum
    totalSpecialCharacters = 0
    totalNumberOfTokens = 0
    totalNumberOfCapitalisedValues = 0
    totalNumberOfCharacters = 0
    totalNumberOfDigits = 0
    totalCountOfValidValues = 0

    for val in column:
        if pd.isna(val) or not val:
            continue
        totalCountOfValidValues += 1
        val_str = str(val)
        for character in val_str:
            if not character.isalnum():
                if character != " ":
                    totalSpecialCharacters += 1
        totalNumberOfTokens += len(re.findall(r"\w+", val_str))
        if val_str and val_str[0].isupper():
            totalNumberOfCapitalisedValues += 1
        totalNumberOfCharacters += len(val_str)
        totalNumberOfDigits += len(re.sub("[^0-9]", "", val_str))

    if totalCountOfValidValues == 0:
        totalCountOfValidValues = 1

    if column_type == simple_type_string:
        addNumericValue(
            column_stats,
            config.averageNumberOfSpecialCharacters,
            totalSpecialCharacters / totalCountOfValidValues,
            type_float,
        )
    if column_type == simple_type_string:
        addNumericValue(
            column_stats,
            config.averageNumberOfTokens,
            totalNumberOfTokens / totalCountOfValidValues,
            type_float,
        )
    if column_type == simple_type_string:
        addNumericValue(
            column_stats,
            config.averageNumberOfCapitalisedValues,
            totalNumberOfCapitalisedValues / totalCountOfValidValues,
            type_float,
        )
    if column_type == simple_type_string:
        addNumericValue(
            column_stats,
            config.averageNumberOfCharacters,
            totalNumberOfCharacters / totalCountOfValidValues,
            type_float,
        )
    addNumericValue(
        column_stats,
        config.averageNumberOfDigits,
        totalNumberOfDigits / totalCountOfValidValues,
        type_float,
    )


def countAverageNumberOfCapitalisedValues(column):
    totalCapitalisedValues = column.str.findall(r"[A-Z]").str.len().sum()
    avgCapitalisedValues = totalCapitalisedValues / column.shape[0]

    return float(avgCapitalisedValues)


def countAverageNumberOfCharacters(column):
    totalDigits = 0
    for val in column:
        totalDigits += len(str(val))

    avgDigit = totalDigits / column.shape[0]

    return float(avgDigit)


# def addOutlierStatistics(stats):
#    # TODO: Add outliers. (lowest priority)
#    pass


def get_polygon_area(area):
    # TODO: return area in qm
    return area.area


def get_line_length(line):
    if not line:
        return None
    # TODO: return length in meters
    return line.length


def get_timestamp(value):
    return time.mktime(value.timetuple())


def get_pd_timestamp(datetime):
    return pd.Timestamp(datetime, unit="s")


def get_datetime_string(datetime):
    return datet.fromtimestamp(datetime).strftime(config.datetime_format[config.lang])


def getStatistics(dataset):
    data = dataset.data

    stats = {}
    totalRecords = data.shape[0]
    i = 0
    areas = None

    dataset.number_of_instances = data.shape[0]

    for colName in data:

        # Ignore longitude/latitude columns. They should be covered by geometry columns.
        for lon_lat_pair in dataset.lon_lat_pairs:
            if colName in lon_lat_pair.values():
                continue

        attribute = dataset.attributes[colName]

        simple_type = attribute.simple_data_type
        stats[colName] = {}
        transform_timestamp = False

        if simple_type == simple_type_string:
            # string statistics are based on string length
            column_data = data[colName].str.len()
        elif simple_type == simple_type_geometry:

            polygon_count = {}

            # TODO: Add fine-grained geo type earlier
            geo_type = Point

            if not areas:
                proj = pyproj.Transformer.from_crs(
                    dataset.coordinate_system, 4326, always_xy=True
                ).transform  # area polygons are in 4326

                dirName = os.path.dirname(__file__)
                dataFilePath = os.path.join(dirName, global_config.areas_file_path)
                areas_df = pd.read_csv(dataFilePath, sep="\t")

                areas = {}
                for indexArea, area in areas_df.iterrows():
                    polygonArea = area["polygon"]
                    polygonWKT = wkt.loads(polygonArea)
                    polygonObject = geometry.Polygon(polygonWKT)
                    areas[area["id"]] = polygonObject

            for geometry_object in data[colName]:
                if type(geometry_object) == Polygon:
                    geo_type = Polygon
                elif type(geometry_object) == LineString and geo_type == Point:
                    geo_type = LineString
                addSpatialValueDistribution(geometry_object, polygon_count, areas, proj)

            stats[colName][config.spatialValueDistribution] = {
                config.type: config.type_spatial_distribution,
                config.type_spatial_distribution_areas: polygon_count,
            }

            if geo_type == Polygon:
                # TODO: We could also add statistics on the polygon area by adding the next line and removing pass
                # column_data = data[colName].apply(area)
                pass
            elif geo_type == LineString:
                # TODO: We could also add statistics on the linestring length by adding the next line and removing pass
                # column_data = data[colName].apply(get_line_length)
                pass
            else:
                # TODO: No statistics for points
                pass
        elif simple_type == simple_type_datetime:
            column_data = data[colName].apply(get_timestamp)
            transform_timestamp = True
        else:
            column_data = data[colName]

        column = data[colName]

        addNumericValue(
            stats[colName], config.numberOfNullValues, int(column.isnull().sum())
        )
        addNumericValue(stats[colName], config.numberOfValidValues, int(totalRecords))
        addNumericValue(
            stats[colName],
            config.numberOfInvalidValues,
            int(data[colName].isna().sum()),
        )
        addNumericValue(
            stats[colName],
            config.numberOfValues,
            int(totalRecords),
            data_type=type_integer,
        )
        addNumericValue(
            stats[colName],
            config.numberOfValidNonNullValues,
            int(data[colName].count()),
            data_type=type_integer,
        )

        if simple_type in [simple_type_numeric, simple_type_string]:
            addGenericStatistics(column, stats[colName], simple_type)

        # deciles
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            deciles, deciles_data_type = addQuantiles(
                column_data, 10, transform_timestamp=transform_timestamp
            )
            stats[colName][config.deciles] = {
                config.type: config.type_list,
                config.id: config.deciles,
                config.list_data_type: get_sml_type_from_python_type(deciles_data_type),
                config.type_list_values: deciles,
            }

        # quartiles
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            quartiles, quartiles_data_type = addQuantiles(
                column_data, 4, transform_timestamp=transform_timestamp
            )
            stats[colName][config.quartiles] = {
                config.type: config.type_box_plot,
                config.id: config.quartiles,
                config.list_data_type: get_sml_type_from_python_type(
                    quartiles_data_type
                ),
                config.type_box_plot_values: quartiles,
            }

        # number of distinct values
        if simple_type == simple_type_geometry:
            number_of_distinct_values = column_data.nunique(dropna=True)
        elif simple_type != simple_type_boolean and simple_type != type_numeric_list:
            number_of_distinct_values = data[colName].nunique(dropna=True)
            addNumericValue(
                stats[colName], config.numberOfDistinctValues, number_of_distinct_values
            )

        # median
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            addNumericValue(
                stats[colName],
                config.median,
                column_data.median(),
                type_float,
                transform_timestamp=transform_timestamp,
            )

        # mean
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            addNumericValue(
                stats[colName],
                config.mean,
                column_data.mean(),
                type_float,
                transform_timestamp=transform_timestamp,
            )

        # maximum
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            addNumericValue(
                stats[colName],
                config.maximum,
                column_data.max(skipna=True),
                attribute.data_type,
                transform_timestamp=transform_timestamp,
            )

        # minimum
        if simple_type in [simple_type_numeric, simple_type_datetime]:
            addNumericValue(
                stats[colName],
                config.minimum,
                column_data.min(skipna=True),
                attribute.data_type,
                transform_timestamp=transform_timestamp,
            )

        if simple_type in [simple_type_numeric]:
            addNumericValue(
                stats[colName], config.standardDeviation, column_data.std(), type_float
            )

        if simple_type in [simple_type_numeric, simple_type_datetime]:
            buckets, buckets_data_type = addHistograms(
                stats,
                column_data,
                colName,
                number_of_distinct_values,
                transform_timestamp=transform_timestamp,
            )
            stats[colName][config.histogram] = {
                config.type: config.type_histogram,
                config.bucket_data_type: get_sml_type_from_python_type(
                    buckets_data_type
                ),
                config.type_histogram_buckets: buckets,
            }

        if simple_type in [simple_type_string, simple_type_boolean]:
            stats[colName][config.value_distribution] = {
                config.type: config.type_bar_chart,
                config.id: config.value,
                config.type_bar_chart_bars: addValueDistribution(column),
            }  # if data[colName].dtype == 'object' and colName != 'geometry' else 0
        i = i + 1

    # sample
    sample = dataset.sample(10, recompute_statistics=False)

    # Drop all geometry columns. Not only "geometry" column
    for attribute in dataset.attributes.values():
        if attribute.simple_data_type == simple_type_geometry:
            sample.data = sample.data.drop(labels=attribute.id, axis=1)
        elif attribute.simple_data_type == simple_type_numeric_list:
            # for the JSON export of the sample, better have the list as a string
            sample.data[attribute.id] = sample.data[attribute.id].apply(
                lambda row: "<" + ", ".join([str(x) for x in row]) + ">"
            )

    dataset.data_sample = sample.data

    return stats
