# Dreamina CLI 持续维护与当前契约

`DreaminaCliExecutor` 与 `DreaminaCanvasCliExecutor` 并行支持。此前仅因 Canvas 迁移新增的 60 处类/成员 Deprecated 注解和 Javadoc 已撤销；旧请求、解析器、账户与 Session 对象继续维护。无效模型别名和已替代内部子进程钩子的历史标记仍保留，因为它们与本次迁移撤销无关。

## 事实源

用户提供的官方指南记录到 v1.4.18；2026-10-28 官方安装入口 version.json 标记 v1.4.20，但安装器实际下发二进制自报 v1.4.21，commit `89c725632a1b0eae7c75436a2690a4838f260261`、build_time `2026-10-19T03:11:21Z`。本次按实际二进制采集根、分组、登录检查和全部业务命令共 24 份帮助，见 `docs/validation/dreamina-1.4.21/`。

## 参数更新

`image2video` 和 `frames2video` 请求新增可选 `DreaminaRatio ratio`，默认不传，保持旧调用行为。视频支持 1:1、3:4、16:9、4:3、9:16、21:9；图片用的 3:2、2:3 不作为视频比例。Seedance 2.5 的图生视频与首尾帧视频拒绝显式 ratio；省略后跟随首帧。文生视频和多模态视频也执行六种比例校验。

保留 v1.4.14、v1.4.15、v1.4.17 分辨率、模型、时长、批次和素材约束测试，新增 v1.4.21 比例与安装态全部指令帮助快照。来源计费等逻辑由官方 CLI 实现，SDK 继续调用官方 CLI，不伪造后端参数。

## 验证范围

生成、查询、账户与 Session 的原有 SDK 入口继续由隔离 mock 回归；安装态验证仅执行 `version`、`-h` 和 Canvas 本地 dry-run，不执行登录、生成、积分消费或真实 Session 写入。通过这些测试不代表真实业务生成或账户权益已验收。
