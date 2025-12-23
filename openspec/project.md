# Project Context

## Purpose
本项目基于 RuoYi-Vue-Pro 的 `master-jdk17` 分支构建，是一个面向企业级场景的快速开发平台。  
核心目标是：在 **保证工程规范、可维护性和可测试性** 的前提下，为中后台管理系统、SaaS 多租户平台、电商、CRM/ERP、报表大屏等业务快速提供一套开箱即用的通用能力，包括系统管理、基础设施、工作流、支付、商城、会员、报表、AI、大屏、IoT 等模块。  
本仓库主要承载 Java 后端服务，管理后台前端与移动端前端则通过独立的前端仓库（如 `yudao-ui-admin-vue3`、`yudao-mall-uniapp` 等）接入。

## Tech Stack
- **语言与运行环境**：Java 17、Maven 多模块项目
- **核心框架**：Spring Boot 3.5.5、Spring MVC 6.2.9
- **数据访问**：MyBatis Plus 3.5.12、Druid 1.2.27、Dynamic Datasource 4.3.1（读写分离 / 多数据源）
- **数据库**：MySQL 5.7/8.0+ 为主，脚本同时支持 Oracle、PostgreSQL、SQL Server、国产达梦 DM、TiDB、Kingbase、openGauss 等
- **缓存与分布式能力**：Redis 6/7、Redisson 3.35.0（分布式锁、幂等、限流等）
- **安全与认证**：Spring Security 6.5.2 + JWT + Redis，支持多终端与多用户类型的认证体系
- **校验与文档**：Hibernate Validator 8.0.2、Springdoc OpenAPI 2.8.9（Swagger 3）
- **工作流与定时任务**：Flowable 7.0.0、Quartz 2.5.0
- **监控与可观测性**：Spring Boot Admin 3.5.2、SkyWalking 9.5.0
- **工具库**：Jackson 2.30.14、MapStruct 1.6.3、Lombok 1.18.38
- **测试**：JUnit 5.12.2、Mockito 5.17.0
- **前端生态（上下文信息）**：Vue 3 + element-plus / vben(ant-design-vue)，Vue 2 + element-ui，uni-app 等（本仓库只包含后端实现）

## Project Conventions

### Code Style
- **语言与分层约定**
  - 后端统一使用 Java 17 + Spring Boot 3.5.5，多模块 Maven 架构。
  - 严格按照 Controller → Service → Mapper 分层架构组织代码，Controller 只做入参校验与编排，业务逻辑放在 Service，数据访问通过 Mapper（MyBatis Plus）。
- **对象模型约定**
  - 严格区分 DO / DTO / VO：
    - `*DO`：数据库实体对象，仅用于持久化层，对应数据表结构；
    - `*ReqVO` / `*DTO`：接口入参或内部传输对象；
    - `*RespVO` / `*VO`：对外返回视图对象。
  - 对象转换统一使用 MapStruct，禁止新代码中引入 BeanUtils 做属性拷贝。
- **编码规范**
  - 遵循《阿里巴巴 Java 开发手册》及芋道官方实践：
    - 使用 Lombok（如 `@Data`、`@Builder` 等）减少样板代码；
    - 禁止使用 `System.out.println`，统一使用 SLF4J 日志接口；
    - 业务异常统一使用 `ServiceException + 错误码` 体系；
    - 公共方法需要具备清晰的 JavaDoc 或注释说明方法职责与关键参数。
  - 命名采用有语义的英文单词，包名使用小写多级结构，类名使用大驼峰命名，方法名与变量名使用小驼峰命名。
- **API 设计与校验**
  - 接口统一使用 RESTful 风格，控制器标注 `@RestController`，请求路径遵循模块前缀 + 资源名的结构。
  - 使用 Springdoc/Swagger 注解（如 `@Tag`、`@Operation`）维护接口文档，保证文档可读、可调试。
  - 请求入参使用 `@Validated` / `@Valid` 配合 Hibernate Validator 做参数校验，常用校验注解如 `@NotNull`、`@NotBlank`、`@Size` 等。
  - 权限控制使用 `@PreAuthorize` 等 Spring Security 注解，并结合自定义表达式（如 `@ss.hasPermi('system:user:list')`）实现菜单与按钮级权限管控。

### Architecture Patterns
- **多模块 Maven 架构**
  - 顶层结构包括：
    - `yudao-dependencies`：Maven BOM，统一管理依赖版本；
    - `yudao-framework`：通用框架扩展与工具类（如 Web、MyBatis、Redis、安全、监控等 Starter）；
    - `yudao-server`：主 Spring Boot 启动应用，聚合各业务模块；
    - `yudao-module-*`：按业务域拆分的模块（system、infra、member、bpm、pay、mall、crm、erp、ai、report、iot 等）。
  - 通过在根 `pom.xml` 的 `<modules>` 中增删 `yudao-module-*` 实现业务模块的按需启用与裁剪。
- **核心业务模块职责**
  - `yudao-module-system`：系统核心功能（用户、角色、菜单、部门、岗位、字典、错误码、日志、租户与租户套餐、敏感词、应用管理等）。
  - `yudao-module-infra`：基础设施能力（代码生成、文件服务、配置管理、定时任务、API 日志、消息队列、监控、链路追踪、服务保障等）。
  - 其他模块（member/mall/pay/bpm/crm/erp/ai/report/iot 等）按业务域提供完整子系统能力，默认可按需开启。
- **安全与多租户**
  - 使用基于 Spring Security + JWT + Redis 的统一认证授权体系，支持多终端、多种用户类型、SSO 单点登录等。
  - 内置多租户（SaaS）支持，在数据访问层通过透明封装实现租户隔离，变更相关逻辑时需慎重评估对所有模块的影响。
- **缓存与服务治理**
  - 广泛使用 Redis 作为缓存与消息基础设施，结合 Redisson 实现分布式锁、幂等控制、限流控制等高并发场景能力。
  - 提供统一的 API 日志、操作/登录日志、链路追踪与 Java 监控，帮助快速定位生产问题。
- **Server 拆分约定（管理后台 vs APP）**
  - 当前默认形态：`yudao-server` 同时承载管理后台接口与 APP 接口，通过包结构（`controller/admin` 与 `controller/app`）区分。
  - 推荐生产形态（详见 `SERVICE_SPLIT_GUIDE.md`）：拆分为 `yudao-server-admin` 与 `yudao-server-app` 两个独立服务，实现物理隔离、独立部署和独立扩缩容。
  - 在 OpenSpec 相关变更中，如涉及 Server 的结构或部署方式调整，应优先参考该拆分方案并在提案中明确影响范围。

### Testing Strategy
- **项目既有测试约定**
  - 使用 JUnit 5 + Mockito 作为单元测试与 Mock 框架。
  - 每个模块在 `src/test/resources/` 下提供 `application-unit-test.yaml` 或类似配置，用于隔离测试环境配置。
  - 官方文档要求对业务逻辑提供单元测试保障，提交前建议执行 `mvn test` 确认通过。
- **推荐实践与优先级**
  - 倡导 **TDD / 测试优先**：对于新功能，优先编写 Service 层或领域逻辑的测试，再实现最小业务代码，最后在重构阶段抽取通用逻辑。
  - 对以下场景优先补充或加强测试：
    - 多租户、权限控制、安全认证相关逻辑；
    - 金额、订单状态、库存等关键业务数据变更逻辑；
    - 复杂查询、动态条件拼接、分页与排序逻辑；
    - 代码生成器生成后经手工扩展的模块。
  - 对公共组件与基础设施（如锁、幂等、限流、文件服务等）保持较高测试覆盖率，避免对全局产生隐性回归。

### Git Workflow
- **推荐分支与提交规范（建议性约定）**
  - 建议以 `master-jdk17` 为主干分支进行后端开发，如需定制化开发可在此基础上创建独立分支。
  - 功能开发建议使用 `feature/xxx` 分支命名，问题修复建议使用 `fix/xxx` 分支命名，避免在主干直接进行大规模改动。
  - 提交信息建议简洁明确，包含模块与意图，例如：
    - `[system] 新增租户套餐分页查询接口`
    - `[infra] 修复定时任务执行日志时间不正确问题`
  - 大型改动建议拆分为多个小提交，每个提交保持可编译、可测试、易回滚。

## Domain Context
本项目是芋道生态下的 **完整版后端**，包含以下关键业务域与上下文信息：

- **系统功能（System）**
  - 用户、角色、菜单、部门、岗位等 RBAC 权限模型与组织结构管理；
  - 多租户与租户套餐管理，支持为不同租户分配不同的菜单/操作/按钮权限；
  - 字典、错误码、敏感词、通知公告、站内信、应用管理等通用系统能力；
  - 操作日志、登录日志、在线用户等审计与运维能力。
- **基础设施（Infra）**
  - 代码生成器：可从数据库表生成 Java 后端 + Vue 前端 + SQL + 单元测试等代码；
  - 文件服务：支持本地、S3/MinIO、阿里云、腾讯云、七牛云等多种存储实现；
  - 配置管理：支持在后台动态维护常用配置，并由 Spring Boot 动态加载；
  - 定时任务：基于 Quartz 的在线任务管理与执行日志；
  - API 日志、数据库与 Redis 监控、消息队列（基于 Redis Stream / PubSub）、Java 监控、链路追踪、日志中心等。
- **扩展业务域**
  - 工作流程（BPM）：基于 Flowable，支持钉钉/飞书风格 SIMPLE 设计器与 BPMN 设计器，满足复杂审批场景；
  - 支付系统：对接支付宝、微信支付，管理商户应用、支付订单、退款订单及回调通知；
  - 商城系统：商品、订单、促销、交易等完整电商闭环能力；
  - 会员中心：会员、会员标签、成长值与积分等用户运营能力；
  - ERP、CRM、报表、大屏、AI 大模型、微信公众号、IoT 等模块按需启用。

在引入或修改需求时，应优先判断其归属模块（system/infra/mall/pay/bpm/...），避免跨域堆叠逻辑；涉及多租户、权限、安全、支付、资金等领域的改动，应在设计与评审阶段明确风险与边界。

## Important Constraints
- **运行环境与依赖约束**
  - 要求至少使用 JDK 17、Maven 3.8+，数据库建议使用 MySQL 8.0+，Redis 建议使用 6.0+。
  - 开发/测试环境可以复用文档中给出的云数据库与 Redis 配置，但生产环境必须自行配置安全可靠的实例，并避免使用示例账号密码。
- **数据库与迁移策略**
  - 当前未引入 Flyway 等自动迁移工具，数据库变更通过 `sql/` 目录下的脚本进行维护与手工导入。
  - 新增或修改表结构时，必须同步更新对应数据库类型（如 MySQL）的脚本文件，并在变更说明/PR 中明确说明影响范围与初始化方式。
- **安全与合规要求**
  - 不允许在新代码中硬编码敏感凭证（数据库密码、AccessKey、Secret 等）；已有明文配置仅用于开发与演示环境，生产应通过环境变量或安全配置中心管理。
  - 需要遵循 OWASP Web 安全实践，避免 SQL 注入、XSS、CSRF、敏感信息泄露等常见安全问题；涉及登录、注册、密码找回、支付等流程时需格外审慎。
  - 多租户与权限相关改动必须确保向后兼容，不得在未充分评估的情况下改变现有租户/用户的数据可见性。
- **性能与复杂度控制**
  - 默认优先代码可读性、可维护性、可测试性，避免过早为性能牺牲清晰度；除非有明确的性能数据与业务场景支撑，否则不引入过度复杂的优化或框架。
  - 遵循 KISS、DRY、YAGNI 原则：保持实现简单、避免重复和过度设计。

## External Dependencies
- **数据库与缓存**
  - MySQL 作为主关系数据库，支持其他多种数据库（Oracle、PostgreSQL、SQL Server、达梦 DM、TiDB、Kingbase、openGauss 等）通过不同脚本适配。
  - Redis 用于缓存、分布式锁、消息队列（Stream / PubSub）、会话与令牌存储等。
- **对象存储与文件服务**
  - 本地文件系统；
  - S3 兼容对象存储（MinIO、阿里云 OSS、腾讯云 COS、七牛云 Kodo 等）。
- **消息与通知渠道**
  - 短信服务：阿里云、腾讯云等主流短信服务商；
  - 邮件服务：SMTP 或第三方邮件平台；
  - 站内信：系统内置消息通知能力。
- **支付与第三方平台**
  - 支付：支付宝、微信支付（包括支付与退款相关回调）；
  - 微信生态：微信公众号、小程序、企业微信，以及钉钉等平台的消息与登录能力。
- **监控与可观测性组件**
  - Spring Boot Admin：监控 Java 应用运行状态；
  - SkyWalking：分布式链路追踪与日志中心。

在使用上述外部依赖时，AI 助手应优先查阅对应模块实现与配置示例，遵循既有集成方式进行扩展或修改，不应擅自更换第三方服务或协议；在测试代码中，如需对外部依赖进行 Mock，需避免改变真实集成代码路径，只在测试范围内封装替身实现。
