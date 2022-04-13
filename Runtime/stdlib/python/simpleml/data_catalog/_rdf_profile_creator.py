from __future__ import annotations

from re import sub

import simpleml.util.jsonLabels_util as config
from rdflib import RDF, Graph, Literal, Namespace, URIRef
from rdflib.namespace import CSVW, DCAT, DCTERMS, XSD
from simpleml.dataset import Dataset
from simpleml.rdf._sparql_connector_local import get_graph
from simpleml.util import get_rdf_type_from_sml_type


def exportStatisticsAsRDF(dataset: Dataset, filename=None):
    profile = dataset.getProfile(remove_lat_lon=False)

    g = Graph()
    g.namespaces = get_graph().namespaces  # get all namespaces

    for x in g.namespaces():
        if not x[0].startswith(
            "default"
        ):  # in external vocabularies, namespaces are sometimes the default
            g.bind(x[0], x[1])

    SML = Namespace("https://simple-ml.de/resource/")
    SEAS = Namespace("https://w3id.org/seas/")

    rdf_dataset = SML[dataset.id]
    g.add((SML["simple-ml"], DCAT.dataset, rdf_dataset))
    g.add((rdf_dataset, RDF.type, DCAT.Dataset))
    g.add((rdf_dataset, DCTERMS.identifier, Literal(dataset.id)))
    g.add(
        (
            rdf_dataset,
            SML.numberOfInstances,
            Literal(dataset.number_of_instances, datatype=XSD.nonNegativeInteger),
        )
    )
    g.add(
        (rdf_dataset, SML.creatorId, Literal(0, datatype=XSD.nonNegativeInteger))
    )  # TODO: is this required?

    for lang, title in dataset.titles.items():
        g.add((rdf_dataset, DCTERMS.title, Literal(title, lang)))

    for lang, description in dataset.descriptions.items():
        g.add((rdf_dataset, DCTERMS.description, Literal(description, lang)))

    for lang, subjects in dataset.subjects.items():
        for subject in subjects:
            g.add((rdf_dataset, DCTERMS.subject, Literal(subject, lang)))

    if dataset.lat_before_lon:
        g.add(
            (
                rdf_dataset,
                SML.latBeforeLon,
                Literal(dataset.lat_before_lon, datatype=XSD.boolean),
            )
        )

    if dataset.coordinate_system:
        g.add(
            (
                rdf_dataset,
                SML.coordinateSystem,
                Literal(dataset.coordinate_system, datatype=XSD.nonNegativeInteger),
            )
        )

    # file
    rdf_file = SML[dataset.id + "File"]
    g.add((rdf_dataset, SML.hasFile, rdf_file))
    g.add((rdf_file, RDF.type, SML.TextFile))
    g.add((rdf_file, SML.fileLocation, Literal(dataset.fileName)))
    g.add((rdf_file, DCTERMS["format"], Literal("text/comma-separated-values")))
    g.add((rdf_file, CSVW.separator, Literal(dataset.separator)))
    g.add((rdf_file, CSVW.header, Literal(dataset.hasHeader, datatype=XSD.boolean)))
    g.add((rdf_file, CSVW.null, Literal(dataset.null_value)))

    # sample
    rdf_sample = SML[dataset.id + "Sample"]
    g.add((rdf_dataset, SML.hasSample, rdf_sample))
    g.add((rdf_sample, RDF.type, SML.DatasetSample))
    g.add((rdf_sample, CSVW.separator, Literal("	")))

    rdf_sample_header = SML[dataset.id + "SampleHeader"]
    g.add((rdf_sample, SML.hasHeader, rdf_sample_header))
    g.add((rdf_sample_header, RDF.type, SML.DatasetSampleLine))
    g.add(
        (
            rdf_sample_header,
            SML.hasContent,
            Literal(
                "\t".join(
                    [attribute.label for attribute in dataset.attributes.values()]
                ),
                datatype=XSD.string,
            ),
        )
    )

    for line_number in range(0, len(dataset.data_sample)):
        sample_line_content = (
            dataset.data_sample.loc[[line_number]]
            .to_csv(header=None, index=False, sep="\t", na_rep="")
            .strip()
        )
        # fillna(dataset.null_value)

        rdf_sample_line = SML[dataset.id + "SampleLine" + str(line_number)]
        g.add((rdf_sample, SML.hasLine, rdf_sample_line))
        g.add((rdf_sample_line, RDF.type, SML.DatasetSampleLine))
        g.add(
            (
                rdf_sample_line,
                SML.hasContent,
                Literal(sample_line_content, datatype=XSD.string),
            )
        )
        g.add((rdf_sample_line, SML.rank, Literal(line_number, datatype=XSD.integer)))

    # attributes
    for column_index, (attribute_id, attribute) in enumerate(
        dataset.attributes.items()
    ):

        attribute_val = profile[config.attributes][attribute.id]
        rdf_attribute = SML[
            dataset.id
            + "Attribute"
            + urify(attribute.id)[0].upper()
            + urify(attribute.id)[1:]
        ]

        g.add((rdf_dataset, SML.hasAttribute, rdf_attribute))
        g.add((rdf_attribute, RDF.type, SML.Attribute))
        g.add((rdf_attribute, DCTERMS.identifier, Literal(attribute.id)))
        g.add(
            (
                rdf_attribute,
                SML.isVirtual,
                Literal(attribute.is_virtual, datatype=XSD.boolean),
            )
        )

        g.add(
            (
                rdf_attribute,
                SML.columnIndex,
                Literal(column_index, datatype=XSD.nonNegativeInteger),
            )
        )

        # semantic graph
        if attribute.graph:
            g.add(
                (
                    rdf_attribute,
                    SML.valueType,
                    URIRef(attribute.graph["value_type"]),
                )
            )
            g.add(
                (
                    rdf_attribute,
                    SML.mapsToProperty,
                    attribute.graph["property"],
                )
            )
            g.add(
                (
                    rdf_attribute,
                    SML.mapsToDomain,
                    URIRef(attribute.graph["resource"]),
                )
            )
            g.add(
                (
                    URIRef(attribute.graph["resource"]),
                    SML.mapsTo,
                    attribute.graph["class"],
                )
            )
            g.add(
                (
                    URIRef(attribute.graph["resource"]),
                    RDF.type,
                    SML.ClassInstance,
                )
            )

            # add classInstance Property
            if "resource_rank" in attribute.graph:
                g.add(
                    (
                        URIRef(attribute.graph["resource"]),
                        SML.classInstance,
                        Literal(
                            attribute.graph["resource_rank"],
                            datatype=XSD.integer,
                        ),
                    )
                )

        for key, value in attribute_val["statistics"].items():
            if config.values in value:
                rank = 0

                for sub_value in value[config.values]:
                    rdf_attribute_stat = SML[
                        dataset.id
                        + "Attribute"
                        + urifyCapitalised(attribute.id)
                        + capitalise(value[config.id])
                        + str(rank)
                    ]

                    evaluation_type = (
                        "Distribution" + capitalise(value[config.id]) + "Evaluation"
                    )

                    # some evaluation types are defined in SEAS, others in SML
                    if capitalise(value[config.id]) in ["Quartile", "Decile"]:
                        evaluation_type = SEAS[evaluation_type]
                    else:
                        evaluation_type = SML[evaluation_type]

                    g.add((rdf_attribute_stat, RDF.type, evaluation_type))
                    g.add(
                        (
                            rdf_attribute_stat,
                            SEAS.evaluatedValue,
                            Literal(
                                sub_value,
                                datatype=get_rdf_type_from_sml_type(
                                    value[config.list_data_type]
                                ),
                            ),
                        )
                    )
                    g.add((rdf_attribute_stat, SEAS.rank, Literal(rank)))
                    g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))
                    rank += 1
            elif config.type_histogram_buckets in value:
                bucket_data_type = value[config.bucket_data_type]
                bucket_rank = 0
                for bucket in value[config.type_histogram_buckets]:
                    rdf_attribute_stat = SML[
                        dataset.id
                        + "Attribute"
                        + urifyCapitalised(attribute.id)
                        + "Histogram"
                        + str(bucket_rank)
                    ]
                    g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))
                    g.add(
                        (
                            rdf_attribute_stat,
                            RDF.type,
                            SML["DistributionHistogramEvaluation"],
                        )
                    )
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.bucketMinimum,
                            Literal(
                                bucket[config.bucketMinimum],
                                datatype=get_rdf_type_from_sml_type(bucket_data_type),
                            ),
                        )
                    )
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.bucketMaximum,
                            Literal(
                                bucket[config.bucketMaximum],
                                datatype=get_rdf_type_from_sml_type(bucket_data_type),
                            ),
                        )
                    )
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.instancesInBucket,
                            Literal(
                                bucket[config.bucketValue],
                                datatype=XSD.nonNegativeInteger,
                            ),
                        )
                    )
                    bucket_rank += 1
            elif config.type_bar_chart_bars in value:
                value_rank = 0
                for bar_value in value[config.type_bar_chart_bars]:
                    rdf_attribute_stat = SML[
                        dataset.id
                        + "Attribute"
                        + urifyCapitalised(attribute.id)
                        + "ValueDistributionValue"
                        + str(value_rank)
                    ]
                    g.add(
                        (rdf_attribute, SEAS.valueDistributionValue, rdf_attribute_stat)
                    )
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.numberOfInstancesOfValue,
                            Literal(
                                bar_value[config.number_of_instances],
                                datatype=XSD.nonNegativeInteger,
                            ),
                        )
                    )
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.instancesOfValue,
                            Literal(bar_value[config.value]),
                        )
                    )
                    g.add((rdf_attribute_stat, RDF.type, SML.ValueDistributionValue))
                    value_rank += 1
            elif config.type_spatial_distribution_areas in value:
                rdf_attribute_stat = SML[
                    dataset.id
                    + "Attribute"
                    + urifyCapitalised(attribute.id)
                    + "SpatialDistribution"
                ]
                g.add((rdf_attribute, SML.hasSpatialDistribution, rdf_attribute_stat))
                g.add((rdf_attribute_stat, RDF.type, SML.SpatialDistribution))

                for area, count in value[
                    config.type_spatial_distribution_areas
                ].items():
                    rdf_attribute_sub_stat = SML[
                        dataset.id
                        + "Attribute"
                        + urifyCapitalised(attribute.id)
                        + "SpatialDistributionLocation"
                        + urifyCapitalised(area)
                    ]
                    g.add(
                        (
                            rdf_attribute_stat,
                            SML.spatialDistributionValue,
                            rdf_attribute_sub_stat,
                        )
                    )
                    g.add(
                        (rdf_attribute_sub_stat, RDF.type, SML.SpatialDistributionValue)
                    )
                    g.add(
                        (rdf_attribute_sub_stat, SML.instancesOfRegion, Literal(area))
                    )
                    g.add(
                        (
                            rdf_attribute_sub_stat,
                            SML.numberOfInstancesInRegion,
                            Literal(count, datatype=XSD.nonNegativeInteger),
                        )
                    )

            else:
                rdf_attribute_stat = SML[
                    dataset.id
                    + "Attribute"
                    + urifyCapitalised(attribute.id)
                    + capitalise(value[config.id])
                ]
                g.add((rdf_attribute, SEAS.evaluation, rdf_attribute_stat))

                g.add(
                    (
                        rdf_attribute_stat,
                        SEAS.evaluatedValue,
                        Literal(
                            value[config.value],
                            datatype=get_rdf_type_from_sml_type(value["data_type"]),
                        ),
                    )
                )

                # some evaluation types are defined in SEAS, others in SML
                evaluation_type = (
                    "Distribution" + capitalise(value[config.id]) + "Evaluation"
                )
                if capitalise(value[config.id]) in [
                    "Median",
                    "Mean",
                    "Maximum",
                    "Minimum",
                    "StandardDeviation",
                ]:
                    evaluation_type = SEAS[evaluation_type]
                else:
                    evaluation_type = SML[evaluation_type]

                g.add((rdf_attribute_stat, RDF.type, evaluation_type))

    if dataset.domain_model:
        for s, p, o in dataset.domain_model.get_attribute_relation_triples_graph():
            g.add((s, p, o))

    if filename:
        g.serialize(
            filename,
            format="turtle",
        )
    else:
        return g.serialize(format="turtle")  # .decode("utf-8")


def urify(string):
    string = sub(r"(_|-)+", " ", string).title().replace(" ", "")
    return string[0].lower() + string[1:]


def urifyCapitalised(string):
    if not string:
        string = "None"

    string = sub(r"(_|-)+", " ", string)
    string = [capitalise(x) for x in string.split(" ")]
    string = "".join(string).replace(" ", "")
    return string[0].upper() + string[1:]


def capitalise(string):
    return string[0].upper() + string[1:]
