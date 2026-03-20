# Conjur Mulesoft Connector

The CyberArk Conjur MuleSoft Connector provides a secure and efficient way to retrieve secrets from Conjur using API key authentication. 

## Certification level
![](https://img.shields.io/badge/Certification%20Level-Certified-28A745?link=https://github.com/cyberark/community/blob/master/Conjur/conventions/certification-levels.md)

This repo is a **Certified** level project. It's a community contributed project that **has been reviewed and tested by CyberArk and is trusted to use with Conjur Open Source, Conjur Enterprise, and Conjur Cloud**. For more detailed information on our certification levels, see [our community guidelines](https://github.com/cyberark/community/blob/master/Conjur/conventions/certification-levels.md#certified).

## Flow Diagram
<img width="544" alt="image" src="https://media.github.cyberng.com/user/1246/files/70c41905-98aa-46de-a5c7-512b71433b28">

## Plugin Requirements & Configuration

Please refer to official [documentation](https://docs.cyberark.com/conjur-enterprise/latest/en/Content/HomeTilesLPs/LP-Tile4.htm)

## Running Unit Tests

### Prerequisites
- Docker installed

### Run Tests

Run the unit tests with JaCoCo code coverage report:

```bash
./run_unit_tests.sh
```

This script will:
1. Build a Docker image with Maven and Java
2. Run Maven tests with JaCoCo code coverage
3. Generate test reports in `target/surefire-reports/`
4. Generate coverage reports in `target/site/jacoco/`

## Running Integration Tests Locally (MUnit)

These integration tests are real end-to-end checks:
- A Mule app exposes an HTTP listener flow.
- The flow calls the Conjur MuleSoft connector.
- The test setup spins up Conjur for the selected deployment type.
- Based on the spun-up deployment configuration, the required Conjur MuleSoft connector settings are applied for the test run.
- MUnit executes the flow and validates returned secrets.

### Why rebuild before running integration tests

After making connector code changes, rebuild and install the connector locally so the MUnit app resolves your latest local artifact.

```bash
mvn clean install -DskipTests
```

This installs the connector into your local Maven cache under:

`$HOME/.m2/repository/com/cyberark/conjur-mule-connector`

### Run integration tests

#### OSS

```bash
./src/test/integration-tests/munit-app/run_munit_integration_tests.sh oss mulesoft-api
```

#### Enterprise

```bash
./src/test/integration-tests/munit-app/run_munit_integration_tests.sh enterprise mulesoft-api
```

#### SaaS

```bash
./src/test/integration-tests/munit-app/run_munit_integration_tests.sh saas mulesoft-api
```

#### Edge

```bash
./src/test/integration-tests/munit-app/run_munit_integration_tests.sh edge mulesoft-api
```

### Additional preconditions for SaaS and Edge

Before running SaaS tests, export tenant-specific variables:

```bash
export INFRAPOOL_CONJUR_APPLIANCE_URL="<your-conjur-cloud-url>"
export INFRAPOOL_CONJUR_AUTHN_LOGIN="<your-tenant-login>"
export IDENTITY_PASSWORD_CLOUD="<your-tenant-password>"
```

Before running Edge tests, export the Edge token:

```bash
export INFRAPOOL_CONJUR_AUTHN_TOKEN="<your-edge-token>"
export PROFILE="edge"
```

### Why these integration tests currently run locally (not in CI pipeline)

The current MUnit integration test setup resolves Mule EE-related runtime dependencies during test execution. In our current pipeline environment, we do not have the required MuleSoft Enterprise account/repository access needed to pull all required artifacts. Because of that, these integration tests are currently executed locally.

### Troubleshooting: `401 Unauthorized` from MuleSoft Enterprise repository

If integration tests fail with a `401 Unauthorized` while resolving MuleSoft Enterprise dependencies, a practical local workaround is to warm your local Maven cache using Anypoint Studio.

In some environments, opening and running a simple Mule project in Anypoint Studio causes required runtime dependencies to be downloaded into your local Maven cache.

1. Install Anypoint Studio.
2. Create a simple Mule project.
3. Run the project once from Studio.
4. Re-run these integration tests from this repository.

After you run the application in Studio, you should have `licm` dependencies in your local Maven cache. To double-check:

```bash
find ~/.m2/repository/com/mulesoft/licm -type f | sort
```

Local cache location used by the test runner:

`$HOME/.m2/repository`

