import json
from pandas import DataFrame


def exportAsJSON(df: DataFrame):
    return df.to_json()


def exportDictionaryAsJSON(dictionary: dict):
    return json.dumps(dictionary)
