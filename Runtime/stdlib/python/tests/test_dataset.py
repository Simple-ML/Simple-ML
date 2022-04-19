from datetime import datetime

import numpy as np
from simpleml.dataset import (
    Instance,
    StandardNormalizer,
    StandardScaler,
    joinTwoDatasets,
    loadDataset,
    readDataSetFromCSV,
)


def test_filter_column():
    dataset = loadDataset("WhiteWineQuality")

    def filterByQuality(instance: Instance):
        return instance.getValue("quality") == 8 or instance.getValue("quality") < 4

    dataset_filtered = dataset.filterInstances(filterFunc=filterByQuality)

    print("Example quality value:", dataset_filtered.getRow(3).getValue("quality"))

    assert isinstance(dataset_filtered.getRow(3).getValue("quality"), float)  # nosec


def test_normalize_dataset():
    dataset = loadDataset("WhiteWineQuality")

    dataset = StandardNormalizer().normalize(dataset)

    assert isinstance(dataset.data["quality"][0], float)  # nosec


def test_scale_dataset():
    dataset = loadDataset("WhiteWineQuality")

    dataset = StandardScaler().scale(dataset)

    assert isinstance(dataset.data["quality"][0], float)  # nosec


def test_transform_text():
    dataset = loadDataset("TrafficTweets")
    dataset = dataset.sample(3)

    dataset = dataset.transformTextToVector("tweetext")

    assert type(dataset.data["tweetext"][0]) == list  # nosec


def test_transform_column():
    dataset = loadDataset("WhiteWineQuality")

    def transformIntoBinaryQuality(instance: Instance):
        return 1 if instance.getValue("quality") >= 7 else 0

    dataset = dataset.addAttribute(
        "goodquality", transformFunc=transformIntoBinaryQuality
    )

    assert isinstance(dataset.getRow(3).getValue("goodquality"), float)  # nosec


def test_geometry_to_vector():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.transformGeometryToVector("geometry")

    assert type(dataset.data["geometry"][0]) == np.ndarray  # nosec


def test_read_local_dataset():
    dataset = readDataSetFromCSV(
        "WhiteWineQuality2.csv",
        "WhiteWineQuality",
        ";",
        True,
        "",
        coordinateSystem=3857,
    )

    assert type(dataset.data["quality"][0]) == np.int64  # nosec


def test_join_datasets():
    dataset1 = readDataSetFromCSV("SpeedAverages.csv", "Local dataset", ",", "True", "")
    dataset2 = readDataSetFromCSV("SpeedAverages.csv", "Local dataset", ",", "True", "")

    dataset = joinTwoDatasets(dataset1, dataset2, "osm_id", "osm_id", "_l", "_r")

    assert type(dataset.data["street_type_l"][0]) == str  # nosec


def test_flatten_vector():
    dataset = loadDataset("SpeedAverages")

    dataset = dataset.transformCategoryToVector("season")
    dataset = dataset.flattenData()

    assert type(dataset.data["season_0"][0]) == np.float64  # nosec


def test_add_is_weekend():
    dataset = loadDataset("SpeedAverages")

    def transformIntoIsWeekend(instance: Instance) -> bool:
        week_num = instance.getValue("start_time").weekday()
        if week_num < 5:
            return False
        else:
            return True

    dataset = dataset.addAttribute(
        "start_time_isWeekend", transformFunc=transformIntoIsWeekend
    )

    assert type(dataset.data["start_time_isWeekend"][0]) == np.bool_  # nosec


def test_add_day_of_year():
    dataset = loadDataset("SpeedAverages")

    def transformIntoDayOfTheYear(instance: Instance):
        return instance.getValue("start_time").timetuple().tm_yday

    dataset = dataset.addAttribute(
        "start_time_DayOfTheYear", transformFunc=transformIntoDayOfTheYear
    )

    assert type(dataset.data["start_time_DayOfTheYear"][0]) == np.int64  # nosec


def test_add_week_day():
    dataset = loadDataset("SpeedAverages")

    def transformIntoWeekDay(instance: Instance) -> str:
        return instance.getValue("start_time").strftime("%A")

    dataset = dataset.addAttribute(
        "start_time_weekDay", transformFunc=transformIntoWeekDay
    )

    assert type(dataset.data["start_time_weekDay"][0]) == str  # nosec


def test_date_to_timestamp():
    dataset = loadDataset("SpeedAverages")

    def transformIntoTimestamp(instance: Instance):
        return datetime.timestamp(instance.getValue("start_time"))

    dataset = dataset.transform(
        "start_time", transformFunc=transformIntoTimestamp
    )

    assert type(dataset.data["start_time"][0]) == np.float64  # nosec
