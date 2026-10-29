## ADDED Requirements
### Requirement: Complete static input contracts and honest live evidence
The SDK SHALL reject statically invalid coordinate pairs, bindings, references, timeline items, generation and postprocessing combinations before starting a process. Dynamic eligibility SHALL remain authoritative in the CLI. Recovery diagnostics SHALL remain accessible without resubmitting accepted operations.
#### Scenario: Invalid combinations do not launch the CLI
- **WHEN** input violates an explicit static CLI rule
- **THEN** the request throws an argument exception and no process starts
#### Scenario: Per-command acceptance is traceable
- **WHEN** command coverage is reported
- **THEN** local tests, installed dry-run and actual business success are reported separately, with blocking conditions preserved
#### Scenario: Real generation requires batch approval
- **WHEN** a live batch can consume credits
- **THEN** it is submitted only after explicit approval of that batch and quote, and accepted tasks are resumed rather than resubmitted

### Requirement: Follow public detailed command contracts
The SDK SHALL follow every public detailCommand advertised by the root schema, including schema director. All director routes SHALL expose named request/result types and executor methods. Official raw document payloads SHALL preserve their JSON structure without inventing a schema. Director rendering/export SHALL use director commands, not timeline node generation.
#### Scenario: Director commands are not omitted by discovery
- **WHEN** root schema advertises a detailCommand
- **THEN** its command schema is included in command coverage and acceptance evidence

### Requirement: Align Starter maintenance lines with the verified SDK
The Starter SHALL map Boot 2.x to SDK 1.0.x / Java 8, Boot 3.x to SDK 2.0.x / Java 17, and Boot 4.x to SDK 3.0.x / Java 21. Each maintenance line SHALL verify Spring wiring of both executors and the complete bundled Canvas contract before private Maven publication.
#### Scenario: A stale snapshot has the same logical version
- **WHEN** the resolved SDK has only the historical 49 Canvas routes
- **THEN** the Starter integration test fails rather than treating matching version text as sufficient evidence
#### Scenario: Published artifacts are consumed independently
- **WHEN** SDK and Starter publication is reported complete
- **THEN** each artifact is resolved and exercised from a separate empty Maven cache and compared with the published JAR checksum

#### Scenario: Maven 4 private publication reaches the declared repository
- **WHEN** a Maven 4 Starter line is deployed as a private snapshot
- **THEN** publication updates the configured Aliyun repository metadata and yields matching remote JAR/POM artifacts; build success alone is insufficient proof

### Requirement: Retain explicitly approved acceptance exceptions
An unavailable account scenario SHALL remain visibly unverified. Publication with such a gap SHALL require an explicit user-approved release exception.
#### Scenario: No team exists for the account
- **WHEN** the user approves release with team acceptance blocked
- **THEN** non-team acceptance and release may complete while the team scenario remains blocked in the evidence matrix

### Requirement: Preserve Boot-managed Jackson runtime compatibility
The SDK used by Boot 2.3 SHALL start with the parent-managed Jackson 2.11 runtime while preserving strict response parsing. Numeric, boolean, array and object tokens SHALL NOT be silently coerced into declared string fields.
#### Scenario: Boot dependency management overrides transitive Jackson
- **WHEN** a consuming application inherits the Boot 2.3 parent and adds the Starter
- **THEN** both executor beans start without linking to Jackson 2.12-only APIs, and malformed scalar response types still fail

#### Scenario: Raw director numbers retain their precision
- **WHEN** a director document contains a decimal with more precision than double supports, including under a Boot-managed older Jackson runtime
- **THEN** the returned raw JSON preserves the decimal value without rounding through double

## MODIFIED Requirements
### Requirement: Canvas CLI v1.2.0 contract parity
The SDK SHALL expose all 60 named v1.2.0 routes, including the root and advertised director detail contracts, and every declared parameter and response field while preserving existing Java signatures. Help and completions SHALL remain text interfaces. Official raw document fields SHALL retain their JSON payload.
#### Scenario: New routes and changed fields are available
- **WHEN** consumers invoke team, space, canvas selection, video postprocessing, sample promotion, image cutout or director commands
- **THEN** typed requests, concrete response objects and executor methods preserve the exact public schema contract
#### Scenario: No implicit remote mutation
- **WHEN** contract verification executes
- **THEN** only help, schema, version, completion and local dry-run execute against the installed CLI; generation and credit approval are not performed
