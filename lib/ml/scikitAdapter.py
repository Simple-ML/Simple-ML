from sklearn import linear_model


def regression(x, y, config=None):
    if config is None:
        config = {}
    config.setdefault("alpha", 0.5)
    return linear_model.Ridge(alpha=config["alpha"]).fit(x, y)


def predict(model, x):
    return model.predict([x])
