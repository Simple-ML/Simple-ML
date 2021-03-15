from __future__ import annotations
import simpleml.util._jsonLabels_util as config
import os
import pandas as pd
import numpy as np
from plpygis import Geometry
from physt import histogram, binnings, h1, h2, h3
import matplotlib.pyplot as plt
import json
import shapely
import geopandas as gpd


def addGeoStatistics(stats, dataset) -> dict:
    # TODO: Create geo distribution statistics

    import pyproj
    from shapely.geometry import shape, Polygon
    from shapely.ops import transform
    print(pyproj.__version__)  # 2.4.1
    print(pyproj.proj_version_str)  # 6.2.1
    proj = pyproj.Transformer.from_crs(3857, 4326, always_xy=True).transform
    #print(dataset.data['geometry'])
    dirName = os.path.dirname(__file__)
    filePath = '../../data/ni_areas_test.tsv'
    dataFilePath = os.path.join(dirName, filePath)
    #areas = pd.read_csv(dataFilePath, sep='\t')
    areas = pd.read_table(dataFilePath)
    #areas2 = gpd.read_file(dataFilePath)
    #areas2.crs = 'epsg:4326'

    #print(areas2)
    #print('polygon')
    #print(areas2['polygon'])
    i = 0
    x = 0
    for indexArea, area in areas.iterrows():
        print('area')
        print(area)
        print('polygon')
        print(area['polygon'])


        import shapely.geometry

        import geopandas

        a1 = list(area['polygon'])
        #shapely_polygon = shapely.geometry.Polygon(a1)
        #a2 = geopandas.GeoSeries([shapely_polygon]).__geo_interface__
        #print(shape(a2['features'][0]['geometry']))

        for indexData, data in dataset.data.iterrows():
            line = shape(Geometry(data['geometry']).geojson)
            #print(area['polygon_simple'])
            print('Line')
            print(line)
            transformed_line = transform(proj, line)
            print(transformed_line)
            #print(transformed_line.within(area['polygon']))
            x += 1
            print(x)

            '''
            #print('asd')
            for long, lat in points:
                longTransformed, latTransformed = proj.transform(long, lat)
                coordinates = list((longTransformed, latTransformed))
                #coor1 = Geometry(coordinates)
                print(coordinates)
                if(str(longTransformed) in area['polygon']):
                    i += 1
                    #print('yes')
            #print(i)
            '''
    #print(i)
    #stats.loc['scount'] = i
    return stats


def addValueDistributions(stats, dataset):
    # TODO: for text data: write down the 10 most frequent values and their count
    # TODO: for boolean data: how many True? how many False?
    pass


def addHistograms(collumn) -> dict:
    hist = histogram(collumn)
    values = hist.frequencies
    bins = hist.bins

    histogramOutput = {}

    histogramOutput[config.values] = [{'bucketMaximum': i[1], 'bucketMinimum': i[0], 'value': int(j)} for i, j in zip(bins, values)]

    return histogramOutput

def addDecile(collumn) -> dict:
    decileOutput = {}
    decileList = []
    decileResult = pd.qcut(collumn, 10, retbins=True, labels=False)

    for val in decileResult[1]:
        decileList.append(dict({'value': val}))

    decileOutput[config.values] = decileList

    return decileOutput


def addQuartile(collumn) -> dict:
    decileOutput = {}
    decileList = []
    decileResult = pd.qcut(collumn, 4, retbins=True, labels=False)

    for val in decileResult[1]:
        decileList.append(dict({'value': val}))

    decileOutput[config.values] = decileList

    return decileOutput

def countAverageNumberOfDigits(collumn):
    totalDigits = 0
    for val in collumn:
        totalDigits += len(str(val))

    countAvgDigit = totalDigits/collumn.shape[0]

    return countAvgDigit

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
              "file": {
                "null_string": "",
                "hasHeader": "true",
                "fileLocation": "winequality-white_binary.csv",
                "separator": ";"
              }
            }
    #print(type(stats))
    print(data)

    totalRecords = data.shape[0]
    i = 0
    for col in data:
        colName = data.columns[i]
        stats[colName] = {}

        stats[colName][config.numberOfNullValues] = {}
        stats[colName][config.numberOfNullValues][config.value] = int(data[col].isnull().sum())

        stats[colName][config.decile] = addDecile(data[col])

        stats[colName][config.quartile] = addQuartile(data[col])

        stats[colName][config.averageNumberOfSpecialCharacters] = {}
        stats[colName][config.averageNumberOfSpecialCharacters][config.value] = 0

        stats[colName][config.averageNumberOfTokens] = {}
        stats[colName][config.averageNumberOfTokens][config.value] = 1

        stats[colName][config.numberOfValidValues] = {}
        stats[colName][config.numberOfValidValues][config.value] = 0

        stats[colName][config.numberOfOutliersBelow] = {}
        stats[colName][config.numberOfOutliersBelow][config.value] = 0

        stats[colName][config.averageNumberOfCapitalisedValues] = {}
        stats[colName][config.averageNumberOfCapitalisedValues][config.value] = 0

        stats[colName][config.averageNumberOfDigits] = {}
        stats[colName][config.averageNumberOfDigits][config.value] = countAverageNumberOfDigits(data[col])

        stats[colName][config.numberOfOutliersAbove] = {}
        stats[colName][config.numberOfOutliersAbove][config.value] = 0

        stats[colName][config.numberOfDistinctValues] = {}
        stats[colName][config.numberOfDistinctValues][config.value] = len(np.unique(data[col])) - int(data[col].isna().sum())

        stats[colName][config.histogram] = addHistograms(data[col])

        stats[colName][config.averageNumberOfCharacters] = {}
        stats[colName][config.averageNumberOfCharacters][config.value] = 0

        stats[colName][config.median] = {}
        stats[colName][config.median][config.value] = data[col].median()

        stats[colName][config.numberOfValues] = {}
        stats[colName][config.numberOfValues][config.value] = totalRecords

        stats[colName][config.mean] = {}
        stats[colName][config.mean][config.value] = int(data[col].mean())

        stats[colName][config.numberOfValidNonNullValues] = {}
        stats[colName][config.numberOfValidNonNullValues][config.value] = int(len(data[col])) - int(data[col].isna().sum())

        stats[colName][config.maximum] = {}
        stats[colName][config.maximum][config.value] = data[col].max(skipna=True)

        stats[colName][config.minimum] = {}
        stats[colName][config.minimum][config.value] = data[col].min(skipna=True)

        stats[colName][config.standardDeviation] = {}
        stats[colName][config.standardDeviation][config.value] = int(data[col].std())

        stats[colName][config.numberOfInvalidValues] = {}
        stats[colName][config.numberOfInvalidValues][config.value] = int(data[col].isna().sum())

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
    addGeoStatistics(stats, dataset)
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
