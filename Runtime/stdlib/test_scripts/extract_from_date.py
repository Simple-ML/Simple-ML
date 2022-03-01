# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset


# Workflow steps ---------------------------------------------------------------


def exampleWorkflow():
    dataset = loadDataset("SpeedAverages")

    dataset_weekend = dataset.addIsWeekendAttribute('start_time')
    dataset_day_of_year = dataset.addDayOfTheYearAttribute('start_time')
    dataset_week_day = dataset.addWeekDayAttribute('start_time')
    print(dataset_weekend.data)
    print(dataset_day_of_year.data)
    print(dataset_week_day.data)


if __name__ == '__main__':
    exampleWorkflow()
