# Steps ------------------------------------------------------------------------

def test(param1, param_2, param_3=0, *param_4):
    f(lambda param1, param_2, param_3=0, *param_4: 1)
    def __block_lambda_0(param1, param_2, param_3=0, *param_4):
        pass
    f(__block_lambda_0)
