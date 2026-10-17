## MODIFIED Requirements

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
