from setuptools import find_packages, setup

if __name__ == "__main__":
    setup(
        name="Simple-ML",
        version="0.0.1",
        description="Adapters for various ML libraries",
        packages=find_packages(include="simpleml.*"),
        include_package_data=True,
        install_requires=[
            "geopandas===0.10.2",
            "numpy==1.21.0",
            "pandas==1.3.5",
            "pyproj==3.1.0",
            "rdflib==6.0.2",
            "scikit-learn==0.24.2",
            "shapely==1.7.1",
            "sparqlwrapper==1.8.5",
            "geopandas==0.10.2",
            "jsonpickle==2.0.0",
            "websockets==9.1",
            "requests==2.27.1",
            "category_encoders==2.3.0",
            "networkx==2.6.3",
            "libpysal==4.6.0",
            "node2vec==0.4.4",
        ],
    )
