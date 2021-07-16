#!/bin/sh

# Fetch submodules
git submodule update --init --recursive

# Build
gradle vsCodeExtension

# Install the Simple-ML Python library
cd de.unibonn.simpleml/stdlib/python
pip3 --disable-pip-version-check --no-cache-dir install -r requirements.txt
pip3 install -e .
