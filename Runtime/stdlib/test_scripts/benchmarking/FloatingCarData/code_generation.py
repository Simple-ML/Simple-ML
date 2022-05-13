# Imports ----------------------------------------------------------------------
from itertools import product


# Code generation workflow -----------------------------------------------------
def grid_parameters(parameters):
    for params in product(*parameters.values()):
        yield dict(zip(parameters.keys(), params))


def code_generation(
    metric_names,
    model_name: str,
    dataset_name: str,
    target_var: str,
    parameters: dict,
    random_seed: str,
) -> str:
    import_path = "classification" if "Classifier" in model_name else "regression"
    import_path += "._tree" if "Tree" in model_name else ""
    metric_import_string = ",".join(metric_names)
    metric_print_strings = "\n".join(
        [
            "    print('{0}:', {0}(y_test, y_pred))".format(metric_name)
            for metric_name in metric_names
        ]
    )

    code = (
        "# Imports ----------------------------------------------------------------------\n"
        "from simpleml.dataset import loadDataset\n"
        "from simpleml.metrics import {0}\n"
        "from simpleml.dataset import StandardScaler\n"
        "from simpleml.model.supervised.{1} import {2}\n"
        "import numpy as np\n"
        "\n"
        "\n"
        "# Workflow steps ---------------------------------------------------------------\n"
        "def exampleWorkflow():\n"
        "    dataset = loadDataset('{3}')\n"
        "    dataset = dataset.dropAllMissingValues()\n"
        "    dataset = dataset.transformDatatypes()\n"
        "    dataset = dataset.setTargetAttribute('{4}')\n"
        "    dataset = StandardScaler().scale(dataset)\n"
        "    X_train, X_test, y_train, y_test = dataset.splitIntoTrainAndTestAndLabels(0.8)\n"
        "    model = {2}(**{5})\n"
        "    lr = model.fit(X_train, y_train)\n"
        "    y_pred = lr.predict(X_test)\n"
        "{6}\n"
        "\n"
        "if __name__ == '__main__':\n"
        "    np.random.seed({7})\n"
        "    exampleWorkflow()\n".format(
            metric_import_string,
            import_path,
            model_name,
            dataset_name,
            target_var,
            parameters,
            metric_print_strings,
            random_seed,
        )
    )
    return code


def dsl_code_generation(
    metric_names,
    model_name: str,
    dataset_name: str,
    target_var: str,
    parameters: dict,
):
    def param_to_str(p):
        if type(p) == str:
            return '"' + p + '"'
        elif type(p) == bool:
            return str(p).lower()
        return str(p)

    import_path = "classification" if "Classifier" in model_name else "regression"
    # import_path += '._tree' if "Tree" in model_name else ''
    # import_path +='.'+model_name

    metric_import_string = "".join(
        ["import simpleml.metrics." + metr_name + "\n" for metr_name in metric_names]
    )
    model_parameter_strings = []
    for param, param_val in parameters.items():
        model_parameter_strings.append(param + "=" + param_to_str(param_val))
    model_parameter_string = ", ".join(model_parameter_strings)

    # if "Sup"in model_name:
    #     print(parameters)
    #     print(model_parameter_string)
    #     a=0/0
    dsl_code = (
        "package example\n"
        "\n"
        "{0}\n"
        "import simpleml.dataset.loadDataset\n"
        "import simpleml.dataset.StandardScaler\n"
        "import simpleml.model.supervised.{1}.{5}\n"
        "\n"
        "workflow exampleWorkflow {{\n"
        '    val dataset1 = loadDataset("{2}");\n'
        "    val dataset2 = dataset1.dropAllMissingValues();\n"
        "    val dataset3 = dataset2.transformDatatypes();\n"
        '    val dataset4 = dataset3.setTargetAttribute("{3}");\n'
        "    val dataset5 = StandardScaler().scale(dataset4);\n"
        "    val X_train, val X_test, val y_train, val y_test = dataset5.splitIntoTrainAndTestAndLabels(0.8);\n"
        "    \n"
        "    val model = {5}({4});\n"
        "    val lr = model.fit(X_train, y_train);\n"
        "    val y_pred = lr.predict(X_test);\n"
        "    \n"
        "}}".format(
            metric_import_string,
            import_path,
            dataset_name,
            target_var,
            model_parameter_string,
            model_name,
        )
    )
    return dsl_code


if __name__ == "__main__":
    # Simple case ------------------------------------------------------------------
    code = code_generation(
        metric_names=["meanAbsoluteError"],
        model_name="LinearRegression",
        dataset_name="WhiteWineQualityBinary",
        target_var="quality",
        parameters={},
        random_seed="2022",
    )
    dsl_code = dsl_code_generation(
        metric_names=["meanAbsoluteError"],
        model_name="LinearRegression",
        dataset_name="WhiteWineQualityBinary",
        target_var="quality",
        parameters={},
    )
    # save generated code to the file
    with open("generated_code_simple.py", "w") as f:
        f.write(code)
    with open("dsl_generated_code_simple", "w") as f:
        f.write(dsl_code)
    # Execute generated code
    try:
        pass  # exec(code) # uncomment to test
    except Exception as error_type:
        print("The created python code generate an error: ", error_type)

    # Benchmark-style case ---------------------------------------------------------
    models_zoo = {
        "SupportVectorMachineClassifier": {
            "model": None,  # SupportVectorMachineClassifier
            "param": {
                "penalty": ["l2"],
                "loss": ["squared_hinge"],
                "dual": [True],
                "tol": [1e-4],
                "c": [1.0],
                "multiClass": ["ovr"],  # or 'crammer_singer'
            },
        },
    }
    metrics_zoo = {
        "metric_names": ["meanAbsoluteError", "accuracy", "balancedAccuracy", "r2"],
        "metric": [None, None, None, None],
    }

    for model_name, model_func in models_zoo.items():
        for cur_param in grid_parameters(model_func["param"]):
            code = code_generation(
                metric_names=metrics_zoo["metric_names"],
                model_name=model_name,
                dataset_name="WhiteWineQualityBinary",
                target_var="quality",
                parameters=cur_param,
                random_seed="2022",
            )
            dsl_code = dsl_code_generation(
                metric_names=metrics_zoo["metric_names"],
                model_name=model_name,
                dataset_name="WhiteWineQualityBinary",
                target_var="quality",
                parameters=cur_param,
                # random_seed='2022'
            )
    # save generated code to the file
    with open("generated_code_benchmark_style.py", "w") as f:
        f.write(code)
    with open("dsl_generated_code_benchmark_style", "w") as f:
        f.write(dsl_code)

    # Execute generated code
    try:
        pass  # exec(code) # uncomment to test
    except Exception as error_type:
        print("The created python code generate an error: ", error_type)
