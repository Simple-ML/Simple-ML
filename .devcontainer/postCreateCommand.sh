#!/bin/sh

# Fetch submodules
git submodule update --init --recursive

# Build server
cd server/de.unibonn.simpleml.parent
gradle clean generateXtextLanguage build

# Build client
cd ../../client
npm i
npm run webpack

# Install the Simple-ML Python library
cd ../server/de.unibonn.simpleml.parent/de.unibonn.simpleml/stdlib/python
pip3 --disable-pip-version-check --no-cache-dir install -r requirements.txt
pip3 install -e .
