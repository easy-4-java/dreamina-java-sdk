# canvas-cli-integration Specification

## Purpose

Provide the versioned Java contract, safe process execution, typed results and traceable acceptance for the official Dreamina Canvas CLI, alongside the maintained legacy CLI and compatible Spring Boot Starters.

## Requirements

### Requirement: Complete versioned command contract

The SDK SHALL map every executable command in the versioned public Canvas schema, including callable model groups, to an SDK route. Help and four completions SHALL return text. Contracts SHALL preserve flags, positional/repeated values and exit codes without inventing dynamic models. Each CLI upgrade SHALL refresh the read-only snapshot and regenerate surfaces so schema routes, enum constants and tested routes remain exactly equal.

#### Scenario: All executable commands have an SDK route

- **WHEN** the versioned schema is enumerated
- **THEN** every leaf command and `model <name>` maps to a public command identifier and can be passed to the client

#### Scenario: Typed options become argv without shell execution

- **WHEN** a prompt contains spaces, quotes, newlines or shell syntax, or repeated references are supplied
- **THEN** each option is passed as a separate literal argv value and reference order is preserved

### Requirement: Explicit configuration and validation

The SDK SHALL configure executable, working directory, profile, region, timeout and per-client concurrency. Business
commands SHALL request JSON and disable interaction. It SHALL NOT add `--yes`, run, confirmation, login or retries
implicitly. It SHALL validate required parameters, known flags, scalar/repeated cardinality and primitive values, plus
run/wait/approval and batch identity relationships.

#### Scenario: Invalid batch identity is rejected before execution

- **WHEN** supplied submit IDs have the wrong count, invalid UUIDs or duplicates
- **THEN** no subprocess starts and an argument error is returned to the caller

#### Scenario: Draft creation is side-effect explicit

- **WHEN** a create request omits run and approval parameters
- **THEN** the SDK passes no run, credit token, ceiling or yes flag

### Requirement: Preserve Canvas response and recovery semantics

The SDK SHALL preserve schemaVersion, ok, data, error, meta, partialData, creditConfirmation, raw envelope and process
exit code. Valid CLI error envelopes SHALL be returned as structured responses, including exits 10 and 20; command
success SHALL require both exit zero and ok true. Transport failures and malformed envelopes SHALL be distinguishable
exceptions. Typed data conversion SHALL be supported without losing the raw envelope.

#### Scenario: Confirmation is required after draft save

- **WHEN** CLI exits 10 with requiredAction confirm and partial node/submit identities
- **THEN** the response retains those fields, is unsuccessful and does not resubmit or confirm

#### Scenario: Accepted work requires resumption

- **WHEN** CLI exits 20 with requiredAction resume
- **THEN** the original operation identity and partial data are returned without rerunning any mutation

### Requirement: Bounded isolated process execution

The client SHALL use its own concurrency limit and include queue waiting in the configured timeout. On timeout or
interruption it SHALL terminate its process, preserve diagnostic output where available and never replay the command.
Tokens and full command lines SHALL NOT be inserted into SDK logs or exception messages.

#### Scenario: Separate clients cannot replace each other's limits

- **WHEN** multiple clients are constructed or a legacy executor is created
- **THEN** an existing Canvas client's semaphore is unchanged

#### Scenario: Interrupted caller cleans up its process

- **WHEN** a caller waiting for a running command is interrupted
- **THEN** its subprocess is terminated and the interrupt flag is preserved

### Requirement: Verifiable integration

The implementation SHALL have offline tests using controlled subprocesses, optional installed CLI schema tests and real
read-only/local dry-run verification. No paid generation is required to validate the integration.

#### Scenario: Installed contract check

- **WHEN** installed Canvas integration tests are explicitly enabled
- **THEN** version/schema execute through the SDK and business command/flag coverage is compared with the current binary

### Requirement: Executor naming

The public Canvas entry point SHALL be named DreaminaCanvasCliExecutor. Since the prior name is unreleased, source,
tests and current usage documentation SHALL be renamed directly, without adding an alias solely for this in-progress
revision.

#### Scenario: New executor can be constructed

- **WHEN** a Java 8 application imports DreaminaCanvasCliExecutor
- **THEN** it can invoke the complete Canvas command catalog

### Requirement: Auditable per-command coverage

Tests SHALL enumerate every executable bundled-schema command and require exact SDK route coverage. Each route SHALL test controlled subprocess success, structured failure and literal argv. A real CLI matrix SHALL link actual evidence for business success, expected failure, dry-run and blocked cases. Help/completion SHALL be tested separately and SHALL NOT count as business execution. Missing routes SHALL fail coverage.

#### Scenario: Schema adds a route

- **WHEN** a schema executable route lacks an SDK command or test case
- **THEN** the coverage assertion fails and names the missing route

#### Scenario: Real authentication or credit approval cannot complete

- **WHEN** a command requires interactive login or a credit authorization not granted by the user
- **THEN** the matrix records the actual pending/error outcome and does not report full business-success coverage

### Requirement: Dreamina naming and audited reuse

New top-level types SHALL start with Dreamina. Every existing production top-level type SHALL have an audited reuse reason and shared/reused or legacy-only status. Compatible interfaces, results, utilities, DTOs and exceptions SHALL be reused rather than duplicated; protocol adapters require documented incompatibility. Canvas SHALL reuse DreaminaCliResult. Legacy response JSON trees SHALL remain available only to legacy callers.

#### Scenario: A duplicate or unprefixed type is introduced

- **WHEN** a new Canvas type omits the Dreamina prefix or an existing type is absent from the inventory
- **THEN** the migration contract test fails

#### Scenario: Generic request and result contracts remain shared

- **WHEN** a Canvas request is passed as DreaminaCliArgumentProvider and a Canvas response exposes its DreaminaCliResult
- **THEN** argument boundaries and raw diagnostic values are preserved while the business payload has a concrete Java
  type

### Requirement: Typed command API without JSON trees

Each business command SHALL expose a concrete typed request for every flag/argument, a bound concrete response and a request-accepting executor method. Help/completion SHALL use explicit request/result types. Canvas source/API SHALL NOT substitute JsonNode, arbitrary Object, Map<String,Object> or public string-keyed option builders for typed models. Dynamic dictionaries SHALL use concrete values. Published legacy signatures SHALL remain compatible.

#### Scenario: Schema parameter or response field changes

- **WHEN** the installed schema adds a flag, positional argument or nested response field absent from the corresponding
  Java object
- **THEN** the contract coverage tests fail and identify the missing path

#### Scenario: Caller invokes a command

- **WHEN** a caller passes a concrete request to its executor method
- **THEN** the response data type is known at compile time and parameters require no string keys

#### Scenario: Recovery or dry-run response

- **WHEN** the CLI returns a dry-run plan, structured failure, partial data or credit confirmation requirement
- **THEN** all declared fields are available through typed Java getters and the raw output remains available as text

### Requirement: Bundled contract snapshot matches the installed public CLI

The SDK SHALL bundle the official public CLI schema and its version. DreaminaCanvasContract SHALL use it for runtime validation. Each CLI upgrade SHALL regenerate it from installed read-only schema/version output and update enums, executor methods, request/response types, coverage cases and the command catalog so schema routes, enum constants and tested routes remain exactly equal.

#### Scenario: CLI adds a destructive command family

- **WHEN** the official CLI ships a new command family such as
  `node delete` with a confirmation-token flow
- **THEN** the SDK exposes one enum constant, request type, response type and
  executor method per route, and the bundled validator enforces the new
  required flags (for example `--node-id` on the delete family)

#### Scenario: CLI tightens a flag to required

- **WHEN** a flag such as `resource upload --project-id` becomes required in
  the official schema
- **THEN** the bundled validator rejects requests missing it before any
  process is spawned, without SDK-specific validation code

### Requirement: Canvas CLI v1.2.0 contract parity
The SDK SHALL expose all 60 named v1.2.0 routes, including the root and advertised director detail contracts, and every declared parameter and response field while preserving existing Java signatures. Help and completions SHALL remain text interfaces. Official raw document fields SHALL retain their JSON payload.
#### Scenario: New routes and changed fields are available
- **WHEN** consumers invoke team, space, canvas selection, video postprocessing, sample promotion, image cutout or director commands
- **THEN** typed requests, concrete response objects and executor methods preserve the exact public schema contract
#### Scenario: No implicit remote mutation
- **WHEN** contract verification executes
- **THEN** only help, schema, version, completion and local dry-run execute against the installed CLI; generation and credit approval are not performed

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
