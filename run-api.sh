#!/bin/bash

cd "$(dirname "$0")/application-template"

# Build the application
./gradlew clean bootRun
