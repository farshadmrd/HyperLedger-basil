#!/bin/bash

# Ensure we're in the right directory
cd "$(dirname "$0")/application-template"

# Build the application if needed
if [ ! -f "./build/libs/asset-transfer-basic.jar" ] || [ "$1" == "rebuild" ]; then
  echo "Building the application..."
  ./gradlew build
  shift # Remove 'rebuild' from args if present
fi

# Run the application with all arguments passed to this script
java -jar ./build/libs/asset-transfer-basic.jar "$@"
