## Why

用户明确禁止 Canvas 集成使用 JsonNode，要求逐条命令以 Java 对象表达参数、请求与响应，原通用字符串 option 接口不满足 SDK
强类型要求。

## What Changes

- 37 个业务命令逐一提供请求类、固定响应类型及执行器方法；帮助与四种补全同样建模。
- Canvas 层移除 JsonNode 和 Map<String,Object> / Object 参数入口，JSON 直接反序列化为 POJO。
- 共用结构只定义一次，复用现有 DTO 并重新审视类级废弃；旧 CLI 兼容签名保留。
- 根据完整 schema 递归核对所有参数和响应字段；增加逐命令 argv、响应、错误、恢复及实机测试。

## Capabilities

### Modified Capabilities

- canvas-cli-integration: 逐命令强类型公共 API，禁用 Canvas JSON 树接口。

## Impact

尚未发布的通用 Canvas API 直接替换；旧 CLI 公共签名不改。保留目录与 Dreamina 命名规则；不自动发起付费生成。
