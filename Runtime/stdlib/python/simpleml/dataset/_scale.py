from __future__ import annotations

import pandas as pd
from sklearn import preprocessing


class StandardScaler:

    def scale(self, dataset):
        if dataset.data.empty:
            dataset.readFile(dataset.separator)

        copy = dataset.copy()

        scaler = preprocessing.StandardScaler()
        scaled_features = scaler.fit_transform(copy.data)

        copy.data = pd.DataFrame(scaled_features, index=copy.data.index, columns=copy.data.columns)

        return copy
