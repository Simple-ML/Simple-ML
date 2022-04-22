package example

import simpleml.dataset.loadDataset
import simpleml.model.supervised.regression.LinearRegression
import simpleml.metrics.meanAbsoluteError
import simpleml.dataset.StandardNormalizer
import simpleml.dataset.WeekDayTransformer

workflow exampleworkflow {
	val dataset = loadDataset("SpeedAverages");
	val datasetWithTarget = dataset.setTargetAttribute("average_speed");
	val datasetFiltered = datasetWithTarget.dropAttribute("geometry");
    val datasetNoNullValues = datasetFiltered.dropAllMissingValues()

	val weekDayTransformer = WeekDayTransformer("start_time");
	val datasetWithWeekday = datasetNoNullValues.transform("start_time", weekDayTransformer);

	val datasetTransformed = datasetWithWeekday.transformDatatypes();
    val datasetNormalized = StandardNormalizer().normalize(datasetTransformed);

	val X_train, val X_test, val y_train, val y_test = datasetNormalized.splitIntoTrainAndTestAndLabels(0.8);

	val lr = LinearRegression();

	val lr_model = lr.fit(X_train,y_train );
	val y_pred = lr_model.predict(X_test);
}


