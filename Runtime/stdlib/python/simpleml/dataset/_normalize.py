from __future__ import annotations

from simpleml.util import simple_type_numeric
from sklearn import preprocessing


class StandardNormalizer:
    def normalize(self, dataset):
        if dataset.data.empty:
            dataset.readFile()

        copy = dataset.copy()
        normalizer = preprocessing.Normalizer()

        columns = []
        for attribute in copy.attributes.values():
            if attribute != copy.target_attribute:
                if attribute.simple_data_type == simple_type_numeric:
                    columns.append(attribute.id)

        normalized_features = normalizer.fit_transform(copy.data[columns])

        copy.data[columns] = normalized_features

        return copy
