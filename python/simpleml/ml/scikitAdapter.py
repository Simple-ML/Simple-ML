from python.simpleml.l3s.api import Query


def fit(model, x: Query, y: Query):
    return model.fit(x.getData(), y.getData())


def predict(model, x):
    return model.predict(x)
