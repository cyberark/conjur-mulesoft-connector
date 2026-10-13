# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [2.0.8] - 2026-10-13

### Security
- Eliminated all HIGH/CRITICAL CVEs from Docker images by upgrading base images
  from Alpine 3.23 to Alpine 3.24 and applying targeted zlib patch (CVE-2026-85091)

### Changed
- MUnit integration tests upgraded from Mule 4.10.0 / MUnit 3.6.1 to Mule 4.12.2 / MUnit 3.7.3
- Updated integration test troubleshooting documentation

## [2.0.7] - 2026-10-01

### Security
- Reduced Docker image CVEs from 106 to 2 by migrating base images to Alpine Linux

## [2.0.6] - 2026-08-13

### Added
- Certificate-based authentication (`authn-cert` / mTLS) as an alternative to API key auth

## [2.0.5] - 2026-04-23

- Improved error messages for faster troubleshooting

## [2.0.4] - 2025-08-06

### Changed
- Updated 'conjur-sdk-java' dependency version to 4.2.2

## [2.0.3] - 2025-08-05

### Changed
- Retrieve SDK version from CHANGELOG instead of VERSION file (CNJR-7704)
  
## [2.0.2] - 2025-04-01
- Added telemetry headers

## [2.0.1] - 2025-03-11
- Automated edge tests

## [2.0.0] - 2024-11-27
- Add support for Java17

## [1.0.3] - 2024-02-01
- Update packaging information

## [1.0.2] - 2024-01-23
- Update artifact information

## [1.0.1] - 2024-01-19
- Add release and promotion capabilities

## [1.0.0] - 2024-01-12
- Mulesoft Connector for Conjur Vault
