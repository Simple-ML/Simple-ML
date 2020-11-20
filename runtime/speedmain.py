from speedPrediction2 import predictSpeed

if __name__ == '__main__':
    x= predictSpeed()
    f=open("runtime/regression.model","w")
    f.write(x)
    f.close()
