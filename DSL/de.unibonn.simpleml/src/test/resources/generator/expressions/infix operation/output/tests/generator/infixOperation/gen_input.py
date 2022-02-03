# Imports ----------------------------------------------------------------------

from simpleml.codegen import eager_or, eager_and, eager_elvis

# Workflows --------------------------------------------------------------------

def test():
    f(eager_or(g(), g()))
    f(eager_and(g(), g()))
    f((h()) == (h()))
    f((h()) != (h()))
    f((h()) is (h()))
    f((h()) is not (h()))
    f((h()) < (h()))
    f((h()) <= (h()))
    f((h()) >= (h()))
    f((h()) > (h()))
    f((h()) + (h()))
    f((h()) - (h()))
    f((h()) * (h()))
    f((h()) / (h()))
    f(eager_elvis(i(), i()))
