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

