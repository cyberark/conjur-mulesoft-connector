#!/usr/bin/env bash
set -euo pipefail

# Resolve repository root and integration-test root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"

SUBMODULE_BASE_DIR="$TEST_ROOT/secrets-manager-integration-environment-bootstrap"
source "$SUBMODULE_BASE_DIR/scripts/helpers.sh"

validate_deployment_type "$@"
set_compose_file

CONFIG_FILE="$SUBMODULE_BASE_DIR/configs/${INTEGRATION_TYPE}.env"

if [[ -f "$CONFIG_FILE" ]]; then
    set -a  # Export all subsequent variable assignments
    source "$CONFIG_FILE"
    set +a  # Stop exporting
		echo "[INFO] Using profile: $PROFILE"
else
    echo "[ERROR] Config file not found: $CONFIG_FILE"
    exit 1
fi

"$SUBMODULE_BASE_DIR/scripts/setup_integration_env.sh" "$DEPLOYMENT_TYPE" "$INTEGRATION_TYPE"

VERSION_FILE="$PROJECT_ROOT/VERSION"

CONNECTOR_VERSION="$(tr -d '\r\n' < "$VERSION_FILE")"
echo "Using connector version: $CONNECTOR_VERSION"

API_KEY="$(tr -d '\r\n' < "$SUBMODULE_BASE_DIR/configs/host_api_key.txt")"
SECRET_PATH="$(tr -d '\r\n' < "$SUBMODULE_BASE_DIR/configs/secret_path.txt")"

if [[ "$DEPLOYMENT_TYPE" == "saas" || "$DEPLOYMENT_TYPE" == "edge" ]]; then
    HOST_PATH="host/data/mulesoft/mulesofthost"
else
    HOST_PATH="host/mulesoft/mulesofthost"
fi

APP_DIR="$TEST_ROOT/munit-app"
DOCKER_NETWORK="${DOCKER_NETWORK:-docker-compose_conjur}"

echo "Running MUnit tests with connector version $CONNECTOR_VERSION..."

docker build -t conjur-munit -f "$APP_DIR/Dockerfile.munit" "$PROJECT_ROOT"

docker run --rm -t \
  --network "$DOCKER_NETWORK" \
  -v "$PROJECT_ROOT":/repo \
  -v "$HOME/.m2":/root/.m2 \
  -w /repo/src/test/integration-tests/munit-app \
  conjur-munit \
  mvn --batch-mode -f pom.xml clean test \
  -Dconnector.version="$CONNECTOR_VERSION" \
  -Dconjur.appliance.url="$CONJUR_URL" \
  -Dconjur.api.key="$API_KEY" \
  -Dconjur.secret.path="$SECRET_PATH" \
  -Dconjur.host.path="$HOST_PATH" \
  -Dconjur.ssl.certificate.path=/repo/conjur.pem

echo "MUnit Reports:"
echo "$APP_DIR/target/surefire-reports"