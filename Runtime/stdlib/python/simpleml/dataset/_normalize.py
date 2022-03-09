from __future__ import annotations

import simpleml.util.jsonLabels_util as config
from sklearn import preprocessing


class StandardNormalizer:
    def normalize(self, dataset):
        if dataset.data.empty:
            dataset.readFile(dataset.separator)

        copy = dataset.copy()
        normalizer = preprocessing.Normalizer()

        columns = []
        for attribute in copy.attributes:
            if attribute != copy.target_attribute:
                if copy.simple_data_types[attribute] == config.type_numeric:
                    columns.append(attribute)

        normalized_features = normalizer.fit_transform(copy.data[columns])

        copy.data[columns] = normalized_features

        return copy
