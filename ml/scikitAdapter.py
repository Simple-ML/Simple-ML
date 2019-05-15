from sklearn import linear_model


def regression(x, y):
    return linear_model.Ridge(alpha=.5).fit(x, y)


def predict(model, x):
    return model.predict([x])
