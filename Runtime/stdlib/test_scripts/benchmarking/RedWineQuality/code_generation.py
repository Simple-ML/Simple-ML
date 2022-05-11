# Imports ----------------------------------------------------------------------
from itertools import product


# Code generation workflow -----------------------------------------------------
def grid_parameters(parameters):
    for params in product(*parameters.values()):
        yield dict(zip(parameters.keys(), params))


def code_generation(metric_names,
                    model_name: str,
                    dataset_name: str,
                    target_var: str,
                    parameters,
                    random_seed: str) -> str:
    import_path = 'classification' if 'Classifier' in model_name else 'regression'
    import_path += '._tree' if "Tree" in model_name else ''
    metric_import_string = ','.join(metric_names)
    metric_print_strings = '\n'.join(["    print('{0}:', {0}(y_test, y_pred))".format(metric_name)
                                      for metric_name in metric_names])

    code = "# Imports ----------------------------------------------------------------------\n" \
           "from simpleml.dataset import loadDataset\n" \
           "from simpleml.metrics import {0}\n" \
           "from simpleml.dataset import StandardScaler\n" \
           "from simpleml.model.supervised.{1} import {2}\n" \
           "import numpy as np\n" \
           "\n" \
           "\n" \
           "# Workflow steps ---------------------------------------------------------------\n" \
           "def exampleWorkflow():\n" \
           "    dataset = loadDataset('{3}')\n" \
           "    dataset = dataset.dropAllMissingValues()\n" \
           "    dataset = dataset.transformDatatypes()\n" \
           "    dataset = dataset.setTargetAttribute('{4}')\n" \
           "    dataset = StandardScaler().scale(dataset)\n" \
           "    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)\n" \
           "    model = {2}(**{5})\n" \
           "    lr = model.fit(X_train, y_train)\n" \
           "    y_pred = lr.predict(X_test)\n" \
           "{6}\n" \
           "\n" \
           "if __name__ == '__main__':\n" \
           "    np.random.seed({7})\n" \
           "    exampleWorkflow()\n".format(metric_import_string,
                                            import_path,
                                            model_name,
                                            dataset_name,
                                            target_var,
                                            parameters,
                                            metric_print_strings,
                                            random_seed)
    return code


if __name__ == "__main__":
    # Simple case ------------------------------------------------------------------
    code = code_generation(metric_names=['meanAbsoluteError'],
                           model_name='LinearRegression',
                           dataset_name='WhiteWineQualityBinary',
                           target_var='quality',
                           parameters='{}',
                           random_seed='2022'
                           )
    # save generated code to the file
    with open('generated_code_simple.py', 'w') as f:
        f.write(code)
    # Execute generated code
    try:
        pass  # exec(code) # uncomment to test
    except Exception as error_type:
        print('The created code generate an error: ', error_type)

    # Benchmark-style case ---------------------------------------------------------
    models_zoo = {
        'SupportVectorMachineClassifier': {'model': None,  # SupportVectorMachineClassifier
                                           'param': {
                                               'penalty': ['l2'],
                                               'loss': ['squared_hinge'],
                                               'dual': [True],
                                               'tol': [1e-4],
                                               'c': [1.0],
                                               'multiClass': ['ovr']  # or 'crammer_singer'
                                           }},
    }
    metrics_zoo = {'metric_names': ['meanAbsoluteError', 'accuracy', 'balancedAccuracy', 'r2'],
                   'metric': [None, None, None, None]}

    for model_name, model_func in models_zoo.items():
        for cur_param in grid_parameters(model_func['param']):
            code = code_generation(metric_names=metrics_zoo['metric_names'],
                                   model_name=model_name,
                                   dataset_name='WhiteWineQualityBinary',
                                   target_var='quality',
                                   parameters=cur_param,
                                   random_seed='2022'
                                   )
    # save generated code to the file
    with open('generated_code_benchmark_style.py', 'w') as f:
        f.write(code)
    # Execute generated code
    try:
        pass  # exec(code) # uncomment to test
    except Exception as error_type:
        print('The created code generate an error: ', error_type)
