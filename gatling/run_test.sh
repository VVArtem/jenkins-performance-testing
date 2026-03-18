#!/bin/bash

GATLING_DIR=$(dirname "$(readlink -f "$0")")
cd "$GATLING_DIR"

echo "Checking Maven version..."
mvn -v

if [ $? -ne 0 ]; then
    echo "ERROR: Maven not found! Install it using 'sudo apt install maven'"
    exit 1
fi

echo "Running Gatling simulation: Simulation1..."

mvn gatling:test \
  -Dmaven.compiler.release=17 \
  -Dgatling.simulationClass=simulation.Simulation1 \
  -Dusers=5 \
  -Dduration=600 \
  -DloadType=closed

if [ $? -eq 0 ]; then
    echo "Gatling test finished successfully."
else
    echo "Gatling test failed!"
    exit 1
fi