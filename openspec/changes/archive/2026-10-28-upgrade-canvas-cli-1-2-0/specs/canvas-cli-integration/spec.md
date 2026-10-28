## ADDED Requirements
### Requirement: Canvas CLI v1.2.0 contract parity
The SDK SHALL expose all 49 v1.2.0 business routes and every declared parameter and response field while preserving existing Java signatures. Help and completions SHALL remain text interfaces.
#### Scenario: New routes and changed fields are available
- **WHEN** consumers invoke team, space, canvas selection, video postprocessing, sample promotion or image cutout commands
- **THEN** typed requests, concrete response objects and executor methods preserve the exact schema contract
#### Scenario: No implicit remote mutation
- **WHEN** contract verification executes
- **THEN** only help, schema, version, completion and local dry-run execute against the installed CLI; generation and credit approval are not performed
