from __future__ import annotations

import os

import requests
from SPARQLWrapper import JSON, SPARQLWrapper

sparqlURI = "http://smldapi.l3s.uni-hannover.de/sparqlJSON"


# sparqlURI = "http://localhost:4567/sparqlJSON"


def endpoint_is_running():
    # check if the endpoint is running and has no time out
    try:
        r = requests.get(sparqlURI, timeout=2)
        return bool(r)
    except requests.exceptions.Timeout:
        return False


def run_query(query_string):
    sparql = SPARQLWrapper(sparqlURI)
    sparql.setQuery(query_string)
    sparql.setReturnFormat(JSON)
    results = sparql.query()
    results = results.convert()
    return results


def load_query(file_name, parameters=None, filter_parameters=None):
    if parameters is None:
        parameters = {}
    if filter_parameters is None:
        filter_parameters = []

    file_name_absolute = os.path.join(
        os.path.dirname(__file__), "../queries/" + file_name + ".sparql"
    )
    with open(file_name_absolute) as file:
        query = file.read()
    for key, value in parameters.items():
        query = query.replace("@" + key + "@", value)
    # Remove SPARQL comments
    for filter_parameter in filter_parameters:
        query = query.replace("#" + filter_parameter + " ", "")
    return query
