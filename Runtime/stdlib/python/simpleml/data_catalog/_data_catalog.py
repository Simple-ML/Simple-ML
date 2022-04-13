from __future__ import annotations

import json
from io import StringIO

import pandas as pd
import simpleml.util.jsonLabels_util as config
from rdflib import URIRef
from simpleml.data_catalog._domain_model import DomainModel, getPythonType
from simpleml.dataset import Dataset
from simpleml.rdf import load_query, run_query
from simpleml.util import (
    exportDictionaryAsJSON,
    get_python_type_from_rdf_type,
    get_sml_type_from_rdf_type,
    simple_type_numeric,
)

lang = "en"  # TODO: Configure in a global config


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

        dataset = Dataset(
            id=identifier,
            title=title,
            subjects={lang: topics},
            number_of_instances=number_of_instances,
        )
        datasets.append(dataset)
    return datasets


def createDatasets(results):
    datasets = []
    for result in results["results"]["bindings"]:
        title = result["title"]["value"]
        identifier = result["identifier"]["value"]
        topics = result["subjects"]["value"].split(";")
        dataset = Dataset(id=identifier, title=title, subjects=topics)
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
        # column_index = result["columnIndex"]["value"]
        identifier = result["identifier"]["value"]
        propertyURI = result["property"]["value"]
        subjectResource = result["domain"]["value"]
        propertyLabel = result["propertyLabel"]["value"]
        domain_node_uri = result["subjectClass"]["value"]
        domain_node_label = result["subjectClassLabel"]["value"]
        is_virtual = result["isVirtual"]["value"] == "true"
        domain_node = domain_model.createClass(domain_node_uri, domain_node_label)
        resource_node = domain_model.createNode(subjectResource, domain_node)
        property_node = domain_model.createProperty(propertyURI, propertyLabel)

        is_geometry = False
        if propertyURI == "https://simple-ml.de/resource/asWKB":
            dataset.wkb_columns.append(identifier)
            is_geometry = True
        elif propertyURI == "https://simple-ml.de/resource/asWKT":
            dataset.wkt_columns.append(identifier)
            is_geometry = True
        elif propertyURI == "http://schema.org/latitude":
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["latitude"] = identifier
        elif propertyURI == "http://schema.org/longitude":
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["longitude"] = identifier

        rdf_value_type = result["valueType"]["value"]

        dataset.addColumnDescription(
            attribute_identifier=identifier,
            resource_node=resource_node,
            domain_node=domain_node,
            property_node=property_node,
            rdf_value_type=rdf_value_type,
            value_type=getPythonType(rdf_value_type),
            attribute_label=domain_node_label + " (" + propertyLabel + ")",
            is_geometry=is_geometry,
            is_virtual=is_virtual,
        )

    for lon_lat_pair in lon_lat_pairs.values():
        dataset.lon_lat_pairs.append(lon_lat_pair)

    # Add class relations
    query = load_query("getClassRelations", {"datasetId": dataset.id})
    results = run_query(query)
    for result in results["results"]["bindings"]:
        dataset.domain_model.addTriple(
            (
                URIRef(result["domain_class1"]["value"]),
                URIRef(result["property"]["value"]),
                URIRef(result["domain_class2"]["value"]),
            )
        )


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

        coordinate_system = 4326
        if "coordinateSystem" in result:
            coordinate_system = int(result["coordinateSystem"]["value"])

        lat_before_lon = False
        if "latBeforeLon" in result:
            lat_before_lon = bool(result["latBeforeLon"]["value"])

        topics = result["subjects"]["value"].split(";")

        title = result["title"]["value"]
        break  # we only expect one result row

    # TODO: Assign spatial columns
    dataset = Dataset(
        id=dataset_id,
        title=title,
        fileName=file_name,
        hasHeader=has_header,
        separator=separator,
        null_value=null_value,
        description=description,
        subjects={lang: topics},
        number_of_instances=number_of_instances,
        titles={lang: title},
        descriptions={lang: description},
        coordinate_system=coordinate_system,
        lat_before_lon=lat_before_lon,
    )

    addDomainModel(dataset)
    addStatistics(dataset)

    dataset.dataset_json = exportDictionaryAsJSON(dataset.getProfile())

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
            current_list: list[object] = []

            list_data_type = get_datatype_from_rdf(result["value"]["datatype"])

            # Special case: quartiles are shown as box plots, not lists

            attr_type = config.type_list
            if evaluation_type == "quartile":
                attr_type = config.type_box_plot

            dataset.stats[attribute_identifier][evaluation_type] = {
                config.type: attr_type,
                config.id: evaluation_type,
                config.list_data_type: list_data_type,
                config.type_list_values: current_list,
            }

        stats_datatype, value = getValue(result["value"])

        if "rank" not in result:
            addNumericValue(
                dataset.stats[attribute_identifier],
                evaluation_type,
                value,
                data_type=stats_datatype,
            )

            # dataset._stats[attribute_identifier][evaluation_type] = value
        else:
            current_list.append(value)

    # add histograms
    addHistograms(dataset)

    # add sample
    addSample(dataset)

    # add value distribution
    addValueDistribution(dataset)

    # add spatial value distribution
    addSpatialDistribution(dataset)

    # Ignore longitude/latitude columns. They should be covered by geometry columns.
    for lon_lat_pair in dataset.lon_lat_pairs:
        del dataset.stats[lon_lat_pair["longitude"]]
        del dataset.stats[lon_lat_pair["latitude"]]


def getValue(result):
    value = result["value"]

    sml_type = None
    if "datatype" in result:
        datatype = result["datatype"]
        # stats_datatype = None

        python_type = get_python_type_from_rdf_type(URIRef(datatype))
        sml_type = get_sml_type_from_rdf_type(URIRef(datatype))

        if not python_type:
            print("Missing data type:", datatype)

    return sml_type, python_type(value)


def addHistograms(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetHistograms", parameters)
    results = run_query(query)

    for result in results["results"]["bindings"]:
        attribute_identifier = result["identifier"]["value"]

        if config.histogram not in dataset.stats[attribute_identifier]:
            dataset.stats[attribute_identifier][config.histogram] = {
                config.type: config.type_histogram,
                config.bucket_data_type: getValue(result["minimum"])[0],
                config.type_histogram_buckets: [],
            }

        histogram = {
            config.bucketMinimum: getValue(result["minimum"])[1],
            config.bucketMaximum: getValue(result["maximum"])[1],
            config.value: getValue(result["instances"])[1],
        }

        dataset.stats[attribute_identifier][config.histogram][
            config.type_histogram_buckets
        ].append(histogram)


def addValueDistribution(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetValueDistribution", parameters)
    results = run_query(query)

    for result in results["results"]["bindings"]:

        attribute_identifier = result["identifier"]["value"]

        if config.valueDistribution not in dataset.stats[attribute_identifier]:
            dataset.stats[attribute_identifier][config.valueDistribution] = {
                config.type: config.type_bar_chart,
                config.id: config.value,
                config.type_bar_chart_bars: [],
            }

        dataset.stats[attribute_identifier][config.valueDistribution][
            config.type_bar_chart_bars
        ].append(
            {
                config.value_distribution_value: result["value"]["value"],
                config.value_distribution_number_of_instances: result["instances"][
                    "value"
                ],
            }
        )


def addSpatialDistribution(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetSpatialDistribution", parameters)
    results = run_query(query)

    areas: dict[str, dict] = {}  # attribute identifier to count (instances in area)

    for result in results["results"]["bindings"]:

        attribute_identifier = result["identifier"]["value"]

        if attribute_identifier not in areas:
            areas[attribute_identifier] = {}

        areas[attribute_identifier][result["region"]["value"]] = result["instances"][
            "value"
        ]

    for attribute_identifier in areas.keys():
        dataset.stats[attribute_identifier][config.spatialValueDistribution] = {}
        dataset.stats[attribute_identifier][config.spatialValueDistribution][
            config.type
        ] = config.type_spatial_distribution
        dataset.stats[attribute_identifier][config.spatialValueDistribution][
            config.type_spatial_distribution_areas
        ] = areas[attribute_identifier]


def addSample(dataset: Dataset):
    parameters = {"datasetId": dataset.id}
    query = load_query("getDatasetSample", parameters)
    results = run_query(query)

    sample_string = ""
    for result in results["results"]["bindings"]:
        sample_string += result["content"]["value"] + "\n"

    dataset.data_sample = pd.read_csv(
        StringIO(sample_string), sep="\t", header=0, na_values=""
    )


def addNumericValue(column_stats, name, value, data_type=None):
    column_stats[name] = createNumericValue(name, value, simple_type_numeric, data_type)


def createNumericValue(name, value, simple_type, data_type=None):
    if not data_type:
        data_type = type(value)
    return {
        config.type: simple_type,
        config.type_numeric_data_type: data_type,
        config.type_numeric_value: value,
        config.i18n_id: name,
    }


def get_pd_timestamp(datetime):
    return pd.Timestamp(datetime, unit="s")


def get_datatype_from_rdf(datatype):
    data_type = get_sml_type_from_rdf_type(URIRef(datatype))
    if not data_type:
        print("Missing data type:", datatype)
    return data_type
