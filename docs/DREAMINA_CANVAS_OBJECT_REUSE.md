# Dreamina 对象复用审计

本轮以 `typed-canvas-command-api` 为规格事实源。原有 67 个顶层类型中，29 个保留共享或兼容职责，38
个限定于旧协议并标注废弃；所有原目录与旧公共签名保留。废弃不等于删除，也不代表相似业务概念永远不能适配。

本轮实际扩展复用 `DreaminaVersion`、`DreaminaLoginAccount`、`DreaminaDeviceLogin`、`DreaminaLogout`。退出登录对象撤销类级废弃标记，用别名适配
`loggedOut`，原属性及 builder 保留。配置、原始进程结果、参数接口、异常、可用性报告及比例/分辨率枚举继续复用。

旧请求的 `toCliArgs()` 包含旧 `model_version`、`session_id` 或本地文件参数，不能作为 Canvas 的 node/project/resource
请求直接执行。因此保留兼容实现，为每条新指令建立具名请求；需要上传、选模型和项目上下文的迁移步骤不能靠复制字段自动完成。

`DreaminaCliResponse<T>` 的历史 JSON 树接口保留给旧 CLI；Canvas 使用 `DreaminaCanvasResponse<T>`，继续共享底层
`DreaminaCliResult`，避免从新 API 间接暴露 JSON 树。新类型均以 Dreamina 开头，不建立替代目录树。

完整逐对象清单如下，路径和机器可检查状态见 [JSON 清单](validation/dreamina-object-reuse.json)
。新增请求与响应的指令映射见 [类型清单](validation/dreamina-canvas-types.json)。

| 对象                                            | 决定                    | 依据                                                                                                                                                   |
|-------------------------------------------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DreaminaCanvasCliProperties`                   | 新增具体类型            | 仅新增 profile/region/maxOutputBytes，继承通用配置。                                                                                                   |
| `DreaminaCliProperties`                         | 保留 / 复用             | 新配置继承原 executable/workDir/timeout/concurrency 字段，未复制定义。                                                                                 |
| `DreaminaCanvasCliExecutor`                     | 新增具体类型            | Canvas 路由入口；旧入口的业务方法/默认值不可改写。                                                                                                     |
| `DreaminaCanvasCommand`                         | 新增具体类型            | 37 条 Canvas 命令身份，旧下划线路由不能复用。                                                                                                          |
| `DreaminaCanvasResponse`                        | 新增具体类型            | 独立的强类型 envelope，底层复用 DreaminaCliResult；旧 DreaminaCliResponse 含历史 JSON 树，故不暴露给 Canvas API。                                      |
| `DreaminaCliExecutor`                           | 旧协议限定 / Deprecated | 旧 dreamina 命令执行入口；替代：DreaminaCanvasCliExecutor。                                                                                            |
| `DreaminaCliResponse`                           | 保留 / 复用             | 保留旧协议兼容响应；Canvas 使用新强类型 envelope，底层继续共享 DreaminaCliResult。                                                                     |
| `DreaminaCliResult`                             | 保留 / 复用             | 新进程执行、帮助/补全、异常诊断均直接使用原结果类型。                                                                                                  |
| `DreaminaCliSubcommands`                        | 旧协议限定 / Deprecated | 旧 login/session/text2image/query_result 路由常量与 Canvas 路由不兼容；替代：DreaminaCanvasCommand。                                                   |
| `DreaminaCliAvailabilityChecker`                | 保留 / 复用             | 增加 checkCanvas 复用路径解析、报告和状态；旧 check 重载方法单独废弃。                                                                                 |
| `DreaminaCliAvailabilityReport`                 | 保留 / 复用             | checkCanvas 直接返回原报告。                                                                                                                           |
| `DreaminaCliAvailabilityStatus`                 | 保留 / 复用             | checkCanvas 直接使用原可用性分类。                                                                                                                     |
| `DreaminaCanvasApproval`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasArgument`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthLoginResult`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthRefreshResult`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthStatusResult`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthWaitResult`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAwaitingConfirmation`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasBinding`                         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasClip`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasConstraint`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCreateResult`                    | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCreateResultProject`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCreditConfirmation`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasEffectiveGeneration`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasError`                           | 新增具体类型            | CLI envelope 的 code/class/message/retryable/requiredAction/validation；不再使用 JSON 树。                                                             |
| `DreaminaCanvasErrorDetails`                    | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasField`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasFlag`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasGeneration`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasGenerationDiff`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasGenerationParam`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasInitialPosition`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasItem`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasLayout`                          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasLimit`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasLocalStorageWarning`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasLsResult`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasMatche`                          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasMaterial`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasMeta`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasMode`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModeReference`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModeReferenceType`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModel`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelFindResult`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelItem`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelListResult`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelListResultItem`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelResult`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasMutation`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNode`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeConfirmResult`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateAudioResult`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateElementResult`         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateImageResult`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateTextResult`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateTimelineResult`        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateVideoResult`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditAudioResult`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditElementResult`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditImageResult`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditTextResult`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditTimelineResult`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditVideoResult`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeFindResult`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeFindResultItem`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeGeneration`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeGenerationReference`         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeQuoteResult`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeQuoteResultItem`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeResource`                    | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeRunResult`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeRunResultItem`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeShowResult`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeShowResultNode`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeUpscaleImageResult`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeUpscaleImageResultItem`      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeUpscaleImageResultItemError` | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasOperationStatusResult`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasOperationStatusResultSubmission` | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasOperationWaitResult`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasParam`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasParameterRule`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPartialData`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPlan`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPlanGeneration`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPlanSubmission`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPostEdit`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasProject`                         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPromptPart`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasPropertie`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasQuote`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasReference`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasReferenceRule`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResource`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResourceDownloadResult`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResourceGetResult`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResourceUploadResult`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSchema`                          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSchemaCommand`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSchemaField`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSchemaFlag`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasServiceError`                    | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSourceRange`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasState`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSubjectBinding`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSubmission`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSubmissionError`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSubmissionResource`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSuccessData`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTimeline`                        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTimelineTrack`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTimelineTrackValue`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTimelineTrackValueClip`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTotalCount`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasTrack`                           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasType`                            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasValidation`                      | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasVipConfig`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasVoiceListResult`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasWritebackBlocker`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCheckLogin`                            | 旧协议限定 / Deprecated | 旧 gen_status/message 登录轮询体不同于 auth wait envelope；替代：AUTH_WAIT 的 data/error。                                                             |
| `DreaminaCommerceInfo`                          | 旧协议限定 / Deprecated | 旧 list_task commerce_info，不等同 Canvas node quote 报价；替代：NODE_QUOTE data。                                                                     |
| `DreaminaCommerceTriplet`                       | 旧协议限定 / Deprecated | 旧计费 triplet 结构，Canvas quote 不返回该协议；替代：NODE_QUOTE data。                                                                                |
| `DreaminaDeviceLogin`                           | 保留 / 复用             | 复用原设备授权对象，补齐 verificationUriComplete/pollSeconds，保留旧字段别名。                                                                         |
| `DreaminaGenerateSubmit`                        | 旧协议限定 / Deprecated | 旧 gen_status/queue_info 提交体与 Canvas operations/partialData 不同；替代：NODE_RUN 响应。                                                            |
| `DreaminaGenerationStatus`                      | 旧协议限定 / Deprecated | 旧 querying/success/fail 状态集合不能无损表示 Canvas operation 状态；替代：OPERATION_STATUS data.state。                                               |
| `DreaminaHelp`                                  | 保留 / 复用             | helpDetails 复用原帮助主题对象与 mapHelp。                                                                                                             |
| `DreaminaLogin`                                 | 旧协议限定 / Deprecated | 旧文本解析 oauthSessionReused/account/device 聚合；替代：AUTH_LOGIN envelope，challenge 可复用 DreaminaDeviceLogin。                                   |
| `DreaminaLoginAccount`                          | 保留 / 复用             | 复用账号 userId/vipLevel 并新增 isVip；不臆造未返回的积分。                                                                                            |
| `DreaminaLogout`                                | 保留 / 复用             | 复用凭据清除语义；增加 loggedOut 映射，保留 localSessionCleared getter 和 builder。                                                                    |
| `DreaminaQueryImage`                            | 旧协议限定 / Deprecated | 旧 result_json.images[].image_url 结构；Canvas 用 resourceId 与资源查询，不能靠字段映射自动完成下载；替代：RESOURCE_GET/DOWNLOAD。                     |
| `DreaminaQueryQueueDebugInfo`                   | 旧协议限定 / Deprecated | 旧 queue_info.debug_info 内嵌结构；Canvas 无该协议，使用 operation/meta 原始数据。                                                                     |
| `DreaminaQueryQueueInfo`                        | 旧协议限定 / Deprecated | 旧 queue_info 排队字段与 Canvas operation 结构不同；替代：OPERATION_STATUS data。                                                                      |
| `DreaminaQueryResult`                           | 旧协议限定 / Deprecated | 旧 query_result 的 gen_status/result_json 与 Canvas operation/resource 分离协议不同；替代：OPERATION_STATUS 与 RESOURCE_GET。                          |
| `DreaminaQueryVideo`                            | 旧协议限定 / Deprecated | 旧 result_json.videos[].video_url/cover_url 结构与 Canvas 资源协议不同；替代：RESOURCE_GET/DOWNLOAD。                                                  |
| `DreaminaQueueInfoSupport`                      | 旧协议限定 / Deprecated | 仅解析旧 queue_info.debug_info；替代：Canvas operation/meta 结构，无需沿用旧嵌套字符串解析。                                                           |
| `DreaminaRelogin`                               | 旧协议限定 / Deprecated | 旧 relogin 文本 requiresBrowserOAuth/device 聚合；替代：AUTH_LOGIN/AUTH_REFRESH。                                                                      |
| `DreaminaResultJson`                            | 旧协议限定 / Deprecated | 旧 images/videos 输出集合，Canvas 返回资源身份和独立查询；替代：operation resources 与 RESOURCE_GET。                                                  |
| `DreaminaSessionDelete`                         | 旧协议限定 / Deprecated | 旧 session delete，Canvas 1.0.1 未提供画布删除；无等价命令，不伪造替代。                                                                               |
| `DreaminaSessionList`                           | 旧协议限定 / Deprecated | 旧数字 session 表格，不是 Canvas UUID 画布列表；替代：CANVAS_LS data.items。                                                                           |
| `DreaminaSessionMutation`                       | 旧协议限定 / Deprecated | 旧 session create/rename；创建改用 CANVAS_CREATE，当前无画布重命名命令。                                                                               |
| `DreaminaSessionRow`                            | 旧协议限定 / Deprecated | 旧 session id/pinned/updated 表格行与 Canvas projectId 结构不同；替代：CANVAS_LS data.items。                                                          |
| `DreaminaSessionSearch`                         | 旧协议限定 / Deprecated | 旧 session search，当前无等价远端画布搜索；可在 CANVAS_LS 结果中由调用者筛选。                                                                         |
| `DreaminaTaskItem`                              | 旧协议限定 / Deprecated | 旧 list_task 任务/计费汇总，Canvas 无同形列表；替代：已知 submitId 的 OPERATION_STATUS。                                                               |
| `DreaminaUserCredit`                            | 旧协议限定 / Deprecated | 旧 user_credit 余额字段，Canvas auth account 不返回积分余额；报价使用 NODE_QUOTE，不能拿会员状态充当余额。                                             |
| `DreaminaVersion`                               | 保留 / 复用             | 复用原对象，补齐 edition/distribution/releaseDate/releaseNotes，兼容 build_time/buildTime。                                                            |
| `DreaminaCanvasArguments`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthAccountRequest`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthLoginRequest`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthLogoutRequest`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthRefreshRequest`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthStatusRequest`               | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasAuthWaitRequest`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCompletionBashRequest`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCompletionFishRequest`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCompletionPowershellRequest`     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasCompletionZshRequest`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasContract`                        | 新增具体类型            | 读取递归 Java schema 对象，复用旧范围校验，校验具体请求的参数。                                                                                        |
| `DreaminaCanvasCreateRequest`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasHelpRequest`                     | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasLsRequest`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelFindRequest`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelListRequest`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasModelRequest`                    | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeConfirmRequest`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateAudioRequest`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateElementRequest`        | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateImageRequest`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateTextRequest`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateTimelineRequest`       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeCreateVideoRequest`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditAudioRequest`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditElementRequest`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditImageRequest`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditTextRequest`             | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditTimelineRequest`         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeEditVideoRequest`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeFindRequest`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeQuoteRequest`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeRunRequest`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeShowRequest`                 | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasNodeUpscaleImageRequest`         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasOperationStatusRequest`          | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasOperationWaitRequest`            | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasParameter`                       | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasRequest`                         | 新增具体类型            | 绑定具体响应类型的请求基类；复用 DreaminaCliArgumentProvider，不提供字符串键参数 builder。                                                             |
| `DreaminaCanvasResourceDownloadRequest`         | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResourceGetRequest`              | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasResourceUploadRequest`           | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasSchemaRequest`                   | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasVersionRequest`                  | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCanvasVoiceListRequest`                | 新增具体类型            | Canvas 1.0.1 独立参数/响应或传输类型；具体字段与官方 schema 的映射由全量字段测试验证。                                                                 |
| `DreaminaCliArgumentProvider`                   | 保留 / 复用             | 新请求实现原接口，toCliArgs 不含完整命令路径。                                                                                                         |
| `DreaminaCliContractValidator`                  | 旧协议限定 / Deprecated | 校验旧 v1.4 模型/请求约束；替代：DreaminaCanvasContract 与实时 model list/find。                                                                       |
| `DreaminaCliRequestSupport`                     | 保留 / 复用             | 新契约直接复用 requireRange；会 trim 文本的 addFlag 不用于原样 prompt。旧视频约束方法单独废弃。                                                        |
| `DreaminaFrames2VideoRequest`                   | 旧协议限定 / Deprecated | 旧 first/last frame 本地路径 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_VIDEO，素材先上传资源。                       |
| `DreaminaImage2ImageRequest`                    | 旧协议限定 / Deprecated | 本地图片路径及旧 model_version/session_id 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_IMAGE，素材先上传资源。          |
| `DreaminaImage2VideoRequest`                    | 旧协议限定 / Deprecated | 旧本地 image 参数 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_VIDEO，素材先上传资源。                                  |
| `DreaminaImageModelVersion`                     | 旧协议限定 / Deprecated | 旧值 4.7/5.0Pro 不是 Canvas canonical 模型 ID；替代：实时 model list/find 返回值。                                                                     |
| `DreaminaImageResolutionType`                   | 保留 / 复用             | 新 request.option 直接接受原枚举，适配 Canvas 的 K 大小写；可用档位仍由实时模型决定。                                                                  |
| `DreaminaImageUpscaleRequest`                   | 旧协议限定 / Deprecated | 旧 image 路径/resolution 参数 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_UPSCALE_IMAGE，素材先上传资源。                     |
| `DreaminaListTaskRequest`                       | 旧协议限定 / Deprecated | 旧 list_task 的分页/任务状态；新 CLI 无同形任务列表 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + OPERATION_STATUS，素材先上传资源。 |
| `DreaminaMultiframe2VideoRequest`               | 旧协议限定 / Deprecated | 旧 transition 参数及本地路径 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_VIDEO，素材先上传资源。                       |
| `DreaminaMultimodal2VideoRequest`               | 旧协议限定 / Deprecated | 旧 image/video/audio 路径和旧模型默认值 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_VIDEO，素材先上传资源。            |
| `DreaminaQueryResultRequest`                    | 旧协议限定 / Deprecated | 旧 submit_id/download_dir 参数 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + OPERATION_STATUS/RESOURCE_DOWNLOAD，素材先上传资源。    |
| `DreaminaRatio`                                 | 保留 / 复用             | 新 request.option 直接接受原枚举并取原 cliValue；不另建比例枚举。                                                                                      |
| `DreaminaText2ImageRequest`                     | 旧协议限定 / Deprecated | model_version/generate_num/session_id/poll 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_IMAGE，素材先上传资源。         |
| `DreaminaText2VideoRequest`                     | 旧协议限定 / Deprecated | 旧 model_version/video_resolution/session_id 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + NODE_CREATE_VIDEO，素材先上传资源。       |
| `DreaminaVideoModelVersion`                     | 旧协议限定 / Deprecated | 硬编码旧模型路由及模态/时长能力，不能代表 Canvas 实时 generation spec；替代：model list/find。                                                         |
| `DreaminaVideoResolutionType`                   | 保留 / 复用             | 新 request.option 直接接受原枚举，保留 p 并适配 K；可用档位由实时模型决定。                                                                            |
| `DreaminaCanvasResponseParser`                  | 新增具体类型            | 严格 schemaVersion/ok JSON envelope 与旧宽松文本解析规则不兼容，复用 Jackson 工厂。                                                                    |
| `DreaminaCliJsonExtract`                        | 保留 / 复用             | 通用混合文本 JSON 提取工具保留；Canvas 要求严格 envelope，不能用宽松提取掩盖畸形响应。                                                                 |
| `DreaminaCliOutputParser`                       | 旧协议限定 / Deprecated | 旧文本/正则提取 gen_status/submit_id，不支持 Canvas ok/partialData；替代：DreaminaCanvasResponseParser。                                               |
| `DreaminaCliStructuredPayloadMapper`            | 保留 / 复用             | 新 parser/contract 复用 Jackson 工厂，新 helpDetails 复用 mapHelp；其余旧协议 map 方法单独废弃。                                                       |
| `DreaminaLoginTextParser`                       | 保留 / 复用             | DreaminaDeviceLogin 继续复用 hasDeviceFlowMaterial；旧文本检测/解析方法单独废弃。                                                                      |
| `DreaminaParsedFields`                          | 保留 / 复用             | 原始结果保留的通用可选诊断快照，旧消费者继续使用；Canvas 不填不可靠的正则推断。                                                                        |
| `DreaminaCanvasProcessRunner`                   | 新增具体类型            | 实例并发/总超时/有界输出无法直接使用旧全局限流工具，返回共享结果及通用异常。                                                                           |
| `SubprocessExecutionSupport`                    | 旧协议限定 / Deprecated | 全局信号量、无界输出与仅进程超时无法满足 Canvas 实例隔离及总预算；替代：DreaminaCanvasProcessRunner，保留旧调用行为。                                  |
| `DreaminaCanvasCliException`                    | 新增具体类型            | 继承原异常，只补原层级未表达的协议/中断/输出限制故障。                                                                                                 |
| `DreaminaCliException`                          | 保留 / 复用             | 新协议异常继续继承此通用异常基类，诊断快照复用。                                                                                                       |
| `DreaminaCliExecutableFailureException`         | 保留 / 复用             | Canvas 启动失败直接抛出原异常类型。                                                                                                                    |
| `DreaminaCliNonZeroExitException`               | 保留 / 复用             | 保留通用非零退出异常供旧执行与严格调用方使用；Canvas 合法业务错误作为响应，不强行改为异常。                                                            |
| `DreaminaCliStartupException`                   | 保留 / 复用             | 可继续包装复用的 Canvas availabilityReport 供应用启动 fail-fast 使用。                                                                                 |
| `DreaminaCliTimeoutException`                   | 保留 / 复用             | Canvas 排队/执行/排空超时直接抛出原异常，保留 partialResult。                                                                                          |
| `DreaminaImageCompressOptions`                  | 保留 / 复用             | Canvas 下载图片可直接使用原压缩选项。                                                                                                                  |
| `DreaminaImageCompressResult`                   | 保留 / 复用             | Canvas 下载图片可直接使用原压缩结果。                                                                                                                  |
| `DreaminaImageCompressSupport`                  | 保留 / 复用             | 与 CLI 协议无关，继续直接处理 Canvas 下载文件，不新增压缩实现。                                                                                        |
| `DreaminaStrings`                               | 保留 / 复用             | 原共享工具仍被复用的请求/认证/可用性对象调用；新增 StringUtils 遵循项目规范，无需删除或废弃原工具。                                                    |

验证由 `DreaminaObjectReuseTest` 检查清单与全部生产类型逐一匹配、废弃状态一致及命名约束；`DreaminaCanvasFieldCoverageTest`
检查请求与响应字段；旧测试继续保护历史行为。当前测试结果见 [强类型验收报告](DREAMINA_CANVAS_TYPED_API.md)。
