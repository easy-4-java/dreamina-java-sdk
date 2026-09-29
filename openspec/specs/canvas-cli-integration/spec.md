# canvas-cli-integration Specification

## Purpose

TBD - created by archiving change integrate-dreamina-canvas-cli. Update Purpose after archive.

## Requirements

### Requirement: Complete versioned command contract

The SDK SHALL represent every executable business command in the installed Canvas 1.0.1 schema, including the callable
model group, and SHALL expose help and four shell completion commands separately as text outputs. The bundled contract
SHALL preserve flags, positional arguments, repeated values and declared exit codes without inventing dynamic model
values.

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

Tests SHALL independently enumerate every executable business command from the bundled schema and assert exact SDK route
coverage. Each route SHALL have controlled subprocess success and structured-failure checks including literal argv. A
real CLI coverage matrix SHALL record each route with actual evidence, distinguishing business success, expected
negative outcomes, local dry-run and blocked operations. Help and completion SHALL be covered separately and SHALL NOT
count as business execution. Missing routes SHALL fail the coverage check.

#### Scenario: Schema adds a route

- **WHEN** a schema executable route lacks an SDK command or test case
- **THEN** the coverage assertion fails and names the missing route

#### Scenario: Real authentication or credit approval cannot complete

- **WHEN** a command requires interactive login or a credit authorization not granted by the user
- **THEN** the matrix records the actual pending/error outcome and does not report full business-success coverage

### Requirement: Dreamina naming and audited reuse

All newly introduced top-level Java types SHALL start with Dreamina. Every pre-existing production top-level type SHALL
appear in an audited reuse inventory with a reason and either shared/reused or legacy-only status. Compatible request
interfaces, raw result containers, utilities, DTOs and exceptions SHALL be reused instead of replaced with duplicate
implementations. Protocol-specific adapters MAY be added only with documented incompatibility. The Canvas response SHALL
reuse DreaminaCliResult; the legacy response containing a JSON tree SHALL remain available only to legacy callers.

#### Scenario: A duplicate or unprefixed type is introduced

- **WHEN** a new Canvas type omits the Dreamina prefix or an existing type is absent from the inventory
- **THEN** the migration contract test fails

#### Scenario: Generic request and result contracts remain shared

- **WHEN** a Canvas request is passed as DreaminaCliArgumentProvider and a Canvas response exposes its DreaminaCliResult
- **THEN** argument boundaries and raw diagnostic values are preserved while the business payload has a concrete Java
  type

### Requirement: Typed command API without JSON trees

Every business command SHALL have a concrete Java request class with explicitly typed properties covering all command
flags and positional arguments, a bound concrete response data type, and an executor method accepting that request. Help
and completion SHALL use explicit Java request/result types. Canvas source and its public API SHALL NOT use JsonNode,
arbitrary Object properties, Map<String,Object>, or public string-keyed option builders as substitutes for typed models.
Dynamic protocol dictionaries SHALL use concrete value types. Existing published legacy CLI signatures SHALL remain
compatible.

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
