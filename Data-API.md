# Description
# Installation

Run the following command in the root directory of the project where the setup.py script is located to install the Simple-ML API: 

    pip install -e API

DSL stubs for the data API are defined [here](https://github.com/Simple-ML/Stdlib/blob/master/stubs/simpleml/dataset/dataset.stub.simpleml).

# Testing

Run the example workflow (using the Python environment that contains the installed Simple-ML API):

    python tests/speedPrediction_predictSpeed.py

# Example

`    datasets = getDatasets()

    dataset = loadDataset("SpeedAverages")

    dataset = dataset.sample(50) # -> statistics get invalid
    dataset = dataset.keepAttributes(
        ["osm_id", "street_type", "number_of_drivers", "start_time", "is_weekend", "geometry"])
    train, test = dataset.splitIntoTrainAndTest(0.8)

    trainStatistics = train.getStatistics()`