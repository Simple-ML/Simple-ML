from __future__ import annotations

from simpleml.util import simple_type_numeric
from sklearn import preprocessing


class StandardScaler:
    def scale(self, dataset):
        if dataset.data.empty:
            dataset.readFile()

        copy = dataset.copy()

        scaler = preprocessing.StandardScaler()

        columns = []
        for attribute in copy.attributes.values():
            if attribute != copy.target_attribute:
                if attribute.simple_data_type == simple_type_numeric:
                    columns.append(attribute.id)

        scaled_features = scaler.fit_transform(copy.data[columns])

        copy.data[columns] = scaled_features

        return copy
