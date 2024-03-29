#!/bin/bash
# Script for cleaning and building CityLoops-API project modules.
# Run this in the "scripts" folder of cityloops-api

cd ../api-spec/
echo "Cleaning api-spec folder"
rm -fR src build
echo "Starting clean build for api-spec"
../gradlew clean build
cd ..

cd api-client/
echo "Cleaning api-client folder"
rm -fR src build
echo "Starting clean build for api-client"
../gradlew clean build

echo "Build done!"
