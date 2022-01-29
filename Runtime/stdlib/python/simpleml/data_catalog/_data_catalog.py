from __future__ import annotations

import json
from io import StringIO

import pandas as pd
from numpy import datetime64

import simpleml.util.jsonLabels_util as config
from simpleml.data_catalog._domain_model import DomainModel, getPythonType
from simpleml.dataset import Dataset
from simpleml.rdf import run_query, load_query

lang = "de"  # TODO: Configure in a global config


def getDatasets(domain=None, topic=None):
    parameters = {"lang": lang}
    filter_parameters = []
    if domain:
        filter_parameters.append("domainFilter")
        parameters["domain"] = domain
    if topic:
        filter_parameters.append("topicFilter")
        parameters["topic"] = topic
    query = load_query("getDatasets", parameters, filter_parameters)

    results = run_query(query)
    # transform result into datasets
    datasets = []
    for result in results["results"]["bindings"]:
        title = result["title"]["value"]
        identifier = result["identifier"]["value"]
        topics = result["subjects"]["value"].split(";")
        number_of_instances = int(result["numberOfInstances"]["value"])
        dataset = Dataset(id=identifier, title=title, subjects={
                          lang: topics}, number_of_instances=number_of_instances)
        datasets.append(dataset)
    return datasets


def createDatasets(results):
    datasets = []
    for result in results["results"]["bindings"]:
        title = result["title"]["value"]
        identifier = result["identifier"]["value"]
        topics = result["subjects"]["value"].split(";")
        dataset = Dataset(id=identifier, title=title, topics=topics)
        datasets.append(dataset)
    return datasets


def getDatasetsJson(datasets):
    dataset_objects = []
    for dataset in datasets:
        dataset_objects.append(json.loads(dataset.getJson()))
    datasetsJson = {"datasets": dataset_objects}
    return json.dumps(datasetsJson)


def addDomainModel(dataset):
    parameters = {"datasetId": dataset.id, "lang": lang}
    query = load_query("getDatasetAttributes", parameters)

    results = run_query(query)
    domain_model = DomainModel()
    dataset.domain_model = domain_model

    lon_lat_pairs = {}

    for result in results["results"]["bindings"]:
        column_index = result["columnIndex"]["value"]
        identifier = result["identifier"]["value"]
        propertyURI = result["property"]["value"]
        subjectResource = result["domain"]["value"]
        propertyLabel = result["propertyLabel"]["value"]
        domain_node_uri = result["subjectClass"]["value"]
        domain_node_label = result["subjectClassLabel"]["value"]
        domain_node = domain_model.createClass(
            domain_node_uri, domain_node_label)
        resource_node = domain_model.createNode(subjectResource, domain_node)
        property_node = domain_model.createProperty(propertyURI, propertyLabel)

        if propertyURI == "https://simple-ml.de/resource/asWKB":
            dataset.wkb_columns.append(identifier)
        elif propertyURI == "https://simple-ml.de/resource/asWKT":
            dataset.wkt_columns.append(identifier)
        elif propertyURI == "http://schema.org/latitude":
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["latitude"] = identifier
        elif propertyURI == "http://schema.org/longitude":
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["longitude"] = identifier

        value_type = result["valueType"]["value"]

        dataset.addColumnDescription(attribute_identifier=identifier, resource_node=resource_node,
                                     domain_node=domain_node, property_node=property_node,
                                     rdf_value_type=value_type, value_type=getPythonType(
                                         value_type),
                                     attribute_label=domain_node_label + " (" + propertyLabel + ")")

    for lon_lat_pair in lon_lat_pairs.values():
        dataset.lon_lat_pairs.append(lon_lat_pair)

    # TODO: Add class relations


def getDataset(dataset_id: str) -> Dataset:
    parameters = {"datasetId": dataset_id, "lang": lang}
    query = load_query("getDataset", parameters)

    results = run_query(query)

    for result in results["results"]["bindings"]:
        file_name = result["fileLocation"]["value"]
        separator = result["separator"]["value"]
        null_value = result["nullValue"]["value"]
        has_header = result["hasHeader"]["value"]
        description = result["description"]["value"]
        number_of_instances = int(result["numberOfInstances"]["value"])

        topics = result["subjects"]["value"].split(";")

        title = result["title"]["value"]
        break  # we only expect one result row

    # TODO: Assign spatial columns
    dataset = Dataset(id=dataset_id, title=title, fileName=file_name, hasHeader=has_header, separator=separator,
                      null_value=null_value, description=description, subjects={
                          lang: topics},
                      number_of_instances=number_of_instances, titles={lang: title}, descriptions={lang: description})

    addDomainModel(dataset)
    addStatistics(dataset)

    return dataset


def addStatistics(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetStatistics", parameters)

    results = run_query(query)
    dataset.stats = {}
    for result in results["results"]["bindings"]:
        attribute_identifier = result["identifier"]["value"]
        evaluation_type = result["evalType"]["value"]

        if attribute_identifier not in dataset.stats:
            dataset.stats[attribute_identifier] = {}

        if evaluation_type not in dataset.stats[attribute_identifier]:
            current_list = []
            dataset.stats[attribute_identifier][evaluation_type] = current_list

        value = result["value"]["value"]

        if "datatype" in result["value"]:
            datatype = result["value"]["datatype"]
            stats_datatype = None
            if datatype == "http://www.w3.org/2001/XMLSchema#double":
                value = float(value)
                stats_datatype = config.type_float
            elif datatype == "http://www.w3.org/2001/XMLSchema#nonNegativeInteger" or datatype == "http://www.w3.org/2001/XMLSchema#integer":
                value = int(value)
                stats_datatype = config.type_integer
            elif datatype == "http://www.w3.org/2001/XMLSchema#long":
                value = int(value)
                stats_datatype = config.type_integer
            elif datatype == "http://www.w3.org/2001/XMLSchema#dateTime":
                value = datetime64(value)
                stats_datatype = config.type_datetime
            else:
                print("Missing data type:", datatype)

        if "rank" not in result:
            # todo
            addNumericValue(dataset.stats[attribute_identifier],
                            evaluation_type, value, data_type=stats_datatype)

            #dataset.stats[attribute_identifier][evaluation_type] = value
        else:
            current_list.append(value)

    # add histograms
    addHistograms(dataset)

    # add sample
    addSample(dataset)

    # add value distribution
    addValueDistribution(dataset)


def addHistograms(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetHistograms", parameters)
    results = run_query(query)
    for result in results["results"]["bindings"]:
        attribute_identifier = result["identifier"]["value"]

        if config.histogram not in dataset.stats[attribute_identifier]:
            dataset.stats[attribute_identifier][config.histogram] = []

        histogram = {config.bucketMinimum: result["minimum"]["value"], config.bucketMaximum: result["maximum"]["value"],
                     config.value: result["instances"]["value"]}

        dataset.stats[attribute_identifier][config.histogram].append(histogram)


def addValueDistribution(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetValueDistribution", parameters)
    results = run_query(query)
    for result in results["results"]["bindings"]:
        attribute_identifier = result["identifier"]["value"]

        if config.valueDistribution not in dataset.stats[attribute_identifier]:
            dataset.stats[attribute_identifier][config.valueDistribution] = []

        dataset.stats[attribute_identifier][config.valueDistribution].append(
            {config.value_distribution_value: result["value"]["value"],
             config.value_distribution_number_of_instances: result["instances"]["value"]})


def addSample(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetSample", parameters)
    results = run_query(query)
    sample_string = ""
    for result in results["results"]["bindings"]:
        sample_string += result["content"]["value"] + "\n"

    dataset.data_sample = pd.read_csv(
        StringIO(sample_string), sep="\t", header=0, na_values='')

    dataset.addSample()

    sample_as_list = dataset.data_sample.values.tolist()

    dataset.sample_for_profile = {config.sample_lines: sample_as_list,
                                  config.sample_header_labels: list(dataset.attribute_labels.values())}


def addNumericValue(column_stats, name, value, data_type=None):
    simple_type = config.type_numeric

    column_stats[name] = createNumericValue(
        name, value, config.type_numeric, data_type)


def createNumericValue(name, value, simple_type, data_type=None):
    if not data_type:
        data_type = config.data_type_labels[type(value)]
    return {config.type: simple_type,
            config.type_numeric_data_type: data_type,
            config.type_numeric_value: value,
            config.i18n_id: name}


def get_pd_timestamp(datetime):
    return pd.Timestamp(datetime, unit='s')
