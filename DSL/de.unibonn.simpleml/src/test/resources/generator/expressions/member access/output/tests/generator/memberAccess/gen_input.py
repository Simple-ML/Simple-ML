# Workflows --------------------------------------------------------------------

def test():
    f(g())
    f(h()[0])
    f(h()[1])
    f(C().a)
    f(C().c)
    f(safe_access(factory(), 'a'))
    f(safe_access(factory(), 'c'))
