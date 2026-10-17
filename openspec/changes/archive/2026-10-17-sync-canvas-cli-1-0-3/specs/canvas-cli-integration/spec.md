## ADDED Requirements

### Requirement: Bundled contract snapshot matches the installed public CLI

The typed Canvas integration SHALL bundle a snapshot of the official public
CLI schema and version it was generated from, and `DreaminaCanvasContract`
SHALL read that snapshot for runtime argument validation. The snapshot SHALL
be regenerated from the installed public CLI's read-only `schema` and
`version` output on every CLI upgrade, and the enum, executor methods,
request/response types, coverage cases, and command catalog SHALL be
regenerated or extended so that schema routes, enum constants and tested
routes remain exactly equal.

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

## MODIFIED Requirements

### Requirement: Complete versioned command contract

The SDK SHALL represent every executable business command in the installed public Canvas CLI schema captured by the
bundled versioned snapshot, including the callable model group, and SHALL expose help and four shell completion
commands separately as text outputs. The bundled contract SHALL preserve flags, positional arguments, repeated values
and declared exit codes without inventing dynamic model values. On every CLI upgrade the snapshot SHALL be refreshed
from read-only output and the generated surfaces SHALL be regenerated so schema routes, enum constants and tested
routes remain exactly equal.

#### Scenario: All executable commands have an SDK route

- **WHEN** the versioned schema is enumerated
- **THEN** every leaf command and `model <name>` maps to a public command identifier and can be passed to the client

#### Scenario: Typed options become argv without shell execution

- **WHEN** a prompt contains spaces, quotes, newlines or shell syntax, or repeated references are supplied
- **THEN** each option is passed as a separate literal argv value and reference order is preserved
