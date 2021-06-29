from __future__ import annotations
import os

from SPARQLWrapper import SPARQLWrapper, JSON
from rdflib import Graph
from rdflib import Graph
from rdflib.util import guess_format
from rdflib.plugins.sparql.results.jsonresults import JSONResultSerializer
from io import StringIO
import json

import os
import sys

print("Init data catalog.")
graph = Graph()

folders = ["datasets", "external_vocabularies", "ml_catalog", "schema"]
dirName = os.path.dirname(__file__)

for folder in folders:
    folderPath = os.path.join(dirName, "../../../data_catalog/" + folder)

    for filename in os.listdir(folderPath):
        filename = os.path.join(folderPath, filename)

        format = "ttl"
        if (folder != 'datasets'):
            format = guess_format(filename)

        graph.parse(filename, format=format)
print(" -> Done.")


def run_query(query_string):
    res = graph.query(query_string)
    json_stream = StringIO()
    JSONResultSerializer(res).serialize(json_stream)
    return json.loads(json_stream.getvalue())


def load_query(file_name, parameters={}, filter_parameters=[]):
    file_name_absolute = os.path.join(os.path.dirname(__file__), "../queries/" + file_name + ".sparql")
    with open(file_name_absolute) as file:
        query = file.read()
    for key, value in parameters.items():
        query = query.replace("@" + key + "@", value)
    # Remove SPARQL comments
    for filter_parameter in filter_parameters:
        query = query.replace("#" + filter_parameter + " ", "")
    return query
