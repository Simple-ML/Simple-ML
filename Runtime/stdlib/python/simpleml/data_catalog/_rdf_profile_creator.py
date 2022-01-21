from __future__ import annotations

from re import sub

from rdflib import Graph, Literal, RDF, Namespace, URIRef
from rdflib.namespace import XSD, RDFS, DCTERMS, CSVW, DCAT

import simpleml.util.jsonLabels_util as config
from simpleml.dataset import Dataset

import simpleml.rdf._rdf_labels as rdf_config


def exportStatisticsAsRDF(dataset: Dataset):
    profile = dataset.getProfile()

    g = Graph()

    SML = Namespace("https://simple-ml.de/resource/")
    SEAS = Namespace("https://w3id.org/seas/")

    g.bind("csvw", CSVW)
    g.bind("sml", SML)
    g.bind("dcat", DCAT)
    g.bind("rdf", RDF)
    g.bind("rdfs", RDFS)
    g.bind("dcterms", DCTERMS)
    g.bind("xsd", XSD)
    g.bind("seas", SEAS)

    rdf_dataset = SML[dataset.id]
    g.add((SML['simple-ml'], DCAT.dataset, rdf_dataset))
    g.add((rdf_dataset, RDF.type, DCAT.Dataset))
    g.add((rdf_dataset, DCTERMS.identifier, Literal(dataset.id)))
    g.add((rdf_dataset, SML.numberOfInstances, Literal(dataset.number_of_instances, datatype=XSD.nonNegativeInteger)))
    g.add((rdf_dataset, SML.creatorId, Literal(0, datatype=XSD.nonNegativeInteger)))  # TODO: is this required?

    for lang, title in dataset.titles.items():
        g.add((rdf_dataset, DCTERMS.title, Literal(title, lang)))

    for lang, description in dataset.descriptions.items():
        g.add((rdf_dataset, DCTERMS.description, Literal(description, lang)))

    for lang, subjects in dataset.subjects.items():
        for subject in subjects:
            g.add((rdf_dataset, DCTERMS.subject, Literal(subject, lang)))

    # file
    rdf_file = SML[dataset.id + "File"]
    g.add((rdf_dataset, SML.hasFile, rdf_file))
    g.add((rdf_file, RDF.type, SML.TextFile))
    g.add((rdf_file, SML.fileLocation, Literal(dataset.fileName)))
    g.add((rdf_file, DCTERMS['format'], Literal("text/comma-separated-values")))
    g.add((rdf_file, CSVW.separator, Literal(dataset.separator)))
    g.add((rdf_file, CSVW.header, Literal(dataset.hasHeader, datatype=XSD.boolean)))
    g.add((rdf_file, CSVW.null, Literal(dataset.null_value)))

    # sample
    rdf_sample = SML[dataset.id + "Sample"]
    g.add((rdf_dataset, SML.hasSample, rdf_sample))
    g.add((rdf_sample, RDF.type, SML.DatasetSample))
    g.add((rdf_sample, CSVW.separator, Literal("	")))

    # attributes
    for column_index, attribute in enumerate(dataset.attributes):

        attribute_val = profile[config.attributes][attribute]
        rdf_attribute = SML[dataset.id + "Attribute" + urify(attribute)[0].upper() + urify(attribute)[1:]]

        g.add((rdf_dataset, SML.hasAttribute, rdf_attribute))
        g.add((rdf_attribute, RDF.type, SML.Attribute))
        g.add((rdf_attribute, DCTERMS.identifier, Literal(attribute)))
        g.add((rdf_attribute, SML.columnIndex, Literal(column_index, datatype=XSD.nonNegativeInteger)))

        # semantic graph
        g.add((rdf_attribute, SML.valueType, URIRef(dataset.attribute_graph[attribute]["value_type"])))
        g.add((rdf_attribute, SML.mapsToProperty, dataset.attribute_graph[attribute]["property"]))
        g.add((rdf_attribute, SML.mapsToDomain, URIRef(dataset.attribute_graph[attribute]["resource"])))
        g.add((URIRef(dataset.attribute_graph[attribute]["resource"]), SML.mapsTo,
               dataset.attribute_graph[attribute]["class"]))
        g.add((URIRef(dataset.attribute_graph[attribute]["resource"]), RDF.type, SML.classInstance))

        for key, value in attribute_val["statistics"].items():
            if config.values in value:
                rank = 0
                for sub_value in value[config.values]:
                    rdf_attribute_stat = SML[
                        dataset.id + "Attribute" + urifyCapitalised(attribute) + capitalise(value[config.id]) + str(
                            rank)]
                    g.add((rdf_attribute_stat, RDF.type,
                           SML["Distribution" + capitalise(value[config.id]) + "Evaluation"]))
                    g.add((rdf_attribute_stat, SEAS.evaluatedValue,
                           Literal(sub_value, datatype=rdf_config.xsd_data_types[value[config.list_data_type]])))
                    g.add((rdf_attribute_stat, SEAS.rank,
                           Literal(rank)))
                    g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))
                    rank += 1
            elif config.type_histogram_buckets in value:
                bucket_rank = 0
                for bucket in value[config.type_histogram_buckets]:
                    rdf_attribute_stat = SML[
                        dataset.id + "Attribute" + urifyCapitalised(attribute) + "Histogram" + str(
                            bucket_rank)]
                    g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))
                    g.add((rdf_attribute_stat, RDF.type,
                           SML["DistributionHistogramEvaluation"]))
                    g.add((rdf_attribute_stat, SML.bucketMinimum,
                           Literal(bucket[config.bucketMinimum], datatype=XSD.double)))
                    g.add((rdf_attribute_stat, SML.bucketMaximum,
                           Literal(bucket[config.bucketMaximum], datatype=XSD.double)))
                    g.add((rdf_attribute_stat, SML.instancesInBucket,
                           Literal(bucket[config.bucketValue], datatype=XSD.nonNegativeInteger)))
                    bucket_rank += 1
            elif config.type_bar_chart_bars in value:
                value_rank = 0
                for bar_value in value[config.type_bar_chart_bars]:
                    rdf_attribute_stat = SML[
                        dataset.id + "Attribute" + urifyCapitalised(attribute) + "ValueDistributionValue" + str(
                            value_rank)]
                    g.add((rdf_attribute, SEAS.valueDistributionValue, rdf_attribute_stat))
                    g.add((rdf_attribute_stat, SML.numberOfInstancesOfValue,
                           Literal(bar_value[config.number_of_instances], datatype=XSD.nonNegativeInteger)))
                    g.add((rdf_attribute_stat, SML.instancesOfValue,
                           Literal(bar_value[config.value])))
                    g.add((rdf_attribute_stat, RDF.type, SML.ValueDistributionValue))
                    value_rank += 1
            elif config.type_spatial_distribution_areas in value:

                rdf_attribute_stat = SML[
                    dataset.id + "Attribute" + urifyCapitalised(attribute) + "SpatialDistribution"]
                g.add((rdf_attribute, SML.hasSpatialDistribution, rdf_attribute_stat))
                g.add((rdf_attribute_stat, RDF.type, SML.SpatialDistribution))

                for area, count in value[config.type_spatial_distribution_areas].items():
                    rdf_attribute_sub_stat = SML[
                        dataset.id + "Attribute" + urifyCapitalised(
                            attribute) + "SpatialDistributionLocation" + urifyCapitalised(area)]
                    g.add((rdf_attribute_stat, SML.spatialDistributionValue, rdf_attribute_sub_stat))
                    g.add((rdf_attribute_sub_stat, RDF.type, SML.SpatialDistributionValue))
                    g.add((rdf_attribute_sub_stat, SML.instancesOfRegion, Literal(area)))
                    g.add((rdf_attribute_sub_stat, SML.numberOfInstancesInRegion,
                           Literal(count, datatype=XSD.nonNegativeInteger)))

            else:
                rdf_attribute_stat = SML[
                    dataset.id + "Attribute" + urifyCapitalised(attribute) + capitalise(value[config.id])]
                g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))
                g.add((rdf_attribute_stat, SEAS.evaluatedValue,
                       Literal(value[config.value], datatype=rdf_config.xsd_data_types[value['data_type']])))
                g.add((rdf_attribute_stat, RDF.type, SML["Distribution" + capitalise(value[config.id]) + "Evaluation"]))

    return g.serialize(format="turtle")#.decode("utf-8")


def urify(string):
    string = sub(r"(_|-)+", " ", string).title().replace(" ", "")
    return string[0].lower() + string[1:]


def urifyCapitalised(string):
    string = sub(r"(_|-)+", " ", string)
    string = [capitalise(x) for x in string.split(" ")]
    string =  "".join(string).replace(" ", "")
    return string[0].upper() + string[1:]


def capitalise(string):
    return string[0].upper() + string[1:]
