## ADDED Requirements

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
