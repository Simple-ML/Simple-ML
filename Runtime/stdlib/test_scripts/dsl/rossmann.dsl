package example

import simpleml.dataset.loadDataset
import simpleml.dataset.joinTwoDatasets
import simpleml.model.supervised.regression.RidgeRegression
import simpleml.metrics.meanAbsoluteError
import simpleml.dataset.StandardScaler

workflow exampleworkflow {
	val datasetSales = loadDataset("RossmannSales");
	val datasetStores = loadDataset("RossmannStores");

	val datastoreJoined = joinTwoDatasets(datasetSales, datasetStores, attributeId1="Store", attributeId2="Store",suffix1="_Sales", suffix2="_Stores");

	val datasetWithTarget = datastoreJoined.setTargetAttribute("Sales");
    val datasetNoNullValues = datasetWithTarget.dropAllMissingValues()
	val datasetTransformed = datasetNoNullValues.transformDatatypes();
    val datasetScaled = StandardScaler().scale(datasetTransformed);

	val X_train, val X_test, val y_train, val y_test = datasetScaled.splitIntoTrainAndTestAndLabels(0.8);

	val rr = RidgeRegression();

	val rr_model = rr.fit(X_train,y_train );
	val y_pred = rr_model.predict(X_test);

	val mae = meanAbsoluteError(y_test, y_pred)
}


