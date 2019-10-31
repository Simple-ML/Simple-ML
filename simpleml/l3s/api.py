from typing import List
import requests
import pandas as pd
from io import StringIO

base = "***REMOVED***"


def createProject(title: str, description: str, language: str, random_seed: int) -> str:
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


def generateDataset(project_id: str, dataset_id: str) -> str:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "generate_dataset",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def keepAttributes(project_id: str, dataset_id: str, attribute_ids: List[str]) -> str:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "keep_attributes",
        "options": {
            "attribute_ids": ",".join(attribute_ids)
        }
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def sample(project_id: str, dataset_id: str, count: int) -> str:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "sample",
        "options": {
            "method": "FIRST_N",
            "number_of_instances": count,
        }
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def materialise(project_id: str, dataset_id: str) -> str:
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "materialise",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    res_json = res.json()
    return res_json["result"]["in_use_id"]


def getDatasetFile(project_id: str, dataset_id: str):
    url = f"{base}/projects/{project_id}/datasets/{dataset_id}/runOperation"
    req_json = {
        "operation": "get_dataset_file",
        "options": {}
    }
    res = requests.post(url, json=req_json)
    print(res.content)


def tsvStringToPandasDataframe(s: str) -> pd.DataFrame:
    path = StringIO(s)


def main():
    project_id = createProject("Title", "Description", "Language", 42)
    print("Project ID:", project_id)
    dataset_id = generateDataset(project_id, "ADACAugust")
    print("Dataset ID (generate_dataset):", dataset_id)
    dataset_id = keepAttributes(project_id, dataset_id, [
        "timestamp-time-string-to-hour",
        "timestamp-time-string-to-week-day",
        "timestamp-time-string-to-month",
        "car_type",
        "road_type"
    ])
    print("Dataset ID (keep_attributes):", dataset_id)
    dataset_id = sample(project_id, dataset_id, 10000)
    print("Dataset ID (sample):", dataset_id)
    dataset_id = materialise(project_id, dataset_id)
    getDatasetFile(project_id, dataset_id)


if __name__ == '__main__':
    main()
