# dreamina-java-sdk

纯 Java SDK（无 Spring 依赖），用于通过本地 `dreamina` CLI 调用即梦能力。适合在普通 Java 应用、命令行工具、批处理任务或其它框架中直接集成。

Spring Boot 应用请使用 [dreamina-spring-boot-starter](../dreamina-spring-boot-starter)。

官方 CLI 体验指南：[飞书 Wiki](https://bytedance.larkoffice.com/wiki/FVTwwm0bGiishxkKOoScdHR2nsg)（编排 SOP 与 FAQ 参考；**命令与 flag 以本机 `dreamina help` 为准**）。

## 功能概览

- 基于 Apache Commons Exec 执行本地 `dreamina` 命令
- 统一封装超时、非零退出码、可执行文件不可用等异常
- 支持官方 CLI 的内建命令与全部生成命令
- 在通用 `DreaminaCliResult` 基础上，提供结构化结果对象
- 提供本地 smoke 入口，便于在真实机器上快速自测

## 安装 CLI

```bash
curl -fsSL https://jimeng.jianying.com/cli | bash
dreamina version
dreamina help
```

## Maven 依赖

```xml
<dependency>
  <groupId>io.github.hiwepy</groupId>
  <artifactId>dreamina-java-sdk</artifactId>
  <version>1.0.x.20260515-SNAPSHOT</version>
</dependency>
```

## 快速开始

```java
import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.DreaminaCliProperties;
import io.github.hiwepy.dreamina.cli.DreaminaCliResponse;
import io.github.hiwepy.dreamina.cli.model.DreaminaGenerateSubmit;

DreaminaCliProperties properties = new DreaminaCliProperties();
properties.setExecutable("dreamina");
properties.setCommandTimeoutMillis(120_000L);

DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);

DreaminaCliResponse<DreaminaGenerateSubmit> submit =
        executor.text2ImageSubmit("a cat portrait", java.util.List.of("--ratio=1:1", "--poll=0"));

String submitId = submit.getBody().getSubmitId();
```

## 配置对象

核心配置类为 [`DreaminaCliProperties`](src/main/java/io/github/hiwepy/dreamina/DreaminaCliProperties.java)：

| 属性 | 说明 |
|------|------|
| `executable` | CLI 可执行文件名或绝对路径，默认 `dreamina` |
| `workingDirectory` | 执行工作目录，可选 |
| `commandTimeoutMillis` | 单次命令执行超时 |
| `startupProbeTimeoutMillis` | 启动探测专用超时（默认 30s），仅用于 `DreaminaCliAvailabilityChecker` |
| `defaultPollIntervalSeconds` | 业务层轮询建议间隔 |

## 启动就绪探测（无 Spring）

在注册 Bean 或接受流量前，可调用 `DreaminaCliAvailabilityChecker` 执行 `dreamina version`：

```java
DreaminaCliAvailabilityChecker checker = new DreaminaCliAvailabilityChecker();
DreaminaCliAvailabilityReport report = checker.check(executor);
if (!report.isAvailable()) {
    throw new IllegalStateException(report.toDiagnosticMessage());
}
```

Spring Boot 应用推荐使用 [dreamina-spring-boot-starter](../dreamina-spring-boot-starter) 的 `dreamina.cli.startup-check-enabled`（默认开启）。

## Agent 编排 SOP

推荐流程（与官方 Wiki 一致）：

```
1. CHECK   → user_creditInfo()           # 确认登录与额度（Query）
2. SUBMIT  → *Submit(..., poll=0)      # 异步提交，拿 submit_id（Generate）
3. POLL    → queryResultInfo(submitId)   # 周期查询 gen_status（Post-query）
4. OPTIONAL→ listTaskInfo(gen_status=success)  # 列表复核（Query）
```

命令分类与真实 exec 样例见 **[docs/CLI_EXEC_CATALOG.md](docs/CLI_EXEC_CATALOG.md)**（Query / Generate / Post-query 三阶段；Auth 仅 `-h` 文档）。

**`--poll` 语义**：提交命令带 `--poll=N` 时，CLI 每秒轮询最多 N 秒；完成则直出结果，超时则返回 `querying`，后续用 `query_result` 继续查。

## 登录与账号（OAuth Device Flow）

当前 CLI 使用 OAuth Device Flow（**已无 `login --debug`**）：

| CLI | SDK 方法 |
|-----|----------|
| `dreamina login` | `login()` |
| `dreamina login --headless` | `loginHeadless()` / `loginHeadlessInfo()` |
| `dreamina login checklogin --device_code=... --poll=30` | `checkLogin(deviceCode, pollSeconds, ...)` |
| `dreamina logout` | `logout()` |
| `dreamina relogin` | `relogin()` |
| `dreamina relogin --headless` | `relogin(List.of("--headless"))` |
| `dreamina user_credit` | `userCreditInfo()` |
| `dreamina version` | `versionInfo()` |

Headless 流程：`loginHeadlessInfo()` 解析 `device_code` → `checkLogin(...)` 轮询完成授权。

## 包结构

```
io.github.hiwepy.dreamina
├── DreaminaCliProperties          # 配置（无 Spring）
└── cli
    ├── DreaminaCliExecutor        # 唯一执行入口
    ├── DreaminaCliResult / DreaminaCliResponse
    ├── model/                     # 结构化解析体（JSON / 文本 / 表格）
    │   ├── DreaminaVersion
    │   ├── DreaminaGenerateSubmit
    │   ├── DreaminaQueryResult
    │   ├── DreaminaTaskItem
    │   └── …                      # login / session / help 等
    ├── opts/                      # 强类型 CLI 参数
    ├── parser/                    # stdout 解析与映射
    ├── availability/              # 启动探测
    └── support/                   # 子进程执行
```

## 命令总表

执行入口：[`DreaminaCliExecutor`](src/main/java/io/github/hiwepy/dreamina/cli/DreaminaCliExecutor.java)。

### Built-in Commands

| CLI | 结构化方法 | 原始方法 |
|-----|------------|----------|
| `help` | `helpInfo()` / `helpInfo(subcommand)` | `help()` |
| `version` | `versionInfo()` | `version()` |
| `user_credit` | `userCreditInfo()` | `userCredit()` |
| `login` / `logout` / `relogin` | `loginHeadlessInfo()` 等 | `login()` / `logout()` / `relogin()` |
| `session create/list/search/rename/delete` | `sessionCreateInfo()` 等 | `sessionCreate()` 等 |
| `list_task` | `listTaskInfo()` / `listTaskInfo(request)` | `listTask()` |
| `query_result` | `queryResultInfo()` / `queryResultInfo(request)` | `queryResult()` |

### Generator Commands

| CLI | 结构化方法 |
|-----|------------|
| `text2image` | `text2ImageSubmit(...)` |
| `image2image` | `image2ImageSubmit(...)` |
| `image_upscale` | `imageUpscaleSubmit(...)` |
| `text2video` | `text2VideoSubmit(...)` |
| `image2video` | `image2VideoSubmit(...)` |
| `frames2video` | `frames2VideoSubmit(...)` |
| `multiframe2video` | `multiframe2VideoSubmit(...)` |
| `multimodal2video` | `multimodal2VideoSubmit(...)` |

通用扩展：`invoke(subcommand, additionalRawArgs)` 或各 Request 的 `additionalRawArgs`。

## Session 工作区

| CLI | SDK |
|-----|-----|
| `dreamina session create [name]` | `sessionCreateInfo(...)` |
| `dreamina session list [-n N]` | `sessionListInfo()` / `sessionListInfo(List.of("-n=100"))` |
| `dreamina session search "keyword"` | `sessionSearchInfo(keyword)` |
| `dreamina session rename <id> <name>` | `sessionRenameInfo(id, name)` |
| `dreamina session delete <id>` | `sessionDelete(id)` |

所有生成命令支持 `--session=<id>`（默认 0 为默认对话；Session 0 不可 rename/delete）。

## 任务查询

```java
// 查询并下载
DreaminaQueryResultRequest query = DreaminaQueryResultRequest.builder()
    .submitId(submitId)
    .downloadDir("./downloads")
    .build();
executor.queryResultInfo(query);

// 列表筛选
DreaminaListTaskRequest list = DreaminaListTaskRequest.builder()
    .genStatus("success")
    .genTaskType("text2image")
    .limit(20)
    .offset(0)
    .build();
executor.listTaskInfo(list);
```

## 生成命令 flag 速查

> 完整说明请运行 `dreamina help <subcommand>`。

| 命令 | 关键 flag |
|------|-----------|
| `text2image` | `--prompt`, `--ratio`, `--resolution_type`, `--model_version`, `--generate_num`（1-10，v1.4.10+）, `--session`, `--poll` |
| `text2video` | `--prompt`, `--duration`, `--ratio`, `--video_resolution`, `--model_version`（仅 seedance 系列）, `--session`, `--poll` |
| `image2image` | `--images`, `--prompt`, `--ratio`, `--resolution_type`（2k/4k）, `--model_version`（4.0+）, `--generate_num`（1-10，v1.4.10+）, `--session`, `--poll` |
| `image_upscale` | `--image`, `--resolution_type`（2k/4k/8k）, `--session`, `--poll` |
| `image2video` | `--image`, `--prompt`, `--duration`, `--model_version`, `--video_resolution`, `--session`, `--poll` |
| `frames2video` | `--first`, `--last`, `--prompt`, `--duration`, `--model_version`, `--video_resolution`, `--session`, `--poll` |
| `multiframe2video` | `--images`, 2 图：`--prompt`+`--duration`；3+ 图：重复 `--transition-prompt` / `--transition-duration` |
| `multimodal2video` | 重复 `--image`/`--video`/`--audio`, `--prompt`, `--duration`, `--ratio`, `--model_version`, `--video_resolution`, `--session`, `--poll` |

**视频分辨率**：CLI 使用小写 `720p` / `1080p` / `4k`。`1080p` 与 `4k` 仅 `seedance2.0_vip` 可用，其中 `4k` 还需要 VIP 账户（v1.4.10+）。

## 适配即梦 CLI v1.4.12（2026-07-15）

本次对齐覆盖 v1.4.x 关键更新；Java SDK 的强类型默认值随之调整为最新能力，但底层仍兼容旧 CLI 输出。

| 能力 / 模型 | 引入版本 | 枚举 / 字段 | 默认值 |
|---|---|---|---|
| `Seedream 4.7` | v1.4.4（2026-06-03） | `DreaminaImageModelVersion.MODEL_4_7("4.7")` | — |
| `Seedream 5.0 Pro`（旗舰） | v1.4.12（2026-07-15） | `DreaminaImageModelVersion.MODEL_5_0_PRO("5.0 Pro")` | 文生图 `modelVersion` 默认 → `MODEL_5_0`；如需 Pro 可显式指定 |
| `seedance 2.0 mini` | v1.4.8（2026-06-18） | `DreaminaVideoModelVersion.SEEDANCE_2_0_MINI("seedance2.0mini")` | — |
| 视频 4K 输出 | v1.4.10（2026-06-26） | `DreaminaVideoResolutionType.RESOLUTION_4K("4k")` | 仅 `seedance2.0_vip` + VIP 账户 |
| 视频模型 3.x → Seedance 1.x | v1.4.10 | 枚举 `MODEL_3_x_*` 标 `@Deprecated`（保留旧值避免旧 CLI 客户端崩溃） | — |
| 批量出图 `--generate_num` 1-10 | v1.4.10 | `DreaminaText2ImageRequest.generateNum` / `DreaminaImage2ImageRequest.generateNum` | `null`（不传 → CLI 默认 1 张） |
| `login --debug` 已废弃 | v1.4.1（2026-04-17） | 不再向 `dreamina login -h` 输出 | — |
| Session 完整 CRUD | v1.3.5（2026-04-16） | `session create/list/ls/search/find/rename/update/delete/rm` | — |

## 适配即梦 CLI v1.4.14（2026-07-21）

v1.4.14 与 v1.4.15 同步对齐：完整的 flag 契约由双向契约测试
`src/test/resources/cli-contract/dreamina-v1.4.14-help.snapshot.tsv` 与
`src/test/resources/cli-contract/dreamina-v1.4.15-help.snapshot.tsv` 维护。

| 能力 / 模型 | 引入版本 | 枚举 / 字段 | 默认值 |
|---|---|---|---|
| 自定义图片宽高 `--width / --height` | v1.4.14 | `DreaminaText2ImageRequest.width / height`、`DreaminaImage2ImageRequest.width / height` | `null`（不传 → 使用 `--ratio`） |
| `--resolution_type` 必填 | v1.4.14 | `DreaminaText2ImageRequest.resolutionType`、`DreaminaImage2ImageRequest.resolutionType` | `2k` |
| `--video_resolution` 必填 | v1.4.14 | `DreaminaText2VideoRequest.videoResolution` / `Image2VideoRequest` / `Frames2VideoRequest` / `Multimodal2VideoRequest` | `720p` |
| `image2video --prompt` 必填 | v1.4.14 | `DreaminaImage2VideoRequest.prompt` | 必填 |
| `multiframe2video` 限定 720p/1080p | v1.4.14 | `DreaminaMultiframe2VideoRequest.videoResolution` | `720p` |
| Seedance 1.x 单图/首尾帧开放 | v1.4.14 | `SEEDANCE_1_0_FAST("seedance1.0fast")` / `SEEDANCE_1_5_PRO("seedance1.5pro")` | — |

## 适配即梦 CLI v1.4.15（2026-08-01）

v1.4.15 在 v1.4.14 基础上**新增 Seedance 2.5 视频模型**与**多模态纯音频输入**支持；SDK 通过新增
`DreaminaVideoModelVersion.SEEDANCE_2_5("seedance2.5")` 与 `DreaminaVideoResolutionType.RESOLUTION_480P("480p")`
完成适配。

| 能力 | 引入版本 | 枚举 / 字段 | 校验 / 备注 |
|---|---|---|---|
| Seedance 2.5 模型 | v1.4.15 | `DreaminaVideoModelVersion.SEEDANCE_2_5` | CLI 参数值 `seedance2.5`；不作为默认 modelVersion（保留旧 CLI 兼容） |
| Seedance 2.5 适用命令 | v1.4.15 | `supportsText2Video/supportsImage2Video/supportsFrames2Video/supportsMultimodal2Video()` | `multiframe2video` **不在** 2.5 支持范围 |
| Seedance 2.5 视频时长 | v1.4.15 | `minDurationSeconds()=4`、`maxDurationSeconds()=30` | 旧模型仍维持 15s 上限 |
| 480P 输出 | v1.4.15 | `DreaminaVideoResolutionType.RESOLUTION_480P` | 仅 Seedance 2.5；`validateVideoModelResolution` 已校验 |
| Seedance 2.5 支持 1080P | v1.4.15 | `RESOLUTION_1080P` | 仅 2.5 / `seedance2.0_vip` |
| 多模态纯音频输入 | v1.4.15 | `DreaminaMultimodal2VideoRequest.audios` 单字段 | 仅 Seedance 2.5；其它模型仍要求 image 或 video 之一 |
| 多模态 2~30 秒参考音视频 | v1.4.15 | `DreaminaMultimodal2VideoRequest.durationSeconds`（4~30） | `requireVideoDuration` 自动按模型选上下界 |

### 示例（Seedance 2.5 + 480P + 10s）

```java
DreaminaText2VideoRequest request = DreaminaText2VideoRequest.builder()
    .prompt("镜头缓慢推进")
    .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
    .videoResolution(DreaminaVideoResolutionType.RESOLUTION_480P)
    .durationSeconds(10)
    .pollSeconds(0)
    .build();

DreaminaCliResponse<DreaminaGenerateSubmit> submit =
    executor.text2VideoSubmit(request);
```

### 示例（多模态纯音频输入，仅 Seedance 2.5）

```java
DreaminaMultimodal2VideoRequest request = DreaminaMultimodal2VideoRequest.builder()
    .audio("/path/to/reference.mp3")
    .prompt("从音频生成口播视频")
    .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
    .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
    .durationSeconds(10)
    .build();

DreaminaCliResponse<DreaminaGenerateSubmit> submit =
    executor.multimodal2VideoSubmit(request);
```

> **以本机 `dreamina help` 为准**：CLI 是真相来源；枚举值仅做常用静态建模，未列出的 CLI flag 可通过每个 Request 的
> `additionalRawArgs(...)` 透传。

## 结果模型

1. **原始结果**：[`DreaminaCliResult`](src/main/java/io/github/hiwepy/dreamina/cli/DreaminaCliResult.java)
2. **结构化响应**：[`DreaminaCliResponse<T>`](src/main/java/io/github/hiwepy/dreamina/cli/DreaminaCliResponse.java) — 含 `stdout` / `stderr` / `exitCode` / `body` / `json` / `getCombinedText()`

常见 `body` 类型：`DreaminaVersion`、`DreaminaQueryResult`、`DreaminaGenerateSubmit`、`DreaminaLogin`、`DreaminaSessionList` 等（均在 `cli.model`）。

所有结构化映射由 [`DreaminaCliStructuredPayloadMapper`](src/main/java/io/github/hiwepy/dreamina/cli/parser/DreaminaCliStructuredPayloadMapper.java) 完成：

| 命令 | 输出形态 | body 类型 |
|------|----------|-----------|
| `version` | JSON | `DreaminaVersion` |
| `user_credit` | JSON | `DreaminaUserCredit` |
| `help` | 文本 | `DreaminaHelp`（全文见 `response.getCombinedText()`） |
| `login` | 文本（复用 OAuth + 账户行） | `DreaminaLogin` |
| `login --headless`（已登录） | 文本（仅复用提示） | `DreaminaLogin`（`isOAuthReuseOnly()`） |
| `login checklogin` | 空或 JSON | `DreaminaCheckLogin` 或 `body=null` |
| `logout` | 文本 | `DreaminaLogout`（`localSessionCleared`） |
| `relogin` | 文本键值对 Device Flow | `DreaminaRelogin` |
| `list_task` | JSON 数组 | `List<DreaminaTaskItem>` |
| `query_result` | JSON | `DreaminaQueryResult` |
| `text2image` 等生成命令 | JSON | `DreaminaGenerateSubmit` |
| `session list/search` | 表格文本 | `DreaminaSessionList` / `DreaminaSessionSearch` |
| `session create/rename/delete` | 文本 | `DreaminaSessionMutation` / `DreaminaSessionDelete` |

`query_result` 嵌套字段：`result_json` → `DreaminaQueryResultPayload`；`queue_info` → `DreaminaQueryQueueInfo`（`debug_info` 再解析为 `parsedDebugInfo`）。`list_task` 条目另含 `commerce_info` → `DreaminaCommerceInfoPayload`（`triplet` / `triplets` → `DreaminaCommerceTripletPayload`）。未知扩展字段仍保留在结果对象的 `json`（`JsonNode`）中。

## FAQ 与本地文件

| 路径 | 说明 |
|------|------|
| `~/.dreamina_cli/config.toml` | 环境配置 |
| `~/.dreamina_cli/tasks.db` | 本地任务记录 |
| `~/.dreamina_cli/logs/` | 运行日志 |

排障：先 `user_credit` 确认登录；生成失败时提供完整命令、报错与 logs 目录内容。

## 查看子命令参数（帮助）

| 推荐 | 示例 |
|------|------|
| `dreamina <cmd> -h` | `dreamina session list -h`、`dreamina text2image -h` |
| `dreamina help <cmd>` | `dreamina help session`（≈ `dreamina session -h`） |

生成类子命令请用 **`dreamina text2image -h`**（`dreamina text2image help` 可能无正文）。`dreamina session help` 在新版 CLI 上通常等价于 **`dreamina session -h`**（exit 0）。二级参数仍推荐 **`dreamina session list -h`** 或 SDK：`session(List.of("create", "-h"))`。


### 真实执行与输出形态（`.cli-audit/exec_*.txt`）

| 命令 | 典型形态 | 未登录 / 备注 |
|------|----------|----------------|
| `version` | JSON | exit 0 |
| `help` / `-h` | 英文 Usage 文本 | exit 0 |
| `user_credit` | JSON（`user_name` 可为 `""`） | 未登录：exit **1**，stdout/stderr **空** |
| `list_task` | JSON 数组 | 需登录；`--limit` / `--offset` / `--gen_status` |
| `query_result` | JSON 对象 | 未登录或无效 id：可能 exit **0** 且无输出体 |
| `session`（无子命令） | Usage 帮助 | exit **0**（等价 `session -h`） |
| `session list` / `ls` | 固定列宽表格 | 需登录 |
| `session search` | 精简三列表格 | 需登录 |
| `login` | 中文复用提示 + 账户 KV，或 Device Flow 材料 | 交互式 `login` 会阻塞 OAuth，优先本机手动登录 |
| `logout` | 中文一行 | 无登录态：`当前没有本地登录态。`（exit 0，非 cleared） |
| 生成类裸调用 | 常无输出 | exit 1；勿在无 `--dry-run` 时跑完整生成 |

采集（三阶段 `PHASE_QUERY` / `PHASE_GENERATE` / `PHASE_POST_QUERY`）：

```bash
./scripts/dreamina-cli-audit.sh                    # Agent/CI（Keychain 可能失败）
./scripts/dreamina-cli-audit-interactive.sh      # 本机 Terminal，已登录时跑完整 Phase B/C
```

详见 [docs/CLI_EXEC_CATALOG.md](docs/CLI_EXEC_CATALOG.md)。**脚本默认不执行** `logout` / `relogin` / `login`。登录后可用 `DREAMINA_AUDIT_SUBMIT_ID` 或脚本从 `list_task` JSON 取首条 `submit_id` 跑 `exec_query_result_real` / `exec_query_result_post`。

## 本地 CLI 全量采集

将本机 `dreamina` 的 stdout/stderr 写入 `.cli-audit/`（已加入 `.gitignore`），用于对齐解析与补测试：

```bash
cd dreamina-java-sdk
chmod +x scripts/dreamina-cli-audit.sh
./scripts/dreamina-cli-audit.sh
# 或指定二进制：DREAMINA_CLI_EXECUTABLE=/path/to/dreamina ./scripts/dreamina-cli-audit.sh
```

对已安装且**已登录**（`dreamina user_credit` 成功）的环境，可跑只读验收测试；未登录时测试会自动跳过：

```bash
export DREAMINA_CLI_AUDIT=true
mvn -q test -Dtest=DreaminaCliLocalAuditTest -DskipTests=false
# 本地审计不执行 logout / relogin / login，也不提供 DREAMINA_CLI_AUDIT_LOGIN。
```

## 本地自测

```bash
cd dreamina-java-sdk
mvn test-compile exec:java \
  -Dexec.mainClass=io.github.hiwepy.dreamina.cli.DreaminaCliLocalSmokeMain \
  -Dexec.classpathScope=test
```

跳过生成任务（省积分）：

```bash
DREAMINA_SMOKE_SKIP_GENERATE=true mvn test-compile exec:java \
  -Dexec.mainClass=io.github.hiwepy.dreamina.cli.DreaminaCliLocalSmokeMain \
  -Dexec.classpathScope=test
```

## 单元测试与覆盖率

Mapper 单元测试在 `src/test/resources/cli-audit/` 中嵌入了 2026-05 交互式审计（`dreamina-cli-audit-interactive.sh` + `dreamina-cli-audit-refresh-query.sh`）的代表性 JSON 片段，覆盖 `querying`/`success` 刷新、`text2image` 提交与 `list_task` multiframe 视频元数据；完整原始输出仍见本地 `.cli-audit/`（gitignore）。

```bash
cd dreamina-java-sdk
mvn test                              # bash mock CLI，不依赖真实 dreamina 二进制
mvn test jacoco:report                # 报告：target/site/jacoco/index.html
mvn clean verify                      # DreaminaCliExecutor LINE+BRANCH 100% 门禁（jacoco:check）
```

## 发布说明

```bash
mvn clean install -DskipTests
mvn -Prelease clean deploy   # 需 GPG 与 Central 凭据
```
