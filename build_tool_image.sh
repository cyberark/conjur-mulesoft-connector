#!/bin/bash

set -euo pipefail

# Build tools image
#FROM eclipse-temurin:${java_version}-jdk-alpine-3.23
docker build --platform=linux/amd64 \
    --build-arg maven_version=3.9.11-r0 \
    --build-arg java_version=17 \
    -t tools \
    -f tools.Dockerfile \
    .

echo "lits docker images"

docker images | grep tools
