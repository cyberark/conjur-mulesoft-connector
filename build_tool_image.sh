#!/bin/bash

set -euo pipefail

# Build tools image
#FROM Alpine 3.19
docker build \
    --build-arg maven_version=3.9.11 \
    --build-arg java_version=17 \
    -t tools \
    -f tools.Dockerfile \
    .

echo "lits docker images"

docker images | grep tools
