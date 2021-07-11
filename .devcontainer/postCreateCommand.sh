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

# Install pre-built preview of Simple-ML extension (remove once it's on the marketplace)
cd ..
code --install-extension "server/de.unibonn.simpleml.parent/de.unibonn.simpleml/stdlib/simple-ml-0.0.7.vsix"
