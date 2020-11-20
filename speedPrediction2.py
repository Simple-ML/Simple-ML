import json
from time import sleep

from numpy import poly1d
from scipy import linspace, polyval, polyfit, sqrt, stats, randn, optimize
import statsmodels.api as sm
import matplotlib.pyplot as plt
import time
import numpy as np
from sklearn.linear_model import LinearRegression
import pandas as pd
#Sample data creation
def predictSpeed():
    #number of points
    n=int(5e6)
    t=np.linspace(-10,10,n)
    #parameters
    a=3.25
    b=-6.5
    x=polyval([a,b],t)
    #add some noise
    xn=x+3*randn(n)

    # xvar=np.random.choice(t,size=20)
    # yvar=polyval([a,b],xvar)+3*randn(20)
    # plt.scatter(xvar,yvar,c='green',edgecolors='k')
    # plt.grid(True)
    # plt.show()


    #Linear regressison -polyfit - polyfit can be used other orders polynomials
    t1=time.time()
    (ar,br)=polyfit(t,xn,1)
    f=poly1d(polyfit(t,xn,1))
    xr=polyval([ar,br],t)
    #compute the mean square error
    err=sqrt(sum((xr-xn)**2)/n)
    time.sleep(120)
    t2=time.time()
    t_polyfit = float(t2-t1)

    print('Linear regression using polyfit')
    print('parameters: a=%.2f b=%.2f, ms error= %.3f' % (ar,br,err))
    print("Time taken: {} seconds".format(t_polyfit))

    return json.dumps([{'polyfit':{'a':ar,'b':br}}])
