import unittest

import pandas

from simpleml.data import TabularDataset


class TestDataset(unittest.TestCase):

    def setUp(self):
        data = {'col1': [1, 2, 3], 'col2': [4, 5, 6], 'col3': [7, 8, 9]}
        self._dataframe = pandas.DataFrame(data=data)

    def test_add_column(self):
        # arrange
        new_column_name = "newCol"
        new_value = 5
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.addColumn(new_column_name, lambda row: new_value)
        # assert
        self.assertEqual(3, len(dataset.getColumnNames()))
        self.assertEqual(4, len(new_dataset.getColumnNames()))
        self.assertEqual(new_column_name, new_dataset.getColumnNames()[3])
        self.assertEqual(new_value, new_dataset.getValue(new_column_name, 0))
        self.assertEqual(new_value, new_dataset.getValue(new_column_name, 1))

    def test_drop_columns(self):
        # arrange
        drop_column_name_1 = "col1"
        drop_column_name_2 = "col3"
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.dropColumns(drop_column_name_1, drop_column_name_2)
        # assert
        self.assertEqual(3, len(dataset.getColumnNames()))
        self.assertEqual(1, len(new_dataset.getColumnNames()))
        self.assertEqual("col2", new_dataset.getColumnNames()[0])

    def test_keep_columns(self):
        # arrange
        keep_column_name_1 = "col2"
        keep_column_name_2 = "col3"
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.keepColumns(keep_column_name_1, keep_column_name_2)
        # assert
        self.assertEqual(3, len(dataset.getColumnNames()))
        self.assertEqual(2, len(new_dataset.getColumnNames()))
        self.assertEqual("col2", new_dataset.getColumnNames()[0])
        self.assertEqual("col3", new_dataset.getColumnNames()[1])

    def test_filter_rows(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.filterRows(lambda row: row.getValue("col1") > 1)
        # assert
        self.assertEqual(2, new_dataset.getNumberOfRows())
        self.assertEqual(2, new_dataset.getValue("col1", 0))
        self.assertEqual(5, new_dataset.getValue("col2", 0))
        self.assertEqual(8, new_dataset.getValue("col3", 0))
        self.assertEqual(3, new_dataset.getValue("col1", 1))
        self.assertEqual(6, new_dataset.getValue("col2", 1))
        self.assertEqual(9, new_dataset.getValue("col3", 1))

    def test_get_value(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        value = dataset.getValue("col2", 1)
        # assert
        self.assertEqual(5, value)

    def test_split(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset_1, new_dataset_2 = dataset.split(fraction=0.6, random_state=0)
        # assert
        self.assertEqual(2, new_dataset_1.getNumberOfRows())
        self.assertEqual(3, new_dataset_1.getValue("col1", 0))
        self.assertEqual(6, new_dataset_1.getValue("col2", 0))
        self.assertEqual(9, new_dataset_1.getValue("col3", 0))
        self.assertEqual(2, new_dataset_1.getValue("col1", 1))
        self.assertEqual(5, new_dataset_1.getValue("col2", 1))
        self.assertEqual(8, new_dataset_1.getValue("col3", 1))
        self.assertEqual(1, new_dataset_2.getNumberOfRows())
        self.assertEqual(1, new_dataset_2.getValue("col1", 0))
        self.assertEqual(4, new_dataset_2.getValue("col2", 0))
        self.assertEqual(7, new_dataset_2.getValue("col3", 0))

    def test_sample(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.sample(count=2, random_state=0)
        # assert
        self.assertEqual(2, new_dataset.getNumberOfRows())
        self.assertEqual(3, new_dataset.getValue("col1", 0))
        self.assertEqual(6, new_dataset.getValue("col2", 0))
        self.assertEqual(9, new_dataset.getValue("col3", 0))
        self.assertEqual(2, new_dataset.getValue("col1", 1))
        self.assertEqual(5, new_dataset.getValue("col2", 1))
        self.assertEqual(8, new_dataset.getValue("col3", 1))

    def test_fist(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.first(count=2)
        # assert
        self.assertEqual(2, new_dataset.getNumberOfRows())
        self.assertEqual(1, new_dataset.getValue("col1", 0))
        self.assertEqual(4, new_dataset.getValue("col2", 0))
        self.assertEqual(7, new_dataset.getValue("col3", 0))
        self.assertEqual(2, new_dataset.getValue("col1", 1))
        self.assertEqual(5, new_dataset.getValue("col2", 1))
        self.assertEqual(8, new_dataset.getValue("col3", 1))

    def test_last(self):
        # arrange
        dataset_frame_copy = self._dataframe.copy(deep=True)
        dataset = TabularDataset(dataset_frame_copy)
        # act
        new_dataset = dataset.last(count=2)
        # assert
        self.assertEqual(2, new_dataset.getNumberOfRows())
        self.assertEqual(2, new_dataset.getValue("col1", 0))
        self.assertEqual(5, new_dataset.getValue("col2", 0))
        self.assertEqual(8, new_dataset.getValue("col3", 0))
        self.assertEqual(3, new_dataset.getValue("col1", 1))
        self.assertEqual(6, new_dataset.getValue("col2", 1))
        self.assertEqual(9, new_dataset.getValue("col3", 1))


if __name__ == '__main__':
    unittest.main()
