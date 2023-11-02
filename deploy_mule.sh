#!/bin/bash

set -euo pipefail
set -x

mkdir -p maven_cache

# Find the plugin jar
# Array variable to force glob expansion during assignment
# echo "${target/*$(<../VERSION)-mule-plugin.jar}"
jar_paths=(target/conjur-mule-connector-*.jar)

echo $jar_paths

# Get first item from the array
jar_path="${jar_paths[0]}"

# Extract jar filename from path
jar="$(basename "${jar_path}")"

# Bring the spring boot conjur jar into this directory so
# it is accessible to docker.
cp "${jar_path}" "${jar}"

docker run \
    --volume "${PWD}:${PWD}" \
    --volume "${PWD}/maven_cache":/root/.m2 \
    --workdir "${PWD}" \
    tools \
        mvn --batch-mode install:install-file \
        -Dfile="${jar}" \
        -DgroupId=com.cyberark.conjur \
        -DartifactId=conjur-mule-connector \
        -Dversion="0.0.1-${BUILD_NUMBER}" \
        -Dpackaging=jar \
        -DgeneratePom=true

# Update the sample app pom to use the version of conjur mule connector plugin thats under test
docker run \
    --volume "${PWD}:${PWD}" \
    --volume "${PWD}/maven_cache":/root/.m2 \
    --workdir "${PWD}" \
    tools \
        mvn --batch-mode -f pom.xml versions:use-dep-version -Dincludes=com.cyberark:conjur-mule-connector -DdepVersion="0.0.1-${BUILD_NUMBER}"

docker run \
    --volume "${PWD}:${PWD}" \
    --volume "${PWD}/maven_cache":/root/.m2 \
    --workdir "${PWD}" \
    tools \
        mvn --batch-mode -f pom.xml dependency:tree

# Use Tools image to package code
docker run \
    --volume "${PWD}:${PWD}" \
    --volume "${PWD}/maven_cache":/root/.m2 \
    --workdir "${PWD}" \
    tools \
        mvn --batch-mode -f pom.xml clean compile package


docker build --no-cache --build-arg maven_version=3.8.4 --build-arg java_version=8 -t conjur-mule-image .
echo "list the docker images"
docker images | grep conjur-mule-image
    

# #deploy into Aynpoint exchange studio
# echo "deploy into Aynpoint exchange studio"
# docker run \
#     --volume "${PWD}:${PWD}" \
#     --volume "${PWD}/maven_cache":/root/.m2 \
#     --workdir "${PWD}" \
#     tools \
#         mvn deploy -P anypoint-exchange-v3 -Dexchange-mule-maven-plugin=0.0.13 -Danypoint.username="fjaleelaRah75"  -Danypoint.password="Rahman1975#"