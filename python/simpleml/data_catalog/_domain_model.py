from __future__ import annotations
from simpleml.rdf import run_query, load_query
from simpleml.dataset import Dataset
from pandas import DataFrame
import rdflib
from rdflib.namespace import DC, DCTERMS, OWL, RDF, RDFS, XSD
from rdflib import Literal
from rdflib.plugins.sparql.datatypes import type_promotion
import numpy as np
import pandas as pd

SML = rdflib.Namespace('https://simple-ml.de/resource/')


class DomainModel:
    def __init__(self):
        self.graph = rdflib.Graph()

    def addNode(self, node_identifier, class_identifier):
        self.graph.add((node_identifier, SML.mapsTo, class_identifier))

    def createClass(self, classURI, classLabel):
        class_node = rdflib.URIRef(classURI)
        self.graph.add((class_node, OWL.subClassOf, OWL.Class))
        self.graph.add((class_node, RDFS.label, Literal(classLabel, datatype=XSD.string)))
        return class_node

    def createNode(self, nodeURI, domainClass):
        domain_node = rdflib.URIRef(nodeURI)
        self.graph.add((domain_node, RDF.type, domainClass))
        return domain_node

    def createProperty(self, propertyURI, propertyLabel):
        property_node = rdflib.URIRef(propertyURI)
        self.graph.add((property_node, RDF.type, OWL.DatatypeProperty))
        self.graph.add((property_node, RDFS.label, Literal(propertyLabel, datatype=XSD.string)))
        return property_node


def getPythonType(data_type):
    data_type = rdflib.URIRef(data_type)
    # temporal?
    if data_type == XSD.date or data_type == XSD.dateTime or data_type == XSD.time:
        return np.datetime64
    # spatial?
    if data_type == SML.wellKnownBinary or data_type == SML.wellKnownText:
        return np.object
    try:
        # boolean?
        if data_type == XSD.boolean:
            return np.bool
    except TypeError:
        pass
    try:
        # integer?
        if type_promotion(data_type, XSD.integer) == XSD.integer:
            return pd.Int32Dtype()
    except TypeError:
        pass
    try:
        # decimal?
        if type_promotion(data_type, XSD.double) == XSD.double:
            return np.double
    except TypeError:
        pass
    return np.str
