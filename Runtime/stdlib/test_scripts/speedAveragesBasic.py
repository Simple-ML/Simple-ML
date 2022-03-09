# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset
from simpleml.dataset._scale import StandardScaler
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression

# Workflow steps ---------------------------------------------------------------

dataset = loadDataset("SpeedAverages")
print(dataset.dataset_json)

dataset = dataset.dropAttributes(["osm_id", "geometry"])

dataset = dataset.setTargetAttribute("average_speed")
dataset = dataset.addWeekDayAttribute("start_time")

dataset = dataset.dropMissingValues("average_speed")
dataset = dataset.transformDatatypes()
dataset = StandardScaler().scale(dataset)
# dataset = StandardNormalizer().normalize(dataset)

X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.75)

print(X_test.dataset_json)

lr = LinearRegression().fit(X_train, y_train)
y_pred = lr.predict(X_test)
print("MAE:", meanAbsoluteError(y_test, y_pred))
