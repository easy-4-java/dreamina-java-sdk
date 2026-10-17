## Why

用户要求新入口按执行器职责命名，并纠正上一轮对所有旧类型批量废弃的问题。同时要求逐条指令覆盖，不能把帮助或 mock
成功等同于真实业务执行。

## What Changes

- 未发布的新入口直接改名为 DreaminaCanvasCliExecutor，更新调用与文档，不保留多余别名。
- 本轮迁移引入的 Deprecated 只保留旧 DreaminaCliExecutor，移除辅助类型上的新增批量标记。
- 增加 schema 枚举驱动的逐命令成功/失败及 argv 断言；执行真实 CLI 覆盖并报告成功、预期错误、本地 dry-run、受阻项目。

## Capabilities

### Modified Capabilities

- canvas-cli-integration: 执行器命名与可审计的指令覆盖。
- legacy-cli-deprecation: 收窄废弃边界。

## Impact

仅当前 Java 8 分支与当前未发布工作；保留既有改动与历史规格。认证破坏性测试使用隔离 profile，不退出主账号；积分批准不得由覆盖率要求隐式授权。
