# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset, StandardNormalizer
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression

# Workflow steps ---------------------------------------------------------------

dataset = loadDataset("SpeedAverages")
print(dataset.dataset_json)

dataset = dataset.setTargetAttribute("average_speed")
dataset = dataset.addWeekDayAttribute("start_time")

dataset = dataset.dropAttributes(["osm_id", "start_time"])

dataset = dataset.dropMissingValues("average_speed")
dataset = dataset.transformDatatypes()

# dataset = StandardScaler().scale(dataset)
dataset = StandardNormalizer().normalize(dataset)

X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)

lr = LinearRegression().fit(X_train, y_train)
y_pred = lr.predict(X_test)

print("\nY_test\n")
print(y_test.data)

print("\nY_pred\n")
print(y_pred.data)
print("MAE:", meanAbsoluteError(y_test, y_pred))
