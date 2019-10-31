from typing import List, Union
import requests
import pandas as pd
from io import StringIO

from simpleml.io.pandasAdapter import read_tsv

base = "http://smldapi.l3s.uni-hannover.de"
project_id: Union[str, None] = None


class Query:
    def __init__(self, query_id: str):
        self.query_id: str = query_id
        self.__data: Union[pd.DataFrame, None] = None

    def getData(self) -> pd.DataFrame:
        if self.__data is None:
            self.__data = _download(self.query_id)
        return self.__data


def loadDataset(dataset_id: str) -> Query:
    global project_id
    if project_id is None:
        project_id = _createProject("Title", "Description", "Language", 42)
    return Query(_generateDataset(dataset_id))


def _createProject(title: str, description: str, language: str, random_seed: int) -> str:
    url = f"{base}/runOperation"
    req_json = {
        "operation": "create_project",
        "options": {
            "title": title,
            # "description": description,
            # "language": language,
            # "random seed": random_seed,
        }
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["project"]["id"]


def _generateDataset(dataset_id: str) -> str:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "generate_dataset",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def keepAttributes(query: Query, attribute_ids: List[str]) -> Query:
    url = f"{base}/projects/{project_id}/datasets/{query.query_id}/runOperation"
    req_json = {
        "operation": "keep_attributes",
        "options": {
            "attribute_ids": ",".join(attribute_ids)
        }
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return Query(res_json["result"]["in_use_id"])


def sample(query: Query, count: int) -> Query:
    url = f"{base}/projects/{project_id}/datasets/{query.query_id}/runOperation"
    req_json = {
        "operation": "sample",
        "options": {
            "method": "FIRST_N",
            "number_of_instances": count,
        }
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return Query(res_json["result"]["in_use_id"])


def _download(query_id: str) -> pd.DataFrame:
    file_id = _materialise(query_id)
    path = _getDatasetFile(file_id)
    return read_tsv(path)


def _materialise(query_id: str) -> str:
    url = f"{base}/projects/{project_id}/datasets/{query_id}/runOperation"
    req_json = {
        "operation": "materialise",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def _getDatasetFile(dataset_id: str) -> StringIO:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "get_dataset_file",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    return StringIO(res.content.decode("utf-8"))


def main():
    from simpleml.ml.regression.scikitAdapter import LassoRegression, DecisionTreeRegression
    from simpleml.ml.scikitAdapter import fit, predict

    adac_august = loadDataset("ADACAugust")
    print("Dataset ID (adac_august):", adac_august.query_id)

    sampled = sample(adac_august, 1000)
    print("Dataset ID (sampled):", sampled.query_id)

    features = keepAttributes(sampled, [
        "timestamp-time-string-to-hour",
        # "timestamp-time-string-to-week-day",
        # "timestamp-time-string-to-month",
        # "car_type",
        # "road_type"
    ])
    print("Dataset ID (features):", features.query_id)
    target = keepAttributes(sampled, [
        "velocity"
    ])
    print("Dataset ID (target):", target.query_id)

    model = LassoRegression({"regularizationStrength": 1})
    trained_model = fit(model, features, target)

    pred_X = [
        [23]
    ]
    pred_y = predict(trained_model, pred_X)
    print(pred_y)


if __name__ == '__main__':
    main()
