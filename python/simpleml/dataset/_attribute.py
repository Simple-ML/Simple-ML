from __future__ import annotations


# import Statistics


# data_folder_name = '../../../data/'  # TODO: Configure globally


class Dataset:
    def __init__(self, id: str = None):
        self.id = id
        self.labels = None
