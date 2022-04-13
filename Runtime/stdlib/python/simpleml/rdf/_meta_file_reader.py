import os

import pandas as pd
import simpleml.util.global_configurations as global_config
from rdflib import RDFS, Namespace, URIRef
from simpleml.data_catalog._domain_model import DomainModel, getPythonType
from simpleml.dataset import Dataset
from simpleml.rdf._sparql_connector_local import get_graph


def get_uri(namespace_dict, uri_str):
    parts = uri_str.split(":", 1)
    return namespace_dict[parts[0]] + parts[1]


#    return URIRef(namespace_dict[parts[0]] + parts[1] )


def get_label(graph, node_uri):
    node = URIRef(node_uri)
    label_lang = graph.preferredLabel(node, global_config.language)

    if global_config.language != "en":
        return graph.preferredLabel(node, "en")[0][1].value

    if not label_lang:
        return graph.preferredLabel(node[0][1]).value

    return label_lang[0][1].value


def read_meta_file(file_path):
    g = get_graph()

    SML = Namespace("https://simple-ml.de/resource/")

    namespace_dict = {c[0]: c[1] for c in g.namespace_manager.namespaces()}

    file = open(file_path, "r", encoding="utf-8")

    res = dict()

    in_properties = False
    in_graph = False
    properties = []
    graph = []

    attribute_number = []

    for line in file.readlines():

        line = line.strip()

        if line == "PROPERTIES":
            in_properties = True
            continue

        if line == "GRAPH":
            in_graph = True
            in_properties = False
            continue

        if in_properties:
            properties.append(line)
            continue

        if in_graph:
            graph.append(line)
            continue

        parts = line.split("\t")
        key = parts[0]

        if len(parts) == 1:
            val = ""
        else:
            val = parts[1]

        res[key] = val

    titles = {}
    for title_key in [k for k, v in res.items() if k.startswith("TITLE_")]:
        titles[title_key.replace("TITLE_", "").lower()] = res[title_key]
    title = titles[global_config.language]

    dataset = Dataset(id=res["ID"], title=title, titles=titles)

    dataset.descriptions = {}
    for title_key in [k for k, v in res.items() if k.startswith("DESCRIPTION_")]:
        dataset.descriptions[title_key.replace("DESCRIPTION_", "").lower()] = res[
            title_key
        ]

    dataset.subjects = {}
    # topics are comma-separated
    for title_key in [k for k, v in res.items() if k.startswith("TOPICS_")]:
        dataset.subjects[title_key.replace("TOPICS_", "").lower()] = res[
            title_key
        ].split(",")

    dataset.fileName = res["FILE"]

    dataset.id = res["ID"]
    dataset.separator = res["SEPARATOR"]
    if dataset.separator == "TAB":
        dataset.separator = "\t"
    dataset.null_value = res["NULL"]
    dataset.hasHeader = res["HAS_HEADER"] == "YES"

    if "COORDINATE_SYSTEM" in res:
        dataset.coordinate_system = int(res["COORDINATE_SYSTEM"])
    if "LAT_LON_ORDER" in res:
        if res["LAT_LON_ORDER"] == "lat_lon":
            dataset.lat_before_lon = True
        else:
            dataset.lat_before_lon = False

    dir_name = os.path.dirname(__file__)
    data_file_path = os.path.join(
        dir_name, global_config.data_folder_name, dataset.fileName
    )
    header = pd.read_csv(
        data_file_path, nrows=0, sep=dataset.separator, na_values=dataset.null_value
    ).columns.tolist()
    attributes = []
    for attribute in header:
        attributes.append(attribute)

    #    dataset = readDataSetFromCSV(res['FILE'],
    # dataset_id=res['ID'], separator=res['SEPARATOR'], null_value = res['NULL'], has_header=res['HAS_HEADER'] == 'YES')

    # Domain model

    domain_model = DomainModel()
    dataset.domain_model = domain_model

    lon_lat_pairs = {}
    resource_map = {}

    for attribute_number in range(0, len(attributes)):

        if not properties[attribute_number]:
            continue

        parts = properties[attribute_number].split(",")

        domain_str = parts[0]
        property_str = parts[1]

        propertyURI = get_uri(namespace_dict, property_str)

        if len(parts) > 2:
            range_str = parts[2]
            value_type = get_uri(namespace_dict, range_str)
        else:
            # if no value type is given, use the property's range
            for _, _, o in g.triples((URIRef(propertyURI), RDFS.range, None)):
                value_type = o

        domain_node_uri = get_uri(namespace_dict, domain_str)

        resource_parts = domain_str.split(":", 1)
        subjectResource = (
            SML[dataset.id] + "_" + resource_parts[0] + "_" + resource_parts[1]
        )

        resource_instance_number = None
        if "@" in domain_node_uri:
            domain_node_uri_tmp = domain_node_uri
            domain_node_uri = domain_node_uri_tmp.split("@")[0]
            resource_instance_number = int(domain_node_uri_tmp.split("@")[1])
            subjectResource = (
                SML[dataset.id]
                + "_"
                + resource_parts[0]
                + "_"
                + str(resource_instance_number)
            )

        domain_node_label = get_label(g, domain_node_uri)
        propertyLabel = get_label(g, propertyURI)

        domain_node = domain_model.createClass(domain_node_uri, domain_node_label)
        resource_node = domain_model.createNode(subjectResource, domain_node)
        property_node = domain_model.createProperty(propertyURI, propertyLabel)

        resource_map[domain_str] = subjectResource

        identifier = attributes[attribute_number]

        is_geometry = False
        if propertyURI == URIRef("https://simple-ml.de/resource/asWKB"):
            dataset.wkb_columns.append(identifier)
            is_geometry = True
        elif propertyURI == URIRef("https://simple-ml.de/resource/asWKT"):
            dataset.wkt_columns.append(identifier)
            is_geometry = True

        elif propertyURI == URIRef("http://schema.org/latitude"):
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["latitude"] = identifier
        elif propertyURI == URIRef("http://schema.org/longitude"):
            if subjectResource not in lon_lat_pairs:
                lon_lat_pairs[subjectResource] = {}
            lon_lat_pairs[subjectResource]["longitude"] = identifier

        dataset.addColumnDescription(
            attribute_identifier=identifier,
            resource_node=resource_node,
            domain_node=domain_node,
            property_node=property_node,
            rdf_value_type=value_type,
            value_type=getPythonType(value_type),
            attribute_label=domain_node_label + " (" + propertyLabel + ")",
            resource_rank=resource_instance_number,
            is_geometry=is_geometry,
        )

    for lon_lat_pair in lon_lat_pairs.values():
        dataset.lon_lat_pairs.append(lon_lat_pair)

    dataset.readFile()

    for graph_line in graph:
        parts = graph_line.split(",")
        domain_str = parts[0]
        property_str = parts[1]
        object_str = parts[2]

        propertyURI = get_uri(namespace_dict, property_str)

        domain_model.addTriple(
            (resource_map[domain_str], URIRef(propertyURI), resource_map[object_str])
        )

    return dataset


# dataset = read_meta_file("../../../data_catalog/meta_files/SpeedAverages.tsv")

# print(exportDictionaryAsJSON(dataset.getProfile()))

# print(exportStatisticsAsRDF(dataset))
