## MODIFIED Requirements

### Requirement: Preserve legacy binary and behavior compatibility

Existing legacy CLI public types SHALL remain available with unchanged signatures and runtime behavior. Migration-added
Deprecated annotations SHALL be limited to DreaminaCliExecutor. Supporting DTOs, requests, enums, parsers, availability
helpers, exceptions and shared utilities SHALL NOT receive blanket deprecation. Pre-existing member-level deprecations
SHALL be preserved.

#### Scenario: Entry points declare migration

- **WHEN** a consumer uses the legacy executor
- **THEN** the execution entry point carry Java 8 compatible Deprecated annotations and migration Javadoc

#### Scenario: Supporting objects remain usable

- **WHEN** consumers use legacy requests, responses, parsers or exceptions
- **THEN** no new migration-wide Deprecated annotation is applied to those types and their runtime behavior remains
  unchanged

#### Scenario: New integration stays independent

- **WHEN** a user constructs DreaminaCanvasCliExecutor
- **THEN** it executes dreamina-canvas without invoking the legacy executor

#### Scenario: Existing consumer compiles and executes

- **WHEN** existing legacy tests and callers use the old executor and request objects
- **THEN** they continue to compile and retain the existing behavior; only the old executor adds a migration deprecation
  notice
