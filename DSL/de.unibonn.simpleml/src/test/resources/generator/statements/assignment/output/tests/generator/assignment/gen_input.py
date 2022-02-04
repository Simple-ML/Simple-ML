# Steps ------------------------------------------------------------------------

def testStep():
    g()
    a, _, c = g()
    x, _, _ = g()
    f(a)
    f(x)
    return c

# Workflows --------------------------------------------------------------------

def testFlow():
    g()
    a, _, _ = g()
    x, _, _ = g()
    f(a)
    f(x)
    def __block_lambda_0():
        g()
        a, _, c = g()
        x, _, _ = g()
        f(a)
        f(x)
        return c
    f(__block_lambda_0)
