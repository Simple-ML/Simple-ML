package example

import simpleml.dataset.loadDataset
import simpleml.model.supervised.regression.LinearRegression
import simpleml.metrics.meanAbsoluteError

workflow exampleworkflow {
	val dataset = loadDataset("WhiteWineQuality");
	val datasetWithTarget = dataset.setTargetAttribute("quality");

	val X_train, val X_test, val y_train, val y_test = datasetWithTarget.splitIntoTrainAndTestAndLabels(0.8);

	val lr = LinearRegression();

	val lr_model = lr.fit(X_train,y_train );
	val y_pred = lr_model.predict(X_test);

	// val err = meanAbsoluteError(y_test, y_pred);
}
