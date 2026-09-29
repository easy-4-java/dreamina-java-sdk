## ADDED Requirements

### Requirement: Dreamina naming and audited reuse

All newly introduced top-level Java types SHALL start with Dreamina. Every pre-existing production top-level type SHALL
appear in an audited reuse inventory with a reason and either shared/reused or legacy-only status. Compatible request
interfaces, generic result/response containers, utilities, DTOs and exceptions SHALL be reused instead of replaced with
duplicate implementations. Protocol-specific adapters MAY be added only with documented incompatibility.

#### Scenario: A duplicate or unprefixed type is introduced

- **WHEN** a new Canvas type omits the Dreamina prefix or an existing type is absent from the inventory
- **THEN** the migration contract test fails

#### Scenario: Generic request and result contracts remain shared

- **WHEN** a Canvas request is passed as DreaminaCliArgumentProvider and a Canvas response is accessed as its shared
  response view
- **THEN** argument boundaries, body identity and raw diagnostic values are preserved
