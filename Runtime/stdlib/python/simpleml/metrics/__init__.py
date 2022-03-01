import imp

from ._classification import (
    accuracy,
    averagePrecision,
    balancedAccuracy,
    precision,
    recall,
)
from ._regression import (
    meanAbsoluteError,
    meanSquaredError,
    meanSquaredLogError,
    medianAbsoluteError,
    r2,
)
