# Workflows --------------------------------------------------------------------

def test():
    def __block_lambda_0(a, b=2):
        d = g()
        return d
    f(__block_lambda_0)
    def __block_lambda_1(a, *c):
        d = g()
        return d
    f(__block_lambda_1)
    def __block_lambda_2():
        pass
    f(__block_lambda_2)
