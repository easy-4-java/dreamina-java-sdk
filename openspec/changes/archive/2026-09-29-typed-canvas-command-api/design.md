## Decisions

请求对象为每命令独立 POJO，属性与 schema flags/positionals 一一对应，公共请求接口绑定响应泛型。公共执行器方法显式返回具体响应类型；不能由调用方任意指定
Class<T> 伪装响应类型。schema 自身也使用 Java 对象建模，递归字段/命令使用递归类型。动态 key（如按模型分组的音色）仅允许 Map<
String,具体类型>。

响应嵌套结构按完整字段签名和语义复用；成功与 dry-run 字段显式建模。未知服务端字段保留在原始 stdout 中，不使用 JSON 树或任意
Object 容器。错误、meta、partialData、creditConfirmation 均有明确对象。旧兼容层已有 JsonNode 公共签名不在本次破坏性删除范围，新
Canvas 不调用该树解析路径。

对旧 DTO 用兼容扩展、别名或显式适配恢复复用；请求的旧 toCliArgs 必须保持旧行为，Canvas 请求不冒充旧路由。保留原 CLI
结果、异常和参数接口。

## Verification

先用反射测试确保所有 route 拥有具体请求及固定响应类型，并检查 Canvas 源码禁止 JsonNode/Map<String,Object>。逐 flag 验证请求字段和
argv，递归验证完整响应 schema；逐 route 受控进程成功与错误测试，安装态完整 schema、只读请求及 dry-run
验收。既有真实生成证据不冒充新类型映射证据，重新读取已完成操作和资源完成验证。
