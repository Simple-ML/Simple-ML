import sklearn.linear_model
import sklearn.tree


# Linear Models


def LinearRegression():
    return sklearn.linear_model.LinearRegression()


def LassoRegression(config):
    return sklearn.linear_model.Lasso(
        alpha=config["regularizationStrength"]
    )


def RidgeRegression(config):
    return sklearn.linear_model.Ridge(
        alpha=config["regularizationStrength"]
    )


def ElasticNetRegression(config):
    return sklearn.linear_model.ElasticNet(
        alpha=config["regularizationStrength"],
        l1_ratio=config["lassoRatio"]
    )


def DecisionTreeRegression(config=None):
    return sklearn.tree.DecisionTreeRegressor()

# def decision_tree_regressor(config=None):
#     if config is None:
#         config = {}
#     config.setdefault("max_leaf_nodes", 100)
#     return sk.tree.DecisionTreeRegressor(
#         max_leaf_nodes=config["max_leaf_nodes"]
#     )
#
#
# def random_forest_regressor():
#     return sk.ensemble.RandomForestRegressor()
