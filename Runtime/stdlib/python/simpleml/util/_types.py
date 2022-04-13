import numpy as np
import pandas as pd
from numpy import datetime64
from rdflib.namespace import RDFS, XSD
from rdflib.plugins.sparql.datatypes import type_promotion

# RDF types to Python types
_rdf_types_to_python_types = {
    XSD.dateTime: datetime64,
    XSD.date: datetime64,
    XSD.long: int,
    XSD.nonNegativeInteger: int,
    XSD.integer: int,
    XSD.double: float,
    XSD.float: float,
    XSD.decimal: float,
    XSD.string: str,
    XSD.boolean: bool,
    RDFS.Literal: str,
}

# simple types
simple_type_numeric = "numeric"
simple_type_numeric_list = "numeric_list"
simple_type_datetime = "temporal"
simple_type_geometry = "geometry"
simple_type_boolean = "boolean"
simple_type_string = "string"

# Types
type_numeric_list = "numeric_list"
type_integer = "integer"
type_long = "long"
type_float = "float"
type_string = "string"
type_datetime = "datetime"
type_bool = "bool"
type_geometry = "geometry"

_sml_types_to_rdf_types = {
    type_long: XSD.long,
    type_float: XSD.float,
    type_integer: XSD.integer,
    type_datetime: XSD.dateTime,
    type_bool: XSD.boolean,
    type_string: XSD.string,
}

_type_to_simple_type = {
    type_numeric_list: simple_type_numeric_list,
    type_long: simple_type_numeric,
    type_float: simple_type_numeric,
    type_integer: simple_type_numeric,
    type_string: simple_type_string,
    type_datetime: simple_type_datetime,
    type_bool: simple_type_boolean,
    type_geometry: simple_type_geometry,
}

# Python types
_python_types_to_sml_types = {
    datetime64: type_datetime,
    int: type_integer,
    float: type_float,
    bool: type_bool,
    str: type_string,
    np.float64: type_float,
}

_python_types_to_simple_types = {
    datetime64: simple_type_datetime,
    int: simple_type_numeric,
    float: simple_type_numeric,
    bool: simple_type_boolean,
    str: simple_type_string,
}

_rdf_types_to_sml_types = {}
for rdf_type, python_type in _rdf_types_to_python_types.items():
    _rdf_types_to_sml_types[rdf_type] = _python_types_to_sml_types[python_type]

# numpy types

# pandas types
"""
data_type_labels = {
    np.int32: type_integer,
    Int32Dtype(): type_integer,
    Int64Dtype(): type_long,
    np.bool_: type_bool,
    np.datetime64: type_datetime,
    str: type_string,
    int: type_integer,
    float: type_float,
    np.int64: type_integer,
    np.float64: type_float,
    np.floating: type_float,
    np.integer: type_integer,
    np.dtype("float64"): type_float,
    np.dtype("int64"): type_integer,
    np.dtype("bool"): type_bool,
    "str": type_string,
    bool: type_bool,
    object: "ERROR",
    geometry: type_geometry,
    Timestamp: type_datetime,
    StringDtype: type_string,
    type_numeric_list: type_numeric_list,
}

"""

_pandas_types_to_python_types = {
    pd.Float64Dtype(): float,
    pd.Int64Dtype(): int,
    pd.StringDtype(): str,
    pd.BooleanDtype(): bool,
    pd.Timestamp: datetime64,
    np.dtype("datetime64[ns]"): datetime64,
}

_python_types_to_pandas_types = {v: k for k, v in _pandas_types_to_python_types.items()}


def promote_rdf_type(rdf_type):
    try:
        if type_promotion(rdf_type, XSD.integer) == XSD.integer:
            return XSD.integer
    except TypeError:
        try:
            if type_promotion(rdf_type, XSD.double) == XSD.double:
                return XSD.double
        except TypeError:
            pass
    finally:
        return rdf_type


def get_sml_type_from_python_type(python_type, return_if_missing: object = None):
    return _python_types_to_sml_types.get(python_type, return_if_missing)


def get_sml_type_from_rdf_type(rdf_type, return_if_missing: object = None):
    return _rdf_types_to_sml_types.get(promote_rdf_type(rdf_type), return_if_missing)


def get_simple_type_from_sml_type(sml_type, return_if_missing: object = None):
    return _type_to_simple_type.get(sml_type, return_if_missing)


def get_python_type_from_rdf_type(rdf_type, return_if_missing: object = None):
    return _rdf_types_to_python_types.get(promote_rdf_type(rdf_type), return_if_missing)


def get_simple_type_from_python_type(python_type, return_if_missing: object = None):
    return _python_types_to_simple_types.get(python_type, return_if_missing)


def get_python_type_from_pandas_type(pandas_type, return_if_missing: object = None):
    return _pandas_types_to_python_types.get(pandas_type, return_if_missing)


def get_rdf_type_from_sml_type(sml_type, return_if_missing: object = None):
    return _sml_types_to_rdf_types.get(sml_type, return_if_missing)


def get_pandas_type_from_python_type(python_type, return_if_missing: object = None):
    return _python_types_to_pandas_types.get(python_type, return_if_missing)
