## 1. 契约与生成器

- [x] 1.1 只读采集 1.0.3 schema/version，替换随包快照并更新契约引用
- [x] 1.2 扩展生成器（map<string,非object>、多行 Javadoc、条件 Objects import）
- [x] 1.3 重跑生成器并复核与既有格式的零回归（未变更文件零 diff）

## 2. 代码与测试

- [x] 2.1 枚举补 node delete 三路由；executor 挂载三个方法
- [x] 2.2 覆盖测试补删除用例；resource upload 用例补必填 project-id
- [x] 2.3 字段覆盖样本生成器支持 map<string,string>；捆绑快照失败测试与
      版本断言更新到 1.0.3
- [x] 2.4 JDK 8 全量测试：字段覆盖 80/80、路由三方一致、真实 CLI 版本/schema
      等价通过；进程 mock 类失败与基线一致（Windows 预存）

## 3. 文档与分支

- [x] 3.1 新增 render_canvas_commands_md.py 并重渲染命令目录（40 入口/49 组）
- [x] 3.2 README 双语、CANVAS/ACCEPTANCE/TYPED_API/OBJECT_REUSE 基线更新；
      对象复用清单 231→238
- [x] 3.3 cherry-pick 到 feature/2.0.x（JDK 17）与 feature/3.0.x（JDK 21），
      各自构建测试通过后提交
