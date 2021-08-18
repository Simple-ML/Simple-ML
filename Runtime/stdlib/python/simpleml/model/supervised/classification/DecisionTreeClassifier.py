from sklearn.tree import DecisionTreeClassifier as DT


class DecisionTreeClassifier:
    def __init__(self, random_state=1):
        self.model = DT(random_state=random_state)

    def fit(self, xTrain, yTrain):
        self.model.fit(xTrain, yTrain)
        return self.model

    def predict(self, newInstance):
        return self.model.predict(newInstance)
