# Dreamina Canvas CLI v1.2.0 SDK 适配

本次以 2026-10-28 安装的官方 public/cn v1.2.0 CLI 为事实源。完整 schema、版本与 71 份命令及分组帮助位于 `docs/validation/canvas-1.2.0/`；逐指令差异见 `contract-diff.json`，全部参数见 `DREAMINA_CANVAS_COMMANDS.md`。

## 新增入口

- `team ls`、`space current`、`space use`
- `canvas current`、`canvas use`
- `node upscale video`、`node interpolate video`、`node cutout image`
- `node promote video-sample`（Java 方法 `nodePromoteVideoSample`）

SDK 现有 49 条业务入口；旧指令新增的样片、导演台/时间轴、音色、账号权益、消费主体、任务恢复与响应字段随 schema 更新。具体对象继续使用 `DreaminaCanvas*Request` 和独立响应类，旧版类和方法保留。

## 本地校验与兼容性

空间选择校验 personal/team 及 canonical positive int64 团队 ID；媒体后处理校验必需参数、合法帧率/分辨率、1..20 唯一节点、对应批次 submit ID、积分参数互斥和 wait 参数关系。动态模型能力、服务端权益和资源状态仍由 CLI 服务端验证。不会自动登录、确认积分、提交重试或改写任务标识。

## 验证边界

全路由测试覆盖 schema/枚举/用例集一致性，98 项强类型参数与响应字段测试、99 项逐指令成功/失败/argv 测试；安装态执行版本、schema、全部帮助、四种 shell completion、图片创建及四种新增媒体指令的无副作用 dry-run。新增空间和媒体校验先确认测试失败，再补齐实现。

这些结果不代表真实团队空间切换、远端画布写入、导演台导出、生成、积分消耗或真实媒体质量验收。此轮未执行这些服务端写操作，也未触发远端 CI。升级后的真实业务使用可能需要重新登录。
