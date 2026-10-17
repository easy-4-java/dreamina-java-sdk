## Decisions

以 docs/validation/dreamina-object-reuse.json 为完整逐类型清单，测试检查清单与生产文件集合、废弃注解一致。DreaminaCanvasRequest
实现旧 argv 接口（不含顶层命令），保留完整 toArguments；DreaminaCanvasResponse 组合旧响应容器；比例和分辨率使用原枚举，模型规则仍由实时发现决定。复用旧
timeout/startup 异常，Canvas 异常只处理原层级没有的失败类型。原可用性检查器增加 Canvas 专用方法，保留旧方法语义。

旧请求硬编码 session/poll/model-version 等旧参数，不能用于 Canvas project/node/ref/update/submit 流程；旧模型枚举也不能直接作为
canonical ID。因此保留原行为但标记旧类型废弃。通用工具不因未用于某条路径而废弃。混合 mapper 保留共享 Jackson 工厂与 help
映射，仅标记旧协议映射方法。

## Verification

先运行会暴露命名与复用缺口的测试，再实现。独立递归 schema 全路由比对、37 条成功/错误执行测试和真实证据矩阵继续保留。人工认证成功必须取得真实
auth wait 成功返回，不能用超时冒充。通过 SDK 在隔离 profile 开始授权，授权引用仅存内存，最后注销隔离 profile。
