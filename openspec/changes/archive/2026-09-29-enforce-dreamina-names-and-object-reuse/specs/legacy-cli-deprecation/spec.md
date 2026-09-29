## MODIFIED Requirements

### Requirement: Preserve legacy binary and behavior compatibility

Existing legacy CLI public types SHALL remain available with unchanged signatures and runtime behavior. Migration-added
Deprecated annotations SHALL follow the per-object reuse inventory. Legacy-only types incompatible with Canvas SHALL be
deprecated with a specific reason and replacement guidance. Shared/reused types SHALL remain non-deprecated; mixed
utilities SHALL deprecate only legacy-specific methods. Pre-existing member-level deprecations SHALL be preserved.

#### Scenario: Entry points declare migration

- **WHEN** a consumer uses the legacy executor
- **THEN** the execution entry point carry Java 8 compatible Deprecated annotations and migration Javadoc

#### Scenario: Supporting objects remain usable

- **WHEN** consumers use legacy requests, responses, parsers or exceptions
- **THEN** only legacy-only types carry a migration Deprecated annotation, shared types remain non-deprecated, and
  existing runtime behavior remains unchanged

#### Scenario: New integration stays independent

- **WHEN** a user constructs DreaminaCanvasCliExecutor
- **THEN** it executes dreamina-canvas without invoking the legacy executor

#### Scenario: Existing consumer compiles and executes

- **WHEN** existing legacy tests and callers use the old executor and request objects
- **THEN** they continue to compile and retain the existing behavior; legacy-only objects carry precise migration
  notices without deleting or changing old execution behavior
