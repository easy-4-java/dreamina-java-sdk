## Why
CLI v1.2.0 已发布，SDK v1.0.3 契约缺少空间、团队、后处理及样片转正指令和新增字段。
## What Changes
采集全部 schema/help；保留已有 Java API，增量补齐 49 条指令的强类型请求、响应、执行入口和本地校验；三条 Java 版本线测试并发布 20260830-SNAPSHOT。
## Capabilities
### Modified Capabilities
- `canvas-cli-integration`: 完整支持 v1.2.0 契约。
## Impact
CLI/Skill 升级；SDK 契约、生成器、测试、文档与私服快照更新。不自动执行登录、积分确认或服务端写操作。
