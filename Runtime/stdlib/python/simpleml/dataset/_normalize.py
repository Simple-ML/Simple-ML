from __future__ import annotations

import pandas as pd
from sklearn.preprocessing import Normalizer


class StandardNormalizer:

    def normalize(self, dataset):
        if dataset.data.empty:
            dataset.readFile(dataset.separator)

        copy = dataset.copy()

        normalizer = Normalizer().fit_transform(copy.data)

        copy.data = pd.DataFrame(normalizer, index=copy.data.index, columns=copy.data.columns)

        return copy
