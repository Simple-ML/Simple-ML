from sklearn import linear_model
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor


def decision_tree_regressor(config=None):
    if config is None:
        config = {}
    config.setdefault("max_leaf_nodes", 100)
    return DecisionTreeRegressor(max_leaf_nodes=config["max_leaf_nodes"])


def random_forest_regressor():
    return RandomForestRegressor()


def regression(config=None):
    if config is None:
        config = {}
    config.setdefault("alpha", 0.5)
    return linear_model.Ridge(alpha=config["alpha"])


def fit(model, x, y):
    return model.fit(x, y)


def predict(model, x):
    return model.predict([x])
