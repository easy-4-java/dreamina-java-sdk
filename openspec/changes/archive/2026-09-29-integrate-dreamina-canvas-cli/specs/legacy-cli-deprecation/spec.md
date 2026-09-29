## ADDED Requirements

### Requirement: Preserve legacy binary and behavior compatibility

Existing legacy CLI public types SHALL remain available with unchanged signatures and runtime behavior. Legacy-specific
types SHALL receive Java 8 compatible Deprecated annotations and migration Javadoc. Shared image utilities and
subprocess support SHALL NOT be deprecated solely because legacy callers use them.

#### Scenario: Existing consumer compiles and executes

- **WHEN** existing legacy tests and callers use the old executor and request objects
- **THEN** they continue to compile and retain the existing behavior, with deprecation notices

#### Scenario: New integration stays independent

- **WHEN** a user constructs DreaminaCanvasCli
- **THEN** it executes dreamina-canvas and does not invoke the legacy DreaminaCliExecutor
