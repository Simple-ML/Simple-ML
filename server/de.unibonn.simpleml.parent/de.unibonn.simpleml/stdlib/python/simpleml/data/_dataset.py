from __future__ import annotations

from typing import Any, Callable, List

import pandas as pd

from ._fetcher import Fetcher


class Parser:
    """
     Defines the interface for reading the raw data of a dataset
     from a particular serialization format.
     This class is abstract, the purpose is to subclass it,
     defining specific constructors in each of the subclasses.
     The subclass constructors take all the specific arguments
     necessary for the particular type of data (e.g. the separator
     character for CSV data).
     """

    def parse(self, filename: str) -> Dataset:
        pass


class CSVParser(Parser):
    def __init__(self, separator: str = ","):
        self.separator = separator

    def parse(self, filename: str) -> TabularDataset:
        return TabularDataset(pd.read_csv(filename, self.separator))

class Dataset:
    """
    Defines the interface for all dataset classes.
    """

    pass


def loadDataset(fetcher: Fetcher, parser: Parser) -> Dataset:
    return parser.parse(fetcher.fetch())


class TabularDataset(Dataset):
    """
    Defines the interface for all dataset classes.
    """

    def __init__(self, dataframe: pd.DataFrame):
        self._dataframe = dataframe

    def addColumn(self, attributeName: str,
                  generatorFunction: Callable[[TabularDatasetRow], Any]) -> TabularDataset:
        new_dataframe = self._dataframe.copy(deep=True)
        new_dataframe[attributeName] = new_dataframe.apply(lambda row: generatorFunction(TabularDatasetRow(row)),
                                                           axis=1)
        return TabularDataset(new_dataframe)

    def dropColumns(self, *columnNames: str) -> TabularDataset:
        """
        Returns a new dataset without the columns with the given names.

        :param columnNames:
            The columns to drop.

        :return:
            The new dataset.
        """

        column_names_list = list(columnNames)
        new_dataframe = self._dataframe.copy(deep=True)
        new_dataframe = new_dataframe.drop(columns=column_names_list)
        return TabularDataset(new_dataframe)

    def keepColumns(self, *columnNames: str) -> TabularDataset:
        """
        Returns a new dataset containing only the specified attributes.
        :param columnNames: The list of the desired attributes.
        :return: A new dataset (deep copy).
        """
        column_names_list = list(columnNames)
        new_dataframe = self._dataframe.copy(deep=True)
        new_dataframe = new_dataframe[column_names_list]
        return TabularDataset(new_dataframe)

    def filterRows(self, filterFunction: Callable[[TabularDatasetRow], bool]) -> TabularDataset:
        """
        Returns a new dataset only containing the rows that match the filter criterion, indictated by the filter\
        function.
        :param filterFunction: A function which returns true if the row should be kept.
        :return: A new dataset (deep copy).
        """
        new_dataframe = self._dataframe.copy(deep=True)
        new_dataframe = new_dataframe[self._dataframe.apply(
            lambda row: filterFunction(TabularDatasetRow(row)), axis=1)]
        return TabularDataset(new_dataframe)

    def getValue(self, columnName: str, rowNumber: int) -> Any:
        """
        Returns the content of a cell inside the dataset. The row number refers to the n-th row of the dataset.
        The row number of a specific row might change when filtering.
        :param columnName: The name of the column.
        :param rowNumber: The n-th row.
        :return: The value of the cell.
        """
        return self._dataframe[columnName].iloc[rowNumber]

    def getColumnNames(self) -> List[str]:
        """
        Returns a list with the names of the columns.
        """

        return list(self._dataframe.columns)

    def getNumberOfRows(self) -> int:
        """
        Returns the number of rows currently in the dataset.
        :return: The number of rows.
        """
        return len(self._dataframe)

    def split(self, fraction: float, random_state: int = None) -> (TabularDataset, TabularDataset):
        """
        Splits a dataset into two parts, according to the fraction. Indices are reset.
        :param fraction: Determines the percentage of rows in the first dataset. Float between 0 and 1.
        :param random_state: Random state used for splitting.
        :return: Two new datasets with the corresponding number of rows.
        """
        if fraction >= 1 or fraction <= 0:
            raise ValueError("fraction must be > 0 and < 1")

        new_dataset_1 = self._dataframe.sample(frac=fraction, random_state=random_state)
        new_dataset_2 = self._dataframe.drop(new_dataset_1.index)

        new_tabular_dataset_1 = TabularDataset(new_dataset_1.reset_index(drop=True))
        new_tabular_dataset_2 = TabularDataset(new_dataset_2.reset_index(drop=True))

        return new_tabular_dataset_1, new_tabular_dataset_2

    def sample(self, count: int, random_state: int = None) -> TabularDataset:
        """
        Creates a sample with 'count' number of rows. Indices are reset.
        :param count: Determines the number of rows in the sample.
        :param random_state: Random state used for sampling.
        :return: A new dataset with the corresponding number of rows.
        """
        if count > len(self._dataframe) or count <= 0:
            raise ValueError("count must be > 0 and <= the number of rows")

        new_dataset = self._dataframe.sample(n=count, random_state=random_state)

        new_tabular_dataset = TabularDataset(new_dataset.reset_index(drop=True))

        return new_tabular_dataset

    def first(self, count: int) -> TabularDataset:
        """
        Creates a new TabularDataset with the first 'count' rows. Indices are reset.
        :param count: Determines the number of rows that are returned.
        :return: A new dataset with the corresponding number of rows.
        """
        if count > len(self._dataframe) or count <= 0:
            raise ValueError("count must be > 0 and <= the number of rows")

        new_dataset = self._dataframe.head(n=count)

        new_tabular_dataset = TabularDataset(new_dataset)

        return new_tabular_dataset

    def last(self, count: int) -> TabularDataset:
        """
        Creates a new TabularDataset with the last 'count' rows.
        :param count: Determines the number of rows that are returned.
        :return: A new dataset with the corresponding number of rows.
        """
        if count > len(self._dataframe) or count <= 0:
            raise ValueError("count must be > 0 and <= the number of rows")

        new_dataset = self._dataframe.tail(n=count)

        new_tabular_dataset = TabularDataset(new_dataset.reset_index(drop=True))

        return new_tabular_dataset


class TabularDatasetRow:
    """
    Defines the interface for all instance classes.
    """

    def __init__(self, row: pd.Series):
        self.row = row

    def getValue(self, columnName: str) -> Any:
        """
        Returns the value in the column with the specified name.
        """

        return self.row[columnName]
