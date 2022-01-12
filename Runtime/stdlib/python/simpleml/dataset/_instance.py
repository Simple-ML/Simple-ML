from __future__ import annotations


class Instance:

    def __init__(self, row):
        self.row = row

    def getValue(self, attribute: str):
        return self.row[attribute]
