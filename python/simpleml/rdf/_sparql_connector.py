from __future__ import annotations
import os

from SPARQLWrapper import SPARQLWrapper, JSON

#sparqlURI = "***REMOVED***/sparqlJSON"
sparqlURI = "http://oekg.l3s.uni-hannover.de/SimpleMLDataAPI/sparqlJSON"

def run_query(query_string):
    sparql = SPARQLWrapper(sparqlURI)
    sparql.setQuery(query_string)
    sparql.setReturnFormat(JSON)
    results = sparql.query().convert()
    return results


def load_query(file_name, parameters={}, filter_parameters = []):
    file_name_absolute = os.path.join(os.path.dirname(__file__), "../queries/" + file_name + ".sparql")
    with open(file_name_absolute) as file:
        query = file.read()
    for key, value in parameters.items():
        query = query.replace("@" + key + "@", value)
    # Remove SPARQL comments
    for filter_parameter in filter_parameters:
        query = query.replace("#" + filter_parameter + " ", "")
    return query
