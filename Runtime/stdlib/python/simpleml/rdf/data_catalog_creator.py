import os

from simpleml.data_catalog._rdf_profile_creator import exportStatisticsAsRDF
from simpleml.rdf._meta_file_reader import read_meta_file
from simpleml.util import exportDictionaryAsJSON

meta_files_folder = "../data_catalog/meta_files"
catalog_files_folder = "../data_catalog/datasets"

for filename in os.listdir(meta_files_folder):

    if "TrafficWarnings.tsv" in filename:
        continue

    # if "FloatingCarData.tsv" not in filename:
    #    continue

    print(filename)

    filepath = os.path.join(meta_files_folder, filename)
    dataset = read_meta_file(filepath)

    output_filepath = os.path.join(catalog_files_folder, filename.replace(".tsv", ".ttl"))

    print(exportDictionaryAsJSON(dataset.getProfile()))
    exportStatisticsAsRDF(dataset, filename=output_filepath)
