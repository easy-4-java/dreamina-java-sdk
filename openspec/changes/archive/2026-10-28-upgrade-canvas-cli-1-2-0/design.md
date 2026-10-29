## Context
Brownfield；沿用 OpenSpec 及现有 schema 生成器。
## Decisions
保留历史公共类和方法，新增类型与入口；使用官方 v1.2.0 schema 作为参数和字段事实源。逐路由测试参数、成功、失败响应；安装态执行全部 help、completion、schema 和无副作用 dry-run。Java 8/17/21 各用本线源码和依赖，不替换兼容契约。
## Risks
服务端真实任务与积分操作不在自动验证范围；不得把离线测试当作真实生成验收。
