# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset
from simpleml.dataset._scale import StandardScaler
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression

# Workflow steps ---------------------------------------------------------------
from simpleml.util import exportDictionaryAsJSON

dataset = loadDataset("SpeedAverages")
print(exportDictionaryAsJSON(dataset.getProfile()))

dataset = dataset.dropAttributes(["osm_id", "geometry"])

dataset = dataset.setTargetAttribute("average_speed")
dataset = dataset.addWeekDayAttribute("start_time")

dataset = dataset.dropMissingValues("average_speed")
dataset = dataset.transformDatatypes()
print(exportDictionaryAsJSON(dataset.getProfile()))
dataset = StandardScaler().scale(dataset)
# dataset = StandardNormalizer().normalize(dataset)

print(dataset.data)

X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.75)

# print(exportDictionaryAsJSON(X_train.getProfile()))

lr = LinearRegression().fit(X_train, y_train)
y_pred = lr.predict(X_test)
print("MAE:", meanAbsoluteError(y_test, y_pred))
