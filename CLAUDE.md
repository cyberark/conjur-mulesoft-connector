# CLAUDE.md

## Overview

This repo is the **CyberArk Conjur MuleSoft Connector** — a Mule extension (`mule-extension` packaging) that lets MuleSoft/Anypoint applications retrieve secrets from Conjur Open Source, Conjur Enterprise, or Conjur Cloud (via Secrets Manager Edge). It supports two auth modes: API key (`authn`) and mutual-TLS certificate auth (`authn-cert`, including SPIFFE identity mode). The connector wraps the `conjur-sdk-java` client and exposes Mule connection/config/operations classes so Mule flows can fetch secrets securely. It is a CyberArk "Certified" community project.

## Tech stack

- **Language:** Java 17
- **Framework:** Mule SDK / MuleSoft extension (`org.mule.sdk:mule-sdk-api`, `mule-modules-parent` parent POM), packaged as a Mule plugin
- **Build:** Maven (`pom.xml`), built inside a Docker "tools" image (Maven 3.9.11 + JDK 17) rather than requiring local Maven
- **Key dependencies:** `com.cyberark:conjur-sdk-java`, OkHttp, Bouncy Castle (mTLS/PKI), SLF4J/Log4j2
- **Test:** JUnit 5 + Mockito for unit tests; MUnit for Mule integration tests; JaCoCo for coverage; SpotBugs + FindSecBugs for static/security analysis

## Build commands

```bash
./build_tool_image.sh          # builds the "tools" Docker image (Maven+JDK17) used by other scripts
./build_package.sh             # (invoked as part of publish/deploy flow) packages the connector jar
mvn clean install -DskipTests  # build & install connector into local ~/.m2 (needed before MUnit integration tests)
```
`publish.sh` sets the pom version from `CHANGELOG.md`/`VERSION` and packages/publishes via the `tools` Docker image (Maven Central / Artifactory).

## Test commands

```bash
./run_unit_tests.sh   # builds Dockerfile.test image, runs `mvn ... jacoco:prepare-agent test jacoco:report`
                       # reports: target/surefire-reports/, target/site/jacoco/
```
Integration tests (real end-to-end, require Docker + local Conjur):
```bash
./src/test/integration-tests/munit-app/run_munit_integration_tests.sh {oss|enterprise|saas|edge} mulesoft-api
```
SaaS/Edge require exported env vars (`INFRAPOOL_CONJUR_APPLIANCE_URL`, `INFRAPOOL_CONJUR_AUTHN_LOGIN`, `IDENTITY_PASSWORD_CLOUD`, `INFRAPOOL_CONJUR_AUTHN_TOKEN`, `PROFILE=edge`) — see README.md for details. These currently run locally only, not in CI, due to lack of MuleSoft EE repo access in the pipeline.

## Repo structure

- `src/main/java/com/cyberark/conjur/` — core connector logic:
  - `authentication/` — `AccessTokenProvider(Impl)` (Conjur token retrieval)
  - `core/ConjurConnection.java` — low-level Conjur connection
  - `domain/ConjurConfiguration.java` — config model
  - `service/ConjurService(Impl).java` — secret-fetch service logic
  - `error/` — Mule error types/provider
  - `mulesoft/internal/` — Mule-facing glue: `ConjurMuleExtension`, `ConjurMuleConfiguration`, `ConjurMuleConnection(Provider)`, `ConjurMuleOperations` (the actual Mule operation(s) exposed to flows)
- `src/test/java/...` — JUnit/Mockito unit tests mirroring `src/main/java` package structure
- `src/test/integration-tests/` — MUnit sample app (`munit-app/`) and a Conjur bootstrap environment (`secrets-manager-integration-environment-bootstrap/`) used for local end-to-end testing
- `pom.xml` — Maven build, Mule extension packaging, profiles for Central Portal publishing (`central-portal`) and GPG signing (`sign`)
- `Dockerfile`, `Dockerfile.aws`, `Dockerfile.test`, `tools.Dockerfile` — container images for packaging, AWS deploy, unit testing, and the Maven build "tools" image
- `build_tool_image.sh`, `build_package.sh`, `publish.sh`, `deploy_mule.sh`, `run_unit_tests.sh` — Docker-driven build/test/publish/deploy scripts
- `fetch_secrets.sh`, `fetch_secrets_enterprise.sh` — example shell scripts showing raw Conjur `authn`/`secrets` REST calls (not part of the connector runtime; illustrative/manual test helpers)
- `conjur.yml`, `secrets.yml`, `conf/policy/*.yml` — sample Conjur policy files (hosts/variables) used to bootstrap a local Conjur instance for manual/integration testing
- `conf/default.conf`, `conf/tls/tls.conf` — sample local Conjur server config
- `cloud/root.yml`, `cloud/cloud_ca.pem` — sample policy/CA cert for Conjur Cloud-style local testing
- `CHANGELOG.md` — source of truth for the release version (read by Jenkins/`publish.sh` to set the Maven version)
- `Jenkinsfile` — CI pipeline definition
- `mvn-settings.xml` — Maven settings for CyberArk-internal/Anypoint Exchange repositories

## Notable conventions / gotchas

- Builds/tests are Docker-first: most scripts run Maven inside a `tools` or `Dockerfile.test` image rather than assuming a local Maven install. Build the `tools` image (`./build_tool_image.sh`) before running scripts that depend on it (`publish.sh`, `deploy_mule.sh`).
- `pom.xml` packaging is `mule-extension`; a comment in the file notes it must be changed to `pom` if deploying to Anypoint Exchange/CloudHub.
- Release version comes from `CHANGELOG.md` via a generated `VERSION` file (Jenkins writes it; locally it defaults to `0.0.1-SNAPSHOT` / `0.0.dev`).
- `authn-cert` mode requires the client private key file to be `chmod 0600`; the connector validates this at startup and rejects world/group-readable key files.
- Never place secrets (`conjurClientKey`, `conjurApiKey`, etc.) as plaintext config — use Mule Secure Properties (`secure::` prefix).
- `conf/`, `cloud/`, `conjur.yml`, `secrets.yml`, and the `fetch_secrets*.sh` scripts are local/manual testing fixtures for spinning up a Conjur instance — they are not consumed by the connector's Java code itself.
- MUnit integration tests need the connector freshly `mvn clean install -DskipTests`'d into the local `~/.m2` cache so the sample MUnit app picks up local code changes.
