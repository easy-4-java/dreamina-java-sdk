## Why

dreamina-canvas 公开版从 1.0.1（09307bb）升级到 1.0.3（244064d，2026-10-14 构建）。
官方更新：支持 Seed Audio 1.0，全能配音支持选择音色或参考素材；支持删除节点和
调整节点位置。SDK 随包契约停留在 1.0.1，出现以下偏差：

- 缺少 `node delete` / `node delete confirm` / `node delete status` 三个业务入口。
- `node edit image/video/audio/element/text/timeline` 缺 `--x/--y`；六个 create 与
  六个 edit 缺 `--resource-source`；audio create/edit 缺 `--scenario` 与 `--ref`。
- `resource upload --project-id` 从可选转为必填（"目标项目 UUID；必填，即使已指定
  resource-id"），随包校验器必须跟随收紧。
- dry-run 计划新增 `resourceSourceProjects`（map<string,string>），`node show`
  新增 position 对象，类型生成器不认识 map<string,非object>。

## What Changes

- 以本机 1.0.3 只读 `schema`/`version` 输出刷新随包快照
  （schema-1.0.3.json、version-1.0.3.json），`DreaminaCanvasContract` 与生成器指向新快照。
- 扩展 `generate_canvas_types.py`：支持 `map<string,非object>`；输出与仓库一致的
  多行 Javadoc；`Objects` import 仅在存在枚举 overload 时添加。
- 重新生成全部 Request/Result：新增 3 个删除族 Request/Result、
  DreaminaCanvasPosition，并落齐上述 flag 字段（Javadoc 取自 1.0.3 schema 描述，
  含 x/y 语义从"导入节点坐标"改为"节点左上角/组内父相对坐标"）。
- `DreaminaCanvasCommand` 枚举补三个删除路由（40 个业务入口）；
  `DreaminaCanvasCliExecutor` 挂载 nodeDelete/nodeDeleteConfirm/nodeDeleteStatus。
- 测试同步：覆盖表新增删除用例并给 resource upload 用例补必填 project-id；
  字段覆盖测试的样本生成器支持 map<string,string>；捆绑快照失败测试与版本断言
  更新到 1.0.3。
- 新增 `render_canvas_commands_md.py`：由随包 schema 重渲染
  docs/DREAMINA_CANVAS_COMMANDS.md（旧快照 round-trip 逐字节一致后渲染 1.0.3）。
- 文档基线更新：README 双语（40 入口/1.0.3）、CANVAS/ACCEPTANCE/TYPED_API/
  OBJECT_REUSE 与对象复用清单（231→238 条，7 个新类型）。

## Capabilities

### Modified Capabilities

- canvas-cli-integration: 契约快照、入口枚举、请求/响应类型与覆盖测试对齐 1.0.3。

## Impact

三个分支同构落地（1.0.x=JDK 8 先行，cherry-pick 到 2.0.x=JDK 17、3.0.x=JDK 21，
仅 parser 存在 JDK 语法差异）。不改变既有 37 个入口的 API 形态；resource upload
的必填收紧是 1.0.3 的破坏性契约变更，随包校验器同步执行。验证：字段覆盖
80/80、schema-枚举-用例三方路由一致、真实 CLI 版本断言 1.0.3 通过、随包 schema
与已安装 CLI schema 语义等价；进程 mock 类测试在本地 Windows 预存失败（基线
同样失败，CI 在 Linux 执行）。
