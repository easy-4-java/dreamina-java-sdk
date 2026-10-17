## Why

用户提供的官方公告说明旧 Dreamina CLI 将于 11 月停止维护。当前 SDK 只支持旧命令，无法表达 Canvas 的节点、报价、确认和可恢复操作协议。

## What Changes

- 保留旧 API 和执行行为，为旧 CLI 专用公开类型增加 `@Deprecated` 与迁移说明。
- 在 `cli` 下新增 `DreaminaCanvasCli` 入口及 `cli.canvas` 支撑类型，保持 Java 8。
- 基于已安装 1.0.1 / 09307bb 的 schema 和所有命令帮助建立版本化命令目录，覆盖全部业务命令以及 help/completion。
- 提供参数构建与校验、独立运行配置、JSON envelope 与结构化错误解析、文本命令、超时与中断处理。
- 不自动重试、批准积分、登录、创建画布或提交生成；调用者显式选择操作和稳定身份。

## Capabilities

### New Capabilities

- `canvas-cli-integration`: Canvas 命令、参数、进程执行、结构化响应、恢复信息和验证。
- `legacy-cli-deprecation`: 旧接口的源码级废弃通知和行为兼容。

### Modified Capabilities

无。

## Impact

新增 Java 8 API、CLI schema 资源、单元与可选真实 CLI 契约测试、迁移文档；新增 Commons Lang StringUtils
依赖以遵守项目字符串工具规范。旧子进程实现和旧 API 签名保持不变。真实付费生成、远程发布、其他分支同步不在本次范围。
