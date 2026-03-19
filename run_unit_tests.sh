#!/bin/bash
set -eo pipefail

docker build -t conjur-mule-test -f Dockerfile.test .

docker run --rm \
  -v "$(pwd)":/repo \
  -v "$(pwd)/maven_cache":/root/.m2 \
  -w /repo \
  conjur-mule-test \
  --batch-mode -f pom.xml jacoco:prepare-agent test jacoco:report