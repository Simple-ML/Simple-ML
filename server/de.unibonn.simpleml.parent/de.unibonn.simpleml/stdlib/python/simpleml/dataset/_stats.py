from __future__ import annotations
import simpleml.util._jsonLabels_util as config
import os
import pandas as pd
import numpy as np
import pyproj
import re
from shapely import geometry, ops, wkt, wkb


def addGeoStatistics(stats, dataset) -> dict:
    proj = pyproj.Transformer.from_crs(3857, 4326, always_xy=True).transform
    dirName = os.path.dirname(__file__)
    filePath = '../../data/ni_areas.tsv'
    dataFilePath = os.path.join(dirName, filePath)
    areas = pd.read_csv(dataFilePath, sep='\t')

    x = 0
    for indexArea, area in areas.iterrows():
        # print('area')
        # print(area)
        # print('polygon')
        polygonArea = area['polygon']
        # print(polygonArea)

        polygonWKT = wkt.loads(polygonArea)
        polygonObject = geometry.Polygon(polygonWKT)

        for indexData, data in dataset.data.iterrows():
            line = data['geometry']
            transformedLine = ops.transform(proj, line)
            # print(transformedLine)

            lineIntersectsPolygon = transformedLine.within(polygonObject)
            if lineIntersectsPolygon:
                print('yes')

            x += 1
            # print(x)

    return stats


def addValueDistribution(stats, column, name) -> dict:
    value_distribution = []

    for value, count in column.value_counts().head(10).iteritems():
        value_distribution.append({config.value_distribution_value: value,
                                   config.value_distribution_number_of_instances: int(count)})

    return value_distribution


def addHistograms(stats, column, name, number_of_unique_values) -> dict:
    count = []
    division = []
    column = column.dropna()
    if isinstance(column, pd.DataFrame):
        if name == 'geometry':
            count, division = np.histogram(column['length'], bins=min(10, number_of_unique_values))
        else:
            count, division = np.histogram(column['strLength'], bins=min(10, number_of_unique_values))
    else:
        if column.dtype == 'bool':
            count, division = np.histogram(column.astype(int), bins=min(10, number_of_unique_values))
        elif column.dtype != 'datetime64[ns]':
            count, division = np.histogram(column, bins=min(10, number_of_unique_values))

    histograms = [{config.bucketMinimum: i, config.bucketValue: int(j)} for i, j in zip(division, count)]

    # add bucket maximum from the column maximum and the bucket minimum of the next bucket
    bucket_maximum = stats[name][config.maximum]
    for i in range(len(histograms) - 1, -1, -1):
        bucket = histograms[i]
        bucket[config.bucketMaximum] = bucket_maximum
        bucket_maximum = bucket[config.bucketMinimum]

    return histograms


def addDecile(column, name) -> dict:
    decileOutput = {}
    decileList = []
    decileResult2 = []
    if isinstance(column, pd.DataFrame):
        if name == 'geometry':
            decileResult = pd.qcut(column['length'], 10, retbins=True, labels=False, duplicates='drop')
            decileResult2 = decileResult[1]
        else:
            decileResult = pd.qcut(column['strLength'], 10, retbins=True, labels=False, duplicates='drop')
            decileResult2 = decileResult[1]
    else:
        if column.dtype == 'bool':
            decileResult = pd.qcut(column.astype(int), 10, retbins=True, labels=False, duplicates='drop')
            decileResult2 = decileResult[1]
        elif column.dtype == 'datetime64[ns]':
            # print(pd.Series.dt.strftime(column))
            # date64 = np.datetime64(column)
            # time_stamp = (date64 - np.datetime64('1970-01-01T00:00:00Z')) / np.timedelta64(1, 's')
            # print(time_stamp)
            time_stamp = column

        else:
            decileResult = pd.qcut(column, 10, retbins=True, labels=False, duplicates='drop')
            decileResult2 = decileResult[1]

    for val in decileResult2:
        decileList.append(dict({'value': float("{:.2f}".format(val))}))

    decileOutput[config.values] = decileList

    return decileOutput


def addQuartile(column, name) -> dict:
    quartile_output = {}
    quartile_list = []
    quartile_result2 = []
    if isinstance(column, pd.DataFrame):
        if name == 'geometry':
            quartile_result = pd.qcut(column['length'], 4, retbins=True, labels=False, duplicates='drop')
            quartile_result2 = quartile_result[1]
        else:
            quartile_result = pd.qcut(column['strLength'], 4, retbins=True, labels=False, duplicates='drop')
            quartile_result2 = quartile_result[1]
    else:
        if column.dtype == 'bool':
            quartile_result = pd.qcut(column.astype(int), 4, retbins=True, labels=False, duplicates='drop')
            quartile_result2 = quartile_result[1]
        elif column.dtype != 'datetime64[ns]':
            quartile_result = pd.qcut(column, 4, retbins=True, labels=False, duplicates='drop')
            quartile_result2 = quartile_result[1]

    for val in quartile_result2:
        quartile_list.append(dict({'value': float("{:.2f}".format(val))}))

    quartile_output[config.values] = quartile_list

    return quartile_output


def countAverageNumberOfDigits(column):
    totalDigits = 0
    for val in column:
        for character in str(val):
            if character.isdigit():
                totalDigits += 1

    avgDigits = totalDigits / column.shape[0]

    return float(avgDigits)


def addGenericStatistics(column, column_stats):
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

    column_stats[config.averageNumberOfSpecialCharacters] = totalSpecialCharacters / totalCountOfValidValues
    column_stats[config.averageNumberOfTokens] = totalNumberOfTokens / totalCountOfValidValues
    column_stats[config.averageNumberOfCapitalisedValues] = totalNumberOfCapitalisedValues / totalCountOfValidValues
    column_stats[config.averageNumberOfCharacters] = totalNumberOfCharacters / totalCountOfValidValues
    column_stats[config.averageNumberOfDigits] = totalNumberOfDigits / totalCountOfValidValues


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


def getStatistics(dataset: Dataset) -> dict:
    data = dataset.data

    stats = {}
    totalRecords = data.shape[0]
    i = 0

    dataset.number_of_instances = data.shape[0]

    for colName in data:
        # colName = data.columns[i]
        stats[colName] = {}
        # print('type')
        # print(data[colName].dtype)
        # print(colName)
        column_data = data[colName]
        columnDF = pd.DataFrame(data[colName])
        geometryDF = pd.DataFrame()
        if data[colName].dtype == 'object':
            columnDF['strLength'] = data[colName].str.len()
            column_data = columnDF['strLength']
            column_data = columnDF['strLength']

        if colName == 'geometry':
            for line in data[colName]:
                # print(type(data[colName]))
                # print(line.length)
                # columnDF['line_len'] = line.length
                geometryDF = geometryDF.append({"line": line, "length": line.length}, ignore_index=True)
                column_data = geometryDF
            # print(geometryDF)

        stats[colName][config.numberOfNullValues] = int(data[colName].isnull().sum())

        stats[colName][config.decile] = addDecile(column_data, colName)

        stats[colName][config.quartile] = addQuartile(column_data, colName)

        stats[colName][config.numberOfValidValues] = int(totalRecords)

        addGenericStatistics(column_data, stats[colName])

        stats[colName][config.numberOfDistinctValues] = data[colName].nunique(dropna=True)

        stats[colName][config.median] = data[colName].median()

        stats[colName][config.numberOfValues] = int(totalRecords)

        stats[colName][config.mean] = data[colName].mean()

        stats[colName][config.numberOfValidNonNullValues] = int(data[colName].count())

        stats[colName][config.maximum] = data[colName].max(skipna=True)

        stats[colName][config.minimum] = data[colName].min(skipna=True)

        stats[colName][config.standardDeviation] = data[colName].std()

        stats[colName][config.numberOfInvalidValues] = int(data[colName].isna().sum())

        stats[colName][config.histogram] = addHistograms(stats, column_data, colName,
                                                         stats[colName][config.numberOfDistinctValues])

        stats[colName][config.value_distribution] = addValueDistribution(stats, column_data, colName)

        i = i + 1

    # sample
    sample = dataset.sample(10)
    dataset.data_sample = sample.data
    dataset.addSample()

    # print('fixed acidity')
    # print(data['fixed acidity'])
    # hist = histogram(data['fixed acidity'])
    # print(type(hist.bins))

    # n, bins, patches = plt.hist(data['fixed acidity'], 10, facecolor='blue', alpha=0.5)
    # plt.show()

    # stats = dataset.data.describe(include='all', datetime_is_numeric=True)
    # stats = stats.to_dict()

    # stats['nullValues'] = data.isnull().sum()
    # stats['validValues'] = data.notnull().sum()
    # stats['median'] = data.median()
    # stats['hist'] = hist.bins

    # addValueDistributions(stats, dataset)
    # addHistograms(stats, dataset)
    # addGeoStatistics(stats, dataset)
    # addOutlierStatistics(stats, dataset)
    # transformStatistics(stats)

    # TODO: Make sure statistics about date/time columns are working and shown as dates

    # TODO: Check if we have the following statistics per attribute:
    # - number of null values
    # - number of valid values
    # - median
    # - histogram (10% bucket/interval size)
    # - value distributions
    # - mean
    # - number of values
    # - numberOfValidNonNullValues
    # - minimum
    # - maximum
    # - standardDeviation
    # - numberOfInvalidValues
    # - deciles and quantiles
    # - outliers

    return stats
