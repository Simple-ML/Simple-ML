class Fetcher:
    """
    Defines the interface for fetching the raw data of a dataset .
    This class is abstract, the purpose is to subclass it,
    defining specific constructors in each of the subclasses.
    The subclass constructors take the specific arguments
    necessary for getting data from a particular location.
    """

    def fetch(self) -> str:
        """
        :return:
            The file name.
        """

        pass


class LocalFetcher(Fetcher):
    """
    A local fetcher takes a file path as
    """

    def __init__(self, filename: str):
        self._filename = filename

    def fetch(self) -> str:
        return self._filename


class KaggleFetcher(Fetcher):
    pass


class NetworkFetcher(Fetcher):
    pass
