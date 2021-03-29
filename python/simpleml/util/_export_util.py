import json
from pandas import DataFrame
from json import encoder
from numpy import datetime64, int64
import pandas as pd
import simpleml.util._jsonLabels_util as config


def exportAsJSON(df: DataFrame):
    return df.to_json()


def exportDictionaryAsJSON(dictionary: dict, precision=2):
    return json.dumps(round_floats_and_transform_temporal(dictionary, precision))


def round_floats_and_transform_temporal(o, precision):
    # Source: https://stackoverflow.com/a/53798633

    if isinstance(o, (datetime64)):
        ts = pd.to_datetime(str(o))
        if config.lang == 'de':
            d = ts.strftime('%d.%m.%Y, %H:%M:%S')
        else:
            d = ts.strftime('%Y-%m-%d %H:%M:%S')
        return d

    if isinstance(o, (int64)):
        return int(o)

    if isinstance(o, float): return round(o, precision)
    if isinstance(o, dict): return {k: round_floats_and_transform_temporal(v, precision) for k, v in o.items()}
    if isinstance(o, (list, tuple)): return [round_floats_and_transform_temporal(x, precision) for x in o]
    return o
