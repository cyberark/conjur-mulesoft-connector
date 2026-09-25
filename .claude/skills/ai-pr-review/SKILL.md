---
name: ai-pr-review
description: Automated audit of code changes before opening a Pull Request against this repo (conjur-mulesoft-connector, the CyberArk Conjur MuleSoft Connector, Java 17/Maven mule-extension). Analyzes the diff against the base branch, checks compliance with CyberArk and Mule SDK conventions used here (architecture, security, quality), and generates a PR_REVIEW_REPORT.md ready to paste into the PR description. Use before every commit that finalizes a task, or when the user writes "@ai-pr-review run analysis and generate report".
---

# AI PR Review

Automated audit of code changes before opening a Pull Request. The skill analyzes the diff, checks compliance with the CyberArk and Mule SDK conventions used in this repo (`conjur-mulesoft-connector`), and generates a report ready to paste into the PR description.

## Workflow

### 1. Context Extraction

- Run `git diff` against the base branch (usually `main`).
- Identify the changed components under `src/main/java/com/cyberark/conjur/`:
  - `authentication/` — `AccessTokenProvider(Impl)` (Conjur token retrieval)
  - `core/ConjurConnection.java` — low-level Conjur connection
  - `domain/ConjurConfiguration.java` — config model
  - `service/ConjurService(Impl).java` — secret-fetch service logic
  - `error/` — Mule error types/provider
  - `mulesoft/internal/` — Mule-facing glue (`ConjurMuleExtension`, `ConjurMuleConfiguration`, `ConjurMuleConnection(Provider)`, `ConjurMuleOperations`)

### 2. Multi-Persona Analysis

- **Architect**:
  - Both auth modes (API key via `authn`, and mutual-TLS `authn-cert` including SPIFFE identity mode) must stay consistent in the `mulesoft/internal/` glue and `authentication/` layer — a change to one shouldn't silently diverge from the other.
  - The connector wraps `conjur-sdk-java` — flag any code that reimplements Conjur API calls directly instead of going through that SDK.
  - `pom.xml` packaging must stay `mule-extension` (a comment notes it must change to `pom` only for Anypoint Exchange/CloudHub deployment) — flag any unexplained packaging change.
  - Release version comes from `CHANGELOG.md` via a generated `VERSION` file — don't hand-edit the Maven version in `pom.xml`.
- **Security**:
  - No hardcoded Conjur secrets, API keys, or client certs/keys committed; sample fixtures (`conjur.yml`, `secrets.yml`, `conf/policy/*.yml`, `cloud/root.yml`, `cloud/cloud_ca.pem`) are local/manual testing fixtures only, not consumed by connector runtime code — don't wire them into `src/main/java`.
  - `authn-cert` mode requires the client private key file to be `chmod 0600`; the connector validates and rejects world/group-readable key files at startup — flag any change that weakens or removes this check.
  - Secrets (`conjurClientKey`, `conjurApiKey`, etc.) must never be plaintext Mule config — they must go through Mule Secure Properties (`secure::` prefix). Flag any new config field holding a credential that doesn't follow this.
  - Static/security analysis runs via SpotBugs + FindSecBugs — new code should be checked against these before merge, not just unit tests.
- **Quality**:
  - New/changed logic in `src/main/java` needs corresponding JUnit 5 + Mockito tests under `src/test/java` mirroring the same package structure, with JaCoCo coverage.
  - Changes to Mule-facing operations (`ConjurMuleOperations`) or connection/config classes should be checked against the MUnit sample app in `src/test/integration-tests/munit-app/` — note in the PR whether `mvn clean install -DskipTests` + a MUnit run was done locally (these integration tests currently only run locally, not in CI, due to lack of MuleSoft EE repo access in the pipeline).

### 3. Standard Compliance

- Java baseline is 17; builds are Docker-first (`tools` image via `build_tool_image.sh`, or `Dockerfile.test` for unit tests) rather than assuming a local Maven install — flag new scripts/docs that assume bare-metal Maven without the Docker path.
- Key dependencies (`com.cyberark:conjur-sdk-java`, OkHttp, Bouncy Castle, SLF4J/Log4j2) should not be casually swapped or version-bumped without checking compatibility with the Mule SDK parent POM (`mule-modules-parent`).
- `fetch_secrets.sh` / `fetch_secrets_enterprise.sh` are illustrative/manual helpers only, not part of the connector runtime — don't treat changes there as needing the same review rigor as `src/main/java`, but flag if they drift from actual connector behavior.

### 4. Report Generation

Generate `PR_REVIEW_REPORT.md` **fully filled in** — never leave a `<placeholder>` in the final file. Derive each field automatically:

- **One-line summary**: one imperative-mood sentence describing what the diff does.
- **Desired Outcome**: if the branch name or commit messages reference a ticket/issue, pull context from it (e.g. `gh issue view <n>` for a `#<n>` reference); otherwise infer the motivating problem directly from what the diff changes.
- **Implemented Changes**: build this directly from the actual `git diff` and the Multi-Persona Analysis findings (step 2) — what changed, why, and how a reviewer should verify it (manual/integration steps if any). State screenshots as "N/A" unless the diff touches UI.
- **Connected Issue/Story**: scan the current branch name and the commit messages on this branch for a ticket pattern like `[A-Z]+-\d+` (e.g. `CP-26179`, `CNJR-1234`) and use the first match as the "CyberArk internal issue ID". If a GitHub issue number is referenced (`#123` in a branch name or commit message), use it for "Resolves #123" — otherwise omit that line entirely rather than leaving a placeholder.
- **Definition of Done** — tick exactly one box per subsection based on what the diff actually contains, never by default:
  - *Changelog*: tick "CHANGELOG has been updated" only if `CHANGELOG.md` (or equivalent) is part of the diff; tick "does not require a CHANGELOG update" only if the change has no user-facing effect.
  - *Test coverage*: tick "includes new tests" only if test files changed in the diff; tick "does not require tests" only if the change is non-functional (docs, config, tooling, skills).
  - *Documentation*: tick "docs were updated" only if README/docs files changed; tick "does not require updating documentation" only if nothing user-facing changed.
  - *Behavior*: tick "no behavior was changed" only if the diff is limited to non-runtime files (skills, docs, CI config, tests); otherwise tick one of the other two options and explain why.
  - *Security*: tick "no security aspects" only if the Security persona (step 2) genuinely found nothing; otherwise tick the appropriate other option and reference the finding.

If a field genuinely cannot be determined, write a short explicit note (e.g. "No linked issue found in branch/commit history") instead of leaving an angle-bracket placeholder.

The output must follow this structure when pasted into a PR description:

```markdown
<filled summary>

### Desired Outcome

<filled>

### Implemented Changes

- <filled bullets>

### Connected Issue/Story

Resolves #<n>  (omit this line if none found)

CyberArk internal issue ID: <filled or note>

### Definition of Done

#### Changelog
- [ ] The CHANGELOG has been updated, or
- [ ] This PR does not include user-facing changes and doesn't require a CHANGELOG update

#### Test coverage
- [ ] This PR includes new unit and integration tests to go with the code changes, or
- [ ] The changes in this PR do not require tests

#### Documentation
- [ ] Docs (e.g. READMEs) were updated in this PR
- [ ] A follow-up issue to update official docs has been filed here: [insert issue ID]
- [ ] This PR does not require updating any documentation

#### Behavior
- [ ] This PR changes product behavior and has been reviewed by a PO, or
- [ ] These changes are part of a larger initiative that will be reviewed later, or
- [ ] No behavior was changed with this PR

#### Security
- [ ] Security architect has reviewed the changes in this PR,
- [ ] These changes are part of a larger initiative with a separate security review, or
- [ ] There are no security aspects to these changes
```

(the checkboxes above are the template shape — in the actual generated report, exactly one box per subsection must be ticked `[x]`, the rest `[ ]`)

## Usage Instructions

Run this command before every commit that finalizes a task:

```
@ai-pr-review run analysis and generate report
```

## Port.io Metadata

- **Maturity Level**: Tier 2 (Repeatable Skill)
- **Service**: Secrets Manager Integrations
