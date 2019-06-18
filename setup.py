from setuptools import setup

# TODO rename project (lib is too generic)
setup(
    name='lib',
    version='0.0.1',
    description='Adapters for various ML libraries',
    packages=['lib'], install_requires=['pandas', 'scikit-learn']
)
