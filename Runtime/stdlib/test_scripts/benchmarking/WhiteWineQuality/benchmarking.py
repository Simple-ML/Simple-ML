# Imports ----------------------------------------------------------------------
from itertools import product

import numpy as np
import pandas as pd
from code_generation import code_generation, dsl_code_generation
from simpleml.dataset import StandardScaler
from simpleml.dataset import loadDataset
# Workflow steps ---------------------------------------------------------------
from simpleml.metrics import meanAbsoluteError, accuracy, balancedAccuracy
from simpleml.metrics import r2
from simpleml.model.supervised.regression import LinearRegression, RidgeRegression
from simpleml.model.supervised.regression._tree import DecisionTreeRegressor, RandomForestRegressor


def grid_parameters(parameters):
    for params in product(*parameters.values()):
        yield dict(zip(parameters.keys(), params))


def exampleWorkflow():
    DATASET_NAME = "WhiteWineQuality"
    TARGET_VAR = "quality"

    # LOADINGDATASET
    dataset = loadDataset(DATASET_NAME)

    # PREPROCESSINGDATASET
    dataset = dataset.dropAllMissingValues()
    dataset = dataset.transformDatatypes()

    # SETTING TARGET VAR
    dataset = dataset.setTargetAttribute(TARGET_VAR)

    dataset = StandardScaler().scale(dataset)
    # dataset = StandardNormalizer().normalize(dataset)

    # SPLIT THE DATA
    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)

    # DEFINE MODELS TO BENCHMARK
    models_zoo = {'LinearRegression': {'model': LinearRegression,
                                       'param': {}},
                  'RidgeRegression': {'model': RidgeRegression,
                                      'param': {}},
                  'DecisionTreeRegressor': {'model': DecisionTreeRegressor,
                                            'param': {'maxDepth': [d for d in range(1, 10, 2)]}},
                  'RandomForestRegressor': {'model': RandomForestRegressor,
                                            'param': {
                                                'nEstimator': [nE for nE in range(5, 65, 10)],
                                                'criterion': ['squared_error', 'absolute_error', 'poisson'],
                                                'maxDepth': [d for d in range(1, 10, 2)],
                                                'randomState': [2022]
                                            }}
                  }

    # DEFINE METRICS TOCHECK
    metrics_zoo = {'metric_names': ['meanAbsoluteError', 'accuracy', 'balancedAccuracy', 'r2'],
                   'metric': [meanAbsoluteError, accuracy, balancedAccuracy, r2]}

    results = pd.DataFrame(columns=['model', 'parameters', *metrics_zoo['metric_names']])
    for model_name, model_func in models_zoo.items():
        for cur_param in grid_parameters(model_func['param']):
            try:
                model = model_func['model'](**cur_param)
                lr = model.fit(X_train, y_train)
                y_pred = lr.predict(X_test)

                source_code = code_generation(metric_names=metrics_zoo['metric_names'],
                                              model_name=model_name,
                                              dataset_name=DATASET_NAME,
                                              target_var=TARGET_VAR,
                                              parameters=cur_param,
                                              random_seed='2022'
                                              )
                dsl_code = dsl_code_generation(metric_names=metrics_zoo['metric_names'],
                                               model_name=model_name,
                                               dataset_name=DATASET_NAME,
                                               target_var=TARGET_VAR,
                                               parameters=cur_param,
                                               # random_seed='2022'
                                               )

                cur_results = pd.DataFrame([[model_name,
                                             str(cur_param) if cur_param is not {} else 'None',
                                             *[metric_func(y_test, y_pred) for metric_func in metrics_zoo['metric']],
                                             source_code,
                                             dsl_code
                                             ]],
                                           columns=['model', 'parameters', *metrics_zoo['metric_names'], 'source_code',
                                                    'dsl_code']
                                           )
                results = pd.concat([results, cur_results])
            except Exception as error_type:
                print(*[[error_type] * len(metrics_zoo['metric'])])
                cur_results = pd.DataFrame([['ERROR' + model_name,
                                             str(cur_param) if cur_param is not {} else 'None',
                                             *[error_type for _ in metrics_zoo['metric']]
                                             ]],
                                           columns=['model', 'parameters', *metrics_zoo['metric_names']]
                                           )
                results = pd.concat([results, cur_results])

    results.to_csv(DATASET_NAME + '_benchmarking.csv', index=False)


if __name__ == "__main__":
    np.random.seed(2022)
    exampleWorkflow()
