## ADDED Requirements

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

## MODIFIED Requirements

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
