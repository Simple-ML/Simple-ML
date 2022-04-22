# Imports ----------------------------------------------------------------------
from simpleml.dataset import StandardNormalizer, loadDataset, WeekendTransformer
from simpleml.metrics import meanAbsoluteError
from simpleml.model.supervised.regression import LinearRegression

# Workflow steps ---------------------------------------------------------------

dataset = loadDataset("SpeedAverages")
print(dataset.dataset_json)

dataset = dataset.setTargetAttribute("average_speed")
dataset = dataset.transform("start_time", WeekendTransformer("start_time"))
dataset = dataset.dropAttribute("geometry")

dataset = dataset.dropAllMissingValues()
dataset = dataset.transformDatatypes()

# dataset = StandardScaler().scale(dataset)
dataset = StandardNormalizer().normalize(dataset)

X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)

print(X_train.data)

lr = LinearRegression().fit(X_train, y_train)
y_pred = lr.predict(X_test)

print("\nY_test\n")
print(y_test.data)

print("\nY_pred\n")
print(y_pred.data)
print("MAE:", meanAbsoluteError(y_test, y_pred))
