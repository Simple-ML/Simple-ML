from sklearn import linear_model
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor


def decision_tree_regressor(x, y, config=None):
    if config is None:
        config = {}
    config.setdefault("max_leaf_nodes", 100)
    return DecisionTreeRegressor(max_leaf_nodes=config["max_leaf_nodes"]).fit(x, y)


def random_forest_regressor(x, y):
    return RandomForestRegressor().fit(x, y)


def regression(x, y, config=None):
    if config is None:
        config = {}
    config.setdefault("alpha", 0.5)
    return linear_model.Ridge(alpha=config["alpha"]).fit(x, y)


def predict(model, x):
    return model.predict([x])
