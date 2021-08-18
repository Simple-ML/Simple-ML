from __future__ import annotations

from simpleml.rdf import run_query, load_query

lang = "en"


def getMLAlgorithmClasses():
    parameters = {"lang": lang}
    query = load_query("ml_catalog/all_algorithms", parameters)
    results = run_query(query)
    mlAlgorithmClasses = []
    for result in results["results"]["bindings"]:
        mlAlgorithmClass = {}
        mlAlgorithmClasses.append(mlAlgorithmClass)
        if "identifier" in result:
            mlAlgorithmClass["identifier"] = result["identifier"]["value"]
        if "label" in result:
            mlAlgorithmClass["label"] = result["label"]["value"]
        if "description" in result:
            mlAlgorithmClass["description"] = result["description"]["value"]

    return mlAlgorithmClasses


def getMLAlgorithms(algorithmClass):
    parameters = {"lang": lang, "identifier": algorithmClass}
    query = load_query("ml_catalog/allAlgorithmsOfClass", parameters)
    results = run_query(query)
    mlAlgorithms = []
    for result in results["results"]["bindings"]:
        mlAlgorithm = {}
        mlAlgorithms.append(mlAlgorithm)
        if "identifier" in result:
            mlAlgorithm["identifier"] = result["identifier"]["value"]
        if "label" in result:
            mlAlgorithm["label"] = result["label"]["value"]
        if "description" in result:
            mlAlgorithm["description"] = result["description"]["value"]

    return mlAlgorithms


def getMetrics(measure):
    parameters = {"lang": lang, "measure": measure}
    query = load_query("ml_catalog/metrics", parameters)
    results = run_query(query)
    metrics = []
    for result in results["results"]["bindings"]:
        metric = {}
        metrics.append(metric)
        if "abbreviation" in result:
            metric["abbreviation"] = result["abbreviation"]["value"]
        if "label" in result:
            metric["label"] = result["label"]["value"]
        metric["range"] = result["range"]["value"]

    return metrics


def getBenchmarks(dataset):
    parameters = {"lang": lang, "dataset": dataset}
    query = load_query("ml_catalog/benchmarks", parameters)
    results = run_query(query)
    benchmarks = []
    for result in results["results"]["bindings"]:
        benchmark = {}
        benchmarks.append(benchmark)
        if "algorithm" in result:
            benchmark["algorithm"] = result["algorithm"]["value"]
        if "metricLabel" in result:
            benchmark["metricLabel"] = result["metricLabel"]["value"]
        benchmark["score"] = result["score"]["value"]

    return benchmarks
