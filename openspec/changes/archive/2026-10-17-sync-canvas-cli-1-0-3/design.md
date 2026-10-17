# Design — sync typed Canvas integration to CLI 1.0.3

## 事实源与差异

以本机公开版 1.0.3 二进制只读输出为唯一事实源（未登录可执行 `version`/`schema`；
模型目录属服务端事实，不进快照）。对 1.0.1 快照与 1.0.3 实时 schema 做逐路由
diff，差异全集：

- 新路由：`node delete`（node-id 必填 1..100、update-id、delete-token）、
  `node delete confirm`（签发 deleteConfirmationToken/expiresAtMs）、
  `node delete status`（只读回查 found/result/idempotentReplay/error）。
- flag 增补：create audio + scenario/ref/resource-source；create 其余 + resource-source；
  edit 全六类 + x/y；edit audio 另 + scenario/ref/resource-source。
- `resource upload --project-id` required: false → true。
- 既有命令无 flag 删除；顶层命令族与退出码集合不变。

## 关键决策

1. **快照驱动，不手写类型。** 沿用 `generate_canvas_types.py` 流水线：换快照 →
   重跑生成器 → 手工仅补枚举/executor/测试。生成器两处扩展（map<string,非object>、
   多行 Javadoc 匹配仓库风格），保证重生成与已提交格式逐字节兼容（除真实变更外
   零 diff，已用 upscale 等未变更文件验证）。
2. **删除族并入公共 node 家族。** 枚举按 schema 树序插在 NODE_UPSCALE_IMAGE 之后；
   executor 方法名沿用驼峰反射约定（nodeDelete/nodeDeleteConfirm/nodeDeleteStatus），
   覆盖测试的反射分发无需特判。
3. **必填收紧由快照自动生效。** `DreaminaCanvasContract.validate` 读快照的
   required 字段，resource upload 的 project-id 收紧不需要新代码；只修测试用例
   与断言（互斥用例补 project-id 以保持其测试意图）。
4. **文档可再生成。** 新增 `render_canvas_commands_md.py`，先以旧快照 round-trip
   校验（与既有 COMMANDS.md 除版本行外逐字节一致），再渲染 1.0.3。位置参数补充
   说明改用 constraints 原文（旧文档为手写中文翻译，保留机械派生原则）。
5. **三分支同构移植。** 2.0.x/3.0.x 与 1.0.x 的画布代码仅 parser 有 JDK 语法
   差异；以 cherry-pick 移植，冲突处保留各分支 JDK 风格。生成器与快照为
   分支无关资产，cherry-pick 后直接生效。

## 验证策略

- `DreaminaCanvasFieldCoverageTest`：每个 schema flag 必须有同型字段+builder
  方法+executor 方法；successData/dryRunData 每字段可反序列化（含
  map<string,string>）。
- `schemaEnumAndTestCasesHaveExactlyTheSameRoutes`：schema 40 路由 == 枚举 ==
  覆盖用例三方一致。
- 真实 CLI：`-Ddreamina.canvas.live=true` 下版本断言 1.0.3；随包 schema 与
  已安装 CLI schema 语义等价（node sortKeys 对比）。
- 本地 Windows 已知预存失败（进程 mock 与路径分隔符类，基线同样失败，CI 在
  Linux 执行）：不作为本变更回归依据，但逐类与基线失败集合比对确认无新增。
