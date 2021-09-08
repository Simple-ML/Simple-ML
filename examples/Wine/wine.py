

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import plotly.express as px
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import AdaBoostClassifier
from sklearn.ensemble import GradientBoostingClassifier

# load data
df = pd.read_csv("winequality-red.csv", sep=';')

# explore data 1: histogram
fig = px.histogram(df, x='quality')
fig.show()

# explore data 2: correlation matrix
corr = df.corr()
plt.subplots(figsize=(15, 10))
sns.heatmap(corr, xticklabels=corr.columns, yticklabels=corr.columns, annot=True, cmap=sns.diverging_palette(220, 20, as_cmap=True))
plt.show()

# convert to a binary classification problem
df['goodquality'] = [1 if x >= 7 else 0 for x in df['quality']]

# explore data 3: proportion of good and bad quality wine
print(df['goodquality'].value_counts())

# split dataset into features and target
X = df.drop(['quality', 'goodquality'], axis=1)
y = df[['goodquality']]

# Normalize feature variables
X_normalized = StandardScaler().fit_transform(X)

# Splitting the data into test and training sets
X_train, X_test, y_train, y_test = train_test_split(X_normalized, y, test_size=.25, random_state=1)


# Models
models = [
    DecisionTreeClassifier(random_state=1),
    RandomForestClassifier(random_state=1),
    AdaBoostClassifier(random_state=1),
    GradientBoostingClassifier(random_state=1)
]

# Select model
model = models[0]

# Train models and print results
model.fit(X_train, y_train)
y_pred = model.predict(X_test)
print(classification_report(y_test, y_pred))

# Model understanding: Feature importance
feat_importance = pd.Series(model.feature_importances_, index=X.columns)
feat_importance.nlargest(25).plot(kind='barh', figsize=(10, 10))
plt.show()

# Data understanding: Investigate some important features for the two different targets
df_good = df[df['goodquality'] == 1]
df_important_good = df_good.filter(items=['alcohol', 'sulphates', 'volatile acidity'])
print(df_important_good.describe())
df_bad = df[df['goodquality'] == 0]
df_important_bad = df_bad.filter(items=['alcohol', 'sulphates', 'volatile acidity'])
print(df_important_bad.describe())
