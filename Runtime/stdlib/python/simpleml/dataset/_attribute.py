from __future__ import annotations

from typing import Any, Dict, Optional


class Attribute:
    def __init__(
        self,
        id: str,
        label: str = None,
        python_data_type: type = None,
        data_type: str = None,
        simple_data_type: str = None,
        graph: dict = None,
        is_virtual: bool = False,
    ):
        self.id: str = id
        self.label: str = id
        if label:
            self.label = label
        self.python_data_type: Optional[type] = python_data_type
        self.data_type: Optional[str] = data_type
        self.simple_data_type: Optional[str] = simple_data_type
        self.graph: Optional[Dict[str, Any]] = graph
        self.is_virtual: bool = is_virtual

    def copy(self) -> Attribute:
        copy = Attribute(
            id=self.id,
            label=self.label,
            python_data_type=self.python_data_type,
            data_type=self.data_type,
            simple_data_type=self.simple_data_type,
            graph=self.graph,
            is_virtual=self.is_virtual,
        )
        return copy
