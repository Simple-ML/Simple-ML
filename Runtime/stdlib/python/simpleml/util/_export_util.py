import json

import pandas as pd
from numpy import datetime64, int64, bool_, int32, float64, float32
from pandas import DataFrame
import math

import simpleml.util.jsonLabels_util as config


def exportAsJSON(df: DataFrame):
    return df.to_json()


def exportDictionaryAsJSON(dictionary: dict, precision=3):
    return json.dumps(round_floats_and_transform_temporal(dictionary, precision), ensure_ascii=False)


def round_floats_and_transform_temporal(o, precision):
    # Source: https://stackoverflow.com/a/53798633

    if isinstance(o, dict):
        return {k: round_floats_and_transform_temporal(v, precision) for k, v in o.items()}
    elif isinstance(o, (list, tuple)):
        return [round_floats_and_transform_temporal(x, precision) for x in o]

    if pd.isna(o):
        return None

    if isinstance(o, (datetime64)):
        ts = pd.to_datetime(str(o))
        return ts.strftime(config.datetime_format[config.lang])

    elif isinstance(o, (float64)) or isinstance(o, (float32)):
        if math.isnan(o):
            return None
        return round(o.item(), precision)

    elif isinstance(o, (int64)) or isinstance(o, (int32)):
        if math.isnan(o):
            return None
        return o.item()

    elif isinstance(o, (bool_)):
        return str(o)

    elif isinstance(o, (pd.Timestamp)):
        return o.strftime(config.datetime_format[config.lang])

    elif isinstance(o, float):
        if math.isnan(o):
            return None
        return round(o, precision)

    return o
