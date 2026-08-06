# dreamina-java-sdk

[English](./README.md) | [简体中文](./README.zh-CN.md)

纯 Java SDK（无 Spring 依赖），通过本地 `dreamina` CLI 调用即梦能力。适合在普通 Java 应用、命令行工具、批处理任务或其它框架中直接集成。

## 目录

- [1. 项目概览](#1-项目概览)
- [2. 功能与状态](#2-features--status)
- [3. 环境要求与兼容性](#3-requirements--compatibility)
- [4. 架构与模块](#4-architecture--modules)
- [5. 安装](#5-installation)
- [6. 快速开始](#6-quick-start)
- [7. 配置](#7-configuration)
- [8. 核心用法 / API](#8-core-usage--api)
- [9. 测试与构建](#9-testing--build)
- [10. 版本线与分支](#10-versioning--branches)
- [11. 参与贡献与许可协议](#11-contributing--license)

## 1. 项目概览

`dreamina-java-sdk` 是面向 JDK 8 版本线、无 Spring 依赖的 SDK，驱动官方 Dreamina 命令行客户端（`dreamina` CLI）。它封装子进程执行、解析结构化输出（JSON / 文本 / 表格），并为 CLI 的全部内建命令与生成命令提供强类型请求与结果对象。

| 是什么 | 不是什么 |
|:---|:---|
| 本地 `dreamina` CLI 的类型化 Java 封装 | HTTP API 客户端（不提供公开 HTTP 端点抽象） |
| 子进程管理（超时、退出码、可用性） | UI 或 Web 服务 |
| JSON / 文本 / 表格输出的结构化解析 | 第三方云 SDK 的封装 |

典型使用场景：

| 场景 | 说明 |
|:---|:---|
| 文生图 / 图生图（`text2image`、`image2image`、`image_upscale`） | 类型化 `*Submit` 请求，支持 `--poll` 语义 |
| 视频生成（`text2video`、`image2video`、`frames2video`、`multiframe2video`、`multimodal2video`） | 模型与分辨率枚举、时长校验 |
| 任务查询与下载（`query_result`、`list_task`） | 轮询状态，可将结果下载到指定目录 |
| 会话工作区管理（`session create/list/search/rename/delete`） | 所有生成命令支持 `--session=<id>` |
| 登录与账户（`login`、`logout`、`relogin`、`user_credit`、`version`） | OAuth Device Flow、headless 登录 |
| 启动就绪探测 | `DreaminaCliAvailabilityChecker` 执行 `dreamina version` |

**项目状态：** 活跃开发；SDK 持续与上游 `dreamina` CLI 契约对齐（当前 v1.4.x）。

<a id="2-features--status"></a>
## 2. 功能与状态

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| 基于 Apache Commons Exec 的子进程执行 | 可用 | 可配置超时、工作目录、并发上限 |
| 类型化异常映射 | 可用 | 超时 / 非零退出码 / 可执行文件不可用 / 启动失败 |
| 内建命令（`help`、`version`、`user_credit`、登录与会话命令） | 可用 | `DreaminaCliExecutor` 方法，原始与结构化两种形态 |
| 全部生成命令 | 可用 | `text2ImageSubmit`、`image2ImageSubmit`、`imageUpscaleSubmit`、`text2VideoSubmit`、`image2VideoSubmit`、`frames2VideoSubmit`、`multiframe2VideoSubmit`、`multimodal2VideoSubmit` |
| 结构化结果对象 | 可用 | `DreaminaCliResponse<T>`，含 `stdout` / `stderr` / `exitCode` / `body` / `json` |
| 启动就绪探测 | 可用 | `DreaminaCliAvailabilityChecker.check(...)` -> `DreaminaCliAvailabilityReport` |
| 图片压缩工具 | 可用 | `DreaminaImageCompressSupport`（基于 thumbnailator） |
| CLI 契约测试（双向） | 可用 | `cli-contract/dreamina-v1.4.14-help.snapshot.tsv` 与 `v1.4.15` 快照 |
| Mock 单元测试 | 可用 | Bash-mock CLI，无需真实 `dreamina` 二进制 |
| 可选本地审计测试 | 可用 | 针对真实已登录 CLI 运行；未登录时自动跳过 |
| 覆盖率门禁 | 强制执行 | JaCoCo：`DreaminaCliExecutor` 100% LINE + BRANCH，`haltOnFailure=true` |

<a id="3-requirements--compatibility"></a>
## 3. 环境要求与兼容性

| 依赖项 | 版本 |
|:---|:---|
| JDK | 8 |
| Maven | 3.0+ |
| 本地 CLI | 官方 `dreamina` CLI（见安装章节） |
| jackson-databind | 2.x（pom 声明） |
| commons-exec | Apache Commons Exec（pom 声明） |
| thumbnailator | 图片压缩支持（pom 声明） |

### 版本线矩阵

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

### CLI 兼容性（适配要点）

SDK 跟踪上游 CLI 契约；CLI 是真相来源（以本机 `dreamina help` 输出为准）。要点如下：

| 能力 / 模型 | 引入版本 | 枚举 / 字段 |
|:---|:---|:---|
| Seedream 5.0 Pro（旗舰） | CLI v1.4.12（2026-07-15） | `DreaminaImageModelVersion.MODEL_5_0_PRO` |
| seedance 2.0 mini | CLI v1.4.8（2026-06-18） | `DreaminaVideoModelVersion.SEEDANCE_2_0_MINI` |
| Seedance 2.5（480P/720P，4～30 秒） | CLI v1.4.15（2026-08-01） | `DreaminaVideoModelVersion.SEEDANCE_2_5`、`RESOLUTION_480P` |
| 视频 4K 输出 | CLI v1.4.10（2026-06-26） | `DreaminaVideoResolutionType.RESOLUTION_4K`（需 `seedance2.0_vip` + VIP 账户） |
| 自定义图片宽高 `--width / --height` | CLI v1.4.14（2026-07-21） | `DreaminaText2ImageRequest.width / height` |
| `--resolution_type` / `--video_resolution` 必填 | CLI v1.4.14 | 类型化请求字段，默认 `2k` / `720p` |
| 批量出图 `--generate_num` 1～10 | CLI v1.4.10 | `DreaminaText2ImageRequest.generateNum` |
| Session 完整 CRUD | CLI v1.3.5（2026-04-16） | `session create/list/search/rename/delete` |

<a id="4-architecture--modules"></a>
## 4. 架构与模块

```text
  业务代码                    dreamina-java-sdk                 本地机器
  --------                  ----------------                  --------
  请求 BO   ->  DreaminaCliExecutor  -> Apache Commons Exec  -> dreamina CLI
  (opts/*)     子进程、watchdog、                             （子命令
                超时、退出码映射                                  + flags）
                       |                                               |
                       v                                               v
                 parser/*  <-  stdout / stderr  <--------------------+
                 （JSON / 文本 / 表格）
                       |
                       v
                 cli.model/*  ->  DreaminaCliResponse<T>
                 （类型化结果体）
```

单一模块，jar 打包：

| 包 | 职责 |
|:---|:---|
| `io.github.easy4j.dreamina` | `DreaminaCliProperties`（运行时配置） |
| `io.github.easy4j.dreamina.cli` | `DreaminaCliExecutor`（唯一执行入口）、`DreaminaCliResult` / `DreaminaCliResponse` |
| `io.github.easy4j.dreamina.cli.opts` | 强类型 CLI 参数（请求、枚举、校验） |
| `io.github.easy4j.dreamina.cli.model` | 结构化解析体（version、submit、query、login、session 等） |
| `io.github.easy4j.dreamina.cli.parser` | stdout 解析与载荷映射 |
| `io.github.easy4j.dreamina.cli.availability` | 启动探测（`DreaminaCliAvailabilityChecker`） |
| `io.github.easy4j.dreamina.cli.support` | 子进程执行支持 |
| `io.github.easy4j.dreamina.image` | 图片压缩工具（`DreaminaImageCompressSupport`） |
| `io.github.easy4j.dreamina.exception` | 类型化异常（`DreaminaCliException` 及其子类） |

<a id="5-installation"></a>
## 5. 安装

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

**可用性：** 构件发布至阿里云私有 Maven 仓库，并通过 GitHub Releases 分发；尚未发布到 Maven Central。

### 安装 CLI

```bash
curl -fsSL https://jimeng.jianying.com/cli | bash
dreamina version
dreamina help
```

> CLI 是真相来源：以本机 `dreamina` 的命令与 flag 为准，优先于任何文档。

<a id="6-quick-start"></a>
## 6. 快速开始

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

String submitId = submit.getBody().getSubmitId();   // CLI JSON 输出中的 submit_id
```

预期结果：得到包含 `submitId` 的 `DreaminaGenerateSubmit` 响应体（来自 `dreamina text2image --poll=0`）。命令为异步提交；之后用 `queryResultInfo(submitId)` 轮询。

<a id="7-configuration"></a>
## 7. 配置

配置保存在纯 POJO `DreaminaCliProperties` 中（无 Spring 依赖；Spring Boot 应用可通过 `@ConfigurationProperties(prefix = "dreamina.cli")` 等方式绑定同一组字段，参见类 Javadoc）：

| 属性 | 默认值 | 说明 |
|:---|:---|:---|
| `executable` | `dreamina` | CLI 可执行文件名或绝对路径 |
| `workingDirectory` | — | 子进程工作目录 |
| `commandTimeoutMillis` | `120000` | 单次 CLI 调用超时（毫秒） |
| `maxConcurrentExecutions` | `0` | 子进程最大并发数；小于等于 0 时使用 `max(CPU 核心数, 2)` |
| `startupProbeTimeoutMillis` | `30000` | 启动探测（`dreamina version`）专用超时（毫秒） |
| `defaultPollIntervalSeconds` | `5` | 编排层建议轮询间隔（秒） |

<a id="8-core-usage--api"></a>
## 8. 核心用法 / API

### 8.1 启动就绪探测

```java
DreaminaCliAvailabilityChecker checker = new DreaminaCliAvailabilityChecker();
DreaminaCliAvailabilityReport report = checker.check(executor);
if (!report.isAvailable()) {
    throw new IllegalStateException(report.toDiagnosticMessage());
}
```

### 8.2 命令面

执行入口：`DreaminaCliExecutor`。

内建命令：

| CLI | 结构化方法 | 原始方法 |
|:---|:---|:---|
| `help` | `helpInfo()` / `helpInfo(subcommand)` | `help()` |
| `version` | `versionInfo()` | `version()` |
| `user_credit` | `userCreditInfo()` | `userCredit()` |
| `login` / `logout` / `relogin` | `loginHeadlessInfo()` 等 | `login()` / `logout()` / `relogin()` |
| `session create/list/search/rename/delete` | `sessionCreateInfo()` 等 | `sessionCreate()` 等 |
| `list_task` | `listTaskInfo()` | `listTask()` |
| `query_result` | `queryResultInfo()` | `queryResult()` |

生成命令（均返回 `DreaminaCliResponse<DreaminaGenerateSubmit>`）：

| CLI | 结构化方法 |
|:---|:---|
| `text2image` | `text2ImageSubmit(...)` |
| `image2image` | `image2ImageSubmit(...)` |
| `image_upscale` | `imageUpscaleSubmit(...)` |
| `text2video` | `text2VideoSubmit(...)` |
| `image2video` | `image2VideoSubmit(...)` |
| `frames2video` | `frames2VideoSubmit(...)` |
| `multiframe2video` | `multiframe2VideoSubmit(...)` |
| `multimodal2video` | `multimodal2VideoSubmit(...)` |

通用扩展：`invoke(subcommand, additionalRawArgs)`，或任意 Request 的 `additionalRawArgs(...)`，用于透传 SDK 未建模的 CLI flag。

### 8.3 登录与账号（OAuth Device Flow）

| CLI | SDK 方法 |
|:---|:---|
| `dreamina login` | `login()` |
| `dreamina login --headless` | `loginHeadless()` / `loginHeadlessInfo()` |
| `dreamina login checklogin --device_code=... --poll=30` | `checkLogin(deviceCode, pollSeconds, ...)` |
| `dreamina logout` | `logout()` |
| `dreamina relogin` | `relogin()` |
| `dreamina user_credit` | `userCreditInfo()` |

Headless 流程：`loginHeadlessInfo()` 解析出 `device_code`，再由 `checkLogin(...)` 轮询完成授权。

### 8.4 推荐编排 SOP

```text
1. CHECK   -> user_creditInfo()              # 确认登录与额度（Query）
2. SUBMIT  -> *Submit(..., poll=0)           # 异步提交，拿 submit_id（Generate）
3. POLL    -> queryResultInfo(submitId)      # 周期查询 gen_status（Post-query）
4. OPTIONAL-> listTaskInfo(gen_status=success)  # 列表复核（Query）
```

`--poll` 语义：提交命令带 `--poll=N` 时，CLI 每秒轮询最多 N 秒；完成则直出结果，超时则返回 `querying`，后续用 `query_result` 继续查。

### 8.5 任务查询示例

```java
DreaminaQueryResultRequest query = DreaminaQueryResultRequest.builder()
        .submitId(submitId)
        .downloadDir("./downloads")
        .build();
executor.queryResultInfo(query);
```

### 8.6 结果模型

- `DreaminaCliResult` — 原始结果（退出码、stdout/stderr）
- `DreaminaCliResponse<T>` — 结构化结果：`stdout` / `stderr` / `exitCode` / `body` / `json` / `getCombinedText()`

常见 `body` 类型（位于 `cli.model`）：`DreaminaVersion`、`DreaminaQueryResult`、`DreaminaGenerateSubmit`、`DreaminaLogin`、`DreaminaSessionList` 等。全部结构化映射由 `DreaminaCliStructuredPayloadMapper` 完成；未知扩展字段保留在结果的 `json`（`JsonNode`）中。

<a id="9-testing--build"></a>
## 9. 测试与构建

```bash
./mvnw test                        # bash-mock CLI 测试，不依赖真实 dreamina 二进制
./mvnw test jacoco:report          # 覆盖率报告：target/site/jacoco/index.html
./mvnw clean verify                # 门禁：DreaminaCliExecutor 100% LINE + BRANCH（jacoco:check）
```

可选的真实 CLI 验证（需已安装并登录的 CLI；未登录时自动跳过）：

```bash
export DREAMINA_CLI_AUDIT=true
./mvnw -q test -Dtest=DreaminaCliLocalAuditTest -DskipTests=false
```

本地 smoke 入口（跳过生成任务省积分：`DREAMINA_SMOKE_SKIP_GENERATE=true`）：

```bash
./mvnw test-compile exec:java \
  -Dexec.mainClass=io.github.easy4j.dreamina.cli.DreaminaCliLocalSmokeMain \
  -Dexec.classpathScope=test
```

其它资产：`docs/CLI_EXEC_CATALOG.md`（命令目录与真实执行样例）、`scripts/dreamina-cli-audit*.sh`（将真实 stdout/stderr 采集到 `.cli-audit/`（gitignore），用于对齐解析与补测试）。

### 常见问题与本地文件

| 路径 | 说明 |
|:---|:---|
| `~/.dreamina_cli/config.toml` | 环境配置 |
| `~/.dreamina_cli/tasks.db` | 本地任务记录 |
| `~/.dreamina_cli/logs/` | 运行日志 |

排障：先 `user_credit` 确认登录；生成失败时提供完整命令、报错与 logs 目录内容。查看子命令参数用 `dreamina <cmd> -h`（或 `dreamina help <cmd>`）；CLI 本身是真相来源。

<a id="10-versioning--branches"></a>
## 10. 版本线与分支

仓库维护三条并行版本线：

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

维护策略：在 JDK 8 作为基线的同时，1.0.x 版本线持续与上游 `dreamina` CLI 契约对齐（契约测试守护 help 快照面）；新功能开发面向 2.0.x / 3.0.x 版本线。

<a id="11-contributing--license"></a>
## 11. 参与贡献与许可协议

欢迎参与贡献——请通过 Issue 反馈问题，或向对应版本线分支提交 Pull Request（JDK 21 相关改动提交到 `feature/3.0.x`）。

本项目基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可发布。详见仓库根目录的 `LICENSE` 文件。
