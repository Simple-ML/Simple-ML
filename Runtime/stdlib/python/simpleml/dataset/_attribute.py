from __future__ import annotations


class Attribute:
    def __init__(self, id: str = None, label: str = None, python_data_type: type = None, data_type: str = None,
                 simple_data_type: str = None,
                 graph: dict = None, is_virtual: bool = False):
        self.id = id
        self.label = label

        self.python_data_type: type = python_data_type
        self.data_type: str = data_type
        self.simple_data_type: str = simple_data_type
        self.is_virtual = is_virtual

        self.graph: dict = graph

    def copy(self) -> Attribute:
        copy = Attribute(id=self.id, label=self.label, python_data_type=self.python_data_type, data_type=self.data_type,
                         simple_data_type=self.simple_data_type,
                         graph=self.graph, is_virtual=self.is_virtual)
        return copy
