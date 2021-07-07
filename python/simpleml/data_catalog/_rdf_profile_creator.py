from __future__ import annotations

from re import sub

from rdflib import Graph, Literal, RDF, Namespace
from rdflib.namespace import XSD, RDFS, DCTERMS, CSVW, DCAT

import simpleml.util.jsonLabels_util as config
from simpleml.dataset import Dataset


def exportStatisticsAsRDF(dataset: Dataset):
    profile = dataset.getProfile()

    g = Graph()

    SML = Namespace("https://simple-ml.de/resource/")

    g.bind("csvw", CSVW)
    g.bind("sml", SML)
    g.bind("dcat", DCAT)
    g.bind("rdf", RDF)
    g.bind("rdfs", RDFS)
    g.bind("dcterms", DCTERMS)
    g.bind("xsd", XSD)

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
    for attribute in dataset.attributes:

        attribute_val = profile[config.attributes][attribute]

        rdf_attribute = SML[dataset.id + urify(attribute)[0].upper() + urify(attribute)[1:]]
        g.add((rdf_dataset, SML.hasAttribute, rdf_attribute))
        g.add((rdf_attribute, RDF.type, SML.Attribute))
        g.add((rdf_attribute, DCTERMS.identifier, Literal(attribute)))

        for key, value in attribute_val["statistics"].items():
            if isinstance(value, list):
                pass
                # print("LIST")
            else:
                pass
                # print(key, " --->", value)

    return g.serialize(format="turtle").decode("utf-8")


def urify(string):
    string = sub(r"(_|-)+", " ", string).title().replace(" ", "")
    return string[0].lower() + string[1:]
