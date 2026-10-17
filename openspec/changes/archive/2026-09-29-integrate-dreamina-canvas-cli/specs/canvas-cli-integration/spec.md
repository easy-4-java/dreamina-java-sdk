## ADDED Requirements

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
