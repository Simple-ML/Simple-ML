import pandas as pd


def read_tsv(path):
    return pd.read_csv(path, sep="\t")


def project(dataframe, rows):
    return dataframe[rows]


def write(obj):
    print(obj)
