from setuptools import setup, find_packages

if __name__ == "__main__":
    setup(
        name='Simple-ML',
        version='0.0.1',
        description='Adapters for various ML libraries',
        packages=find_packages(include="simpleml.*"),
        include_package_data=True,
        install_requires=[
            "geopandas===0.8.1",
            "numpy==1.21.0",
            "pandas==1.3.0",
            "pyproj==3.1.0",
            "rdflib==6.0.0",
            "scikit-learn==0.24.2",
            "shapely==1.7.1",
            "sparqlwrapper==1.8.5",
        ]
    ) 