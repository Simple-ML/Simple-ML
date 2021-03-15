from setuptools import setup

setup(
    name='Simple-ML',
    version='0.0.1',
    description='Adapters for various ML libraries',
    packages=['simpleml'], install_requires=['pandas', 'scikit-learn', 'torch', 'pyspark', 'requests','json','shapely']
)
