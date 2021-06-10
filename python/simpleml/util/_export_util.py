import json
from pandas import DataFrame
from json import encoder
from numpy import datetime64, int64, bool_, int32
import pandas as pd
import simpleml.util._jsonLabels_util as config


def exportAsJSON(df: DataFrame):
    return df.to_json()


def exportDictionaryAsJSON(dictionary: dict, precision=2):
    return json.dumps(round_floats_and_transform_temporal(dictionary, precision), ensure_ascii=False)


def round_floats_and_transform_temporal(o, precision):
    # Source: https://stackoverflow.com/a/53798633

    if isinstance(o, (datetime64)):
        ts = pd.to_datetime(str(o))
        return ts.strftime(config.datetime_format[config.lang])

    elif isinstance(o, (int64)) or isinstance(o, (int32)):
        return int(o)

    elif isinstance(o, (bool_)):
        return str(o)

    elif isinstance(o, (pd.Timestamp)):
        return o.strftime(config.datetime_format[config.lang])

    elif isinstance(o, float):
        return round(o, precision)
    elif isinstance(o, dict):
        return {k: round_floats_and_transform_temporal(v, precision) for k, v in o.items()}
    elif isinstance(o, (list, tuple)):
        return [round_floats_and_transform_temporal(x, precision) for x in o]
    return o
