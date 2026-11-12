#!/usr/bin/env bash
set -euo pipefail

# Run from repo root regardless of where the script was invoked
cd "$(dirname "$0")"

if [[ -z "${ARTIFACTORY_HOST:-}" ]]; then
  # No Artifactory configured — use public dist settings (no credentials)
  DOCKER_BUILDKIT=1 docker build \
    --secret id=mvn_settings,src=mvn-settings.xml.dist \
    -f Dockerfile.test \
    -t conjur-mule-test \
    .
else
  # Resolve credentials into a temp file for the BuildKit secret.
  # Credentials are used only during docker build and are never exposed as container env vars.
  tmp_settings=$(mktemp -u)
  install -m 600 /dev/null "${tmp_settings}"
  trap 'rm -f "${tmp_settings}"' EXIT
  python3 -c "
import os, sys
t = open('mvn-settings.xml').read()
t = t.replace('\${env.ARTIFACTORY_HOST}',     os.environ['ARTIFACTORY_HOST'])
t = t.replace('\${env.ARTIFACTORY_USERNAME}', os.environ['ARTIFACTORY_USERNAME'])
t = t.replace('\${env.ARTIFACTORY_PASSWORD}', os.environ['ARTIFACTORY_PASSWORD'])
sys.stdout.write(t)
" > "${tmp_settings}"

  DOCKER_BUILDKIT=1 docker build \
    --secret id=mvn_settings,src="${tmp_settings}" \
    -f Dockerfile.test \
    -t conjur-mule-test \
    .
fi

# Stage 2: run tests — no credentials in scope; deps are pre-cached in the image
docker run --rm \
  -v "$(pwd)":/repo \
  -w /repo \
  conjur-mule-test \
  --batch-mode -f pom.xml jacoco:prepare-agent test jacoco:report
