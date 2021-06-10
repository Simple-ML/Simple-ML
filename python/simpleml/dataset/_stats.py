from __future__ import annotations
import simpleml.util._jsonLabels_util as config
import simpleml.util._global_configurations as global_config
import os
import pandas as pd
import numpy as np
import pyproj
import re
from shapely import geometry, ops, wkt, wkb
from shapely.geometry import Point, LineString, Polygon
import time
from datetime import datetime as datet


def addSpatialValueDistribution(geometry_object, polygon_count, areas, proj) -> dict:
    for polygon_id, polygonObject in areas.items():
        transformedLine = ops.transform(proj, geometry_object)

        lineIntersectsPolygon = transformedLine.within(polygonObject)
        if lineIntersectsPolygon:
            if (polygon_id not in polygon_count):
                polygon_count[polygon_id] = 1
            else:
                polygon_count[polygon_id] = polygon_count[polygon_id] + 1


def addValueDistribution(stats, column, name) -> dict:
    value_distribution = []

    for value, count in column.value_counts().head(10).iteritems():
        value_distribution.append({config.value_distribution_value: value,
                                   config.value_distribution_number_of_instances: int(count)})

    return value_distribution


def addHistograms(stats, column, name, number_of_unique_values, transform_timestamp=False) -> dict:
    count = []
    division = []
    column = column.dropna()

    count, division = np.histogram(column, bins=min(10, number_of_unique_values))

    histograms = [{config.bucketMinimum: i, config.bucketValue: int(j)} for i, j in zip(division, count)]

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
            bucket[config.bucketMinimum] = get_pd_timestamp(bucket[config.bucketMinimum])
            if i < len(histograms): # the last bucket maximum is date already
                bucket[config.bucketMaximum] = get_pd_timestamp(bucket[config.bucketMaximum])

    return histograms


def addQuantiles(column, name, bins, transform_timestamp=False) -> dict:
    quantiles = pd.qcut(column, bins, retbins=True, labels=False, duplicates='drop')[1].tolist()
    if transform_timestamp:
        quantiles = [get_pd_timestamp(item) for item in quantiles]
    return quantiles


def addNumericValue(column_stats, name, value, data_type=None, transform_timestamp=False):
    simple_type = config.type_numeric

    if transform_timestamp:
        value = get_pd_timestamp(value)
        data_type = config.type_datetime
        simple_type = config.type_datetime

    column_stats[name] = createNumericValue(name, value, simple_type, data_type)


def createNumericValue(name, value, simple_type, data_type=None):
    if not data_type:
        data_type = config.data_type_labels[type(value)]
    return {config.type: simple_type,
            config.type_numeric_data_type: data_type,
            config.type_numeric_value: value,
            config.i18n_id: name}


def addGenericStatistics(column, column_stats, column_type):
    # TODO: for efficiency and to avoid number overflow: don't compute total sum
    totalSpecialCharacters = 0
    totalNumberOfTokens = 0
    totalNumberOfCapitalisedValues = 0
    totalNumberOfCharacters = 0
    totalNumberOfDigits = 0

    totalCountOfValidValues = 0

    for val in column:
        if not val:
            continue
        totalCountOfValidValues += 1
        val_str = str(val)
        for character in val_str:
            if not character.isalnum():
                if character != " ":
                    totalSpecialCharacters += 1
        totalNumberOfTokens += len(re.findall(r'\w+', val_str))
        if val_str and val_str[0].isupper():
            totalNumberOfCapitalisedValues += 1
        totalNumberOfCharacters += len(val_str)
        totalNumberOfDigits += len(re.sub("[^0-9]", "", val_str))
        # print(totalCountOfValidValues)

    if (totalCountOfValidValues == 0):
        totalCountOfValidValues = 1

    if column_type == config.type_string:
        addNumericValue(column_stats, config.averageNumberOfSpecialCharacters,
                        totalSpecialCharacters / totalCountOfValidValues)
    if column_type == config.type_string:
        addNumericValue(column_stats, config.averageNumberOfTokens, totalNumberOfTokens / totalCountOfValidValues)
    if column_type == config.type_string:
        addNumericValue(column_stats, config.averageNumberOfCapitalisedValues,
                        totalNumberOfCapitalisedValues / totalCountOfValidValues)
    if column_type == config.type_string:
        addNumericValue(column_stats, config.averageNumberOfCharacters,
                        totalNumberOfCharacters / totalCountOfValidValues)
    addNumericValue(column_stats, config.averageNumberOfDigits, totalNumberOfDigits / totalCountOfValidValues)


def countAverageNumberOfCapitalisedValues(column):
    totalCapitalisedValues = column.str.findall(r'[A-Z]').str.len().sum()
    avgCapitalisedValues = totalCapitalisedValues / column.shape[0]

    return float(avgCapitalisedValues)


def countAverageNumberOfCharacters(column):
    totalDigits = 0
    for val in column:
        totalDigits += len(str(val))

    avgDigit = totalDigits / column.shape[0]

    return float(avgDigit)


def addOutlierStatistics(stats, dataset):
    # TODO: Add outliers. (lowest priority)
    pass


def get_polygon_area(area):
    # TODO: return area in qm
    return area.area


def get_line_length(line):
    # TODO: return length in meters
    return line.length


def get_timestamp(value):
    return time.mktime(value.timetuple())

def get_pd_timestamp(datetime):
    return pd.Timestamp(datetime, unit='s')

def get_datetime_string(datetime):
    return datet.fromtimestamp(datetime).strftime(config.datetime_format[config.lang])

def getStatistics(dataset: Dataset) -> dict:
    data = dataset.data
    # print(data)

    stats = {}
    totalRecords = data.shape[0]
    i = 0
    areas = None

    dataset.number_of_instances = data.shape[0]

    for colName in data:

        simple_type = dataset.simple_data_types[colName]
        stats[colName] = {}
        transform_timestamp = False

        if simple_type == config.type_string:
            # string statistics are based on string length
            column_data = data[colName].str.len()
        elif simple_type == config.type_geometry:
            polygon_count = {}

            # TODO: Add fine-grained geo type earlier
            geo_type = Point

            if not areas:
                proj = pyproj.Transformer.from_crs(3857, 4326, always_xy=True).transform
                dirName = os.path.dirname(__file__)
                dataFilePath = os.path.join(dirName, global_config.areas_file_path)
                areas_df = pd.read_csv(dataFilePath, sep='\t')

                areas = {}
                for indexArea, area in areas_df.iterrows():
                    polygonArea = area['polygon']
                    polygonWKT = wkt.loads(polygonArea)
                    polygonObject = geometry.Polygon(polygonWKT)
                    areas[area['id']] = polygonObject

            for geometry_object in data[colName]:
                if type(geometry_object) == Polygon:
                    geo_type = Polygon
                elif type(geometry_object) == LineString and geo_type == Point:
                    geo_type = LineString
                addSpatialValueDistribution(geometry_object, polygon_count, areas, proj)

            stats[colName][config.spatialValueDistribution] = polygon_count
            if geo_type == Polygon:
                column_data = data[colName].apply(area)
            elif geo_type == LineString:
                column_data = data[colName].apply(get_line_length)
            else:
                # TODO: No statistics for points
                pass
        elif simple_type == config.type_datetime:
            column_data = data[colName].apply(get_timestamp)
            transform_timestamp = True
        else:
            column_data = data[colName]

        column = data[colName]

        addNumericValue(stats[colName], config.numberOfNullValues, int(column.isnull().sum()))
        addNumericValue(stats[colName], config.numberOfValidValues, int(totalRecords))
        addNumericValue(stats[colName], config.numberOfInvalidValues, int(data[colName].isna().sum()))
        addNumericValue(stats[colName], config.numberOfValues, int(totalRecords), data_type=config.type_integer)
        addNumericValue(stats[colName], config.numberOfValidNonNullValues, int(data[colName].count()),
                        data_type=config.type_integer)

        if simple_type in [config.type_numeric, config.type_string]:
            addGenericStatistics(column, stats[colName], simple_type)

        # deciles
        if simple_type in [config.type_numeric, config.type_datetime]:
            stats[colName][config.deciles] = {config.type: config.type_list,
                                              config.type_list_values: addQuantiles(column_data, colName, 10,
                                                                                    transform_timestamp=transform_timestamp)}

        # quartiles
        if simple_type in [config.type_numeric, config.type_datetime]:
            stats[colName][config.quartiles] = {config.type: config.type_box_plot,
                                                config.type_box_plot_values: addQuantiles(column_data, colName, 4,
                                                                                          transform_timestamp=transform_timestamp)}

        # number of distinct values
        if simple_type == config.type_geometry:
            number_of_distinct_values = column_data.nunique(dropna=True)
        elif simple_type != config.type_bool:
            number_of_distinct_values = data[colName].nunique(dropna=True)
            addNumericValue(stats[colName], config.numberOfDistinctValues, number_of_distinct_values)

        # median
        if simple_type in [config.type_numeric, config.type_datetime]:
            addNumericValue(stats[colName], config.median, column_data.median(),
                            transform_timestamp=transform_timestamp)

        if simple_type in [config.type_numeric, config.type_datetime]:
            addNumericValue(stats[colName], config.mean, column_data.mean(), transform_timestamp=transform_timestamp)

        if simple_type in [config.type_numeric, config.type_datetime]:
            addNumericValue(stats[colName], config.maximum, column_data.max(skipna=True),
                            transform_timestamp=transform_timestamp)

        if simple_type in [config.type_numeric, config.type_datetime]:
            addNumericValue(stats[colName], config.minimum, column_data.min(skipna=True),
                            transform_timestamp=transform_timestamp)

        if simple_type in [config.type_numeric]:
            addNumericValue(stats[colName], config.standardDeviation, column_data.std())

        if simple_type in [config.type_numeric, config.type_datetime]:
            stats[colName][config.histogram] = {config.type: config.type_histogram,
                                                config.type_histogram_buckets: addHistograms(stats, column_data,
                                                                                             colName,
                                                                                             number_of_distinct_values,
                                                                                             transform_timestamp=transform_timestamp)}

        if simple_type in [config.type_string, config.type_bool]:
            stats[colName][config.value_distribution] = {config.type: config.type_bar_chart,
                                                         config.type_bar_chart_bars: addValueDistribution(stats, column,
                                                                                                          colName)}  # if data[colName].dtype == 'object' and colName != 'geometry' else 0

        i = i + 1

    # sample
    sample = dataset.sample(10)

    if 'geometry' in sample.data:
        sample.data = sample.data.drop(labels='geometry', axis=1)

    dataset.data_sample = sample.data
    dataset.addSample()

    return stats
