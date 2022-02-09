# Imports ----------------------------------------------------------------------

import simpleml.codegen.safe_access

# Workflows --------------------------------------------------------------------

def test():
    f(g())
    f(h()[0])
    f(h()[1])
    f(C().a)
    f(C().c)
    f(simpleml.codegen.safe_access(factory(), 'a'))
    f(simpleml.codegen.safe_access(factory(), 'c'))
