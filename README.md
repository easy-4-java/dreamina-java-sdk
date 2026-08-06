# dreamina-java-sdk

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/dreamina-java-sdk) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

A pure Java SDK (no Spring dependency) for invoking Dreamina (即梦 / Jimeng) capabilities through the local `dreamina` CLI. Suitable for plain Java applications, command-line tools, batch jobs, or any other framework.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`dreamina-java-sdk` is a JDK 8 line, Spring-free SDK that drives the official Dreamina command line interface (`dreamina` CLI). It wraps subprocess execution, parses structured output (JSON / text / tables), and exposes strongly typed request and result objects for every built-in and generator command of the CLI.

| What it is | What it is not |
|:---|:---|
| A typed Java wrapper around the local `dreamina` CLI | An HTTP API client (there is no public HTTP endpoint abstraction) |
| Subprocess management (timeout, exit codes, availability) | A UI or web service |
| Structured parsing for JSON / text / table outputs | A wrapper around third-party cloud SDKs |

Typical use cases:

| Use case | Notes |
|:---|:---|
| Text-to-image / image-to-image generation (`text2image`, `image2image`, `image_upscale`) | Typed `*Submit` requests, `--poll` semantics |
| Video generation (`text2video`, `image2video`, `frames2video`, `multiframe2video`, `multimodal2video`) | Model & resolution enums, duration validation |
| Task query & download (`query_result`, `list_task`) | Poll status, download results to a directory |
| Session workspace management (`session create/list/search/rename/delete`) | All generator commands accept `--session=<id>` |
| Login / account (`login`, `logout`, `relogin`, `user_credit`, `version`) | OAuth Device Flow, headless login support |
| Startup readiness probing | `DreaminaCliAvailabilityChecker` runs `dreamina version` |

**Project status:** active development; the SDK is continuously aligned with the upstream `dreamina` CLI contract (currently v1.4.x).

## 2. Features & Status

| Feature | Status | Notes |
|:---|:---|:---|
| Subprocess execution via Apache Commons Exec | Available | Configurable timeout, working directory, concurrency limit |
| Typed exception mapping | Available | Timeout / non-zero exit / executable-unavailable / startup failures |
| Built-in CLI commands (`help`, `version`, `user_credit`, login & session commands) | Available | `DreaminaCliExecutor` methods, raw and structured variants |
| All generator commands | Available | `text2ImageSubmit`, `image2ImageSubmit`, `imageUpscaleSubmit`, `text2VideoSubmit`, `image2VideoSubmit`, `frames2VideoSubmit`, `multiframe2VideoSubmit`, `multimodal2VideoSubmit` |
| Structured result objects | Available | `DreaminaCliResponse<T>` with `stdout` / `stderr` / `exitCode` / `body` / `json` |
| Startup readiness probe | Available | `DreaminaCliAvailabilityChecker.check(...)` -> `DreaminaCliAvailabilityReport` |
| Image compression helper | Available | `DreaminaImageCompressSupport` (thumbnailator based) |
| CLI contract tests (bidirectional) | Available | `cli-contract/dreamina-v1.4.14-help.snapshot.tsv` and `v1.4.15` snapshots |
| Mock-based unit tests | Available | Bash-mock CLI, no real `dreamina` binary required |
| Optional local audit tests | Available | Run against a real logged-in CLI; auto-skip when not logged in |
| Coverage gate | Enforced | JaCoCo: `DreaminaCliExecutor` 100% LINE + BRANCH, `haltOnFailure=true` |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 8 |
| Maven | 3.0+ |
| Local CLI | Official `dreamina` CLI (see Installation) |
| jackson-databind | 2.x (declared in pom) |
| commons-exec | Apache Commons Exec (declared in pom) |
| thumbnailator | Image compression support (declared in pom) |

### Version lines

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

### CLI compatibility (adaptation highlights)

The SDK tracks the upstream CLI contract; the CLI is the source of truth (`dreamina help` on your machine wins over any documentation). Highlights:

| Capability / model | Introduced in | Enum / field |
|:---|:---|:---|
| Seedream 5.0 Pro (flagship) | CLI v1.4.12 (2026-07-15) | `DreaminaImageModelVersion.MODEL_5_0_PRO` |
| seedance 2.0 mini | CLI v1.4.8 (2026-06-18) | `DreaminaVideoModelVersion.SEEDANCE_2_0_MINI` |
| Seedance 2.5 (480P/720P, 4~30 s) | CLI v1.4.15 (2026-08-01) | `DreaminaVideoModelVersion.SEEDANCE_2_5`, `RESOLUTION_480P` |
| Video 4K output | CLI v1.4.10 (2026-06-26) | `DreaminaVideoResolutionType.RESOLUTION_4K` (needs `seedance2.0_vip` + VIP account) |
| Custom image width/height `--width / --height` | CLI v1.4.14 (2026-07-21) | `DreaminaText2ImageRequest.width / height` |
| `--resolution_type` / `--video_resolution` required | CLI v1.4.14 | Typed request fields, defaults `2k` / `720p` |
| Batch generation `--generate_num` 1~10 | CLI v1.4.10 | `DreaminaText2ImageRequest.generateNum` |
| Full session CRUD | CLI v1.3.5 (2026-04-16) | `session create/list/search/rename/delete` |

## 4. Architecture & Modules

```text
   Your code                    dreamina-java-sdk               local machine
  -----------                  ----------------               --------------
  Request BOs  ->  DreaminaCliExecutor  -> Apache Commons Exec  -> dreamina CLI
  (opts/*)          subprocess, watchdog,                         (subcommand
                     timeout, exit-code mapping                   + flags)
                       |                                               |
                       v                                               v
                 parser/*  <-  stdout / stderr  <--------------------+
                 (JSON / text / table)
                       |
                       v
                 cli.model/*  ->  DreaminaCliResponse<T>
                 (typed result bodies)
```

Single module, jar packaging:

| Package | Responsibility |
|:---|:---|
| `io.github.easy4j.dreamina` | `DreaminaCliProperties` (runtime config) |
| `io.github.easy4j.dreamina.cli` | `DreaminaCliExecutor` (sole execution entry), `DreaminaCliResult` / `DreaminaCliResponse` |
| `io.github.easy4j.dreamina.cli.opts` | Strongly typed CLI parameters (requests, enums, validation) |
| `io.github.easy4j.dreamina.cli.model` | Structured parse bodies (version, submit, query, login, session, ...) |
| `io.github.easy4j.dreamina.cli.parser` | stdout parsing and payload mapping |
| `io.github.easy4j.dreamina.cli.availability` | Startup probe (`DreaminaCliAvailabilityChecker`) |
| `io.github.easy4j.dreamina.cli.support` | Subprocess execution support |
| `io.github.easy4j.dreamina.image` | Image compression helper (`DreaminaImageCompressSupport`) |
| `io.github.easy4j.dreamina.exception` | Typed exceptions (`DreaminaCliException` and subclasses) |

## 5. Installation

### Maven

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dreamina-java-sdk</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.easy4j:dreamina-java-sdk:3.0.x.x.20260630-SNAPSHOT'
```

**Availability:** the artifact is published to the Aliyun private Maven repository and distributed through GitHub Releases; it has not yet been published to Maven Central.

### Install the CLI

```bash
curl -fsSL https://jimeng.jianying.com/cli | bash
dreamina version
dreamina help
```

> The CLI is the source of truth: command names and flags on your machine take precedence over any documentation.

## 6. Quick Start

```java
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCliResponse;
import io.github.easy4j.dreamina.cli.model.DreaminaGenerateSubmit;

import java.util.Arrays;

DreaminaCliProperties properties = new DreaminaCliProperties();
properties.setExecutable("dreamina");
properties.setCommandTimeoutMillis(120_000L);

DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);

DreaminaCliResponse<DreaminaGenerateSubmit> submit =
        executor.text2ImageSubmit("a cat portrait", Arrays.asList("--ratio=1:1", "--poll=0"));

String submitId = submit.getBody().getSubmitId();   // submit_id from the CLI JSON output
```

Expected result: a `DreaminaGenerateSubmit` body containing the `submitId` returned by `dreamina text2image --poll=0`. The command is submitted asynchronously; poll later with `queryResultInfo(submitId)`.

## 7. Configuration

Configuration is held in a plain POJO, `DreaminaCliProperties` (Spring-free; Spring Boot applications can bind the same fields, e.g. via `@ConfigurationProperties(prefix = "dreamina.cli")` — see the class Javadoc):

| Property | Default | Description |
|:---|:---|:---|
| `executable` | `dreamina` | CLI executable name or absolute path |
| `workingDirectory` | — | Working directory for the child process |
| `commandTimeoutMillis` | `120000` | Timeout for a single CLI invocation (millis) |
| `maxConcurrentExecutions` | `0` | Max concurrent child processes; `<=0` uses `max(CPU cores, 2)` |
| `startupProbeTimeoutMillis` | `30000` | Timeout dedicated to the startup probe (`dreamina version`) |
| `defaultPollIntervalSeconds` | `5` | Suggested polling interval for the orchestration layer |

## 8. Core Usage / API

### 8.1 Startup readiness probe

```java
DreaminaCliAvailabilityChecker checker = new DreaminaCliAvailabilityChecker();
DreaminaCliAvailabilityReport report = checker.check(executor);
if (!report.isAvailable()) {
    throw new IllegalStateException(report.toDiagnosticMessage());
}
```

### 8.2 Command surface

Execution entry: `DreaminaCliExecutor`.

Built-in commands:

| CLI | Structured method | Raw method |
|:---|:---|:---|
| `help` | `helpInfo()` / `helpInfo(subcommand)` | `help()` |
| `version` | `versionInfo()` | `version()` |
| `user_credit` | `userCreditInfo()` | `userCredit()` |
| `login` / `logout` / `relogin` | `loginHeadlessInfo()` and friends | `login()` / `logout()` / `relogin()` |
| `session create/list/search/rename/delete` | `sessionCreateInfo()` and friends | `sessionCreate()` and friends |
| `list_task` | `listTaskInfo()` | `listTask()` |
| `query_result` | `queryResultInfo()` | `queryResult()` |

Generator commands (all return `DreaminaCliResponse<DreaminaGenerateSubmit>`):

| CLI | Structured method |
|:---|:---|
| `text2image` | `text2ImageSubmit(...)` |
| `image2image` | `image2ImageSubmit(...)` |
| `image_upscale` | `imageUpscaleSubmit(...)` |
| `text2video` | `text2VideoSubmit(...)` |
| `image2video` | `image2VideoSubmit(...)` |
| `frames2video` | `frames2VideoSubmit(...)` |
| `multiframe2video` | `multiframe2VideoSubmit(...)` |
| `multimodal2video` | `multimodal2VideoSubmit(...)` |

Generic escape hatch: `invoke(subcommand, additionalRawArgs)` or the `additionalRawArgs(...)` on any request object, for CLI flags not modeled by the SDK.

### 8.3 Login (OAuth Device Flow)

| CLI | SDK method |
|:---|:---|
| `dreamina login` | `login()` |
| `dreamina login --headless` | `loginHeadless()` / `loginHeadlessInfo()` |
| `dreamina login checklogin --device_code=... --poll=30` | `checkLogin(deviceCode, pollSeconds, ...)` |
| `dreamina logout` | `logout()` |
| `dreamina relogin` | `relogin()` |
| `dreamina user_credit` | `userCreditInfo()` |

Headless flow: `loginHeadlessInfo()` parses the `device_code`, then `checkLogin(...)` polls until authorization completes.

### 8.4 Recommended orchestration SOP

```text
1. CHECK   -> user_creditInfo()             # confirm login & quota (Query)
2. SUBMIT  -> *Submit(..., poll=0)          # async submit, get submit_id (Generate)
3. POLL    -> queryResultInfo(submitId)     # poll gen_status periodically (Post-query)
4. OPTIONAL-> listTaskInfo(gen_status=success)  # list audit (Query)
```

`--poll` semantics: with `--poll=N`, the CLI polls up to N seconds per second; it returns the result directly when finished, or `querying` on timeout — continue with `query_result`.

### 8.5 Task query example

```java
DreaminaQueryResultRequest query = DreaminaQueryResultRequest.builder()
        .submitId(submitId)
        .downloadDir("./downloads")
        .build();
executor.queryResultInfo(query);
```

### 8.6 Result model

- `DreaminaCliResult` — raw result (exit code, stdout/stderr)
- `DreaminaCliResponse<T>` — structured result: `stdout` / `stderr` / `exitCode` / `body` / `json` / `getCombinedText()`

Common `body` types (in `cli.model`): `DreaminaVersion`, `DreaminaQueryResult`, `DreaminaGenerateSubmit`, `DreaminaLogin`, `DreaminaSessionList`, ... All structured mapping is performed by `DreaminaCliStructuredPayloadMapper`; unknown extension fields remain available in the `json` (`JsonNode`) of the result.

## 9. Testing & Build

```bash
./mvnw test                        # bash-mock CLI tests; no real dreamina binary needed
./mvnw test jacoco:report          # coverage report: target/site/jacoco/index.html
./mvnw clean verify                # enforces 100% LINE + BRANCH on DreaminaCliExecutor (jacoco:check)
```

Optional real-CLI validation (requires an installed, logged-in CLI; auto-skips otherwise):

```bash
export DREAMINA_CLI_AUDIT=true
./mvnw -q test -Dtest=DreaminaCliLocalAuditTest -DskipTests=false
```

Local smoke entry (skip generation tasks to save credits with `DREAMINA_SMOKE_SKIP_GENERATE=true`):

```bash
./mvnw test-compile exec:java \
  -Dexec.mainClass=io.github.easy4j.dreamina.cli.DreaminaCliLocalSmokeMain \
  -Dexec.classpathScope=test
```

Additional assets: `docs/CLI_EXEC_CATALOG.md` (command catalog with real exec samples), `scripts/dreamina-cli-audit*.sh` (capture real stdout/stderr into `.cli-audit/`, gitignored, to align parsing and add tests).

### FAQ / local files

| Path | Description |
|:---|:---|
| `~/.dreamina_cli/config.toml` | Environment configuration |
| `~/.dreamina_cli/tasks.db` | Local task records |
| `~/.dreamina_cli/logs/` | Runtime logs |

Troubleshooting: first run `user_credit` to confirm login; on generation failures provide the full command, the error output and the logs directory content. To inspect subcommand flags use `dreamina <cmd> -h` (or `dreamina help <cmd>`); the CLI itself is the source of truth.

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

Maintenance strategy: the 1.0.x line stays aligned with the upstream `dreamina` CLI contract (contract tests guard the help-snapshot surface) while JDK 8 remains the baseline; feature work targets the 2.0.x / 3.0.x lines.

## 11. Contributing & License

Contributions are welcome — open an issue or submit a pull request against the matching version-line branch (`feature/3.0.x` for JDK 21 changes).

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See the `LICENSE` file in the repository root for details.
