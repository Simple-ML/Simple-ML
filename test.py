import pandas as pd
# Read data from file 'filename.csv'
# (in the same directory that your python process is based)
# Control delimiters, rows, column names with read_csv (see later)
data = pd.read_csv("dataset.tsv")
# Preview the first 5 lines of the loaded data
print(data.head())
