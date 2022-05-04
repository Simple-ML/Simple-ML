# Imports ----------------------------------------------------------------------
from simpleml.dataset import loadDataset, TimestampTransformer

# Workflow steps ---------------------------------------------------------------
from simpleml.metrics import meanAbsoluteError, accuracy,averagePrecision,balancedAccuracy,meanSquaredError
from simpleml.metrics import meanSquaredLogError, medianAbsoluteError, precision, r2, recall
from simpleml.dataset import StandardNormalizer, StandardScaler
from simpleml.model.supervised.regression import LinearRegression, RidgeRegression
from simpleml.model.supervised.regression._tree import DecisionTreeRegressor, RandomForestRegressor

from typing import Iterable, Any
from itertools import product
import pandas as pd


def grid_parameters(parameters: dict[str, Iterable[Any]]) -> Iterable[dict[str, Any]]:
    for params in product(*parameters.values()):
        yield dict(zip(parameters.keys(), params))

def exampleWorkflow():

    DATASET_NAME = "FloatingCarData"
    TARGET_VAR = "speed"
    #### LOADING DATASET ####
    dataset = loadDataset(DATASET_NAME)
    dataset = dataset.sample(1000)

    #### PREPROCESSING DATASET #### # does it affect target var?
    dataset = dataset.dropAllMissingValues()
    dataset = dataset.transformDatatypes()

    #### SETTING TARGET VAR ####
    dataset = dataset.setTargetAttribute(TARGET_VAR)

    dataset = StandardScaler().scale(dataset)
    # dataset = StandardNormalizer().normalize(dataset)

    #### SPLIT THE DATA ####
    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)

    #### DEFINE MODELS TO BENCHMARK ####
    models_zoo = {'LinearRegression': {'model': LinearRegression,
                                       'param': {}},
                  'RidgeRegression': {'model': RidgeRegression,
                                      'param': {}},
                  'DecisionTreeRegressor': {'model': DecisionTreeRegressor,
                                            'param': {'maxDepth': [d for d in range(1,10, 2)]} },
                  'RandomForestRegressor': {'model': RandomForestRegressor,
                                            'param': {
                                                'nEstimator': [nE for nE in range(5,65,10)],
                                                'criterion': ['squared_error', 'absolute_error', 'poisson'],
                                                'maxDepth': [d for d in range(1,6, 2)],
                                                'randomState': [2022]
                                            }},
                  }

    #### DEFINE METRICS TO CHECK ####
    metrics_zoo = {'metric_names': ['meanAbsoluteError', 'accuracy', 'balancedAccuracy', 'r2'],
                   'metric': [meanAbsoluteError, accuracy, balancedAccuracy, r2]}

    results = pd.DataFrame(columns=['model', 'parameters', *metrics_zoo['metric_names']])
    for model_name, model_func in models_zoo.items():
        for cur_param in grid_parameters(model_func['param']):
            try:
                model = model_func['model'](**cur_param)
                lr = model.fit(X_train, y_train)
                y_pred = lr.predict(X_test)

                cur_results = pd.DataFrame([[model_name,
                                            str(cur_param) if cur_param is not {} else 'None',
                                            *[metric_func(y_test, y_pred) for metric_func in metrics_zoo['metric']]
                                            ]],
                    columns=['model', 'parameters', *metrics_zoo['metric_names']]
                )
                results = pd.concat([results, cur_results])
            except Exception as error_type:
                print(*[[error_type]*len(metrics_zoo['metric'])])
                cur_results = pd.DataFrame([['ERROR'+model_name,
                                             str(cur_param) if cur_param is not {} else 'None',
                                             *[error_type for _ in metrics_zoo['metric']]
                                             ]],
                                           columns=['model', 'parameters', *metrics_zoo['metric_names']]
                                           )
                results = pd.concat([results, cur_results])

    results.to_csv(DATASET_NAME+'_benchmarking.csv', index=False)


if __name__ == "__main__":
    exampleWorkflow()
