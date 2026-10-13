# Conjur Mulesoft Connector

The CyberArk Conjur MuleSoft Connector provides a secure and efficient way to retrieve secrets from Conjur. It supports two authentication modes:

- **API key authentication** (`authn`) — authenticates with a Conjur username and API key.
- **Certificate authentication** (`authn-cert`) — authenticates via mutual TLS (mTLS) using an X.509 client certificate and private key.
> **Note for Secrets Manager SaaS:** `authn-cert` is only available through a **Secrets Manager Edge** instance. Set `conjurApplianceUrl` to the Edge endpoint (e.g. `https://us-edge.acme.dev/api`).

## Certification level
![](https://img.shields.io/badge/Certification%20Level-Certified-28A745?link=https://github.com/cyberark/community/blob/master/Conjur/conventions/certification-levels.md)

This repo is a **Certified** level project. It's a community contributed project that **has been reviewed and tested by CyberArk and is trusted to use with Conjur Open Source, Conjur Enterprise, and Conjur Cloud**. For more detailed information on our certification levels, see [our community guidelines](https://github.com/cyberark/community/blob/master/Conjur/conventions/certification-levels.md#certified).

## Flow Diagram
<img width="544" alt="image" src="https://media.github.cyberng.com/user/1246/files/70c41905-98aa-46de-a5c7-512b71433b28">

## Plugin Requirements & Configuration

Please refer to official [documentation](https://docs.cyberark.com/conjur-enterprise/latest/en/Content/HomeTilesLPs/LP-Tile4.htm)

## Authentication modes

### API key authentication (`authn`)

Configure the following parameters in the Mule global config element:

| Parameter | Required | Description |
|---|---|---|
| `conjurAccount` | Yes | Conjur account name |
| `conjurApplianceUrl` | Yes | Conjur / Edge base URL |
| `conjurAuthnLogin` | Yes | Conjur username (e.g. `host/data/my-app`) |
| `conjurApiKey` | Yes | API key for the Conjur identity |
| `conjurSslCertificate` / `conjurCertFile` | Recommended | Path to the Conjur CA certificate (PEM) |

### Certificate authentication (`authn-cert` / mTLS)

Set `conjurAuthnCertServiceId` and the client certificate/key to activate cert auth. When this parameter is present, API key auth is bypassed.

| Parameter | Required | Description |
|---|---|---|
| `conjurAccount` | Yes | Conjur account name |
| `conjurApplianceUrl` | Yes | Conjur / Edge base URL (include `/api` for SaaS Edge) |
| `conjurAuthnCertServiceId` | Yes | Authenticator service ID (e.g. `acme-vm`) |
| `conjurClientCertFile` | Yes* | Path to the client X.509 certificate file (PEM) |
| `conjurClientKeyFile` | Yes* | Path to the client private key file (PEM). **Must be owner-read-only (`chmod 0600`)** |
| `conjurClientCert` | Yes* | Inline PEM certificate content (alternative to `conjurClientCertFile`) |
| `conjurClientKey` | Yes* | Inline PEM private key content (alternative to `conjurClientKeyFile`) |
| `conjurSslCertificate` / `conjurCertFile` | Recommended | Path to the Conjur CA certificate for server verification |
| `conjurAuthnLogin` | No | Workload host ID for **request host-mode** (URL-encoded, e.g. `host/data/vm-workloads/vm-01`). Leave empty for **SPIFFE mode** |

\* Provide either the file path (`conjurClientCertFile` / `conjurClientKeyFile`) **or** the inline PEM content (`conjurClientCert` / `conjurClientKey`). File path takes precedence.

#### Security requirements for `authn-cert`

- **Private key file permissions** — the key file must be readable only by the owner (`chmod 0600` on Linux/macOS). The connector validates this at startup and rejects world-readable or group-readable files.
- **Inline PEM** — never place `conjurClientKey` (or `conjurClientCert`) as plaintext in `global-config.xml`. Always use [Mule Secure Properties](https://docs.mulesoft.com/mule-runtime/latest/secure-configuration-properties) (`secure::` prefix) to encrypt sensitive configuration values.
- **Conjur Cloud SaaS** — set `conjurApplianceUrl` to the **Secrets Manager Edge** endpoint, not the direct Conjur Cloud SaaS endpoint.

#### Authentication URL

```
POST <conjurApplianceUrl>/authn-cert/<conjurAuthnCertServiceId>/<conjurAccount>[/<conjurAuthnLogin>]/authenticate
```

- **Request host-mode** — `conjurAuthnLogin` is set → included (URL-encoded) in the path.
- **SPIFFE mode** — `conjurAuthnLogin` is empty → omitted from the path; identity is derived from the certificate's SPIFFE SAN.

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

The MUnit app targets **Mule 4.12.2 / MUnit 3.7.3**. Anypoint Studio must be used with a project on that exact runtime version to seed all required EE artifacts into the local Maven cache.

1. Install Anypoint Studio.
2. Create a new Mule project and select runtime **4.12.2**.
3. Run the project once from Studio — this downloads all required EE runtime services into `~/.m2`.
4. Verify the key EE artifact is now present:

```bash
ls ~/.m2/repository/org/mule/services/mule-service-soap/1.9.10/mule-service-soap-1.9.10-mule-service.jar
```

5. Clean up any stale failure records left by previous failed download attempts:

```bash
find ~/.m2/repository/org/mule/services -name "*.lastUpdated" -delete
find ~/.m2/repository/com/mulesoft -name "*.lastUpdated" -delete
```

6. Delete any stale build output from a previous failed run:

```bash
rm -rf src/test/integration-tests/munit-app/target/
```

7. Re-run these integration tests from this repository.

Local cache location used by the test runner:

`$HOME/.m2/repository`

