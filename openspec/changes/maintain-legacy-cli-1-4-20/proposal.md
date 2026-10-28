## Why
用户明确要求旧 dreamina CLI 继续完整维护，撤销此前 Canvas 迁移导致的废弃策略。官方指南含 v1.4.18 视频比例更新，实际官方分发为 v1.4.21。
## What Changes
采集真实 CLI 全部帮助；撤销迁移新增的类型/成员废弃标记；保持历史失效参数与内部迁移标记；补齐新增参数与契约测试；保留 Canvas 支持。
## Capabilities
### Modified Capabilities
- `legacy-cli-deprecation`: 双 CLI 持续维护，旧 API 保持可用。
## Impact
旧 CLI 请求、类型元数据、复用清单、文档和测试。
