# Dreamina SDK 与 Starter 阿里云 Maven 发布结果

日期：2026-10-29。全部 13 个制品串行发布，远端 JAR/POM、SHA-1/SHA-256、字节码以及 13 个独立空缓存消费测试均通过。10 个 Starter 的空缓存中实际 SDK JAR 与对应 SDK 新发布制品逐一相同。

| 制品 | 逻辑版本 | 最新私服快照 | Java | 空缓存消费 |
|---|---|---|---:|---|
| dreamina-java-sdk | 1.0.x.20260830-SNAPSHOT | 1.0.x.20260830-20261029.034422-6 | 8 | 通过 |
| dreamina-java-sdk | 2.0.x.20260830-SNAPSHOT | 2.0.x.20260830-20261029.034648-5 | 17 | 通过 |
| dreamina-java-sdk | 3.0.x.20260830-SNAPSHOT | 3.0.x.20260830-20261029.034928-5 | 21 | 通过 |
| dreamina-spring-boot-starter | 2.3.x.20260830-SNAPSHOT | 2.3.x.20260830-20261029.035223-3 | 8 | 通过 |
| dreamina-spring-boot-starter | 2.7.x.20260830-SNAPSHOT | 2.7.x.20260830-20261029.035404-3 | 8 | 通过 |
| dreamina-spring-boot-starter | 3.0.x.20260830-SNAPSHOT | 3.0.x.20260830-20261029.035545-2 | 17 | 通过 |
| dreamina-spring-boot-starter | 3.1.x.20260830-SNAPSHOT | 3.1.x.20260830-20261029.035716-2 | 17 | 通过 |
| dreamina-spring-boot-starter | 3.2.x.20260830-SNAPSHOT | 3.2.x.20260830-20261029.035902-1 | 17 | 通过 |
| dreamina-spring-boot-starter | 3.3.x.20260830-SNAPSHOT | 3.3.x.20260830-20261029.040055-1 | 17 | 通过 |
| dreamina-spring-boot-starter | 3.4.x.20260830-SNAPSHOT | 3.4.x.20260830-20261029.040256-1 | 17 | 通过 |
| dreamina-spring-boot-starter | 3.5.x.20260830-SNAPSHOT | 3.5.x.20260830-20261029.040500-1 | 17 | 通过 |
| dreamina-spring-boot-starter | 4.0.x.20260830-SNAPSHOT | 4.0.x.20260830-20261029.041140-3 | 21 | 通过 |
| dreamina-spring-boot-starter | 4.1.x.20260830-SNAPSHOT | 4.1.x.20260830-20261029.041428-1 | 21 | 通过 |

本轮真实验收累计扣费 444 积分，最后两项为 10+1。多帧请求 720p，实际为 960×960、H.264、约 5.183 秒；图片超清实际 2160×2160 PNG。保留服务端实际输出差异。

团队场景：当前账户无团队，未通过真实团队空间验收。用户明确批准保留该受阻记录后发布，不将个人空间结果或 mock 作为团队通过证明。

首次 Boot 2.3 空缓存启动暴露 Jackson 2.11 与 SDK 的 API 链接不兼容；Java 8 SDK 已改为兼容接口并保留严格标量校验，针对性及完整回归后使用新时间戳快照更正 SDK 1.0.x 与 Starter 2.3.x。同时对三线 TokenBuffer 显式保留 BigDecimal，避免旧 Jackson 的原始文档小数精度丢失；所有 Starter 下游消费增加该精度与严格标量回归。原制品与失败日志保留。

本地 SDK 回归及 10 条 Starter 完整回归、发布阶段回归均通过；启用开关控制的付费/认证测试由独立真实验收日志证明，不把发布时的条件跳过视为真实验收。未提交、推送或切换分支，未创建 worktree；未声明远端 CI 通过。

发布记录：publication-final/results.json；每个制品子目录含 deploy.log、consumer.log、独立 empty-cache、远端 metadata 和已核对的制品。源码包及 SHA256SUMS 单独保留。

Starter 4.0.x 默认 Central 扩展在 Maven 4 下未更新指定私服；已将其移入显式 release profile，保留原工具链并完成原生测试、阿里云 metadata 和空缓存验证。

规格已同步并归档至 `openspec/changes/archive/2026-10-29-complete-dual-cli-contract-and-live-acceptance`。

规格已同步并归档至 `openspec/changes/archive/2026-10-29-complete-dual-cli-contract-and-live-acceptance`。
