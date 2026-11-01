# Security policy

## Reporting a vulnerability

Please report suspected vulnerabilities privately through GitHub's
[private vulnerability reporting](https://github.com/stephen-masters/qzr/security/advisories/new)
(Security tab → "Report a vulnerability"). Do not open a public issue for a
security problem.

## Supported versions

This is a demonstration project with no releases. Only `main` is maintained.

## What is done to keep it safe

- Dependency versions come from the Spring Boot parent, kept on the latest
  GA release by Dependabot, which also keeps the Maven wrapper and the
  workflow actions current.
- Trivy scans the dependencies for HIGH and CRITICAL vulnerabilities on
  every push and pull request, and weekly on a schedule, and CI publishes a
  CycloneDX SBOM.
- The application holds no credentials and stores nothing: each visitor's
  answers live in memory, in a Drools session that ends with their HTTP
  session. When run, it listens on port 40080 with no authentication, so it
  is meant for a local machine, not the internet.
