from __future__ import annotations

import numpy as np
import rdflib
from rdflib import Literal, URIRef
from rdflib.namespace import OWL, RDF, RDFS, XSD
from simpleml.util import get_python_type_from_rdf_type

SML = rdflib.Namespace("https://simple-ml.de/resource/")


class DomainModel:
    def __init__(self):
        self.graph = rdflib.Graph()
        self.triple_graph = rdflib.Graph()

    def addNode(self, node_identifier, class_identifier):
        self.graph.add((node_identifier, SML.mapsTo, class_identifier))

    def createClass(self, classURI, classLabel):
        class_node = rdflib.URIRef(classURI)
        self.graph.add((class_node, RDFS.subClassOf, OWL.Class))
        self.graph.add(
            (class_node, RDFS.label, Literal(classLabel, datatype=XSD.string))
        )
        return class_node

    def createNode(self, nodeURI, domainClass):
        domain_node = rdflib.URIRef(nodeURI)
        self.graph.add((domain_node, RDF.type, domainClass))
        return domain_node

    def createProperty(self, propertyURI, propertyLabel):
        property_node = rdflib.URIRef(propertyURI)
        self.graph.add((property_node, RDF.type, OWL.DatatypeProperty))
        self.graph.add(
            (property_node, RDFS.label, Literal(propertyLabel, datatype=XSD.string))
        )
        return property_node

    def addTriple(self, triple):
        self.graph.add(triple)
        self.triple_graph.add(triple)

    def get_attribute_relation_triples_graph(self):
        return self.triple_graph

    def copy(self):
        copy = DomainModel()
        copy.graph = self.graph
        copy.triple_graph = rdflib.Graph()
        for s, p, o in self.triple_graph.triples((None, None, None)):
            copy.triple_graph.add((s, p, o))
        return copy


def getPythonType(data_type):
    data_type = rdflib.URIRef(data_type)

    # TODO: Add to types?
    if data_type == SML.wellKnownBinary or data_type == SML.wellKnownText:
        return np.object

    return get_python_type_from_rdf_type(URIRef(data_type))
