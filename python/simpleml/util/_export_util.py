import json
from pandas import DataFrame
from json import encoder


def exportAsJSON(df: DataFrame):
    return df.to_json()


def exportDictionaryAsJSON(dictionary: dict, precision=2):
    return json.dumps(round_floats(dictionary, precision))


def round_floats(o, precision):
    # Source: https://stackoverflow.com/a/53798633
    if isinstance(o, float): return round(o, precision)
    if isinstance(o, dict): return {k: round_floats(v, precision) for k, v in o.items()}
    if isinstance(o, (list, tuple)): return [round_floats(x, precision) for x in o]
    return o
