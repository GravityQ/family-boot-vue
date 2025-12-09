# 服务拆分方案：管理后台与 APP 接口分离

## 当前架构分析

**现状**：
- 所有接口（管理后台 + APP）都在 `yudao-server` 服务中
- 通过包名区分：`controller/admin/` 和 `controller/app/`
- 共享相同的业务模块（`yudao-module-system`、`yudao-module-infra` 等）

**问题**：
- 无法独立部署和扩展
- 资源无法隔离（内存、线程池等）
- 安全边界不清晰

---

## 方案一：创建两个独立的 Server 模块（推荐）

### 架构设计

```
yudao/
├── yudao-server-admin/          # 管理后台服务（新增）
│   ├── pom.xml                  # 只引入 admin 相关模块
│   └── src/main/java/.../YudaoServerAdminApplication.java
│
├── yudao-server-app/            # APP 服务（新增）
│   ├── pom.xml                  # 只引入 app 相关模块
│   └── src/main/java/.../YudaoServerAppApplication.java
│
└── yudao-server/                # 保留（可选：用于本地开发或单服务部署）
```

### 优点
- ✅ **完全物理隔离**：两个独立服务，互不影响
- ✅ **独立部署扩展**：可分别扩容、配置不同的 JVM 参数
- ✅ **安全隔离**：管理后台和 APP 接口完全分离
- ✅ **资源隔离**：各自独立的线程池、连接池
- ✅ **灵活配置**：可为不同服务配置不同的中间件（如不同的 Redis DB）

### 缺点
- ❌ **代码重复**：需要维护两个 server 模块的配置
- ❌ **部署复杂**：需要部署两个服务
- ❌ **共享数据**：仍共享相同的数据库和 Redis

### 实施步骤

#### 1. 创建 `yudao-server-admin` 模块

**创建目录结构**：
```bash
mkdir -p yudao-server-admin/src/main/java/cn/iocoder/yudao/server/admin
mkdir -p yudao-server-admin/src/main/resources
```

**创建 `yudao-server-admin/pom.xml`**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>cn.iocoder.boot</groupId>
        <artifactId>yudao</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>yudao-server-admin</artifactId>
    <packaging>jar</packaging>

    <name>${project.artifactId}</name>
    <description>
        管理后台服务，提供 RESTful API 给 yudao-ui-admin 前端项目
    </description>

    <dependencies>
        <!-- 核心模块（包含 admin controller） -->
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-system</artifactId>
            <version>${revision}</version>
        </dependency>
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-infra</artifactId>
            <version>${revision}</version>
        </dependency>

        <!-- 按需引入其他业务模块（只包含 admin 接口的） -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-member</artifactId>
            <version>${revision}</version>
        </dependency> -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-bpm</artifactId>
            <version>${revision}</version>
        </dependency> -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-pay</artifactId>
            <version>${revision}</version>
        </dependency> -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-mall</artifactId>
            <version>${revision}</version>
        </dependency> -->

        <!-- Spring Boot 配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 服务保障 -->
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-spring-boot-starter-protection</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**创建启动类 `YudaoServerAdminApplication.java`**：
```java
package cn.iocoder.yudao.server.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理后台服务启动类
 */
@SpringBootApplication
public class YudaoServerAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(YudaoServerAdminApplication.class, args);
    }

}
```

**复制配置文件**：
```bash
# 从 yudao-server 复制配置文件
cp yudao-server/src/main/resources/application*.yaml yudao-server-admin/src/main/resources/
```

**修改 `application.yaml`**（可选，用于区分服务）：
```yaml
spring:
  application:
    name: yudao-server-admin  # 修改服务名

server:
  port: 48080  # 管理后台端口（保持原端口或修改）

yudao:
  swagger:
    title: 芋道管理后台 API
    description: 提供管理后台的所有功能
```

#### 2. 创建 `yudao-server-app` 模块

**创建目录结构**：
```bash
mkdir -p yudao-server-app/src/main/java/cn/iocoder/yudao/server/app
mkdir -p yudao-server-app/src/main/resources
```

**创建 `yudao-server-app/pom.xml`**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>cn.iocoder.boot</groupId>
        <artifactId>yudao</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>yudao-server-app</artifactId>
    <packaging>jar</packaging>

    <name>${project.artifactId}</name>
    <description>
        APP 服务，提供 RESTful API 给 yudao-ui-app 前端项目
    </description>

    <dependencies>
        <!-- 核心模块（包含 app controller） -->
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-system</artifactId>
            <version>${revision}</version>
        </dependency>
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-infra</artifactId>
            <version>${revision}</version>
        </dependency>

        <!-- 按需引入其他业务模块（只包含 app 接口的） -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-member</artifactId>
            <version>${revision}</version>
        </dependency> -->
        <!-- <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-module-mall</artifactId>
            <version>${revision}</version>
        </dependency> -->

        <!-- Spring Boot 配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 服务保障 -->
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-spring-boot-starter-protection</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**创建启动类 `YudaoServerAppApplication.java`**：
```java
package cn.iocoder.yudao.server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APP 服务启动类
 */
@SpringBootApplication
public class YudaoServerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(YudaoServerAppApplication.class, args);
    }

}
```

**复制并修改配置文件**：
```yaml
spring:
  application:
    name: yudao-server-app  # 修改服务名

server:
  port: 48081  # APP 端口（使用不同端口）

yudao:
  swagger:
    title: 芋道 APP API
    description: 提供用户 APP 的所有功能
```

#### 3. 修改根 `pom.xml`，添加新模块

```xml
<modules>
    <module>yudao-dependencies</module>
    <module>yudao-framework</module>
    
    <!-- Server 服务（三选一或全部保留） -->
    <module>yudao-server</module>           <!-- 原服务（可选保留） -->
    <module>yudao-server-admin</module>     <!-- 管理后台服务（新增） -->
    <module>yudao-server-app</module>       <!-- APP 服务（新增） -->
    
    <!-- 业务模块 -->
    <module>yudao-module-system</module>
    <module>yudao-module-infra</module>
    <!-- ... 其他模块 ... -->
</modules>
```

#### 4. 通过配置排除不需要的 Controller（可选优化）

如果希望更彻底地隔离，可以在启动类中排除 `app` 或 `admin` 包：

**`YudaoServerAdminApplication.java`**：
```java
@SpringBootApplication(
    scanBasePackages = "cn.iocoder.yudao",
    exclude = {
        // 排除 APP Controller（如果不需要）
        // 注意：这种方式需要精确配置，可能影响其他组件
    }
)
public class YudaoServerAdminApplication {
    // ...
}
```

**更推荐的方式**：通过 Spring 的条件注解，在 Controller 上标记：

```java
// 在 admin Controller 上
@ConditionalOnProperty(name = "yudao.server.type", havingValue = "admin", matchIfMissing = true)
@RestController
@RequestMapping("/system/auth")
public class AuthController {
    // ...
}

// 在 app Controller 上
@ConditionalOnProperty(name = "yudao.server.type", havingValue = "app")
@RestController
@RequestMapping("/system/tenant")
public class AppTenantController {
    // ...
}
```

然后在各自的 `application.yaml` 中配置：
```yaml
# yudao-server-admin/application.yaml
yudao:
  server:
    type: admin

# yudao-server-app/application.yaml
yudao:
  server:
    type: app
```

---

## 方案二：通过 Spring Profile 条件加载（逻辑分离）

### 架构设计

保持单一 `yudao-server` 服务，通过 Spring Profile 和条件注解控制加载哪些 Controller。

### 优点
- ✅ **简单快速**：无需创建新模块
- ✅ **配置灵活**：通过 Profile 切换
- ✅ **代码复用**：共享同一套配置和启动类

### 缺点
- ❌ **非物理隔离**：仍在同一 JVM 进程中
- ❌ **无法独立扩展**：无法单独扩容
- ❌ **资源不隔离**：共享线程池、连接池
- ❌ **安全边界模糊**：代码层面仍在一起

### 实施步骤

#### 1. 在 Controller 上添加条件注解

**修改 admin Controller**：
```java
@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@ConditionalOnProperty(name = "yudao.server.profiles", havingValue = "admin", matchIfMissing = true)
public class AuthController {
    // ...
}
```

**修改 app Controller**：
```java
@Tag(name = "用户 App - 租户")
@RestController
@RequestMapping("/system/tenant")
@ConditionalOnProperty(name = "yudao.server.profiles", havingValue = "app")
public class AppTenantController {
    // ...
}
```

#### 2. 创建不同的 Profile 配置

**`application-admin.yaml`**：
```yaml
yudao:
  server:
    profiles: admin

server:
  port: 48080
```

**`application-app.yaml`**：
```yaml
yudao:
  server:
    profiles: app

server:
  port: 48081
```

#### 3. 启动时指定 Profile

```bash
# 启动管理后台服务
java -jar yudao-server.jar --spring.profiles.active=dev,admin

# 启动 APP 服务
java -jar yudao-server.jar --spring.profiles.active=dev,app
```

---

## 方案对比总结

| 维度 | 方案一：独立 Server 模块 | 方案二：Profile 条件加载 |
|------|------------------------|------------------------|
| **隔离程度** | 完全物理隔离 | 逻辑隔离 |
| **独立部署** | ✅ 支持 | ❌ 不支持 |
| **独立扩展** | ✅ 支持 | ❌ 不支持 |
| **实施复杂度** | 中等（需创建新模块） | 简单（只需添加注解） |
| **维护成本** | 较高（两套配置） | 较低（一套配置） |
| **适用场景** | 生产环境、需要独立扩展 | 开发环境、小规模项目 |

---

## 推荐方案

**生产环境推荐使用方案一**，原因：
1. 真正的服务隔离，符合微服务架构思想
2. 可以独立扩展和优化
3. 安全边界清晰
4. 便于后续演进（如引入 API 网关、服务注册中心等）

**开发环境可考虑方案二**，用于快速验证和开发。

---

## 后续优化建议

1. **引入 API 网关**（如 Spring Cloud Gateway）：统一入口，路由到不同服务
2. **服务注册发现**（如 Nacos、Consul）：动态服务发现
3. **配置中心**：统一管理配置
4. **监控告警**：分别监控两个服务的健康状态

