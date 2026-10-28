## MODIFIED Requirements
### Requirement: Preserve legacy binary and behavior compatibility
Existing dreamina CLI public types SHALL remain available and actively maintained alongside Canvas with unchanged existing signatures and default runtime behavior. Deprecated annotations and migration Javadoc added solely for Canvas migration SHALL be removed. Pre-existing deprecations for obsolete values and superseded internal hooks SHALL remain. Current CLI flags SHALL have typed request support.
#### Scenario: Entry points declare migration
- **WHEN** a consumer uses either CLI executor
- **THEN** neither executor nor its supported public request types carry migration deprecation annotations
#### Scenario: New video ratio flags
- **WHEN** image2video or frames2video requests provide a ratio
- **THEN** the SDK passes one literal --ratio flag; omitting the ratio preserves the previous argv
#### Scenario: New integration stays independent
- **WHEN** Canvas requests execute
- **THEN** all v1.2.0 contract tests continue to pass without calling dreamina

#### Scenario: Supporting objects remain usable
- **WHEN** consumers use legacy requests, responses, parsers or exceptions
- **THEN** supported objects remain non-deprecated and preserve their original public signatures

#### Scenario: Existing consumer compiles and executes
- **WHEN** existing legacy callers use the executor and request objects
- **THEN** they compile and retain existing default behavior while receiving optional new flags when explicitly supplied
