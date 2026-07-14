# Java\-Standards\-DDD 简化微服务脚手架架构文档

## 一、项目基础信息

1. **项目名称**：java\-standards\-ddd

2. **项目定位**：阿里实战简化版 DDD 微服务架构，合并应用层 \+ 领域层为业务层，摒弃过度分层，适配企业日常开发通用架构

3. **技术栈**：SpringBoot 4\.1\.0、Maven3、MyBatis\-Plus 3\.5\.17、JDK21、Lombok

4. **项目坐标分组**

    - DDD 架构：`groupId: top.binaryx.ddd`

    - 统一版本号：`1.0.0`

5. **打包运行规则**：父工程采用 pom 聚合打包，仅`facade`模块为可独立运行启动工程，其余子模块均为 Jar 依赖模块

## 二、整体工程模块结构

```Plain Text
java-standards-ddd 【父聚合工程 pom】
├─ java-standards-common    公共通用模块
├─ java-standards-api       对外契约二方库模块
├─ java-standards-infra     基础设施层模块
├─ java-standards-service   核心业务逻辑层模块
└─ java-standards-facade    统一接入层 + 项目启动入口模块
```

## 三、模块单向依赖规则（严禁循环依赖）

1. java\-standards\-common：无任何内部业务模块依赖，仅引入 Spring 基础依赖

2. java\-standards\-api：仅依赖 common 模块

3. java\-standards\-infra：依赖 api 模块

4. java\-standards\-service：依赖 infra 模块

5. java\-standards\-facade：依赖 service 模块，**禁止直接依赖 infra**

## 四、各模块职责、分包与开发规范

### 1\. java\-standards\-common 公共模块

**核心职责**：存放项目全局通用能力，全项目所有模块均可引用
**Java 根包路径**：`top.binaryx.common`
**固定业务分包**

- result：全局统一响应结果实体 Result

- exception：自定义业务异常 BizException、全局统一异常处理器 GlobalExceptionHandler

- util：通用工具类

- constant：项目全局常量类
**开发规范**：无任何业务代码、无数据库操作、无接口请求相关逻辑

### 2\. java\-standards\-api 对外契约模块

**核心职责**：定义微服务之间调用契约，只做定义不做业务实现
**Java 根包路径**：`top.binaryx.api`
**固定业务分包**

- dto：接口请求入参实体

- vo：接口响应出参实体

- query：分页、条件查询参数实体

- enums：项目全局业务枚举

- service：对外暴露服务接口（仅编写 interface 接口，不编写实现类）
**开发规范**：不注册 Spring 容器 Bean、无业务逻辑、无任何业务实现代码

### 3\. java\-standards\-infra 基础设施层

**核心职责**：对接底层各类资源，纯数据搬运适配层
**Java 根包路径**：`top.binaryx.infra`
**固定业务分包**

- do：数据库持久化实体，严格对应数据表结构

- mapper：MyBatis\-Plus 数据持久层 Mapper 接口

- manager：底层数据操作管理器

- config：框架配置类（包含 MyBatisPlus 分页插件配置类）

- remote：第三方接口调用、RPC 远程调用适配类
**开发规范**：仅实现 CRUD、数据存取、外部接口转发，**禁止编写任何业务判断逻辑**

### 4\. java\-standards\-service 业务逻辑层

**核心职责**：合并流程编排能力 \+ 核心业务规则，项目所有业务逻辑统一收敛层
**Java 根包路径**：`top.binaryx.service`
**固定业务分包**

- interface：业务服务顶层抽象接口

- impl：业务服务接口具体实现类
**开发规范**

1. 统一处理业务流程编排、事务控制、业务规则校验、业务数据组装

2. 仅调用 infra 底层资源能力，不直接接收前端请求

3. `@Service`、`@Transactional` 事务相关注解统一在此层使用

### 5\. java\-standards\-facade 统一接入层（启动层）

**核心职责**：项目唯一启动入口、请求统一门面接入，替代传统 web 层
**Java 根包路径**：`top.binaryx.facade`
**固定业务分包**

- controller：HTTP 接口控制器

- interceptor：全局请求拦截器

- config：WEB 场景自定义配置类
**资源存放**：项目全局配置文件 `application.yml` 存放于此模块
**启动硬性规则**

1. 项目主启动类 `DddApplication` 必须放置在此模块

2. 全局包扫描范围：`top.binaryx` 全路径

3. Mapper 扫描指定固定路径：`top.binaryx.infra.mapper`
**开发规范**

4. Controller 仅负责请求接收、参数校验、调用 service 业务层

5. 严禁在 Controller 内部编写任何业务逻辑

6. 预留扩展位置，可后续新增 RPC 接口、MQ 消费接入逻辑

## 五、项目标准执行调用链路

```Plain Text
前端请求 → 网关路由 → facade控制器 → service业务层 → infra基础设施层 → 数据库/第三方中间件
```

## 六、全局核心配置硬性要求

1. SpringBoot 固定版本：4\.1\.0

2. MyBatis\-Plus 固定版本：3\.5\.17，内置标准 MySQL 分页插件配置类

3. 数据库适配：默认适配 MySQL8\.0 及以上，开启下划线转驼峰命名，主键采用自增策略

4. 异常统一处理：全局拦截业务异常、参数校验异常、系统未知异常，统一按照 Result 格式返回数据

5. 编译运行环境：统一 JDK21，项目文件编码统一为 UTF\-8

## 七、项目开发红线规范（强制遵守）

1. 禁止跨层越级调用，如 facade 直接调用 infra、service 反向依赖 facade

2. 禁止将业务逻辑编写至 facade 控制器层

3. 禁止将业务判断、业务规则编写至 infra 底层数据层

4. 禁止所有子模块之间形成 Maven 循环依赖

5. 数据库 DO 实体仅允许定义在 infra 层，禁止向外层模块直接暴露

## 八、启动类固定编写要求

1. 启动类固定名称：`DddApplication`

2. 必须添加注解：`@SpringBootApplication`、`@ComponentScan` 全包扫描、`@MapperScan` 指定 mapper 路径

3. 内置标准 main 方法，实现 SpringBoot 项目启动逻辑

## 九、默认 yml 基础配置内容

1. 服务默认启动端口：8080

2. 内置 MySQL 数据源基础配置

3. MyBatis\-Plus 全局基础配置

4. 预留日志配置、线程池配置、中间件配置扩展位置

> （注：部分内容可能由 AI 生成）
