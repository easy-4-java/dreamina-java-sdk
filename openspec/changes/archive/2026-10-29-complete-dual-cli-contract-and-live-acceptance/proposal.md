## Why
两套 CLI 已接入，但 Java 校验、恢复诊断和逐指令真实验收仍有缺口。本变更执行已批准的完整整合计划。
## What Changes
维护逐指令矩阵；补齐静态可判定规则、恢复字段和测试；使用当前账户及可用团队逐批进行真实验收；收费批次先报价批准，全部完成后才发布三条快照。
## Capabilities
### Modified Capabilities
- `canvas-cli-integration`: 完整静态契约与恢复信息验证。
## Impact
保留两套 API 和既有兼容；不改分支、不建 worktree、不降低门禁；不自动重提任务。
