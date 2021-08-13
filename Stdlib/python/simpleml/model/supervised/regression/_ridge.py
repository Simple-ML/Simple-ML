from sklearn.linear_model import Ridge as SkRidge


class Ridge:
    def __init__(self, regularizationStrength: float) -> None:
        self.__regularizationStrength = regularizationStrength
        self.__model = SkRidge(
            alpha=regularizationStrength,
            fit_intercept=True,
            normalize=False,
            copy_X=True,
            max_iter=None,
            tol=1e-3,
            solver="auto",
            random_state=None
        )
