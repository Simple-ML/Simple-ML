from __future__ import annotations
from typing import Any, Tuple
import os

# import Statistics

from simpleml.dataset import getStatistics
import simpleml.util._jsonLabels_util as config

import pandas as pd  # For data processing
import numpy as np  # For huge arrays and matrices
import json
import matplotlib as plt  # for graphs
from plpygis import Geometry
from shapely import wkt, wkb
import geopandas
import simpleml.util._jsonLabels_util as config
import simpleml.util._global_configurations as global_config

#data_folder_name = '../../../data/'  # TODO: Configure globally


class Dataset:
    def __init__(self, id: str = None):
        self.id = id
        self.labels = None