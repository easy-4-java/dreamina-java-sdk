## Why

用户要求所有新增对象以 Dreamina 为前缀，逐对象确认复用，无法适配 Canvas 的旧专属对象明确废弃，并覆盖所有指令。

## What Changes

- 统一新类型命名，直接改名未发布类型。
- 建立覆盖全部既有顶层类型的复用清单；复用请求接口、响应容器、比例/分辨率枚举、通用参数校验、可用性检查、版本与设备授权
  DTO、通用异常。
- 只给旧协议专属类型和混合工具的旧协议方法加 Deprecated 与具体替代说明，保留旧行为。
- 保持 schema、枚举、逐命令测试及真实调用证据一致；继续补 auth wait 成功分支。

## Capabilities

### Modified Capabilities

- canvas-cli-integration: 新类型命名、真实对象复用、全部指令覆盖。
- legacy-cli-deprecation: 逐对象判定废弃边界，替代仅废弃入口的上一轮规则。

## Impact

保持目录、旧签名和旧执行行为。未发布 Canvas 类型直接重命名，调用方使用新名称。没有新生成或扩大积分授权。
