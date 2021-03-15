from __future__ import annotations
import simpleml.util._jsonLabels_util as config
import os
import pandas as pd
import numpy as np
import pyproj
from shapely import geometry, ops, wkt, wkb
#from physt import histogram, binnings, h1, h2, h3

def addGeoStatistics(stats, dataset) -> dict:
    proj = pyproj.Transformer.from_crs(3857, 4326, always_xy=True).transform
    dirName = os.path.dirname(__file__)
    filePath = '../../data/ni_areas.tsv'
    dataFilePath = os.path.join(dirName, filePath)
    areas = pd.read_csv(dataFilePath, sep='\t')

    x = 0
    for indexArea, area in areas.iterrows():
        #print('area')
        #print(area)
        #print('polygon')
        polygonArea = area['polygon']
        #print(polygonArea)

        polygonWKT = wkt.loads(polygonArea)
        polygonObject = geometry.Polygon(polygonWKT)

        for indexData, data in dataset.data.iterrows():
            line = data['geometry']
            transformedLine = ops.transform(proj, line)
            #print(transformedLine)

            lineIntersectsPolygon = transformedLine.within(polygonObject)
            if lineIntersectsPolygon:
                print('yes')

            x += 1
            #print(x)

    return stats


def addValueDistributions(stats, dataset):
    # TODO: for text data: write down the 10 most frequent values and their count
    # TODO: for boolean data: how many True? how many False?
    pass


def addHistograms(column, name) -> dict:
    count = []
    division = []
    column = column.dropna()
    if isinstance(column, pd.DataFrame):
        if name == 'geometry':
            count, division = np.histogram(column['length'], bins='auto')
        else:
            count, division = np.histogram(column['strLength'], bins='auto')
    else:
        if column.dtype == 'bool':
            count, division = np.histogram(column.astype(int), bins='auto')
        elif column.dtype != 'datetime64[ns]':
            count, division = np.histogram(column, bins='auto')

    #count, division = np.histogram(column, bins='auto')

    #hist = histogram(column)
    #values = hist.frequencies
    #bins = hist.bins
    #histogramOutput = {}

    #histogramOutput[config.values] = [{'bucketMinimum': i[0], 'bucketMaximum': i[1], 'value': int(j)} for i, j in zip(bins, values)]

    return [{'bucketMinimum': int(i), 'value': int(j)} for i, j in zip(division, count)]

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
        elif column.dtype != 'datetime64[ns]':
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

    avgDigits = totalDigits/column.shape[0]

    return float(avgDigits)

def countAverageNumberOfSpecialCharacters(column):
    totalSpecialCharacters = 0
    for val in column:
        for character in str(val):
            if not character.isalnum():
                if character != " ":
                    totalSpecialCharacters += 1

    avgSpecialCharacters = totalSpecialCharacters / column.shape[0]

    return float(avgSpecialCharacters)

def countAverageNumberOfCapitalisedValues(column):
    totalCapitalisedValues = column.str.findall(r'[A-Z]').str.len().sum()
    avgCapitalisedValues = totalCapitalisedValues / column.shape[0]

    return float(avgCapitalisedValues)

def countAverageNumberOfCharacters(column):
    totalDigits = 0
    for val in column:
        totalDigits += len(str(val))

    avgDigit = totalDigits/column.shape[0]

    return float(avgDigit)

def transformStatistics(stats):
    # TODO: remove null and NaN values (?)
    # TODO: use the well-known keys (see https://smlpub.l3s.uni-hannover.de/confluence/download/attachments/18907912/input_profile.json?version=2&modificationDate=1599050322971&api=v2)

    # TODO: Transform the stats data frame into dictionary???

    # This does not work!
    for attribute in stats:
        stats[attribute]["numberOfValues"] = stats[attribute].pop("count")

    # TODO: Does not work yet.
    pass

def addOutlierStatistics(stats, dataset):
    # TODO: Add outliers. (lowest priority)
    pass


def getStatistics(dataset: Dataset) -> dict:
    #print(dataset.data.dtypes)
    data = dataset.data
    stats = {
#              "file": {
#                "null_string": "",
#                "hasHeader": "true",
#                "fileLocation": "winequality-white_binary.csv",
#                "separator": ";"
#              }
            }
    #print(type(stats))
    #addGeoStatistics(stats, dataset)

    totalRecords = data.shape[0]
    i = 0
    for colName in data:
        #colName = data.columns[i]
        stats[colName] = {}
        #print('type')
        #print(data[colName].dtype)
        print(colName)
        column_data = data[colName]
        columnDF = pd.DataFrame(data[colName])
        geometryDF = pd.DataFrame()
        if data[colName].dtype == 'object':
            columnDF['strLength'] = data[colName].str.len()
            column_data = columnDF['strLength']

        if colName == 'geometry':
            for line in data[colName]:
                #print(type(data[colName]))
                print(line.length)
                #columnDF['line_len'] = line.length
                geometryDF = geometryDF.append({"line":line, "length":line.length}, ignore_index=True)
                column_data = geometryDF
            print(geometryDF)


        stats[colName][config.numberOfNullValues] = {}
        stats[colName][config.numberOfNullValues][config.value] = int(data[colName].isnull().sum())

        stats[colName][config.decile] = addDecile(column_data, colName)

        stats[colName][config.quartile] = addQuartile(column_data, colName)

        stats[colName][config.averageNumberOfSpecialCharacters] = {}
        stats[colName][config.averageNumberOfSpecialCharacters][config.value] = countAverageNumberOfSpecialCharacters(data[colName])

        stats[colName][config.averageNumberOfTokens] = {}
        stats[colName][config.averageNumberOfTokens][config.value] = 1

        stats[colName][config.numberOfValidValues] = {}
        stats[colName][config.numberOfValidValues][config.value] = int(totalRecords)

        stats[colName][config.numberOfOutliersBelow] = {}
        stats[colName][config.numberOfOutliersBelow][config.value] = 0

        stats[colName][config.averageNumberOfCapitalisedValues] = {}
        stats[colName][config.averageNumberOfCapitalisedValues][config.value] = countAverageNumberOfCapitalisedValues(data[colName]) if data[colName].dtype == 'object' and colName != 'geometry' else 0

        stats[colName][config.averageNumberOfDigits] = {}
        stats[colName][config.averageNumberOfDigits][config.value] = countAverageNumberOfDigits(data[colName])

        stats[colName][config.numberOfOutliersAbove] = {}
        stats[colName][config.numberOfOutliersAbove][config.value] = 0

        stats[colName][config.numberOfDistinctValues] = {}
        stats[colName][config.numberOfDistinctValues][config.value] = 0

        stats[colName][config.histogram] = addHistograms(column_data, colName)

        stats[colName][config.averageNumberOfCharacters] = {}
        stats[colName][config.averageNumberOfCharacters][config.value] = countAverageNumberOfCharacters(data[colName])

        stats[colName][config.median] = {}
        #stats[colName][config.median][config.value] = float("{:.2f}".format(columnDF['strLength'].median())) if data[colName].dtype == 'object' and colName != 'geometry' else 0 if data[colName].dtype == 'datetime64[ns]' else float("{:.2f}".format(data[colName].median()))
        stats[colName][config.median][config.value] = int(data[colName].median()) if data[colName].dtype == 'int64' else 0

        stats[colName][config.numberOfValues] = {}
        stats[colName][config.numberOfValues][config.value] = int(totalRecords)

        stats[colName][config.mean] = {}
        stats[colName][config.mean][config.value] = int(data[colName].mean()) if data[colName].dtype == 'int64' else 0

        stats[colName][config.numberOfValidNonNullValues] = {}
        stats[colName][config.numberOfValidNonNullValues][config.value] = int(len(data[colName])) - int(data[colName].isna().sum())

        stats[colName][config.maximum] = {}
        stats[colName][config.maximum][config.value] = int(data[colName].max(skipna=True)) if data[colName].dtype == 'int64' else 0

        stats[colName][config.minimum] = {}
        stats[colName][config.minimum][config.value] = int(data[colName].min(skipna=True)) if data[colName].dtype == 'int64' else 0

        stats[colName][config.standardDeviation] = {}
        stats[colName][config.standardDeviation][config.value] = int(data[colName].std()) if data[colName].dtype == 'int64' else 0

        stats[colName][config.numberOfInvalidValues] = {}
        stats[colName][config.numberOfInvalidValues][config.value] = int(data[colName].isna().sum())

        i = i+1

    #print('fixed acidity')
    #print(data['fixed acidity'])
    #hist = histogram(data['fixed acidity'])
    #print(type(hist.bins))

    #n, bins, patches = plt.hist(data['fixed acidity'], 10, facecolor='blue', alpha=0.5)
    #plt.show()

    #stats = dataset.data.describe(include='all', datetime_is_numeric=True)
    #stats = stats.to_dict()

    #stats['nullValues'] = data.isnull().sum()
    #stats['validValues'] = data.notnull().sum()
    #stats['median'] = data.median()
    #stats['hist'] = hist.bins

    #addValueDistributions(stats, dataset)
    #addHistograms(stats, dataset)
    #addGeoStatistics(stats, dataset)
    #addOutlierStatistics(stats, dataset)
    #transformStatistics(stats)

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
