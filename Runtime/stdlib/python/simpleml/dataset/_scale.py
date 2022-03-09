from __future__ import annotations

import simpleml.util.jsonLabels_util as config
from sklearn import preprocessing


class StandardScaler:
    def scale(self, dataset):
        if dataset.data.empty:
            dataset.readFile()

        copy = dataset.copy()

        scaler = preprocessing.StandardScaler()

        columns = []
        for attribute in copy.attributes:
            if attribute != copy.target_attribute:
                if copy.simple_data_types[attribute] == config.type_numeric:
                    columns.append(attribute)

        scaled_features = scaler.fit_transform(copy.data[columns])

        copy.data[columns] = scaled_features

        return copy
