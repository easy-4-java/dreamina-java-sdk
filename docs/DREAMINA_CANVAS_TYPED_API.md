# Canvas 强类型 API 验收

> 分支同步说明：下文的 Java 8、1.0.x 统计与真实生成记录是源分支历史证据。当前 `feature/3.0.x` 以 Java 21、`3.0.x.20260830-SNAPSHOT` 完成 `clean verify`：591 项测试、579 通过、12 跳过、0 失败，JaCoCo 门禁通过。Canvas 使用独立的 Jackson 2 解析路径，原有 CLI 保持 Jackson 3。

本轮规格：`typed-canvas-command-api`，2026-09-29。基线为本机官方 CLI 1.0.1，Java 8。原目录结构不变。

## 实现

- 37 个具体业务请求，分别绑定 Java 响应数据类型及执行器方法。
- 帮助与 bash、zsh、fish、powershell 补全的 5 个具体请求，复用原文本结果对象。
- 全部 259 个命令 flag、7 个位置参数具名建模；profile、region 等全局配置由执行器快照管理。
- 成功和 dry-run 契约共 3,781 个字段路径进行递归校验（按每条命令计数，重复使用的结构重复计数），包括模型动态字典、递归规则、节点、资源、提交、认证、时间轴等。
- Canvas 生产接口和实现没有 JsonNode、任意 Object 属性、Map<String,Object> 或公开的字符串键 option builder。
- 原版本、账号、设备授权及退出登录 DTO 扩展复用。原参数接口、通用配置、原始结果、异常、可用性报告与比例/分辨率枚举继续复用。
- 旧 CLI 已发布接口仍保留历史签名；其中旧响应的 JSON 树只属于兼容路径，不由 Canvas API 暴露。

[完整指令与类型映射](validation/dreamina-canvas-types.json)；[对象复用清单](DREAMINA_CANVAS_OBJECT_REUSE.md)。

## 验证结果

先增加测试，观察到缺少 `DreaminaCanvasModelRequest` 及 Canvas 仍含 JSON 树两项失败；完成实现后执行最终 Java 8
`clean verify`，开启安装态测试：

| 验证                | 结果                                                     |
|---------------------|----------------------------------------------------------|
| 全量测试            | 573 项，561 通过，12 跳过，0 失败、0 错误                |
| Canvas 专项         | 188 项全部通过，无跳过                                   |
| 业务指令覆盖        | 37/37，逐条成功与结构化失败                              |
| 参数/响应字段覆盖   | 37 组参数检查 + 37 组成功/dry-run 深度字段检查           |
| 本机安装态          | 版本、完整 schema、各级帮助、4 种补全及本地 dry-run 通过 |
| Canvas 执行器行覆盖 | 93/95（97.89%）                                          |
| 参数层行覆盖        | 591/610（96.89%）                                        |
| Canvas 解析层行覆盖 | 17/17（100%）                                            |
| Canvas 进程层行覆盖 | 81/91（89.01%）                                          |
| 构建与门禁          | BUILD SUCCESS，全部 JaCoCo 门禁通过                      |

12 项跳过属于旧 CLI 可选真实审计等测试，不计为通过。Java 8 发出 CodeCache 提示，但测试、报告和覆盖率门禁均实际完成。

## 真实结果复验

通过本轮具体 Java 请求执行 `operation status`、`operation wait`、`node show`、`resource get`、`resource download`
，全部成功。原任务状态为 `succeeded`；下载得到 1024×1024 PNG，987,513 字节，实际文件 SHA-256：

`bad93aebd8c4bee4561bda4798caa5821305b0ca36a7c22aa3c35e462b4ef4bf`

哈希与此前真实生成验收一致，图片已查看，符合白底橘猫内容。本轮复验已有生成结果，没有提交新的付费生成。

此前 37 条业务命令及 5 条辅助命令的真实执行记录继续保留在 [原命令覆盖报告](DREAMINA_CANVAS_COMMAND_COVERAGE.md)
。这些是历史证据，不能当作本轮重新执行了全部登录、扣费与写入操作。本轮所有指令均重跑离线契约测试，真实复验范围如上。

## CodeGraph 与规格

已同步 176 个变化文件，并查询执行器、具体请求、解析器、进程执行链及受影响测试。图谱含未解析引用及同名方法推断，作为定位依据；实际调用和运行结果以源码、测试及
CLI 验证为准。

OpenSpec
严格校验通过；本轮增量规格与实现同步归档。机器可读结果见 [验证清单](validation/dreamina-canvas-typed-verification.json)
。本机完整日志、Surefire/JaCoCo、图谱输出和图片位于：

`/Users/wandl/.codex/visualizations/2026/09/29/01a0eb79-a9cd-79a2-be81-04ff4198b650/canvas-typed-api/`

未提交或推送 Git 变更。

## 覆盖率补测（2026-09-29）

在上述首次验收基础上，补充参数边界、结构化响应类型、配置兼容、损坏的随包 schema、真实子进程的输出上限与继承管道，以及已安装
CLI 的帮助/补全测试。测试发现并修复了数字响应字段被 Jackson 隐式转换成字符串的问题；内部时长校验收敛到具体请求实际编码的纳秒格式。

重新以 Java 8 执行 `mvn -B --no-transfer-progress -Ddreamina.canvas.live=true clean verify`，结果为 592 项测试、580 通过、12
跳过、0 失败、0 错误，`BUILD SUCCESS`，JaCoCo 门禁通过。JaCoCo **仅统计 `pom.xml` 中明确纳入的 166 个类**，并非整个 SDK：行覆盖
1,333/1,340（99.48%），分支覆盖 376/394（95.43%）。Canvas 执行器为 95/95 行、22/22 分支；Canvas 解析器为 23/23 行、10/10 分支。

因此不能宣称 100%。剩余 7 行主要在随包 schema 缺失处理及子进程 I/O 故障清理；剩余 18 个分支在契约校验和进程故障路径。缺包、损坏、不完整
schema 已通过隔离 ClassLoader 测试验证，JaCoCo 未把隔离加载的覆盖计入主报告。12 项跳过仍是旧 CLI
可选真实审计等测试。此次补测没有重做付费生成；上文的真实生成验收证据继续有效。

[补测机器可读记录](validation/dreamina-canvas-coverage-followup.json)。本轮构建日志、Surefire 报告和 JaCoCo 报告保存在
`/Users/wandl/.codex/visualizations/2026/09/29/01a0eb79-a9cd-79a2-be81-04ff4198b650/canvas-coverage-followup/`。
