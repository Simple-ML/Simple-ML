#!/bin/sh

# Fetch submodules
git submodule update --init --recursive

# Build server
cd server/de.unibonn.simpleml.parent
./gradlew clean generateXtextLanguage build

# Build client
cd ../../client
npm i
npm run webpack
