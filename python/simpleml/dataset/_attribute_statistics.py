from __future__ import annotations
from typing import Any, Tuple
import os

# import Statistics

from simpleml.dataset import getStatistics

import pandas as pd  # For data processing
import numpy as np  # For huge arrays and matrices
import json
import matplotlib as plt  # for graphs
from plpygis import Geometry
from shapely import wkt, wkb
import geopandas

class AttributeStatistics:

    def __init__(self, values: {}):
        self.values = values

