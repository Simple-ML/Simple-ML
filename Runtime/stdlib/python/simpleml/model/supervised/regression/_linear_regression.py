from sklearn.linear_model import LinearRegression as SkLinearRegression


class LinearRegression:
    def __init__(self) -> None:
        self.__model = SkLinearRegression(
            fit_intercept=True,
            normalize=False,
            copy_X=True,
            n_jobs=None
        )
