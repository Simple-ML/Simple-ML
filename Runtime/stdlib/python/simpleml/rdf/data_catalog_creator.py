import os

from simpleml.data_catalog._rdf_profile_creator import exportStatisticsAsRDF
from simpleml.rdf._meta_file_reader import read_meta_file
from simpleml.util import exportDictionaryAsJSON, global_configurations

dirName = os.path.dirname(__file__)
metadataFolderPath = os.getenv(
    "SML_DATA_CATALOG_PATH",
    os.path.join(dirName, global_configurations.meta_data_folder_name),
)

dataCatalogFolderPath = os.getenv(
    "SML_DATASET_PATH",
    os.path.join(dirName, global_configurations.data_catalog_folder_name),
)

for filename in os.listdir(metadataFolderPath):
    print(filename)
    if "TrafficWarnings.tsv" in filename:
        continue

    # if "SpeedAverages.tsv" not in filename:
    #    continue

    print(filename)

    output_filepath = os.path.join(
        dataCatalogFolderPath, filename.replace(".tsv", ".ttl")
    )

    filepath = os.path.join(metadataFolderPath, filename)
    dataset = read_meta_file(filepath)

    print(dataset.getProfile())

    print(exportDictionaryAsJSON(dataset.getProfile()))
    exportStatisticsAsRDF(dataset, filename=output_filepath)
