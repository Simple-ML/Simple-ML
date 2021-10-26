from rdflib import Graph, URIRef, Literal
from rdflib.namespace import Namespace, RDF, OWL, SKOS, RDFS, DCTERMS
from rdflib.namespace import DefinedNamespace, Namespace
import json
import pandas as pd


class MEX_ALGO(DefinedNamespace):
    _fail = True

    # http://www.w3.org/2000/01/rdf-schema#Class
    HyperParameter: URIRef
    AlgorithmClass: URIRef
    Algorithm: URIRef

    _NS = Namespace("http://mex.aksw.org/mex-algo#")


def make_uri(value):
    return (value[0].upper() + value[1:]).replace(" ", "").replace("(", "_").replace(")", "_").replace("'", "")


# load stubs
# load parameter descriptions

g = Graph()
g.bind('mex-algo', MEX_ALGO)
SML = Namespace("http://simple-ml.de/rdf#")
DBR = Namespace("http://dbpedia.org/resource/")
DBPEDIA_DE = Namespace("http://de.dbpedia.org/resource/")
WD = Namespace("http://www.wikidata.org/entity/")
DBO = Namespace("http://dbpedia.org/ontology/")

dbpedia_namespaces = {"en": DBR, "de": DBPEDIA_DE}

g.bind('sml', SML)
g.bind('dcterms', DCTERMS)
g.bind('owl', OWL)
g.bind('skos', SKOS)

languages = ["en", "de"]

# load blacklist of ignored algorithms
algorithms_blacklist = set(line.strip() for line in open(
    '../../../data_catalog/ml_processes_catalog/algorithms_blacklist.csv'))

id_to_ref = dict()
all_parents = dict()
with open('../../../data_catalog/ml_processes_catalog/algorithms.json') as json_file:
    data = json.load(json_file)

    for algorithm_id in data:

        algorithm_json = data[algorithm_id]
        dbpedia_id = algorithm_json['dbpedia_id']

        if dbpedia_id in algorithms_blacklist:
            continue

        print(algorithm_json)

        pref_labels = dict()

        dbpedia_ids = algorithm_json['dbpedia_ids']

        algorithm_ref = SML[make_uri(dbpedia_ids["en"][0])]

        print("algorithm_id:", algorithm_id)
        id_to_ref[int(algorithm_id)] = algorithm_ref

        g.add((algorithm_ref, RDF.type, OWL.Class))

        if "parents" not in algorithm_json:
            g.add((algorithm_ref, RDF.type, MEX_ALGO.AlgorithmClass))
        else:
            all_parents[int(algorithm_id)] = algorithm_json["parents"]

        for dbpedia_language in dbpedia_ids:
            for dbpedia_id in dbpedia_ids[dbpedia_language]:
                pref_labels[dbpedia_language] = dbpedia_id.replace("_", " ")
                g.add((algorithm_ref, OWL.sameAs, dbpedia_namespaces[dbpedia_language][dbpedia_id]))

        # Wikidata ID
        if 'wikidata_id' in algorithm_json:
            wikidata_id = algorithm_json['wikidata_id']
            g.add((algorithm_ref, OWL.sameAs, WD[wikidata_id]))

        longest_descriptions = dict()

        # descriptions
        if 'descriptions' in algorithm_json:
            descriptions = algorithm_json['descriptions']
            for description_language in descriptions:
                for description in descriptions[description_language]:
                    g.add((algorithm_ref, DCTERMS.description, Literal(description, lang=description_language)))
                    if description_language not in longest_descriptions or len(
                            longest_descriptions[description_language]) < len(description):
                        longest_descriptions[description_language] = description

        shortest_abbreviations = dict()

        # labels
        labels = algorithm_json['labels']
        for label_language in labels:
            for label in labels[label_language]:
                if label.isupper() and len(label) <= 10:
                    g.add((algorithm_ref, SML.abbreviation, Literal(label, lang=label_language)))
                    if label_language not in shortest_abbreviations or len(
                            shortest_abbreviations[label_language]) > len(label):
                        shortest_abbreviations[label_language] = label
                else:
                    g.add((algorithm_ref, RDFS.label, Literal(label, lang=label_language)))

        # aliases
        if 'aliases' in algorithm_json:
            aliases = algorithm_json['aliases']
            for alias_language in aliases:
                for alias in aliases[alias_language]:
                    if alias.isupper() and len(alias) <= 10:
                        g.add((algorithm_ref, SML.abbreviation, Literal(alias, lang=alias_language)))
                        if alias_language not in shortest_abbreviations or len(
                                shortest_abbreviations[alias_language]) > len(alias):
                            shortest_abbreviations[alias_language] = alias
                    else:
                        g.add((algorithm_ref, DCTERMS.alternative, Literal(alias, lang=alias_language)))

        # prefLabel
        for pref_label_language in pref_labels:
            g.add((algorithm_ref, SKOS.prefLabel, Literal(pref_labels[pref_label_language], lang=pref_label_language)))

        # prefAbbreviation
        for pref_abbreviation_language in shortest_abbreviations:
            g.add((algorithm_ref, SML.prefAbbreviation,
                   Literal(shortest_abbreviations[pref_abbreviation_language], lang=pref_abbreviation_language)))

        # prefDescription
        for pref_description_language in longest_descriptions:
            g.add((algorithm_ref, SML.prefDescription,
                   Literal(longest_descriptions[pref_description_language], lang=pref_description_language)))


for algorithm_id in all_parents:
    for parent_id in all_parents[algorithm_id]:
        g.add((id_to_ref[algorithm_id], RDFS.subClassOf, id_to_ref[parent_id]))

for s, p, o in g.triples((None, None, None)):
    print(s, p, o)

g.serialize(destination="../../../data_catalog/ml_processes_catalog/ml_catalog.ttl")

# TODO: Error with sml:Mean_shift and sml:Mean-shift
