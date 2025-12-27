# 项目开发指南 (Project Guide)

本文档旨在帮助开发者快速理解 `yudao-boot-vue` 项目的技术架构，并掌握模块管理与 API 文档的使用方法。

## 1. 技术架构概览

本项目是基于 RuoYi-Vue-Pro 重构的企业级快速开发平台，采用前后端分离架构。

### 后端架构
*   **核心框架**: Spring Boot 3.5.5 (JDK 17)
*   **构建工具**: Maven (多模块架构)
*   **数据库**: MySQL 5.7/8.0+ (配合 MyBatis Plus 3.5.12)
*   **缓存**: Redis 6/7 (配合 Redisson 3.35.0)
*   **权限安全**: Spring Security 6.5.2 + JWT
*   **API 文档**: Springdoc (Swagger 3) / Knife4j
*   **工作流**: Flowable 7.0.0
*   **定时任务**: Quartz 2.5.0

### 前端架构
*   **框架**: Vue 3
*   **UI 组件库**: Element Plus / Ant Design Vue (Vben)
*   **移动端**: uni-app

## 2. 如何开启/关闭功能模块

本项目采用 Maven 多模块架构，业务功能（如 CRM、ERP、商城等）被拆分为独立的模块。你可以通过修改根目录下的 `pom.xml` 文件来灵活开启或关闭这些功能。

### 步骤
1.  打开项目根目录下的 `pom.xml` 文件。
2.  找到 `<modules>` 标签部分。
3.  **开启模块**: 将被注释的 `<module>` 标签取消注释。
4.  **关闭模块**: 将不需要的 `<module>` 标签注释掉。

### 示例

```xml
<modules>
    <!-- 核心基础模块 (必须保留) -->
    <module>yudao-dependencies</module>
    <module>yudao-framework</module>
    <module>yudao-server</module>
    <module>yudao-module-system</module>
    <module>yudao-module-infra</module>
    
    <!-- 业务扩展模块 (按需开启) -->
    <module>yudao-module-member</module>
    <!-- <module>yudao-module-bpm</module> -->     <!-- 工作流模块 (当前已关闭) -->
    <!-- <module>yudao-module-report</module> -->  <!-- 报表模块 (当前已关闭) -->
    <module>yudao-module-pay</module>
    <module>yudao-module-mall</module>
    <!-- <module>yudao-module-crm</module> -->     <!-- CRM 模块 (当前已关闭) -->
    <!-- <module>yudao-module-erp</module> -->     <!-- ERP 模块 (当前已关闭) -->
    <!-- <module>yudao-module-ai</module> -->      <!-- AI 模块 (当前已关闭) -->
    <module>yudao-module-iot</module>
</modules>
```

**注意**: 修改 `pom.xml` 后，请务必在 IDE 中刷新 Maven 项目 (Reload Maven Projects)，以便依赖变更生效。

## 3. 如何查看 API 接口文档

项目集成了 Swagger/Knife4j，启动服务后可直接在浏览器中查看和调试接口。

### 访问地址
*   **接口文档首页**: `http://localhost:48080/doc.html`
    *   注: 端口默认为 `48080`，如果修改了 `application.yaml` 中的端口，请相应调整。

### 使用说明
1.  启动 `yudao-server` 服务。
2.  访问上述 URL。
3.  在左侧菜单中选择对应的模块（如“系统模块”、“基础设施”等）。
4.  点击具体接口，可以查看请求参数、响应结构，并使用“调试”功能直接发送请求。
5.  **Authorize**: 如果接口需要登录，请先登录系统获取 Token，或在文档页面的全局参数设置中配置 Token。

## 4. 快速启动

### 环境准备
*   JDK 17+
*   Maven 3.8+
*   MySQL 8.0+
*   Redis 6.0+

### 启动步骤
1.  **编译项目**:
    ```bash
    mvn clean install -DskipTests
    ```
2.  **配置数据库**:
    *   在 MySQL 中创建数据库（如 `ruoyi-vue-pro`）。
    *   导入 `sql/` 目录下对应的 SQL 脚本。
    *   修改 `yudao-server/src/main/resources/application-dev.yaml` 中的数据库连接信息。
3.  **运行服务**:
    ```bash
    cd yudao-server
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```
    或者直接运行 `yudao-server/target/yudao-server.jar`。

## 5. 项目开发规则

### 5.1 运行环境配置
**规则**: 运行和debug时均必须指定dev环境。

**说明**: 
- 在IDE中运行或调试应用时，必须通过VM options或环境变量指定 `spring.profiles.active=dev`
- 这确保开发环境使用正确的配置文件（`application-dev.yaml`）
- 避免因环境配置错误导致的开发问题

**配置方式**:
- **IntelliJ IDEA**: Run/Debug Configuration → VM options: `-Dspring.profiles.active=dev`
- **命令行**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- **环境变量**: `SPRING_PROFILES_ACTIVE=dev`

## 6. 常见问题
*   **依赖下载失败**: 建议配置阿里云 Maven 镜像，配置在 `pom.xml` 中已包含。
*   **Lombok 报错**: 确保 IDE 已安装 Lombok 插件并启用注解处理。

