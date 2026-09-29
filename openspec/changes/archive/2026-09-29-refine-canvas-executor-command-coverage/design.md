## Decisions

按用户修订直接重命名未发布入口。旧 API 保留行为，只将旧迁移入口 DreaminaCliExecutor 标记废弃；保留原来就存在的方法级废弃。命令覆盖由
schema 独立递归枚举得到，并与枚举、逐条参数化测试及真实运行记录比对。运行真实写操作使用专用验收画布，认证注销/未认证刷新使用专用
profile。真实响应只记录稳定身份、状态、错误码与 requestId，不记录访问令牌、登录码或签名 URL。

## Validation

先观察改名和辅助类型撤销废弃断言失败，再修改；逐命令测试对 argv 及非零错误 envelope 做断言。真实 CLI
尽可能执行全部命令，并把无法完整授权的命令作为明确缺口，不以 mock/dry-run 充当端到端成功。

## 用户追加的目录和复用约束

保留既有 cli/opts/model/parser/support、exception 与根配置目录，不保留独立 cli.canvas 生产包。复用 DreaminaCliResult
作为原始/帮助/补全结果，复用 DreaminaCliException 作为异常基类，复用 DreaminaCliProperties 的通用配置字段（新配置只添加
Canvas 特有字段）。因此通用配置也不废弃，最终仅旧执行器新增类级 Deprecated。旧共享执行器的全局限流不适合新协议的实例限流与有界中断，新执行工具放回已有
support 包。
